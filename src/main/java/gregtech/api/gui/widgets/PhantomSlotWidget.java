package gregtech.api.gui.widgets;

import com.google.common.collect.Lists;
import gregtech.api.gui.igredient.IGhostIngredientTarget;
import gregtech.api.util.SlotUtil;
import mezz.jei.api.gui.IGhostIngredientHandler.Target;
import mezz.jei.bookmarks.BookmarkItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.items.IItemHandlerModifiable;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.awt.*;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class PhantomSlotWidget extends SlotWidget implements IGhostIngredientTarget {

    public PhantomSlotWidget(IItemHandlerModifiable itemHandler, int slotIndex, int xPosition, int yPosition) {
        super(itemHandler, slotIndex, xPosition, yPosition, false, true);
    }

    @Override
    public ItemStack slotClick(int dragType, ClickType clickTypeIn, EntityPlayer player) {
        ItemStack stackHeld = player.inventory.getItemStack();
        return SlotUtil.slotClickPhantom(slotReference, dragType, clickTypeIn, stackHeld);
    }

    @Override
    public boolean canMergeSlot(ItemStack stack) {
        return false;
    }

    @Override
    public List<Target<?>> getPhantomTargets(Object ingredient) {
        if (!(ingredient instanceof ItemStack || ingredient instanceof BookmarkItem))
            return Collections.emptyList();

        Rectangle rectangle = toRectangleBox();
        return Lists.newArrayList(new Target<>() {
            @Override
            @NotNull
            public Rectangle getArea() {
                return rectangle;
            }

            @Override
            public void accept(@NotNull Object ingredient) {
                if (ingredient instanceof BookmarkItem bookmark) {
                    if (bookmark.ingredient instanceof ItemStack stack) {
                        ItemStack temp = stack.copy();
                        temp.setCount(clampLong(bookmark.getDisplayAmount()));
                        ingredient = temp;
                    }
                }
                if (ingredient instanceof ItemStack stack) {
                    int mouseButton = Mouse.getEventButton();
                    boolean shiftDown = Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT);
                    writeClientAction(1, buffer -> {
                        buffer.writeItemStack(stack);
                        buffer.writeVarInt(mouseButton);
                        buffer.writeBoolean(shiftDown);
                    });
                }
            }
        });
    }

    @Override
    public void handleClientAction(int id, PacketBuffer buffer) {
        if (id == 1) {
            ItemStack stackHeld;
            try {
                stackHeld = buffer.readItemStack();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            int mouseButton = buffer.readVarInt();
            boolean shiftKeyDown = buffer.readBoolean();
            ClickType clickType = shiftKeyDown ? ClickType.QUICK_MOVE : ClickType.PICKUP;
            SlotUtil.slotClickPhantom(slotReference, mouseButton, clickType, stackHeld);
        }
    }
}
