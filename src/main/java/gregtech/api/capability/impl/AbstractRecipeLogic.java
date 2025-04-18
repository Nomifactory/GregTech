package gregtech.api.capability.impl;

import gregtech.api.GTValues;
import gregtech.api.capability.GregtechTileCapabilities;
import gregtech.api.capability.IMultipleTankHandler;
import gregtech.api.capability.IWorkable;
import gregtech.api.metatileentity.MTETrait;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.recipes.Recipe;
import gregtech.api.recipes.RecipeMap;
import gregtech.api.util.GTUtility;
import gregtech.common.ConfigHolder;
import gregtech.common.sound.GTSoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.*;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.function.LongSupplier;

public abstract class AbstractRecipeLogic extends MTETrait implements IWorkable {

    private static final String ALLOW_OVERCLOCKING = "AllowOverclocking";
    private static final String OVERCLOCK_VOLTAGE = "OverclockVoltage";
    private static final String WORK_ENABLED = "WorkEnabled";
    private static final String PROGRESS = "Progress";
    private static final String MAX_PROGRESS = "MaxProgress";
    private static final String RECIPE_EUT = "RecipeEUt";
    private static final String ITEM_OUTPUTS = "ItemOutputs";
    private static final String FLUID_OUTPUTS = "FluidOutputs";
    private static final String CHANCE = "Chance";
    private static final String CHANCED_ITEM_OUTPUTS = "ChancedItemOutputs";
    private static final String NON_CHANCED_ITEM_AMOUNT = "NonChancedItemAmt";

    public final RecipeMap<?> recipeMap;

    protected Recipe previousRecipe;
    protected boolean allowOverclocking = true;
    private long overclockVoltage = 0;
    private LongSupplier overclockPolicy = this::getMaxVoltage;

    protected int progressTime;
    protected int maxProgressTime;
    protected int recipeEUt;
    protected List<FluidStack> fluidOutputs;
    protected NonNullList<ItemStack> itemOutputs;
    protected final Random random = new Random();

    protected boolean isActive;
    protected boolean workingEnabled = true;
    protected boolean hasNotEnoughEnergy;
    protected boolean wasActiveAndNeedsUpdate;
    protected boolean isOutputsFull;
    protected boolean invalidInputsForRecipes;
    protected int nonChancedItemAmt;

    /**
     * A list containing all possible chanced item outputs from the active recipe, paired with their
     * overclock-boosted chance value.
     */
    protected List<Pair<ItemStack, Integer>> chancedItemOutputs;


    public AbstractRecipeLogic(MetaTileEntity tileEntity, RecipeMap<?> recipeMap) {
        super(tileEntity);
        this.recipeMap = recipeMap;
    }

    protected abstract long getEnergyStored();

    protected abstract long getEnergyCapacity();

    protected abstract boolean drawEnergy(int recipeEUt);

    protected abstract long getMaxVoltage();

    protected IItemHandlerModifiable getInputInventory() {
        return metaTileEntity.getImportItems();
    }

    protected IItemHandlerModifiable getOutputInventory() {
        return metaTileEntity.getExportItems();
    }

    protected IMultipleTankHandler getInputTank() {
        return metaTileEntity.getImportFluids();
    }

    protected IMultipleTankHandler getOutputTank() {
        return metaTileEntity.getExportFluids();
    }

    public SoundEvent getSound() {
        if (isActive() && isHasNotEnoughEnergy()) {
            return GTSoundEvents.INTERRUPTED;
        }
        return recipeMap.getSound();
    }

    @Override
    public String getName() {
        return "RecipeMapWorkable";
    }

    @Override
    public int getNetworkID() {
        return TraitNetworkIds.TRAIT_ID_WORKABLE;
    }

    @Override
    public <T> T getCapability(Capability<T> capability) {
        if(capability == GregtechTileCapabilities.CAPABILITY_WORKABLE) {
            return GregtechTileCapabilities.CAPABILITY_WORKABLE.cast(this);
        } else if(capability == GregtechTileCapabilities.CAPABILITY_CONTROLLABLE) {
            return GregtechTileCapabilities.CAPABILITY_CONTROLLABLE.cast(this);
        } else if(capability == GregtechTileCapabilities.CAPABILITY_MUFFLEABLE) {
            return GregtechTileCapabilities.CAPABILITY_MUFFLEABLE.cast(getMetaTileEntity());
        }
        return null;
    }

