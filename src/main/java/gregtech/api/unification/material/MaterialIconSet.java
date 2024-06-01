package gregtech.api.unification.material;

import crafttweaker.annotations.ZenRegister;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenGetter;
import stanhebben.zenscript.annotations.ZenMethod;

import java.util.HashMap;
import java.util.Map;

@ZenClass("mods.gregtech.material.MaterialIconSet")
@ZenRegister
public class MaterialIconSet {

    private static int idCounter = 0;

    public static final Map<String, MaterialIconSet> ICON_SETS = new HashMap<>();
    public static final MaterialIconSet DULL = new MaterialIconSet("dull", null, true);
    public static final MaterialIconSet NONE = new MaterialIconSet("none", DULL);
    public static final MaterialIconSet METALLIC = new MaterialIconSet("metallic");
    public static final MaterialIconSet MAGNETIC = new MaterialIconSet("magnetic", METALLIC);
    public static final MaterialIconSet SHINY = new MaterialIconSet("shiny", METALLIC);
    public static final MaterialIconSet DIAMOND = new MaterialIconSet("diamond", SHINY);
    public static final MaterialIconSet EMERALD = new MaterialIconSet("emerald", DIAMOND);
    public static final MaterialIconSet GEM_HORIZONTAL = new MaterialIconSet("gem_horizontal", EMERALD);
    public static final MaterialIconSet GEM_VERTICAL = new MaterialIconSet("gem_vertical", EMERALD);
    public static final MaterialIconSet RUBY = new MaterialIconSet("ruby", EMERALD);
    public static final MaterialIconSet OPAL = new MaterialIconSet("opal", RUBY);
    public static final MaterialIconSet GLASS = new MaterialIconSet("glass", RUBY);
    public static final MaterialIconSet NETHERSTAR = new MaterialIconSet("netherstar", GLASS);
    public static final MaterialIconSet FINE = new MaterialIconSet("fine");
    public static final MaterialIconSet ROUGH = new MaterialIconSet("rough", FINE);
    public static final MaterialIconSet SAND = new MaterialIconSet("sand", FINE);
    public static final MaterialIconSet WOOD = new MaterialIconSet("wood", FINE);
    public static final MaterialIconSet FLINT = new MaterialIconSet("flint", ROUGH);
    public static final MaterialIconSet LIGNITE = new MaterialIconSet("lignite", ROUGH);
    public static final MaterialIconSet QUARTZ = new MaterialIconSet("quartz", ROUGH);
    public static final MaterialIconSet CERTUS = new MaterialIconSet("certus", QUARTZ);
    public static final MaterialIconSet LAPIS = new MaterialIconSet("lapis", QUARTZ);
    public static final MaterialIconSet FLUID = new MaterialIconSet("fluid");
    public static final MaterialIconSet GAS = new MaterialIconSet("gas");
    public static final MaterialIconSet PAPER = new MaterialIconSet("paper");
    public static final MaterialIconSet LEAF = new MaterialIconSet("leaf");
    public static final MaterialIconSet SHARDS = new MaterialIconSet("shards");

    public final String name;
    public final boolean isRootIconSet;
    public final MaterialIconSet parent;
    protected final int id;

    public MaterialIconSet(String name, MaterialIconSet parent, boolean isRootIconSet) {
        this.name = name;
        this.parent = parent;
        this.isRootIconSet = isRootIconSet;
        this.id = idCounter++;
        ICON_SETS.put(name.toLowerCase(), this);
    }

    public MaterialIconSet(String name, MaterialIconSet parent) {
        this(name, parent, false);
    }

    public MaterialIconSet(String name) {
        this(name, DULL);
    }

    @ZenGetter("name")
    public String getName() {
        return this.name;
    }

    @ZenMethod("isRootIconSet")
    public boolean isRoot() {
        return isRootIconSet;
    }

    @ZenGetter("parent")
    public MaterialIconSet getParent() {
        return parent;
    }

    public int ordinal() {
        return this.id;
    }

    @ZenMethod("get")
    public static MaterialIconSet getByName(String name) {
        return ICON_SETS.get(name.toLowerCase());
    }

    @Override
    @ZenMethod
    public String toString() {
        return name;
    }
}
