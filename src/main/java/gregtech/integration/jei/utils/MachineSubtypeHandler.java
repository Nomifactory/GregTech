package gregtech.integration.jei.utils;

import gregtech.api.block.machines.MachineItemBlock;
import gregtech.api.metatileentity.MetaTileEntity;
import mezz.jei.api.ISubtypeRegistry.ISubtypeInterpreter;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class MachineSubtypeHandler implements ISubtypeInterpreter {
    @Override
    @NotNull
    public String apply(@NotNull ItemStack itemStack) {
        String additionalData = "";
        MetaTileEntity metaTileEntity = MachineItemBlock.getMetaTileEntity(itemStack);
        if (metaTileEntity != null) {
            additionalData = metaTileEntity.getItemSubTypeId(itemStack);
        }
        return String.format("%d;%s", itemStack.getMetadata(), additionalData);
    }
}
