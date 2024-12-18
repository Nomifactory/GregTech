package gregtech.api.capability.impl;

import gregtech.api.capability.IEnergyContainer;
import gregtech.api.capability.IMultipleTankHandler;
import gregtech.api.metatileentity.multiblock.MultiblockAbility;
import gregtech.api.metatileentity.multiblock.RecipeMapMultiblockController;
import gregtech.api.recipes.Recipe;
import gregtech.common.sound.GTSoundEvents;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.items.IItemHandlerModifiable;

import javax.annotation.Nullable;
import java.util.List;

import static gregtech.api.metatileentity.MetaTileEntity.addItemsToItemHandler;
import static gregtech.api.metatileentity.MetaTileEntity.addFluidsToFluidHandler;

public class MultiblockRecipeLogic extends AbstractRecipeLogic {

    /** Indicates that a structure fails to meet requirements for proceeding with the active recipe */
    protected boolean isJammed = false;

    /** DataIDs for packets */
    private static final class DataIDs {
        /** Jammed state changed */
        private static final int JAMMED = 3;
    }

    private boolean invalidated = true;

    public MultiblockRecipeLogic(RecipeMapMultiblockController tileEntity) {
        super(tileEntity, tileEntity.recipeMap);
    }

    @Override
    public void update() {
    }

    public void updateWorkable() {
        super.update();
    }

    /**
     * Used to reset cached values in the Recipe Logic on structure deform
     */
    public void invalidate() {
        invalidated = true;
    }

    public IEnergyContainer getEnergyContainer() {
        RecipeMapMultiblockController controller = (RecipeMapMultiblockController) metaTileEntity;
        return controller.getEnergyContainer();
    }

    @Override
    protected IItemHandlerModifiable getInputInventory() {
        RecipeMapMultiblockController controller = (RecipeMapMultiblockController) metaTileEntity;
        return controller.getInputInventory();
    }

    // Used for distinct bus recipe checking in SoG
    protected List<IItemHandlerModifiable> getInputBuses() {
        RecipeMapMultiblockController controller = (RecipeMapMultiblockController) metaTileEntity;
        return controller.getAbilities(MultiblockAbility.IMPORT_ITEMS);
    }

    @Override
    protected IItemHandlerModifiable getOutputInventory() {
        RecipeMapMultiblockController controller = (RecipeMapMultiblockController) metaTileEntity;
        return controller.getOutputInventory();
    }

    @Override
    protected IMultipleTankHandler getInputTank() {
        RecipeMapMultiblockController controller = (RecipeMapMultiblockController) metaTileEntity;
        return controller.getInputFluidInventory();
    }

    @Override
    protected IMultipleTankHandler getOutputTank() {
        RecipeMapMultiblockController controller = (RecipeMapMultiblockController) metaTileEntity;
        return controller.getOutputFluidInventory();
    }

    @Override
    protected boolean setupAndConsumeRecipeInputs(Recipe recipe) {
        RecipeMapMultiblockController controller = (RecipeMapMultiblockController) metaTileEntity;
        if (controller.checkRecipe(recipe, false) &&
            super.setupAndConsumeRecipeInputs(recipe)) {
            controller.checkRecipe(recipe, true);
            return true;
        } else return false;
    }

    @Override
    protected long getEnergyStored() {
        return getEnergyContainer().getEnergyStored();
    }

    @Override
    protected long getEnergyCapacity() {
        return getEnergyContainer().getEnergyCapacity();
    }

    @Override
    protected boolean drawEnergy(int recipeEUt) {
        long resultEnergy = getEnergyStored() - recipeEUt;
        if (resultEnergy >= 0L && resultEnergy <= getEnergyCapacity()) {
            getEnergyContainer().changeEnergy(-recipeEUt);
            return true;
        } else return false;
    }

    @Override
    protected long getMaxVoltage() {
        return Math.max(getEnergyContainer().getInputVoltage(), getEnergyContainer().getOutputVoltage());
    }

    public boolean isJammed() {
        return this.isJammed;
    }

    private void checkIfJammed() {
        if(metaTileEntity instanceof RecipeMapMultiblockController controller) {
            // determine if outputs will fit
            boolean canFitItems = addItemsToItemHandler(getOutputInventory(), true, itemOutputs);
            boolean canFitFluids = addFluidsToFluidHandler(getOutputTank(), true, fluidOutputs);

            // clear output notifications since we just checked them
            metaTileEntity.getNotifiedItemOutputList().clear();
            metaTileEntity.getNotifiedFluidOutputList().clear();

            // remember prior value
            boolean oldJammed = this.isJammed;

            // Jam if we can't output all items and fluids, or we fail whatever other conditions the controller imposes
            this.isJammed = !(canFitItems && canFitFluids && controller.checkRecipe(previousRecipe, false));

            // Sync state if changed
            if(this.isJammed != oldJammed)
                writeCustomData(DataIDs.JAMMED, buf -> buf.writeBoolean(this.isJammed));
        }
    }

    private boolean hasOutputChanged() {
        return hasNotifiedOutputs() &&
            (!metaTileEntity.getNotifiedItemOutputList().isEmpty() ||
                !metaTileEntity.getNotifiedFluidOutputList().isEmpty());
    }

    @Override
    protected void updateRecipeProgress() {
        // Recheck jammed status after the structure has been invalidated or output inventories were modified
        if(invalidated || hasOutputChanged())
            checkIfJammed();

        // Only proceed if we're not jammed
        if(!isJammed)
            // if the recipe is running
            if(progressTime < maxProgressTime) {
                // clear invalidation flag
                invalidated = false;
                // do normal update check
                super.updateRecipeProgress();
            } else
                // the recipe is done but was probably jammed. Try to complete it.
                completeRecipe();
    }

    @Override
    protected void completeRecipe() {
        /*
            Since multiblocks can share parts, if multiple machines try to output on the same tick and can't,
            the excess outputs would be silently voided. Avoid this scenario by doing a final Jammed state check.
        */
        checkIfJammed();

        // If we're not jammed, proceed with completing the recipe. Otherwise, wait for outputs to notify.
        if(!this.isJammed)
            super.completeRecipe();
    }

    @Override
    public void writeInitialData(PacketBuffer buf) {
        super.writeInitialData(buf);
        buf.writeBoolean(isJammed);
    }

    @Override
    public void receiveInitialData(PacketBuffer buf) {
        super.receiveInitialData(buf);
        isJammed = buf.readBoolean();
    }

    @Override
    public void receiveCustomData(int dataId, PacketBuffer buf) {
        super.receiveCustomData(dataId, buf);
        if(dataId == DataIDs.JAMMED)
            isJammed = buf.readBoolean();
    }

    @Override
    @Nullable
    public SoundEvent getSound() {
        if (isActive && (isJammed || hasNotEnoughEnergy))
            return GTSoundEvents.INTERRUPTED;
        return recipeMap.getSound();
    }
}
