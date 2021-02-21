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
package bleach.hack.command.commands;

import bleach.hack.command.Command;
import bleach.hack.utils.BleachLogger;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.packet.c2s.play.BookUpdateC2SPacket;
import net.minecraft.text.LiteralText;

public class CmdDupe extends Command {

    @Override
    public String getAlias() {
        return "dupe";
    }

    @Override
    public String getDescription() {
        return "Does the book and quill dupe.";
    }

    @Override
    public String getSyntax() {
        return "dupe";
    }

    @Override
    public void onCommand(String command, String[] args) throws Exception {
        ItemStack itemStack = new ItemStack(Items.WRITABLE_BOOK, 1);
        if (mc.player.inventory.getMainHandStack().getItem() != Items.WRITABLE_BOOK) {
            BleachLogger.errorMessage("Hand is not holding book and quill!");
            return;
        }
        String str1;

        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < 21845; i++)
            stringBuilder.append('\u0800');
        str1 = stringBuilder.toString();
        System.out.println("CALLED");
        ListTag listTag = new ListTag();
        listTag.add(0, StringTag.of(str1));
        for (int i = 1; i < 38; i++)
            listTag.add(i, StringTag.of("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"));
        itemStack.putSubTag("pages", listTag);
        itemStack.putSubTag("title", StringTag.of("a"));
        mc.getNetworkHandler().sendPacket(new BookUpdateC2SPacket(itemStack, true, mc.player.inventory.selectedSlot));
    }
}
