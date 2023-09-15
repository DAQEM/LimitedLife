package com.daqem.limitedlife.event;

import com.daqem.limitedlife.player.LimitedLifePlayer;
import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.EntityEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;

public class EventPlayerHurt {

    public static void registerEvent() {
        EntityEvent.LIVING_HURT.register(EventPlayerHurt::onPlayerHurt);
    }

    private static EventResult onPlayerHurt(LivingEntity livingEntity, DamageSource damageSource, float damage) {
        if (damageSource.getEntity() instanceof LimitedLifePlayer source && livingEntity instanceof LimitedLifePlayer target) {
            if (!source.canAttack(target)) {
                return EventResult.interruptFalse();
            }
        }
        return EventResult.pass();
    }
}
