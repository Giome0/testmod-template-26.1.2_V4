package net.giome.testmod.effect;

import net.giome.testmod.TestMod;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.phys.AABB;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingChangeTargetEvent;

import java.util.List;

@EventBusSubscriber(modid = TestMod.MODID)
public class BlessingOfLove extends MobEffect {
    private static final double BASE_RANGE = 5.0;
    private static final double RANGE_PER_LEVEL = 5.0;
    private static final double FOLLOW_DISTANCE = 3.0;
    private static final double FOLLOW_SPEED = 1.3;

    public BlessingOfLove(MobEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public boolean applyEffectTick(ServerLevel serverLevel, LivingEntity victim, int amplification) {
        double range = BASE_RANGE + RANGE_PER_LEVEL * amplification;
        AABB area = victim.getBoundingBox().inflate(range);

        List<Mob> mobs = serverLevel.getEntitiesOfClass(Mob.class, area, mob -> mob != victim && mob.isAlive());

        for (Mob mob : mobs) {
            if (mob instanceof Enemy) {
                // Mobs agressifs : ils oublient complètement l'entité
                if (mob.getTarget() == victim) {
                    mob.setTarget(null);
                    mob.getNavigation().stop();
                }
            } else if (mob instanceof PathfinderMob) {
                // Mobs passifs : ils viennent vers l'entité et s'arrêtent à 2 blocs
                mob.getLookControl().setLookAt(victim, 30.0F, 30.0F);

                if (mob.distanceTo(victim) > FOLLOW_DISTANCE) {
                    mob.getNavigation().moveTo(victim, FOLLOW_SPEED);
                } else {
                    mob.getNavigation().stop();
                }
            }
        }

        return super.applyEffectTick(serverLevel, victim, amplification);
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int tickCount, int amplification) {
        return tickCount % 5 == 0;
    }

    // Empêche les mobs de prendre pour cible une entité qui a la bénédiction
    @SubscribeEvent
    public static void onChangeTarget(LivingChangeTargetEvent event) {
        LivingEntity newTarget = event.getNewAboutToBeSetTarget();

        if (newTarget != null && newTarget.hasEffect(ModEffect.BLESSING_OF_LOVE)) {
            event.setCanceled(true);
        }
    }
}
