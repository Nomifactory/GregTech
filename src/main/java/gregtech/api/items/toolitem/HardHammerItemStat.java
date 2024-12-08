package gregtech.api.items.toolitem;

import gregtech.api.capability.GregtechCapabilities;
import gregtech.api.capability.tool.IHardHammerItem;
import gregtech.api.items.metaitem.stats.IItemCapabilityProvider;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

public class HardHammerItemStat implements IItemCapabilityProvider {
	@Override
	public ICapabilityProvider createProvider(ItemStack itemStack) {
		return new HardHammerItemStat.CapabilityProvider(itemStack);
	}

	private static class CapabilityProvider
		extends AbstractToolItemCapabilityProvider<IHardHammerItem>
		implements IHardHammerItem {

		public CapabilityProvider(ItemStack itemStack) {
			super(itemStack);
		}

		@Override
		protected Capability<IHardHammerItem> getCapability() {
			return GregtechCapabilities.CAPABILITY_HAMMER;
		}
	}
}
