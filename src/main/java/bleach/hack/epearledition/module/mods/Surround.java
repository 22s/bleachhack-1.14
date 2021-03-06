package bleach.hack.epearledition.module.mods;

import bleach.hack.epearledition.event.events.EventTick;
import bleach.hack.epearledition.module.Category;
import bleach.hack.epearledition.module.Module;
import bleach.hack.epearledition.module.ModuleManager;
import bleach.hack.epearledition.setting.base.SettingMode;
import bleach.hack.epearledition.setting.base.SettingSlider;
import bleach.hack.epearledition.setting.base.SettingToggle;
import bleach.hack.epearledition.setting.other.SettingRotate;
import bleach.hack.epearledition.utils.BleachLogger;
import bleach.hack.epearledition.utils.WorldUtils;
import com.google.common.eventbus.Subscribe;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public class Surround extends Module {

    BlockPos coords;
    int currentSlot;

    public Surround() {
        super("Surround", KEY_UNBOUND, Category.COMBAT, "Surrounds yourself with obsidian",
                new SettingMode("Mode", "1x1", "2x2", "trap").withDesc("Mode, 1x1 places 4 blocks around you"),
                new SettingToggle("Autocenter", false).withDesc("Autocenters you to the nearest block"),
                new SettingToggle("Keep on", true).withDesc("Keeps the module on after placing the obsidian"),
                new SettingToggle("Jump disable", true).withDesc("Disables the module if you jump"),
                new SettingSlider("BPT", 1, 8, 2, 0).withDesc("Blocks per tick, how many blocks to place per tick"),
                new SettingRotate(false).withDesc("Rotates when placing"));
    }

    public void onEnable() {
        super.onEnable();

        if (mc.player == null) {
            super.setToggled(false);
            return;
        }

        coords = mc.player.getBlockPos();

        int obby = -1;
        for (int i = 0; i < 9; i++) {
            if (mc.player.inventory.getStack(i).getItem() == Items.NETHERITE_BLOCK || mc.player.inventory.getStack(i).getItem() == Items.OBSIDIAN) {
                obby = i;
                break;
            }
        }

        if (obby == -1) {
            BleachLogger.errorMessage("No netherite/obsidian in hotbar!");
            setToggled(false);
            return;
        }

        if (getSetting(1).asToggle().state) {
            Vec3d centerPos = Vec3d.of(coords).add(0.5, 0.5, 0.5);
            mc.player.updatePosition(centerPos.x, centerPos.y, centerPos.z);
            mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionOnly(centerPos.x, centerPos.y, centerPos.z, mc.player.isOnGround()));
        }

        placeTick(obby);
    }

    @Subscribe
    public void onTick(EventTick event) {
        if (getSetting(3).asToggle().state && mc.options.keyJump.isPressed() && !ModuleManager.getModule(Freecam.class).isToggled()) {
            setToggled(false);
            return;
        }

        int obby = -1;
        for (int i = 0; i < 9; i++) {
            if (mc.player.inventory.getStack(i).getItem() == Items.NETHERITE_BLOCK || mc.player.inventory.getStack(i).getItem() == Items.OBSIDIAN) {
                obby = i;
                break;
            }
        }

        if (obby == -1) {
            BleachLogger.errorMessage("Ran out of netherite/obsidian!");
            setToggled(false);
            return;
        }

        placeTick(obby);
    }

    private void placeTick(int obsidian) {
        int cap = 0;

        if (getSetting(0).asMode().mode == 0) {
            for (BlockPos b : new BlockPos[]{
                    coords.down().north(), coords.down().east(),
                    coords.down().south(), coords.down().west(),
                    coords.north(), coords.east(),
                    coords.south(), coords.west()}) {

                if (cap >= (int) getSetting(4).asSlider().getValue()) {
                    return;
                }

                if (getSetting(5).asRotate().state) {
                    WorldUtils.facePosAuto(b.getX() + 0.5, b.getY() + 0.5, b.getZ() + 0.5, getSetting(5).asRotate());
                }

                currentSlot = mc.player.inventory.selectedSlot;
                if (WorldUtils.placeBlock(b, obsidian, false, false)) {
                    cap++;

                }
                mc.player.inventory.selectedSlot = currentSlot;
            }
        } else if (getSetting(0).asMode().mode == 1) {
            for (BlockPos b : new BlockPos[]{
                    coords.down().north(), coords.down().east(),
                    coords.down().south(), coords.down().west(),
                    coords.north(), coords.east(),
                    coords.south(), coords.west(),
                    coords.up().north(), coords.up().east(),
                    coords.up().south(), coords.up().west()}) {

                if (cap >= (int) getSetting(4).asSlider().getValue()) {
                    return;
                }

                if (getSetting(5).asRotate().state) {
                    WorldUtils.facePosAuto(b.getX() + 0.5, b.getY() + 0.5, b.getZ() + 0.5, getSetting(5).asRotate());
                }
                if (WorldUtils.isBlockEmpty(b)) {
                    currentSlot = mc.player.inventory.selectedSlot;
                    WorldUtils.placeBlock(b, obsidian, false, false);
                    mc.player.inventory.selectedSlot = currentSlot;
                    cap++;
                }
            }
        } else if (getSetting(0).asMode().mode == 2) {
            for (BlockPos b : new BlockPos[]{
                    coords.down().north(), coords.down().east(),
                    coords.down().south(), coords.down().west(),
                    coords.north(), coords.east(),
                    coords.south(), coords.west(),
                    coords.up().north(), coords.up().east(),
                    coords.up().south(), coords.up().west()}) {

                if (cap >= (int) getSetting(4).asSlider().getValue()) {
                    return;
                }

                if (getSetting(5).asRotate().state) {
                    WorldUtils.facePosAuto(b.getX() + 0.5, b.getY() + 0.5, b.getZ() + 0.5, getSetting(5).asRotate());
                }
                if (WorldUtils.isBlockEmpty(b)) {
                    currentSlot = mc.player.inventory.selectedSlot;
                    WorldUtils.placeBlock(b, obsidian, false, false);
                    mc.player.inventory.selectedSlot = currentSlot;
                    //mc.interactionManager.attackBlock(b, Direction.UP);
                    cap++;
                }
            }
            if(mc.world.getBlockState(coords.up(2)).getMaterial().isReplaceable()){
                currentSlot = mc.player.inventory.selectedSlot;
                mc.player.inventory.selectedSlot = obsidian;
                mc.interactionManager.interactBlock(mc.player, mc.world, Hand.MAIN_HAND, new BlockHitResult(Vec3d.of(coords.up(1)), Direction.UP, coords.up(2), false));
                mc.player.swingHand(Hand.MAIN_HAND);
                mc.player.inventory.selectedSlot = currentSlot;
            }
        }

        if (!getSetting(2).asToggle().state) {
            setToggled(false);
        }
    }

}