/*
 * This file is part of the BleachHack distribution (https://github.com/BleachDrinker420/bleachhack-1.14/).
 * Copyright (c) 2019 Bleach.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package bleach.hack.epearledition.module.mods;

import bleach.hack.epearledition.event.events.EventTick;
import bleach.hack.epearledition.module.Category;
import bleach.hack.epearledition.module.Module;
import bleach.hack.epearledition.setting.base.SettingMode;
import bleach.hack.epearledition.setting.base.SettingSlider;
import bleach.hack.epearledition.utils.Timer;
import com.google.common.eventbus.Subscribe;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.SlotActionType;

import java.util.ArrayList;

public class HotbarCache extends Module {

    public HotbarCache() {
        super("HotbarCache", KEY_UNBOUND, Category.MISC, "Autototem for items",
                new SettingMode("Item", "Pickaxe", "Crystal", "Gapple", "Snowball"),
                new SettingMode("Mode", "Switch", "Pull", "Refill"),
                new SettingSlider("Delay", 0.0, 1.0, 0.003, 3));
    }

    private final ArrayList<Item> Hotbar = new ArrayList<Item>();
    private final bleach.hack.epearledition.utils.Timer timer = new Timer();

    public void toggleNoSave() {

    }

    public void onEnable() {
        MinecraftClient mc = MinecraftClient.getInstance();
        PlayerEntity player = mc.player;
        if (mc.world == null) return;

        Hotbar.clear();

        for (int l_I = 0; l_I < 9; ++l_I)
        {
            ItemStack l_Stack = player.inventory.getStack(l_I);

            if (!l_Stack.isEmpty() && !Hotbar.contains(l_Stack.getItem()))
                Hotbar.add(l_Stack.getItem());
            else
                Hotbar.add(Items.AIR);
        }
    }
    public void onDisable() {
        Hotbar.clear();
    }

    @Subscribe
    public void onTick(EventTick event) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.currentScreen != null)
            return;

        if (!timer.passed(getSettings().get(2).asSlider().getValue() * 1000))
            return;

        if (getSettings().get(1).asMode().mode == 1) {
            for (int l_I = 0; l_I < 9; ++l_I) {
                if (SwitchSlotIfNeed(l_I)) {
                    timer.reset();
                    return;
                }
            }
        }
        if (getSettings().get(1).asMode().mode == 2) {
            for (int l_I = 0; l_I < 9; ++l_I) {
                if (RefillSlotIfNeed(l_I)) {
                    timer.reset();
                    return;
                }
            }
        }
    }
    private boolean SwitchSlotIfNeed(int targetSlot)
    {
        MinecraftClient mc = MinecraftClient.getInstance();
        PlayerEntity player = mc.player;
        Item itemFromCache = Hotbar.get(targetSlot);

        if (itemFromCache == Items.AIR)
            return false;

        if (!player.inventory.getStack(targetSlot).isEmpty() && player.inventory.getStack(targetSlot).getItem() == itemFromCache)
            return false;

        int slotFromCache = GetItemSlot(itemFromCache);

        if (slotFromCache != -1 && slotFromCache != 45)
        {
            mc.interactionManager.clickSlot(player.currentScreenHandler.syncId, slotFromCache, 0,
                    SlotActionType.PICKUP, player);
            mc.interactionManager.clickSlot(player.currentScreenHandler.syncId, targetSlot+36, 0, SlotActionType.PICKUP,
                    player);
            mc.interactionManager.clickSlot(player.currentScreenHandler.syncId, slotFromCache, 0,
                    SlotActionType.PICKUP, player);
            return true;
        }

        return false;
    }

    public int GetItemSlot(Item input)
    {
        MinecraftClient mc = MinecraftClient.getInstance();
        PlayerEntity player = mc.player;
        if (player == null)
            return 0;

        for (int i = 0; i < player.inventory.size(); ++i)
        {
            if (i == 0 || i == 5 || i == 6 || i == 7 || i == 8)
                continue;

            ItemStack s = player.inventory.getStack(i);

            if (s.isEmpty())
                continue;

            if (s.getItem() == input)
            {
                return i;
            }
        }
        return -1;
    }

    private boolean RefillSlotIfNeed(int targetSlot)
    {
        ItemStack itemInTargetSlot = mc.player.inventory.getStack(targetSlot);

        if (itemInTargetSlot.isEmpty() || itemInTargetSlot.getItem() == Items.AIR)
            return false;

        if (!itemInTargetSlot.isStackable())
            return false;

        if (itemInTargetSlot.getCount() >= itemInTargetSlot.getMaxCount())
            return false;

        /// We're going to search the entire inventory for the same stack, WITH THE SAME NAME, and use quick move.
        for (int l_I = 9; l_I < 36; ++l_I)
        {
            final ItemStack l_Item = mc.player.inventory.getStack(l_I);

            if (l_Item.isEmpty())
                continue;

            if (CanItemBeMergedWith(itemInTargetSlot, l_Item))
            {
                mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, l_I, 0,
                        SlotActionType.QUICK_MOVE, mc.player);
                /// Check again for more next available tick
                return true;
            }
        }

        return false;
    }

    private boolean CanItemBeMergedWith(ItemStack p_Source, ItemStack p_Target)
    {
        return p_Source.getItem() == p_Target.getItem() && p_Source.getName().equals(p_Target.getName());
    }

}
