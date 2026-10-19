package net.giome.testmod.effect;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;

public class BlessingOfTheSun extends MobEffect {

    public BlessingOfTheSun(MobEffectCategory category, int color) {
        super(category, color);
    }
//
    @Override
    public boolean applyEffectTick(
            ServerLevel level,
            LivingEntity entity,
            int amplifier) {

        long time = level.getOverworldClockTime() % 24000L;

        // Active uniquement pendant le jour
        if (time < 12000L) {
            int effectLevel = amplifier + 1;

            entity.addEffect(new MobEffectInstance(
                    MobEffects.SPEED, 30, effectLevel));

            entity.addEffect(new MobEffectInstance(
                    MobEffects.STRENGTH, 30, effectLevel));

            entity.addEffect(new MobEffectInstance(
                    MobEffects.RESISTANCE, 30, effectLevel));

            entity.addEffect(new MobEffectInstance(
                    MobEffects.HEALTH_BOOST, 30, effectLevel));
        }

        return super.applyEffectTick(level, entity, amplifier);
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(
            int tickCount, int amplification) {
        return tickCount % 10 == 0;
    }
}

