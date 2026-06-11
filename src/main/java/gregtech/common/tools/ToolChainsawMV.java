package gregtech.common.tools;

import gregtech.api.capability.GregtechCapabilities;
import gregtech.api.capability.IElectricItem;
import gregtech.common.items.MetaItems;
import net.minecraft.item.ItemStack;

public class ToolChainsawMV extends ToolChainsawLV {

    @Override
    public float getBaseDamage(ItemStack stack) {
        return 5.0F;
    }

    @Override
    public float getDigSpeedMultiplier(ItemStack stack) {
        return 3.0F;
    }

    @Override
    public float getMaxDurabilityMultiplier(ItemStack stack) {
        return 10.0F;
    }

    @Override
    public ItemStack getBrokenStack(ItemStack stack) {
        IElectricItem electricItem = stack.getCapability(GregtechCapabilities.CAPABILITY_ELECTRIC_ITEM, null);
        return MetaItems.POWER_UNIT_MV.getChargedStackWithOverride(electricItem);
    }
}
