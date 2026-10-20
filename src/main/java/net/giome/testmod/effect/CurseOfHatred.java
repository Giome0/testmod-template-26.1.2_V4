package net.giome.testmod.effect;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.phys.AABB;

import java.util.List;

public class CurseOfHatred extends MobEffect {
    private static final double BASE_RANGE = 5.0;
    private static final double RANGE_PER_LEVEL = 5.0;
    private static final double ATTACK_REACH_SQR = 2.5 * 2.5;
    private static final float PASSIVE_MOB_DAMAGE = 2.0F;

    public CurseOfHatred(MobEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public boolean applyEffectTick(ServerLevel serverLevel, LivingEntity victim, int amplification) {
        // Seule la portée augmente avec l'intensité de l'effet
        double range = BASE_RANGE + RANGE_PER_LEVEL * amplification;
        AABB area = victim.getBoundingBox().inflate(range);

        List<Mob> mobs = serverLevel.getEntitiesOfClass(Mob.class, area, mob -> mob != victim && mob.isAlive());

        for (Mob mob : mobs) {
            // Tous les mobs (araignées de jour comprises) prennent la victime pour cible
            mob.setTarget(victim);

            // Les mobs passifs n'ont pas d'attaque : on les fait avancer et frapper à la main
            if (!mob.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE)) {
                mob.getNavigation().moveTo(victim, 1.2);

                if (mob.distanceToSqr(victim) <= ATTACK_REACH_SQR && mob.tickCount % 20 < 5) {
                    victim.hurtServer(serverLevel, mob.damageSources().mobAttack(mob), PASSIVE_MOB_DAMAGE);
                }
            }
        }

        return super.applyEffectTick(serverLevel, victim, amplification);
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int tickCount, int amplification) {
        return tickCount % 5 == 0;
    }
}