/*
    Code adapted from Nomi-Labs https://github.com/Nomi-CEu/Nomi-Labs @ 6e14c06
 */
package gregtech.integration.nomilabs.element;

import gregtech.api.util.GTLog;
import gregtech.integration.nomilabs.util.LabsTranslate;
import gregtech.integration.theoneprobe.TheOneProbeCompatibility;
import io.netty.buffer.ByteBuf;
import mcjty.theoneprobe.api.IElement;
import mcjty.theoneprobe.api.TextStyleClass;
import mcjty.theoneprobe.apiimpl.client.ElementTextRender;
import mcjty.theoneprobe.network.NetworkTools;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

public class LabsFluidNameElement implements IElement {

    private final String fluidName;
    private final int amount;
    private final boolean showLang;
    private final String translatedName;

    public LabsFluidNameElement(FluidStack fluid, boolean showLang) {
        this.fluidName = fluid.getFluid().getName();
        this.amount = fluid.amount;
        this.showLang = showLang;

        // Temp Translated Name, for usage if needed
        this.translatedName = fluid.getUnlocalizedName();
    }

    public LabsFluidNameElement(ByteBuf byteBuf) {
        this.fluidName = NetworkTools.readStringUTF8(byteBuf);
        this.amount = byteBuf.readInt();
        this.showLang = byteBuf.readBoolean();
        this.translatedName = translateFluid(fluidName, amount, "LabsFluidNameElement");
    }

    @Override
    public int getWidth() {
        return ElementTextRender.getWidth(getTranslated());
    }

    @Override
    public int getHeight() {
        return 10;
    }

    @Override
    public void toBytes(ByteBuf byteBuf) {
        NetworkTools.writeStringUTF8(byteBuf, fluidName);
        byteBuf.writeInt(amount);
        byteBuf.writeBoolean(showLang);
    }

    @Override
    public void render(int x, int y) {
        ElementTextRender.render(TextStyleClass.NAME + getTranslated(), x, y);
    }

    @Override
    public int getID() {
        return TheOneProbeCompatibility.FLUID_NAME_ELEMENT;
    }

    public String getTranslated() {
        if (showLang)
            return LabsTranslate.translate("gregtech.top_override.fluid", translatedName);
        else
            return translatedName;
    }

    public static String translateFluid(@Nullable String fluidName, int amount, String packet) {
        if (fluidName == null || fluidName.isEmpty()) return ""; // Empty Tank

        var fluid = FluidRegistry.getFluid(fluidName);

        // At least try and translate it if fluid is null
        if (fluid == null) {
            GTLog.logger.error("Received Fluid Info Packet {} with Unknown Fluid {}!", packet, fluidName);
            return LabsTranslate.translate(fluidName);
        }

        return fluid.getLocalizedName(new FluidStack(fluid, amount));
    }
}
