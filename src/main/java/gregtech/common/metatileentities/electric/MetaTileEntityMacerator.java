package gregtech.common.metatileentities.electric;

import gregtech.api.GTValues;
import gregtech.api.capability.impl.NotifiableItemStackHandler;
import gregtech.api.capability.impl.RecipeLogicEnergy;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.MetaTileEntityHolder;
import gregtech.api.metatileentity.SimpleMachineMetaTileEntity;
import gregtech.api.recipes.Recipe;
import gregtech.api.recipes.RecipeMap;
import gregtech.api.render.OrientedOverlayRenderer;
import gregtech.api.util.GTUtility;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.items.IItemHandlerModifiable;

public class MetaTileEntityMacerator extends SimpleMachineMetaTileEntity {

    private int outputAmount;

    public MetaTileEntityMacerator(ResourceLocation metaTileEntityId, RecipeMap<?> recipeMap, int outputAmount, OrientedOverlayRenderer renderer, int tier) {
        super(metaTileEntityId, recipeMap, renderer, tier);
        this.outputAmount = outputAmount;
        initializeInventory();
    }

    @Override
    protected RecipeLogicEnergy createWorkable(RecipeMap<?> recipeMap) {
        final RecipeLogicEnergy result = new RecipeLogicEnergy(this, recipeMap, () -> energyContainer) {
            /*
             * Lower tier macerators are unable to produce byproducts. MV macerators used to have two slots, so the
             * reported tier for computing overclocking bonuses was adjusted so that no bonuses are granted until
             * after MV. Macerators now only gain slots at HV, but the prior convention is retained.
             */
            @Override
            protected int getMachineTierForRecipe(Recipe recipe) {
                // if the recipe base tier is above MV, use default logic
                int baseTier = recipe.getBaseTier();
                if(baseTier > GTValues.MV)
                    return super.getMachineTierForRecipe(recipe);

                // otherwise, reduce the number of bonuses applied as though the recipe is MV base
                int tier = GTUtility.getTierByVoltage(getMaxVoltage());
                if (tier > GTValues.MV) {
                    return tier - (GTValues.MV - baseTier);
                }
                return baseTier;
            }
        };
        result.enableOverclockVoltage();
        return result;
    }

    @Override
    protected IItemHandlerModifiable createExportItemHandler() {
        return new NotifiableItemStackHandler(outputAmount, this, true);
    }

    @Override
    public MetaTileEntity createMetaTileEntity(MetaTileEntityHolder holder) {
        return new MetaTileEntityMacerator(metaTileEntityId, workable.recipeMap, outputAmount, renderer, getTier());
    }
}