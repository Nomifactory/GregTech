package gregtech.loaders.recipe;

import gregtech.api.items.toolitem.ToolMetaItem;
import gregtech.api.recipes.ModHandler;
import gregtech.api.unification.material.Materials;
import gregtech.api.unification.material.type.IngotMaterial;
import gregtech.api.unification.material.type.SolidMaterial;
import gregtech.api.unification.ore.OrePrefix;
import gregtech.api.unification.stack.UnificationEntry;
import gregtech.common.items.MetaItems;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import java.util.function.Function;

import static gregtech.common.items.MetaItems.*;

public class ToolRecipeLoader {
    private ToolRecipeLoader() {}

    public static void init() {
        registerRecipes();
    }

    private static void registerRecipes() {
        IngotMaterial[] mortarMaterials = new IngotMaterial[]{
            Materials.Bronze, Materials.Iron,
            Materials.Steel, Materials.DamascusSteel, Materials.WroughtIron, Materials.RedSteel,
            Materials.BlackSteel, Materials.BlueSteel};

        for (IngotMaterial material : mortarMaterials) {
            ModHandler.addShapedRecipe("mortar_" + material.toString(),
                                       MORTAR.getStackForm(material),
                                       " I ", "SIS", "SSS",
                                       'I', new UnificationEntry(OrePrefix.ingot, material),
                                       'S', OrePrefix.stone);
        }

        SolidMaterial[] softHammerMaterials = new SolidMaterial[]{
            Materials.Wood, Materials.Rubber, Materials.Plastic, Materials.Polytetrafluoroethylene
        };
        for (int i = 0; i < softHammerMaterials.length; i++) {
            SolidMaterial solidMaterial = softHammerMaterials[i];
            ItemStack itemStack = MetaItems.SOFT_HAMMER.getStackForm();
            MetaItems.SOFT_HAMMER.setToolData(itemStack, solidMaterial, 128 * (1 << i), 1, 4.0f, 1.0f);
            ModHandler.addShapedRecipe(String.format("soft_hammer_%s", solidMaterial.toString()), itemStack,
                                       "XX ", "XXS", "XX ",
                                       'X', new UnificationEntry(OrePrefix.ingot, solidMaterial),
                                       'S', new UnificationEntry(OrePrefix.stick, Materials.Wood));
        }

        Function<ToolMetaItem.MetaToolValueItem, ItemStack> woodenToolDataApplier = item ->
            item.setToolData(item.getStackForm(), Materials.Wood, 55, 1, 4.0f, 1.0f);

        ModHandler.addShapedRecipe("soft_hammer_wooden", woodenToolDataApplier.apply(MetaItems.SOFT_HAMMER),
                                   "XX ", "XXS", "XX ",
                                   'X', new UnificationEntry(OrePrefix.plank, Materials.Wood),
                                   'S', new UnificationEntry(OrePrefix.stick, Materials.Wood));

        registerFlintToolRecipes();
    }

    private static void registerFlintToolRecipes() {
        Function<ToolMetaItem.MetaToolValueItem, ItemStack> toolDataApplier = item -> {
            ItemStack itemStack = item.setToolData(item.getStackForm(), Materials.Flint, 80, 1, 6.0f, 2.0f);
            if (itemStack.getItem().canApplyAtEnchantingTable(itemStack, Enchantments.FIRE_ASPECT)) {
                itemStack.addEnchantment(Enchantments.FIRE_ASPECT, 2);
            }
            return itemStack;
        };
        ModHandler.addShapedRecipe("mortar_flint", toolDataApplier.apply(MORTAR),
                                   " I ", "SIS", "SSS",
                                   'I', new ItemStack(Items.FLINT, 1),
                                   'S', OrePrefix.stone);

        ModHandler.addShapedRecipe("sword_flint", toolDataApplier.apply(SWORD),
                                   "F", "F", "S",
                                   'S', new UnificationEntry(OrePrefix.stick, Materials.Wood),
                                   'F', new ItemStack(Items.FLINT, 1));

        ModHandler.addShapedRecipe("pickaxe_flint", toolDataApplier.apply(PICKAXE),
                                   "FFF", " S ", " S ",
                                   'S', new UnificationEntry(OrePrefix.stick, Materials.Wood),
                                   'F', new ItemStack(Items.FLINT, 1));

        ModHandler.addShapedRecipe("shovel_flint", toolDataApplier.apply(SHOVEL),
                                   "F", "S", "S",
                                   'S', new UnificationEntry(OrePrefix.stick, Materials.Wood),
                                   'F', new ItemStack(Items.FLINT, 1));

        ModHandler.addMirroredShapedRecipe("axe_flint", toolDataApplier.apply(AXE),
                                           "FF", "FS", " S",
                                           'S', new UnificationEntry(OrePrefix.stick, Materials.Wood),
                                           'F', new ItemStack(Items.FLINT, 1));

        ModHandler.addMirroredShapedRecipe("hoe_flint", toolDataApplier.apply(HOE),
                                           "FF", " S", " S",
                                           'S', new UnificationEntry(OrePrefix.stick, Materials.Wood),
                                           'F', new ItemStack(Items.FLINT, 1));

        ModHandler.addShapedRecipe("knife_flint", toolDataApplier.apply(KNIFE),
                                   "F", "S",
                                   'S', new UnificationEntry(OrePrefix.stick, Materials.Wood),
                                   'F', new ItemStack(Items.FLINT, 1));
    }
}