    @Override
    public void update() {
        World world = getMetaTileEntity().getWorld();
        if (world != null && !world.isRemote) {
            if (workingEnabled) {
                if (progressTime > 0) {
                    updateRecipeProgress();
                }
                //check everything that would make a recipe never start here.
                if (progressTime == 0 && shouldSearchForRecipes()) {
                    trySearchNewRecipe();
                }
            }
            if (wasActiveAndNeedsUpdate) {
                this.wasActiveAndNeedsUpdate = false;
                setActive(false);
            }
        }
    }

    protected boolean shouldSearchForRecipes() {
        return canWorkWithInputs() && canFitNewOutputs();
    }

    protected boolean hasNotifiedInputs() {
        return (metaTileEntity.getNotifiedItemInputList().size() > 0 ||
            metaTileEntity.getNotifiedFluidInputList().size() > 0);
    }

    protected boolean hasNotifiedOutputs() {
        return (metaTileEntity.getNotifiedItemOutputList().size() > 0 ||
            metaTileEntity.getNotifiedFluidOutputList().size() > 0);
    }

    protected boolean canFitNewOutputs() {
        // if the output is full check if the output changed so we can process recipes results again.
        if (this.isOutputsFull && !hasNotifiedOutputs()) return false;
        else {
            this.isOutputsFull = false;
            metaTileEntity.getNotifiedItemOutputList().clear();
            metaTileEntity.getNotifiedFluidOutputList().clear();
        }
        return true;
    }

    protected boolean canWorkWithInputs() {
        // if the inputs were bad last time, check if they've changed before trying to find a new recipe.
        if (this.invalidInputsForRecipes && !hasNotifiedInputs()) return false;
        else {
            this.invalidInputsForRecipes = false;
        }
        return true;
    }

    protected void updateRecipeProgress() {
        boolean drawEnergy = drawEnergy(recipeEUt);
        if (drawEnergy || (recipeEUt < 0)) {
            //as recipe starts with progress on 1 this has to be > only not => to compensate for it
            setHasNotEnoughEnergy(false);
            if (++progressTime > maxProgressTime) {
                completeRecipe();
            }
        } else if (recipeEUt > 0) {
            //only set hasNotEnoughEnergy if this recipe is consuming recipe
            //generators always have enough energy
            setHasNotEnoughEnergy(true);
            //if current progress value is greater than 2, decrement it by 2
            if (progressTime >= 2) {
                if (ConfigHolder.insufficientEnergySupplyWipesRecipeProgress) {
                    this.progressTime = 1;
                } else {
                    this.progressTime = Math.max(1, progressTime - 2);
                }
            }

        }
    }

    protected void trySearchNewRecipe() {
        long maxVoltage = getMaxVoltage();
        Recipe currentRecipe = null;
        IItemHandlerModifiable importInventory = getInputInventory();
        IMultipleTankHandler importFluids = getInputTank();

        // see if the last recipe we used still works
        if (checkPreviousRecipe())
            currentRecipe = this.previousRecipe;
        // If there is no active recipe, then we need to find one.
        else {
            currentRecipe = findRecipe(maxVoltage, importInventory, importFluids);
        }
        // If a recipe was found, then inputs were valid. Cache found recipe.
        if (currentRecipe != null) {
            this.previousRecipe = currentRecipe;
        }
        this.invalidInputsForRecipes = (currentRecipe == null);

        // proceed if we have a usable recipe.
        if (currentRecipe != null && setupAndConsumeRecipeInputs(currentRecipe))
            setupRecipe(currentRecipe);

        // Inputs have been inspected.
        metaTileEntity.getNotifiedItemInputList().clear();
        metaTileEntity.getNotifiedFluidInputList().clear();
    }

    protected boolean checkPreviousRecipe() {
        if(this.previousRecipe == null) return false;
        if(this.previousRecipe.getEUt() > this.getMaxVoltage()) return false;
        return this.previousRecipe.matches(false, getInputInventory(), getInputTank());
    }

    protected int getMinTankCapacity(IMultipleTankHandler tanks) {
        if(tanks.getTanks() == 0) {
            return 0;
        }
        int result = Integer.MAX_VALUE;
        for(IFluidTank fluidTank : tanks.getFluidTanks()) {
            result = Math.min(fluidTank.getCapacity(), result);
        }
        return result;
    }

