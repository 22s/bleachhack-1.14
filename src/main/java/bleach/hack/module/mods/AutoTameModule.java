package bleach.hack.module.mods;

import bleach.hack.event.events.EventReadPacket;
import bleach.hack.event.events.EventTick;
import bleach.hack.module.Category;
import bleach.hack.utils.BleachLogger;
import com.google.common.eventbus.Subscribe;
import bleach.hack.module.Module;
import net.minecraft.text.LiteralText;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.AbstractDonkeyEntity;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;

public class AutoTameModule extends Module
{
    public AutoTameModule() {
        super("AutoTame", KEY_UNBOUND, Category.EXPLOITS, "What do you think?");
    }

    private AbstractDonkeyEntity EntityToTame = null;

    public void onEnable() {
        super.onEnable();
        BleachLogger.errorMessage("Right click an animal you want to tame.");
    }

    @Subscribe
    public void onPacket(EventReadPacket event)
    {
        if (event.getPacket() instanceof PlayerInteractEntityC2SPacket)
        {
            if (EntityToTame != null)
                return;
            
            final PlayerInteractEntityC2SPacket l_Packet = (PlayerInteractEntityC2SPacket) event.getPacket();

            Entity l_Entity = l_Packet.getEntity(mc.world);

            if (l_Entity instanceof AbstractDonkeyEntity)
            {
                if (!((AbstractDonkeyEntity) l_Entity).isTame())
                {
                    EntityToTame = (AbstractDonkeyEntity) l_Entity;
                    BleachLogger.errorMessage("Will try to tame " + l_Entity.getEntityName());
                }
            }
        }
    }

    @Subscribe
    public void onTick(EventTick event)
    {
        if (EntityToTame == null)
            return;

        if (EntityToTame.isTame())
        {
            BleachLogger.errorMessage("Successfully tamed animal, disabling.");
            this.setToggled(false);
            return;
        }

        if (mc.player.isRiding())
            return;

        if (mc.player.getAttackDistanceScalingFactor(EntityToTame) > 5.0f)
            return;

        mc.player.networkHandler.sendPacket(new PlayerInteractEntityC2SPacket(EntityToTame, false));
    }
}
