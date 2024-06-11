package gregtech.api.gui.widgets;

import net.minecraftforge.items.IItemHandlerModifiable;

public class DischargerSlotWidget extends SlotWidget {

    public DischargerSlotWidget(IItemHandlerModifiable itemHandler, int slotIndex, int xPosition, int yPosition) {
        super(itemHandler, slotIndex, xPosition, yPosition, true, true);
    }
}