    protected Recipe findRecipe(long maxVoltage, IItemHandlerModifiable inputs, IMultipleTankHandler fluidInputs) {
        return recipeMap.findRecipe(maxVoltage, inputs, fluidInputs, getMinTankCapacity(getOutputTank()));
    }

    /**
     * @deprecated Use {@link #hasNotifiedInputs() } instead
     * Left here for binary compatibility purposes
     */
    @Deprecated
    protected boolean checkRecipeInputsDirty(IItemHandler inputs, IMultipleTankHandler fluidInputs) {
        boolean isDirty = this.hasNotifiedInputs();
        metaTileEntity.getNotifiedItemInputList().clear();
        metaTileEntity.getNotifiedFluidInputList().clear();
        return isDirty;
    }

    protected static boolean areItemStacksEqual(ItemStack stackA, ItemStack stackB) {
        return (stackA.isEmpty() && stackB.isEmpty()) ||
            (ItemStack.areItemsEqual(stackA, stackB) &&
                ItemStack.areItemStackTagsEqual(stackA, stackB));
    }

    /**
     * Attempts to start the specified recipe. A recipe will fail to start if there is insufficient energy,
     * the output inventories are full, or the required ingredients are not present.
     *
     * @param recipe the recipe to start
     * @return {@code true} if the recipe was started and inputs consumed, {@code false} otherwise.
     */
    protected boolean setupAndConsumeRecipeInputs(Recipe recipe) {
        int[] resultOverclock = calculateOverclock(recipe);
        int totalEUt = resultOverclock[0] * resultOverclock[1];
        IItemHandlerModifiable importInventory = getInputInventory();
        IItemHandlerModifiable exportInventory = getOutputInventory();
        IMultipleTankHandler importFluids = getInputTank();
        IMultipleTankHandler exportFluids = getOutputTank();
        /*
           Ensure standard recipes have enough EU to perform either the first tick of work, or the whole craft if the
           total EU is at most half the capacity. Recipes generating EU must be able to store the first tick of energy.
         */
        if (!(totalEUt >= 0 ? getEnergyStored() >= (totalEUt > getEnergyCapacity() / 2 ? resultOverclock[0] : totalEUt) :
            (getEnergyStored() - resultOverclock[0] <= getEnergyCapacity()))) {
            return false;
        }
        if (!MetaTileEntity.addItemsToItemHandler(exportInventory, true, recipe.getAllItemOutputs(exportInventory.getSlots()))) {
            this.isOutputsFull = true;
            return false;
        }
        if (!MetaTileEntity.addFluidsToFluidHandler(exportFluids, true, recipe.getFluidOutputs())) {
            this.isOutputsFull = true;
            return false;
        }
        this.isOutputsFull = false;
        return recipe.matches(true, importInventory, importFluids);
    }

    /**
     * Performs overclocking with voltage using {@link #overclockPolicy} for the voltage.
     * @see #calculateOverclock(Recipe, long)
     */
    protected int[] calculateOverclock(@NotNull Recipe recipe) {
        return calculateOverclock(recipe, this.overclockPolicy.getAsLong());
    }

    /**
     * Attempts to overclock a given recipe.
     * <ul>
     * <li>Recipes at or below 16 EU/t overclock by halving duration and quadrupling energy consumption, until the duration
     * reaches a single game tick or the EU/t can no longer be increased.</li>
     * <li>Recipes above 16 EU/t overclock by dividing duration by 2.8 and quadrupling energy consumption, until the duration
     * reaches fewer than 3 game ticks or the EU/t can no longer be increased.</li>
     * </ul>
     * @param recipe the Recipe to overclock
     * @param voltage the maximum EU/t to use for overclocking. This value must be positive.
     * @return an {@code int[]} of length 2, where [0] is the computed EU/t and [1] the duration.
     */
    protected int[] calculateOverclock(@NotNull final Recipe recipe, final long voltage) {
        assert(voltage >= 0);

        int EUt = recipe.getEUt();
        int duration = recipe.getDuration();

        if (!allowOverclocking) {
            return new int[] {EUt, duration};
        }
        boolean negativeEU = EUt < 0;
        int tier = getOverclockingTier(voltage);
        if (GTValues.V[tier] <= EUt || tier == 0)
            return new int[]{EUt, duration};
        if (negativeEU)
            EUt = -EUt;
        if (EUt <= 16) {
            int multiplier = EUt <= 8 ? tier : tier - 1;
            // Restrict the maximum number of overclocks to how many times the duration can be halved
            int speedCap = (31 - Integer.numberOfLeadingZeros(duration));
            if(multiplier > speedCap) multiplier = speedCap;
            EUt *= (1 << 2 * multiplier);
            duration /= (1 << multiplier);
        } else {
            // Restrict the maximum number of overclocks to how many times the duration is divisible by 2.8
            int speedCap = (int) (Math.log(duration) / Math.log(2.8));
            int dt = tier - recipe.getBaseTier();
            if(dt > speedCap) dt = speedCap;
            if(dt > 0) {
                EUt *= Math.pow(4, dt);
                duration /= Math.pow(2.8, dt);
            }
        }
        return new int[]{negativeEU ? -EUt : EUt, duration};
    }

