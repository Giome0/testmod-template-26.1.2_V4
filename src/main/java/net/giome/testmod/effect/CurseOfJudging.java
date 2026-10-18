
package net.giome.testmod.effect;

import net.giome.testmod.TestMod;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingChangeTargetEvent;
import net.minecraft.world.phys.AABB;

import java.util.List;

@EventBusSubscriber(modid = TestMod.MODID)
public class CurseOfJudging extends MobEffect {

    private static final double RANGE = 24.0;
    private static final double TARGET_DISTANCE = 6.0;

    // Vitesse pour se rapprocher.
    private static final double APPROACH_SPEED = 1.0;

    // Vitesse pour s'éloigner.
    private static final double RETREAT_SPEED = 1.8;

    // Tolérance autour des 6 blocs pour éviter les changements constants.
    private static final double MIN_DISTANCE = 5.5;
    private static final double MAX_DISTANCE = 6.5;

    public CurseOfJudging(MobEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public boolean applyEffectTick(
            ServerLevel level,
            LivingEntity victim,
            int amplifier
    ) {
        AABB area = victim.getBoundingBox().inflate(RANGE);

        List<Mob> mobs = level.getEntitiesOfClass(
                Mob.class,
                area,
                mob -> mob != victim && mob.isAlive()
        );

        for (Mob mob : mobs) {
            // Ne jamais attaquer ni poursuivre l'entité maudite.
            if (mob.getTarget() == victim) {
                mob.setTarget(null);
                mob.getNavigation().stop();
            }

            double dx = mob.getX() - victim.getX();
            double dy = mob.getY() - victim.getY();
            double dz = mob.getZ() - victim.getZ();

            double distance = Math.sqrt(dx * dx + dz * dz);

            // Si les entités sont presque au même endroit,
            // choisir une direction de déplacement.
            if (distance < 0.001) {
                dx = mob.getId() % 2 == 0 ? 1.0 : -1.0;
                dz = 0.0;
                distance = 1.0;
            }

            if (distance > MAX_DISTANCE) {
                // Trop loin : se rapprocher jusqu'à environ 6 blocs.
                double moveX = victim.getX()
                        + (dx / distance) * TARGET_DISTANCE;
                double moveZ = victim.getZ()
                        + (dz / distance) * TARGET_DISTANCE;

                mob.getNavigation().moveTo(
                        moveX,
                        victim.getY(),
                        moveZ,
                        APPROACH_SPEED
                );

            } else if (distance < MIN_DISTANCE) {
                // Trop proche : s'éloigner plus rapidement.
                double moveX = mob.getX()
                        + (dx / distance) * (TARGET_DISTANCE + 3.0);
                double moveZ = mob.getZ()
                        + (dz / distance) * (TARGET_DISTANCE + 3.0);

                mob.getNavigation().moveTo(
                        moveX,
                        mob.getY(),
                        moveZ,
                        RETREAT_SPEED
                );
            }
            // Entre 5,5 et 6,5 blocs : ne pas modifier la navigation.
        }

        return super.applyEffectTick(level, victim, amplifier);
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(
            int tickCount,
            int amplifier
    ) {
        return tickCount % 5 == 0;
    }

    // Empêcher les mobs de choisir la victime comme cible.
    @SubscribeEvent
    public static void onChangeTarget(LivingChangeTargetEvent event) {
        LivingEntity target = event.getNewAboutToBeSetTarget();

        if (target == null
                || !target.hasEffect(ModEffect.CURSE_OF_JUDGING)) {
            return;
        }

        if (event.getEntity() instanceof Mob mob) {
            event.setCanceled(true);
            mob.setTarget(null);
            mob.getNavigation().stop();
        }
    }
}

