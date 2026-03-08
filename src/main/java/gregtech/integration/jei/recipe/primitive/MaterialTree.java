package gregtech.integration.jei.recipe.primitive;

import com.google.common.collect.ImmutableList;
import gregtech.api.unification.OreDictUnifier;
import gregtech.api.unification.material.type.FluidMaterial;
import gregtech.api.unification.material.type.IngotMaterial;
import gregtech.api.unification.material.type.Material;
import gregtech.api.unification.ore.OrePrefix;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.List;

public class MaterialTree implements IRecipeWrapper {

    public final static ImmutableList<OrePrefix> PREFIXES = ImmutableList.of(OrePrefix.dustTiny,
            OrePrefix.dust,
            OrePrefix.dustSmall,
            OrePrefix.cableGtSingle,
            OrePrefix.ingotHot,
            OrePrefix.ingot,
            OrePrefix.gem,
            OrePrefix.block,
            OrePrefix.wireGtSingle,
            OrePrefix.stick,
            OrePrefix.nugget,
            OrePrefix.plate,
            OrePrefix.wireFine,
            OrePrefix.frameGt,
            OrePrefix.screw,
            OrePrefix.bolt,
            OrePrefix.gear,
            OrePrefix.spring,
            OrePrefix.stickLong,
            OrePrefix.gearSmall,
            OrePrefix.plateDense,
            OrePrefix.springSmall,
            OrePrefix.ring,
            OrePrefix.lens,
            OrePrefix.foil);

    private final List<List<ItemStack>> itemInputs = new ArrayList<>();
    private final List<List<FluidStack>> fluidInputs = new ArrayList<>();

    private final String name;
    private final String formula;
    private final int blastTemp;
    private final long avgM;
    private final long avgP;
    private final long avgN;

    public MaterialTree(Material material) {
        List<ItemStack> inputDusts = new ArrayList<>();
        for (OrePrefix prefix : PREFIXES) {
            inputDusts.add(OreDictUnifier.get(prefix, material));
        }
        for (ItemStack stack : inputDusts) {
            List<ItemStack> tempList = new ArrayList<>();
            tempList.add(stack);
            itemInputs.add(tempList);
        }

        List<FluidStack> fluids = new ArrayList<>();
        if (material instanceof FluidMaterial) {
            FluidStack stack = ((FluidMaterial) material).getFluid(1000);
            if (stack != null) {
                fluids.add(stack);
            }
        }
        fluidInputs.add(fluids);

        name = material.getLocalizedName();
        formula = material.chemicalFormula;
        avgM = material.getAverageMass();
        avgN = material.getAverageNeutrons();
        avgP = material.getAverageProtons();

        if (material instanceof IngotMaterial && ((IngotMaterial) material).blastFurnaceTemperature > 0) {
            blastTemp = ((IngotMaterial) material).blastFurnaceTemperature;
        } else blastTemp = 0;
    }

    @Override
    public void getIngredients(IIngredients ingredients) {
        ingredients.setInputLists(VanillaTypes.ITEM, itemInputs);
        ingredients.setInputLists(VanillaTypes.FLUID, fluidInputs);
        ingredients.setOutputLists(VanillaTypes.ITEM, itemInputs);
        ingredients.setOutputLists(VanillaTypes.FLUID, fluidInputs);
    }

    public String getMaterialName() {
        return name;
    }

    public String getMaterialFormula() {
        return formula;
    }

    public long getAvgM() {
        return avgM;
    }

    public long getAvgP() {
        return avgP;
    }

    public long getAvgN() {
        return avgN;
    }

    public int getBlastTemp() {
        return blastTemp;
    }
}