    protected int getOverclockingTier(long voltage) {
        return GTUtility.getTierByVoltage(voltage);
    }

    protected long getVoltageByTier(final int tier) {
        return GTValues.V[tier];
    }

    public String[] getAvailableOverclockingTiers() {
        final int maxTier = getOverclockingTier(getMaxVoltage());
        final String[] result = new String[maxTier + 2];
        result[0] = "gregtech.gui.overclock.off";
        for (int i = 0; i < maxTier + 1; ++i) {
            result[i+1] = GTValues.VN[i];
        }
        return result;
    }

    protected void setupRecipe(Recipe recipe) {
        int[] resultOverclock = calculateOverclock(recipe);
        this.progressTime = 1;
        setMaxProgress(resultOverclock[1]);
        this.recipeEUt = resultOverclock[0];
        this.fluidOutputs = GTUtility.copyFluidList(recipe.getFluidOutputs());
        int overclocks = getMachineTierForRecipe(recipe) - recipe.getBaseTier();
        this.nonChancedItemAmt = recipe.getOutputs().size();
        this.chancedItemOutputs = recipe.getChancedRecipeOutputsAtTier(overclocks);
        this.itemOutputs = GTUtility.copyStackList(recipe.getResultItemOutputs(getOutputInventory().getSlots(), random, overclocks));
        if (this.wasActiveAndNeedsUpdate) {
            this.wasActiveAndNeedsUpdate = false;
        } else {
            this.setActive(true);
        }
    }

    /**
     * @return a copy of the currently active recipe's item outputs.
     */
    public @NotNull List<ItemStack> getItemOutputs() {
        if(itemOutputs == null)
            return Collections.emptyList();
        return GTUtility.copyStackList(itemOutputs);
    }

    /**
     * @return a copy of the currently active recipe's fluid outputs.
     */
    public @NotNull List<FluidStack> getFluidOutputs() {
        if(fluidOutputs == null)
            return Collections.emptyList();
        return GTUtility.copyFluidList(fluidOutputs);
    }

    /**
     * @return all possible chanced item outputs paired with their computed chances for the currently active recipe
     */
    public List<Pair<ItemStack, Integer>> getChancedItemOutputs() {
        if(itemOutputs == null)
            return Collections.emptyList();
        return chancedItemOutputs;
    }

    /**
     * <b>This is NOT for energy calculations. Use {@link #getOverclockingTier(long)} for energy.</b><br />
     * Used to override the machine's tier for the purposes of determining chanced outputs.
     * The default implementation simply returns the overclocking tier of the maximum voltage of the machine.
     */
    protected int getMachineTierForRecipe(Recipe recipe) {
        return getOverclockingTier(getMaxVoltage());
    }

    protected void completeRecipe() {
        MetaTileEntity.addItemsToItemHandler(getOutputInventory(), false, itemOutputs);
        MetaTileEntity.addFluidsToFluidHandler(getOutputTank(), false, fluidOutputs);
        this.progressTime = 0;
        setMaxProgress(0);
        this.recipeEUt = 0;
        this.fluidOutputs = null;
        this.itemOutputs = null;
        this.chancedItemOutputs = null;
        this.nonChancedItemAmt = 0;
        setHasNotEnoughEnergy(false);
        this.wasActiveAndNeedsUpdate = true;
    }

    public double getProgressPercent() {
        return getMaxProgress() == 0 ? 0.0 : getProgress() / (getMaxProgress() * 1.0);
    }

