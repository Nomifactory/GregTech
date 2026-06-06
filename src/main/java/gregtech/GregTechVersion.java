package gregtech;

import gregtech.api.util.Version;

public final class GregTechVersion {

    public static final Version VERSION = Version.parse(GTInternalTags.VERSION);
    public static final int MAJOR    = VERSION.getNumber(0);
    public static final int MINOR    = VERSION.getNumber(1);
    public static final int REVISION = VERSION.getNumber(2);
    public static final int BUILD    = VERSION.getNumber(3);

    private GregTechVersion() {
    }
}
