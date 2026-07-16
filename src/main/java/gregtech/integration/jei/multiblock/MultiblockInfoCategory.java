package gregtech.integration.jei.multiblock;

import gregtech.api.GTValues;
import gregtech.api.gui.GuiTextures;
import gregtech.common.metatileentities.MetaTileEntities;
import gregtech.integration.jei.multiblock.infos.*;
import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.gui.recipes.RecipeLayout;
import net.minecraft.client.resources.I18n;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;
import java.util.Map;

public class MultiblockInfoCategory implements IRecipeCategory<MultiblockInfoRecipeWrapper> {

    private final IDrawable background;
    private final IDrawable icon;
    private final IGuiHelper guiHelper;

    public MultiblockInfoCategory(IJeiHelpers helpers) {
        this.guiHelper = helpers.getGuiHelper();
        this.background = this.guiHelper.createBlankDrawable(176, 166);
        this.icon = guiHelper.drawableBuilder(GuiTextures.MULTIBLOCK_CATEGORY.imageLocation, 0, 0, 18, 18).setTextureSize(18, 18).build();
    }

    public static final Map<String, MultiblockInfoRecipeWrapper> multiblockRecipes = new LinkedHashMap<>();
    static {
        var map = multiblockRecipes;
        map.put("primitive_blast_furnace", new MultiblockInfoRecipeWrapper(new PrimitiveBlastFurnaceInfo()));
        map.put("coke_oven", new MultiblockInfoRecipeWrapper(new CokeOvenInfo()));
        map.put("vacuum_freezer", new MultiblockInfoRecipeWrapper(new VacuumFreezerInfo()));
        map.put("implosion_compressor", new MultiblockInfoRecipeWrapper(new ImplosionCompressorInfo()));
        map.put("pyrolyze_oven", new MultiblockInfoRecipeWrapper(new PyrolyzeOvenInfo()));
        map.put("cracker_unit", new MultiblockInfoRecipeWrapper(new CrackerUnitInfo()));
        map.put("diesel_engine", new MultiblockInfoRecipeWrapper(new DieselEngineInfo()));
        map.put("distillation_tower", new MultiblockInfoRecipeWrapper(new DistillationTowerInfo()));
        map.put("electric_blast_furnace", new MultiblockInfoRecipeWrapper(new ElectricBlastFurnaceInfo()));
        map.put("multi_smelter", new MultiblockInfoRecipeWrapper(new MultiSmelterInfo()));
        map.put("large_bronze_boiler", new MultiblockInfoRecipeWrapper(new LargeBoilerInfo(MetaTileEntities.LARGE_BRONZE_BOILER)));
        map.put("large_steel_boiler", new MultiblockInfoRecipeWrapper(new LargeBoilerInfo(MetaTileEntities.LARGE_STEEL_BOILER)));
        map.put("large_titanium_boiler", new MultiblockInfoRecipeWrapper(new LargeBoilerInfo(MetaTileEntities.LARGE_TITANIUM_BOILER)));
        map.put("large_tungstensteel_boiler", new MultiblockInfoRecipeWrapper(new LargeBoilerInfo(MetaTileEntities.LARGE_TUNGSTENSTEEL_BOILER)));
        map.put("large_steam_turbine", new MultiblockInfoRecipeWrapper(new LargeTurbineInfo(MetaTileEntities.LARGE_STEAM_TURBINE)));
        map.put("large_gas_turbine", new MultiblockInfoRecipeWrapper(new LargeTurbineInfo(MetaTileEntities.LARGE_GAS_TURBINE)));
        map.put("large_plasma_turbine", new MultiblockInfoRecipeWrapper(new LargeTurbineInfo(MetaTileEntities.LARGE_PLASMA_TURBINE)));
    }

    public static void registerRecipes(IModRegistry registry) {
        registry.addRecipes(multiblockRecipes.values(), "gregtech:multiblock_info");
    }

    @Override
    @NotNull
    public String getUid() {
        return "gregtech:multiblock_info";
    }

    @Override
    @NotNull
    public String getTitle() {
        return I18n.format("gregtech.multiblock.title");
    }

    @Override
    @NotNull
    public String getModName() {
        return GTValues.MODID;
    }

    @Override
    @NotNull
    public IDrawable getBackground() {
        return background;
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout,
                          MultiblockInfoRecipeWrapper recipeWrapper,
                          @NotNull IIngredients ingredients)
    {
        recipeWrapper.setRecipeLayout((RecipeLayout) recipeLayout, this.guiHelper);

        IGuiItemStackGroup itemStackGroup = recipeLayout.getItemStacks();
        itemStackGroup.addTooltipCallback(recipeWrapper);
    }
}