    public int getTicksTimeLeft() {
        return maxProgressTime == 0 ? 0 : (maxProgressTime - progressTime);
    }

    @Override
    public int getProgress() {
        return progressTime;
    }

    @Override
    public int getMaxProgress() {
        return maxProgressTime;
    }

    public int getRecipeEUt() {
        return recipeEUt;
    }

    /**
     * @return the number of non-chanced item outputs this recipe produces
     */
    public int getNonChancedItemAmt() {
        return nonChancedItemAmt;
    }

    public void setMaxProgress(int maxProgress) {
        this.maxProgressTime = maxProgress;
        metaTileEntity.markDirty();
    }

    protected void setActive(boolean active) {
        this.isActive = active;
        metaTileEntity.markDirty();
        World world = metaTileEntity.getWorld();
        if (world != null && !world.isRemote) {
            writeCustomData(1, buf -> {
                buf.writeBoolean(active);
                buf.writeBoolean(workingEnabled);
            });
        }
    }

    protected void setHasNotEnoughEnergy(boolean hasNotEnoughEnergy) {
        this.hasNotEnoughEnergy = hasNotEnoughEnergy;
        metaTileEntity.markDirty();
        World world = metaTileEntity.getWorld();
        if (world != null && !world.isRemote) {
            writeCustomData(2, buf -> buf.writeBoolean(hasNotEnoughEnergy));
        }
    }

    @Override
    public void setWorkingEnabled(boolean workingEnabled) {
        this.workingEnabled = workingEnabled;
        metaTileEntity.markDirty();
        World world = metaTileEntity.getWorld();
        if (world != null && !world.isRemote) {
            writeCustomData(1, buf -> {
                buf.writeBoolean(isActive);
                buf.writeBoolean(workingEnabled);
            });
        }
    }

    public void setAllowOverclocking(boolean allowOverclocking) {
        this.allowOverclocking = allowOverclocking;
        this.overclockVoltage = allowOverclocking ? getMaxVoltage() : 0;
        metaTileEntity.markDirty();
    }

    public boolean isHasNotEnoughEnergy() {
        return hasNotEnoughEnergy;
    }

    @Override
    public boolean isWorkingEnabled() {
        return workingEnabled;
    }

    @Override
    public boolean isActive() {
        return isActive;
    }

    public boolean isAllowOverclocking() {
        return allowOverclocking;
    }

    public long getOverclockVoltage() {
        return overclockVoltage;
    }

    public void setOverclockVoltage(final long overclockVoltage) {
        this.overclockPolicy = this::getOverclockVoltage;
        this.overclockVoltage = overclockVoltage;
        this.allowOverclocking = (overclockVoltage != 0);
        metaTileEntity.markDirty();
    }

    /**
     * Sets the overclocking policy to use getOverclockVoltage() instead of getMaxVoltage()
     * and initialises the overclock voltage to max voltage.
     * The actual value will come from the saved tag when the tile is loaded for pre-existing machines.
     *
     * NOTE: This should only be used directly after construction of the workable.
     * Use setOverclockVoltage() or setOverclockTier() for a more dynamic use case.
     */
    public void enableOverclockVoltage() {
        setOverclockVoltage(getMaxVoltage());
    }

    // The overclocking tier
    // it is 1 greater than the index into GTValues.V since here the "0 tier" represents 0 EU or no overclock
    public int getOverclockTier() {
        if (this.overclockVoltage == 0) {
            return 0;
        }
        return 1 + getOverclockingTier(this.overclockVoltage);
    }

    public void setOverclockTier(final int tier) {
        if (tier == 0) {
            setOverclockVoltage(0);
            return;
        }
        setOverclockVoltage(getVoltageByTier(tier - 1));
    }

    @Override
    public void receiveCustomData(int dataId, PacketBuffer buf) {
        if (dataId == 1) {
            this.isActive = buf.readBoolean();
            this.workingEnabled = buf.readBoolean();
            getMetaTileEntity().getHolder().scheduleChunkForRenderUpdate();
        } else if (dataId == 2) {
            this.hasNotEnoughEnergy = buf.readBoolean();
        }
    }

    @Override
    public void writeInitialData(PacketBuffer buf) {
        buf.writeBoolean(this.isActive);
        buf.writeBoolean(this.hasNotEnoughEnergy);
        buf.writeBoolean(this.workingEnabled);
    }

