package gregtech.api.unification.material;

import com.google.common.base.CaseFormat;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Table;
import gregtech.api.GTValues;
import gregtech.api.util.GTUtility;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.FMLCommonHandler;
import org.jetbrains.annotations.NonBlocking;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;

public enum MaterialIconType {

    //ITEM TEXTURES
    dustTiny,
    dustSmall,
    dust,
    dustImpure,
    dustPure,
    crushed,
    crushedPurified,
    crushedCentrifuged,
    gem,
    nugget,
    ingot,
    ingotHot,
    ingotDouble,
    ingotTriple,
    ingotQuadruple,
    ingotQuintuple,
    plate,
    plateCurved, // SoG
    plateDouble,
    plateTriple,
    plateQuadruple,
    plateQuintuple,
    plateDense,
    stick,
    lens,
    round,
    bolt,
    screw,
    ring,
    cell,
    cellPlasma,
    toolHeadSword,
    toolHeadPickaxe,
    toolHeadShovel,
    toolHeadAxe,
    toolHeadHoe,
    toolHeadHammer,
    toolHeadFile,
    toolHeadSaw,
    toolHeadBuzzSaw,
    toolHeadDrill,
    toolHeadChainsaw,
    toolHeadSense,
    toolHeadArrow,
    toolHeadScrewdriver,
    toolHeadBuzSaw,
    toolHeadSoldering,
    toolHeadWrench,
    toolHeadUniversalSpade,
    wireFine,
    gearSmall,
    rotor,
    stickLong,
    springSmall,
    spring,
    arrow,
    gemChipped,
    gemFlawed,
    gemFlawless,
    gemExquisite,
    gear,
    foil,
    crateGtDust,
    crateGtIngot,
    crateGtGem,
    crateGtPlate,
    turbineBlade,
    handleMallet,
    toolHeadMallet,

    //BLOCK TEXTURES
    block,
    foilBlock,
    wire,
    ore,
    frameGt,
    frameSide,
    frameTop,
    pipeSide,
    pipeTiny,
    pipeSmall,
    pipeMedium,
    pipeLarge,
    pipeHuge;

    public static final ImmutableMap<String, MaterialIconType> values;

    private static final Table<MaterialIconType, MaterialIconSet, ResourceLocation> ITEM_MODEL_CACHE = HashBasedTable.create();
    private static final Table<MaterialIconType, MaterialIconSet, ResourceLocation> BLOCK_TEXTURE_CACHE = HashBasedTable.create();

    static {
        ImmutableMap.Builder<String, MaterialIconType> builder = ImmutableMap.builder();
        for (MaterialIconType value : values()) {
            builder.put(CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, value.name()), value);
        }
        values = builder.build();
    }

    public String getFormattedName() {
        return CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, name());
    }

    /**
     * Finds the location of the asset associated with the iconset using its parent as a fallback
     */
    public ResourceLocation getIconSetPath(MaterialIconSet iconSet, Table<MaterialIconType, MaterialIconSet, ResourceLocation> cache, String fullPath, String path, String extension) {
        if (cache.contains(this, iconSet)) {
            return cache.get(this, iconSet);
        }

        if (!iconSet.isRootIconSet && FMLCommonHandler.instance().getSide().isClient()) {
            ResourceLocation fullLocation = new ResourceLocation(GTValues.MODID, String.format(fullPath, iconSet.name, this.getFormattedName(), extension));
            if (!GTUtility.doResourcepacksHaveResource(fullLocation)) {
                ResourceLocation iconSetPath = getIconSetPath(iconSet.getParent(), cache, fullPath, path, extension);
                cache.put(this, iconSet, iconSetPath);
                return iconSetPath;
            }
        }

        ResourceLocation iconSetPath = new ResourceLocation(GTValues.MODID, String.format(path, iconSet.name, this.getFormattedName()));
        cache.put(this, iconSet, iconSetPath);
        return iconSetPath;
    }

    public ResourceLocation getBlockPath(MaterialIconSet materialIconSet) {
        return getIconSetPath(materialIconSet, BLOCK_TEXTURE_CACHE, "textures/blocks/material_sets/%s/%s%s", "blocks/material_sets/%s/%s", ".png");
    }

    public ResourceLocation getItemModelPath(MaterialIconSet materialIconSet) {
        return getIconSetPath(materialIconSet, ITEM_MODEL_CACHE, "models/item/material_sets/%s/%s%s", "material_sets/%s/%s", ".json");
    }

    public ResourceLocation getItemOverlayPath(MaterialIconSet materialIconSet) {
        String iconSet = materialIconSet.getName();
        String iconType = getFormattedName();
        return new ResourceLocation(GTValues.MODID, "material_sets/" + iconSet + "/" + iconType + "_overlay");
    }

}
