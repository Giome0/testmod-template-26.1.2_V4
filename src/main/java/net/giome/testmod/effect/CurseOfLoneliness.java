
package net.giome.testmod.effect;

import net.giome.testmod.TestMod;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
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
public class CurseOfLoneliness extends MobEffect {
    private static final double BASE_RANGE = 5.0;
    private static final double RANGE_PER_LEVEL = 5.0;
    private static final double FOLLOW_DISTANCE = 3.0;
    private static final double FOLLOW_SPEED = 1.3;

    public CurseOfLoneliness(MobEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public boolean applyEffectTick(ServerLevel level, LivingEntity victim, int amplifier) {
        // Effet Curse of Death : dégâts aux entités vivantes proches.
        AABB damageArea = victim.getBoundingBox().inflate(amplifier + 2);

        List<Entity> entities = level.getEntities(victim, damageArea);

        for (Entity entity : entities) {
            if (!(entity instanceof LivingEntity living)) {
                continue;
            }

            // Ne pas attaquer une entité déjà morte.
            if (living == victim || !living.isAlive()) {
                continue;
            }

            // Le porteur de Curse of Loneliness inflige les dégâts.
            boolean hurt = living.hurtServer(
                    level,
                    victim.damageSources().magic(),
                    100000
            );
        }

        // Effet Blessing of Love : comportement toutes les 2 ticks.
        if (victim.tickCount % 2 == 0) {
            double range = BASE_RANGE + RANGE_PER_LEVEL * amplifier;
            AABB area = victim.getBoundingBox().inflate(range);

            List<Mob> mobs = level.getEntitiesOfClass(
                    Mob.class, area, mob -> mob != victim && mob.isAlive()
            );

            for (Mob mob : mobs) {
                if (mob instanceof Enemy) {
                    if (mob.getTarget() == victim) {
                        mob.setTarget(null);
                        mob.getNavigation().stop();
                    }
                } else if (mob instanceof PathfinderMob) {
                    mob.getLookControl().setLookAt(victim, 30.0F, 30.0F);

                    if (mob.distanceTo(victim) > FOLLOW_DISTANCE) {
                        mob.getNavigation().moveTo(victim, FOLLOW_SPEED);
                    } else {
                        mob.getNavigation().stop();
                    }
                }
            }
        }

        return super.applyEffectTick(level, victim, amplifier);
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int tickCount, int amplifier) {
        return true;
    }

    @SubscribeEvent
    public static void onChangeTarget(LivingChangeTargetEvent event) {
        LivingEntity target = event.getNewAboutToBeSetTarget();

        if (target != null
                && (target.hasEffect(ModEffect.BLESSING_OF_LOVE)
                || target.hasEffect(ModEffect.CURSE_OF_LONELINESS))) {
            event.setCanceled(true);
        }
    }
}