    @Override
    public void receiveInitialData(PacketBuffer buf) {
        this.isActive = buf.readBoolean();
        this.hasNotEnoughEnergy = buf.readBoolean();
        this.workingEnabled = buf.readBoolean();
    }

    @Override
    public NBTTagCompound serializeNBT() {
        NBTTagCompound compound = new NBTTagCompound();
        compound.setBoolean(WORK_ENABLED, workingEnabled);
        compound.setBoolean(ALLOW_OVERCLOCKING, allowOverclocking);
        compound.setLong(OVERCLOCK_VOLTAGE, this.overclockVoltage);
        if (progressTime > 0) {
            compound.setInteger(PROGRESS, progressTime);
            compound.setInteger(MAX_PROGRESS, maxProgressTime);
            compound.setInteger(RECIPE_EUT, this.recipeEUt);
            NBTTagList itemOutputsList = new NBTTagList();
            for (ItemStack itemOutput : itemOutputs) {
                itemOutputsList.appendTag(itemOutput.writeToNBT(new NBTTagCompound()));
            }
            NBTTagList fluidOutputsList = new NBTTagList();
            for (FluidStack fluidOutput : fluidOutputs) {
                fluidOutputsList.appendTag(fluidOutput.writeToNBT(new NBTTagCompound()));
            }
            compound.setTag(ITEM_OUTPUTS, itemOutputsList);
            compound.setTag(FLUID_OUTPUTS, fluidOutputsList);

            NBTTagList chancedItemOutputsList = new NBTTagList();
            for(var entry : chancedItemOutputs) {
                NBTTagCompound tag = entry.getKey().writeToNBT(new NBTTagCompound());
                tag.setInteger(CHANCE, entry.getValue());
                chancedItemOutputsList.appendTag(tag);
            }

            compound.setTag(CHANCED_ITEM_OUTPUTS, chancedItemOutputsList);
            compound.setInteger(NON_CHANCED_ITEM_AMOUNT, nonChancedItemAmt);
        }
        return compound;
    }

    @Override
    public void deserializeNBT(NBTTagCompound compound) {
        this.workingEnabled = compound.getBoolean(WORK_ENABLED);
        this.progressTime = compound.getInteger(PROGRESS);
        if(compound.hasKey(ALLOW_OVERCLOCKING)) {
            this.allowOverclocking = compound.getBoolean(ALLOW_OVERCLOCKING);
        }
        if (compound.hasKey(OVERCLOCK_VOLTAGE)) {
            this.overclockVoltage = compound.getLong(OVERCLOCK_VOLTAGE);
        } else {
            // Calculate overclock voltage based on old allow flag
            this.overclockVoltage = this.allowOverclocking ? getMaxVoltage() : 0;
        }
        this.isActive = false;
        if (progressTime > 0) {
            this.isActive = true;
            this.maxProgressTime = compound.getInteger(MAX_PROGRESS);
            this.recipeEUt = compound.getInteger(RECIPE_EUT);

            NBTTagList itemOutputsList = compound.getTagList(ITEM_OUTPUTS, Constants.NBT.TAG_COMPOUND);
            this.itemOutputs = NonNullList.create();
            for(var i : itemOutputsList)
                if(i instanceof NBTTagCompound tag)
                    this.itemOutputs.add(new ItemStack(tag));

            NBTTagList fluidOutputsList = compound.getTagList(FLUID_OUTPUTS, Constants.NBT.TAG_COMPOUND);
            this.fluidOutputs = new ArrayList<>();
            for(var f : fluidOutputsList)
                if(f instanceof NBTTagCompound tag)
                    this.fluidOutputs.add(FluidStack.loadFluidStackFromNBT(tag));

            NBTTagList chancedItemOutputsList = compound.getTagList(CHANCED_ITEM_OUTPUTS, Constants.NBT.TAG_COMPOUND);
            this.chancedItemOutputs = new ArrayList<>();
            for(var ci : chancedItemOutputsList)
                if(ci instanceof NBTTagCompound tag)
                    this.chancedItemOutputs.add(Pair.of(new ItemStack(tag), tag.getInteger(CHANCE)));

            this.nonChancedItemAmt = compound.getInteger(NON_CHANCED_ITEM_AMOUNT);
        }
    }
}
