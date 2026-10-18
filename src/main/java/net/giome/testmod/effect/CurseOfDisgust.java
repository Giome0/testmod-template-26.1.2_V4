
package net.giome.testmod.effect;

import net.giome.testmod.TestMod;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.phys.AABB;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingChangeTargetEvent;

import java.util.List;

@EventBusSubscriber(modid = TestMod.MODID)
public class CurseOfDisgust extends MobEffect {

    // Portée dans laquelle la malédiction agit.
    private static final double BASE_RANGE = 12.0;
    private static final double RANGE_PER_LEVEL = 5.0;

    // Distance minimale à maintenir.
    private static final double BASE_DISTANCE = 8.0;
    private static final double DISTANCE_PER_LEVEL = 2.0;

    private static final double ESCAPE_SPEED = 2.2;

    public CurseOfDisgust(MobEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public boolean applyEffectTick(
            ServerLevel level,
            LivingEntity victim,
            int amplifier
    ) {
        double range = BASE_RANGE + RANGE_PER_LEVEL * amplifier;
        double minDistance =
                BASE_DISTANCE + DISTANCE_PER_LEVEL * amplifier;

        AABB area = victim.getBoundingBox().inflate(range);

        // Toutes les entités vivantes mobiles, agressives ou non.
        List<Mob> mobs = level.getEntitiesOfClass(
                Mob.class,
                area,
                mob -> mob != victim && mob.isAlive()
        );

        for (Mob mob : mobs) {
            double dx = mob.getX() - victim.getX();
            double dz = mob.getZ() - victim.getZ();
            double distance = Math.sqrt(dx * dx + dz * dz);

            // Si le mob est trop proche, il doit s'éloigner.
            if (distance < minDistance) {

                // Annuler toute poursuite de la victime.
                if (mob.getTarget() == victim) {
                    mob.setTarget(null);
                }

                mob.getNavigation().stop();

                // Choisir une position plus loin de la victime.
                if (distance < 0.001) {
                    dx = mob.getId() % 2 == 0 ? 1.0 : -1.0;
                    dz = 0.0;
                    distance = 1.0;
                }

                double escapeX = mob.getX()
                        + (dx / distance) * (minDistance + 3.0);

                double escapeZ = mob.getZ()
                        + (dz / distance) * (minDistance + 3.0);

                mob.getNavigation().moveTo(
                        escapeX,
                        mob.getY(),
                        escapeZ,
                        ESCAPE_SPEED
                );
            } else {
                // Empêcher les mobs de cibler la victime
                // même lorsqu'ils sont déjà assez éloignés.
                if (mob.getTarget() == victim) {
                    mob.setTarget(null);
                    mob.getNavigation().stop();
                }
            }
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

    // Empêcher les mobs de sélectionner la victime comme cible.
    @SubscribeEvent
    public static void onChangeTarget(LivingChangeTargetEvent event) {
        LivingEntity target = event.getNewAboutToBeSetTarget();

        if (target == null
                || !target.hasEffect(ModEffect.CURSE_OF_DISGUST)) {
            return;
        }

        if (event.getEntity() instanceof Mob mob) {
            mob.setTarget(null);
            mob.getNavigation().stop();
            event.setCanceled(true);
        }
    }
}

