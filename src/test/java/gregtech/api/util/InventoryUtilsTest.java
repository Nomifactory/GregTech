package gregtech.api.util;

import net.minecraft.init.*;
import net.minecraft.item.*;
import net.minecraft.nbt.*;
import net.minecraftforge.items.*;
import org.junit.jupiter.api.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class InventoryUtilsTest
{
    /**
     * Required. Without this all item-related operations will fail because registries haven't been initialized.
     */
    @BeforeAll
    public static void bootStrap()
    {
        Bootstrap.register();
    }

    @Test
    public void simulateItemStackMerge_succeeds_for_inserting_single_stack_into_empty_one_slot_inventory()
    {
        IItemHandler handler  = new ItemStackHandler(1);
        ItemStack    feathers = new ItemStack(Items.FEATHER, 64);

        boolean result =
            InventoryUtils.simulateItemStackMerge(
                Collections.singletonList(feathers),
                handler
            );

        assertTrue(
            result,
            "Merging one full stack into a single empty slot failed."
        );
    }

    @Test
    public void simulateItemStackMerge_succeeds_for_inserting_two_half_stacks_into_empty_one_slot_inventory()
    {
        IItemHandler handler  = new ItemStackHandler(1);
        ItemStack    feathers = new ItemStack(Items.FEATHER, 32);

        boolean result =
            InventoryUtils.simulateItemStackMerge(
                Arrays.asList(feathers, feathers),
                handler
            );

        assertTrue(
            result,
            "Merging two half-stacks into an empty inventory with one slot failed."
        );
    }

    @Test
    public void simulateItemStackMerge_succeeds_for_inserting_one_half_stack_into_inventory_with_one_half_stack()
    {
        IItemHandler handler  = new ItemStackHandler(1);
        ItemStack    feathers = new ItemStack(Items.FEATHER, 32);

        handler.insertItem(0, feathers, false);

        boolean result =
            InventoryUtils.simulateItemStackMerge(
                Collections.singletonList(feathers),
                handler
            );

        assertTrue(
            result,
            "Merging half a stack into an inventory with one slot containing half a stack of the same item failed."
        );
    }

    @Test
    public void simulateItemStackMerge_succeeds_for_inserting_one_half_stack_into_inventory_with_two_three_quarter_stacks()
    {
        IItemHandler handler     = new ItemStackHandler(2);
        ItemStack    feathers_32 = new ItemStack(Items.FEATHER, 32);
        ItemStack    feathers_48 = new ItemStack(Items.FEATHER, 48);

        handler.insertItem(0, feathers_48, false);
        handler.insertItem(1, feathers_48, false);

        boolean result =
            InventoryUtils.simulateItemStackMerge(
                Collections.singletonList(feathers_32),
                handler
            );

        assertTrue(
            result,
            "Merging half a stack into an inventory with two three-quarter stacks of the same item failed."
        );
    }

    @Test
    public void simulateItemStackMerge_succeeds_for_inserting_one_half_stack_into_inventory_with_one_three_quarter_stack_and_one_empty_slot()
    {
        IItemHandler handler     = new ItemStackHandler(2);
        ItemStack    feathers_32 = new ItemStack(Items.FEATHER, 32);
        ItemStack    feathers_48 = new ItemStack(Items.FEATHER, 48);

        handler.insertItem(0, feathers_48, false);
        handler.insertItem(1, feathers_48, false);

        boolean result =
            InventoryUtils.simulateItemStackMerge(
                Collections.singletonList(feathers_32),
                handler
            );

        assertTrue(
            result,
            "Merging half a stack into an inventory with one three-quarter stack of the same item and one empty slot failed."
        );
    }

    @Test
    public void simulateItemStackMerge_fails_to_insert_items_into_a_full_inventory_with_no_common_items()
    {
        IItemHandler handler  = new ItemStackHandler(1);
        ItemStack    feathers = new ItemStack(Items.FEATHER, 32);
        ItemStack    arrows   = new ItemStack(Items.ARROW, 1);

        handler.insertItem(0, feathers, false);

        boolean result =
            InventoryUtils.simulateItemStackMerge(
                Collections.singletonList(arrows),
                handler
            );

        assertFalse(
            result,
            "Unexpectedly succeeded at merging an arrow into an inventory full of feathers."
        );
    }

    @Test
    public void simulateItemStackMerge_fails_to_insert_items_into_a_full_inventory_with_common_items()
    {
        IItemHandler handler       = new ItemStackHandler(1);
        ItemStack    inv_feathers  = new ItemStack(Items.FEATHER, 64);
        ItemStack    more_feathers = new ItemStack(Items.FEATHER, 1);

        handler.insertItem(0, inv_feathers, false);

        boolean result =
            InventoryUtils.simulateItemStackMerge(
                Collections.singletonList(more_feathers),
                handler
            );

        assertFalse(
            result,
            "Unexpectedly succeeded at merging feathers into an inventory full of feathers."
        );
    }

    @Test
    public void simulateItemStackMerge_respects_different_NBT_tags_as_different_items()
    {
        IItemHandler handler          = new ItemStackHandler(1);
        ItemStack    feathers         = new ItemStack(Items.FEATHER, 1);
        ItemStack    special_feathers = new ItemStack(Items.FEATHER, 1);
        special_feathers.setTagInfo("Foo", new NBTTagString("Test"));

        handler.insertItem(0, feathers, false);

        boolean result =
            InventoryUtils.simulateItemStackMerge(
                Collections.singletonList(special_feathers),
                handler
            );

        assertFalse(
            result,
            "Unexpectedly succeeded at merging feathers with NBT tags into a stack of plain feathers."
        );
    }

    @Test
    public void simulateItemStackMerge_respects_different_damage_values_as_different_items()
    {
        IItemHandler handler          = new ItemStackHandler(1);
        ItemStack    feathers         = new ItemStack(Items.FEATHER, 1);
        ItemStack    special_feathers = new ItemStack(Items.FEATHER, 1);
        special_feathers.setItemDamage(1);

        handler.insertItem(0, feathers, false);

        boolean result =
            InventoryUtils.simulateItemStackMerge(
                Collections.singletonList(special_feathers),
                handler
            );

        assertFalse(
            result,
            "Unexpectedly succeeded at merging damaged feathers into a stack of plain feathers."
        );
    }

    @Test
    public void simulateItemStackMerge_respects_unstackable_but_otherwise_identical_items()
    {
        IItemHandler handler        = new ItemStackHandler(1);
        ItemStack    pickaxe        = new ItemStack(Items.IRON_PICKAXE, 1);
        ItemStack    anotherPickaxe = new ItemStack(Items.IRON_PICKAXE, 1);

        assertFalse(pickaxe.isStackable());

        handler.insertItem(0, pickaxe, false);

        boolean result =
            InventoryUtils.simulateItemStackMerge(
                Collections.singletonList(anotherPickaxe),
                handler
            );

        assertFalse(
            result,
            "Unexpectedly succeeded at merging a pickaxe into another one."
        );
    }

    @Test
    public void normalizeItemStack_returns_empty_list_for_single_empty_stack()
    {
        List<ItemStack> result = InventoryUtils.normalizeItemStack(ItemStack.EMPTY);
        assertTrue(
            result.isEmpty(),
            "Unexpectedly got results when normalizing an empty ItemStack"
        );
    }

    @Test
    public void normalizeItemStack_returns_single_element_list_for_a_single_already_normal_stack()
    {
        ItemStack       stack  = new ItemStack(Items.ENDER_PEARL, 16);
        List<ItemStack> result = InventoryUtils.normalizeItemStack(stack);

        assertFalse(
            result.isEmpty(),
            "Unexpectedly got no results when normalizing an already normal ItemStack"
        );
        assertEquals(
            1, result.size(),
            "Unexpectedly got wrong number of resulting stacks when normalizing an already normal ItemStack"
        );
        assertTrue(
            ItemStack.areItemStacksEqual(stack, result.get(0)),
            "ItemStack was modified when it didn't need to be"
        );
    }

    @Test
    public void normalizeItemStack_returns_normalized_stacks_for_an_abnormal_stack()
    {
        ItemStack       stack  = new ItemStack(Items.ENDER_PEARL, 45);
        List<ItemStack> result = InventoryUtils.normalizeItemStack(stack);

        assertFalse(
            result.isEmpty(),
            "Unexpectedly got no results when normalizing an abnormal stack"
        );
        assertEquals(
            3, result.size(),
            "Unexpectedly got wrong number of resulting stacks when normalizing an abnormal ItemStack"
        );

        ItemStack expectedFull    = new ItemStack(Items.ENDER_PEARL, 16);
        ItemStack expectedPartial = new ItemStack(Items.ENDER_PEARL, 13);
        assertTrue(ItemStack.areItemStacksEqual(expectedFull, result.get(0)),
                   "First item stack does not match expected full stack");
        assertTrue(ItemStack.areItemStacksEqual(expectedFull, result.get(1)),
                   "Second item stack does not match expected full stack");
        assertTrue(ItemStack.areItemStacksEqual(expectedPartial, result.get(2)),
                   "Third item stack does not match expected partial stack");
    }

    @Test
    public void apportionStack_throws_AssertionError_when_supplied_stack_is_empty()
    {
        IllegalArgumentException e =
            assertThrows(IllegalArgumentException.class,
                         () -> InventoryUtils.apportionStack(ItemStack.EMPTY, 64));
        assertEquals("Cannot apportion an empty stack.", e.getMessage());
    }

    @Test
    public void apportionStack_throws_AssertionError_when_maxCount_is_zero()
    {
        IllegalArgumentException e =
            assertThrows(IllegalArgumentException.class,
                         () -> InventoryUtils.apportionStack(new ItemStack(Items.ARROW, 1), 0));
        assertEquals("Count must be non-zero and positive.", e.getMessage());
    }

    @Test
    public void apportionStack_throws_AssertionError_when_maxCount_is_negative()
    {
        IllegalArgumentException e =
            assertThrows(IllegalArgumentException.class,
                         () -> InventoryUtils.apportionStack(new ItemStack(Items.ARROW, 1), -1));
        assertEquals("Count must be non-zero and positive.", e.getMessage());
    }

    @Test
    public void apportionStack_splits_evenly_divisible_stack()
    {
        ItemStack oversized = new ItemStack(Items.ENDER_PEARL, 64);
        ItemStack normal    = new ItemStack(Items.ENDER_PEARL, 16);

        List<ItemStack> result = InventoryUtils.apportionStack(oversized, 16);

        assertFalse(result.isEmpty());
        assertEquals(4, result.size());
        for(ItemStack stack : result)
        {
            assertTrue(ItemStack.areItemStacksEqual(stack, normal));
        }
    }

    @Test
    public void apportionStack_splits_unevenly_divisible_stack_with_remainder_at_end()
    {
        ItemStack oversized = new ItemStack(Items.ENDER_PEARL, 45);
        ItemStack normal    = new ItemStack(Items.ENDER_PEARL, 16);
        ItemStack remainder = new ItemStack(Items.ENDER_PEARL, 13);

        List<ItemStack> result = InventoryUtils.apportionStack(oversized, 16);

        assertFalse(result.isEmpty());
        assertEquals(3, result.size());

        assertTrue(ItemStack.areItemStacksEqual(result.get(0), normal));
        assertTrue(ItemStack.areItemStacksEqual(result.get(1), normal));
        assertTrue(ItemStack.areItemStacksEqual(result.get(2), remainder));
    }

    @Test
    public void deepCopy_retains_empty_stacks_when_requested()
    {
        IItemHandler inventory = new ItemStackHandler(2);

        inventory.insertItem(1, new ItemStack(Items.FEATHER, 1), false);

        assertTrue(inventory.getStackInSlot(0).isEmpty());
        assertFalse(inventory.getStackInSlot(1).isEmpty());

        List<ItemStack> result = InventoryUtils.deepCopy(inventory, true);

        assertFalse(result.isEmpty());
        assertEquals(2, result.size());
        assertTrue(result.get(0).isEmpty());
    }

    @Test
    public void deepCopy_discards_empty_stacks_when_requested()
    {
        IItemHandler inventory = new ItemStackHandler(2);
        ItemStack    feather   = new ItemStack(Items.FEATHER, 1);
        inventory.insertItem(1, feather, false);

        assertTrue(inventory.getStackInSlot(0).isEmpty());
        assertFalse(inventory.getStackInSlot(1).isEmpty());

        List<ItemStack> result = InventoryUtils.deepCopy(inventory, false);

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertTrue(ItemStack.areItemStacksEqual(feather, result.get(0)));
    }
}