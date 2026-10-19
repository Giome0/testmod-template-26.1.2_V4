package net.giome.testmod.effect;

import java.util.List;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;

public class CurseOfDeath extends MobEffect {

    public CurseOfDeath(MobEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public boolean applyEffectTick(
            ServerLevel serverLevel,
            LivingEntity mob,
            int amplification
    ) {
        AABB boundingBox = mob.getBoundingBox()
                .inflate(amplification + 2);

        List<Entity> entities = serverLevel.getEntities(mob, boundingBox);

        for (Entity entity : entities) {
            if (!(entity instanceof LivingEntity victim)) {
                continue;
            }

            // Ne pas attaquer le porteur lui-même.
            if (victim == mob || !victim.isAlive()) {
                continue;
            }

            // Infliger les dégâts de Curse of Death.
            boolean hurt = victim.hurtServer(
                    serverLevel,
                    mob.damageSources().magic(),
                    100000
            );

            // Si les dégâts de cette malédiction tuent la victime,
            // transférer ses malédictions au porteur.
            if (hurt && victim.isDeadOrDying()) {
                transferCurses(victim, mob);
            }
        }

        return super.applyEffectTick(serverLevel, mob, amplification);
    }

    private static void transferCurses(
            LivingEntity victim,
            LivingEntity killer
    ) {
        MobEffectInstance[] curses = {
                victim.getEffect(ModEffect.CURSE_OF_DEATH),
                victim.getEffect(ModEffect.CURSE_OF_HATRED),
                victim.getEffect(ModEffect.CURSE_OF_LONELINESS),
                victim.getEffect(ModEffect.CURSE_OF_DISGUST),
                victim.getEffect(ModEffect.CURSE_OF_JUDGING)
        };

        for (MobEffectInstance curse : curses) {
            if (curse == null) {
                continue;
            }

            killer.addEffect(new MobEffectInstance(
                    curse.getEffect(),
                    curse.getDuration(),
                    curse.getAmplifier()
            ));
        }
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(
            int tickCount,
            int amplification
    ) {
        return true;
    }
}

