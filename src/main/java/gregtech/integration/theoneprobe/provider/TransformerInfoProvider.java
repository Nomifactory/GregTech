package gregtech.integration.theoneprobe.provider;

import gregtech.api.capability.IEnergyContainer;
import gregtech.api.metatileentity.MetaTileEntityHolder;
import gregtech.common.metatileentities.electric.MetaTileEntityTransformer;
import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import org.jetbrains.annotations.NotNull;

import static gregtech.api.GTValues.*;
import static mcjty.theoneprobe.api.ElementAlignment.*;
import static mcjty.theoneprobe.api.TextStyleClass.*;
import static net.minecraft.util.text.TextFormatting.*;

public class TransformerInfoProvider extends ElectricContainerInfoProvider {

    @Override
    public String getID() {
        return MODID + ":transformer_info_provider";
    }

    @Override
    protected void addProbeInfo(@NotNull IEnergyContainer capability, @NotNull IProbeInfo probeInfo, EntityPlayer player,
                                @NotNull TileEntity tileEntity, @NotNull IProbeHitData data) {
        if (tileEntity instanceof MetaTileEntityHolder mte) {
            if (mte.getMetaTileEntity() instanceof MetaTileEntityTransformer mteTransformer) {
                int tier = mteTransformer.getTier();
                long inputAmperage = capability.getInputAmperage();
                long outputAmperage = capability.getOutputAmperage();
                IProbeInfo horizontalPane = probeInfo.vertical(probeInfo.defaultLayoutStyle().alignment(ALIGN_CENTER));
                String mode;

                String inputVoltageN, outputVoltageN;
                // Step Up/Step Down line
                if (mteTransformer.isInverted()) {
                    mode = "up";
                    inputVoltageN = VNF[tier - 1];
                    outputVoltageN = VNF[tier];
                } else {
                    mode = "down";
                    inputVoltageN = VNF[tier];
                    outputVoltageN = VNF[tier - 1];
                }

                horizontalPane.text(String.format("%s{*gregtech.top.transform_%s*} %s%s (%dA) -> %s%s (%dA)",
                                                  INFO, mode, inputVoltageN,
                                                  RESET, inputAmperage, outputVoltageN,
                                                  RESET, outputAmperage));

                // Input/Output side line
                horizontalPane = probeInfo.vertical(probeInfo.defaultLayoutStyle().alignment(ALIGN_CENTER));
                if (capability.inputsEnergy(data.getSideHit()))
                    horizontalPane.text(String.format("%s{*gregtech.top.transform_input*} %s (%dA)",
                                                      INFO, inputVoltageN, inputAmperage));
                else if(capability.outputsEnergy(data.getSideHit()))
                    horizontalPane.text(String.format("%s{*gregtech.top.transform_output*} %s (%dA)",
                                                      INFO, outputVoltageN, outputAmperage));
            }
        }
    }
}
