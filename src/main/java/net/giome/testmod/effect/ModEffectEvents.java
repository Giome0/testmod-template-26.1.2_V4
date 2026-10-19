package net.giome.testmod.effect;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import net.giome.testmod.TestMod;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

@EventBusSubscriber(modid = TestMod.MODID)
public class ModEffectEvents {

    // Chance d'attribution aléatoire : 5 %.
    private static final int CHANCE_EFFET = 5;

    private static List<Holder<MobEffect>> getRandomEffects() {
        return List.of(
                ModEffect.CURSE_OF_DEATH,
                ModEffect.CURSE_OF_HATRED,
                ModEffect.BLESSING_OF_LOVE,
                ModEffect.CURSE_OF_LONELINESS,
                ModEffect.CURSE_OF_DISGUST,
                ModEffect.CURSE_OF_JUDGING
        );
    }

    // Compte uniquement les bénédictions personnalisées.
    private static int countBlessings(LivingEntity entity) {
        int count = 0;

        if (entity.hasEffect(ModEffect.BLESSING_OF_LOVE)) count++;
        if (entity.hasEffect(ModEffect.BLESSING_OF_THE_MOON)) count++;
        if (entity.hasEffect(ModEffect.BLESSING_OF_THE_SUN)) count++;
        if (entity.hasEffect(ModEffect.BLESSING_OF_REBIRTH)) count++;

        return count;
    }

    @SubscribeEvent
    public static void onEntityJoinLevel(EntityJoinLevelEvent event) {
        if (!(event.getEntity() instanceof LivingEntity entity)) {
            return;
        }

        if (!(event.getLevel() instanceof ServerLevel)) {
            return;
        }

        if (event.loadedFromDisk()) {
            return;
        }

        List<Holder<MobEffect>> effects = getRandomEffects();

        // Ne pas remplacer un effet personnalisé déjà présent.
        if (entity.getActiveEffects().stream()
                .anyMatch(activeEffect ->
                        effects.contains(activeEffect.getEffect()))) {
            return;
        }

        if (ThreadLocalRandom.current().nextInt(100) >= CHANCE_EFFET) {
            return;
        }

        Holder<MobEffect> selectedEffect = effects.get(
                ThreadLocalRandom.current().nextInt(effects.size())
        );

        // Durée infinie.
        entity.addEffect(new MobEffectInstance(selectedEffect, -1));
    }

    @SubscribeEvent
    public static void onEntityTick(EntityTickEvent.Post event) {
        Entity target = event.getEntity();

        if (!(target instanceof LivingEntity entity)) {
            return;
        }

        if (!(entity.level() instanceof ServerLevel serverLevel)) {
            return;
        }

        // Trois bénédictions ou plus : explosion et mort.
        if (countBlessings(entity) >= 3) {
            serverLevel.getServer().getPlayerList().broadcastSystemMessage(
                    net.minecraft.network.chat.Component.literal(
                            "The holy power is too much for a mere human to hold"
                    ),
                    false
            );

            serverLevel.explode(
                    entity,
                    entity.getX(),
                    entity.getY(),
                    entity.getZ(),
                    4.0F,
                    Level.ExplosionInteraction.MOB
            );

            entity.kill(serverLevel);
            return;
        }

        // Particules de fumée autour du porteur de Curse of Death.
        MobEffectInstance deathCurse =
                entity.getEffect(ModEffect.CURSE_OF_DEATH);

        if (deathCurse != null && entity.tickCount % 2 == 0) {
            double radius = deathCurse.getAmplifier() + 2.0;

            int horizontalPoints = 15;
            int verticalPoints = 6;

            double centerY = entity.getY()
                    + entity.getBbHeight() / 2.0;

            for (int i = 1; i < verticalPoints; i++) {
                double phi = Math.PI * i / verticalPoints;

                for (int j = 0; j < horizontalPoints; j++) {
                    double theta =
                            2.0 * Math.PI * j / horizontalPoints;

                    double x = entity.getX()
                            + radius * Math.sin(phi) * Math.cos(theta);

                    double y = centerY
                            + radius * Math.cos(phi);

                    double z = entity.getZ()
                            + radius * Math.sin(phi) * Math.sin(theta);

                    serverLevel.sendParticles(
                            ParticleTypes.SMOKE,
                            x, y, z,
                            1,
                            0.01, 0.01, 0.01,
                            0.001
                    );
                }
            }
        }

        // Fusion : Curse of Disgust + Blessing of Love
        // -> Curse of Judging
        if (!entity.hasEffect(ModEffect.CURSE_OF_JUDGING)) {
            MobEffectInstance disgust =
                    entity.getEffect(ModEffect.CURSE_OF_DISGUST);

            MobEffectInstance love =
                    entity.getEffect(ModEffect.BLESSING_OF_LOVE);

            if (disgust != null && love != null) {
                int duration = Math.max(
                        disgust.getDuration(),
                        love.getDuration()
                );

                int amplifier = disgust.getAmplifier()
                        + love.getAmplifier() + 1;

                entity.removeEffect(ModEffect.CURSE_OF_DISGUST);
                entity.removeEffect(ModEffect.BLESSING_OF_LOVE);

                entity.addEffect(new MobEffectInstance(
                        ModEffect.CURSE_OF_JUDGING,
                        duration,
                        amplifier
                ));

                return;
            }
        }

        // Fusion : Curse of Death + Blessing of Love
        // -> Curse of Loneliness
        if (!entity.hasEffect(ModEffect.CURSE_OF_LONELINESS)) {
            MobEffectInstance death =
                    entity.getEffect(ModEffect.CURSE_OF_DEATH);

            MobEffectInstance love =
                    entity.getEffect(ModEffect.BLESSING_OF_LOVE);

            if (death != null && love != null) {
                int duration = Math.max(
                        death.getDuration(),
                        love.getDuration()
                );

                int amplifier = death.getAmplifier()
                        + love.getAmplifier() + 1;

                entity.removeEffect(ModEffect.CURSE_OF_DEATH);
                entity.removeEffect(ModEffect.BLESSING_OF_LOVE);

                entity.addEffect(new MobEffectInstance(
                        ModEffect.CURSE_OF_LONELINESS,
                        duration,
                        amplifier
                ));
            }
        }
    }

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        LivingEntity deadEntity = event.getEntity();

        // Blessing of Rebirth : effet similaire au Totem of Undying.
        if (deadEntity.hasEffect(ModEffect.BLESSING_OF_REBIRTH)) {
            int amplifier = deadEntity
                    .getEffect(ModEffect.BLESSING_OF_REBIRTH)
                    .getAmplifier();

            event.setCanceled(true);

            // La bénédiction disparaît après avoir sauvé l'entité.
            deadEntity.removeEffect(ModEffect.BLESSING_OF_REBIRTH);

            // Empêche la mort.
            deadEntity.setHealth(1.0F);

            // Chaque niveau augmente la durée et le niveau des effets.
            int duration = 900 + (amplifier * 400);
            int effectLevel = 1 + amplifier;

            deadEntity.addEffect(new MobEffectInstance(
                    MobEffects.REGENERATION,
                    duration,
                    effectLevel
            ));

            deadEntity.addEffect(new MobEffectInstance(
                    MobEffects.FIRE_RESISTANCE,
                    duration,
                    effectLevel
            ));

            deadEntity.addEffect(new MobEffectInstance(
                    MobEffects.ABSORPTION,
                    200 + (amplifier * 100),
                    effectLevel
            ));

            return;
        }

        Entity attacker = event.getSource().getEntity();

        if (!(attacker instanceof LivingEntity killer)) {
            return;
        }

        if (deadEntity == killer) {
            return;
        }

        // Transmission habituelle des malédictions au tueur direct.
        MobEffectInstance[] curses = {
                deadEntity.getEffect(ModEffect.CURSE_OF_DEATH),
                deadEntity.getEffect(ModEffect.CURSE_OF_HATRED),
                deadEntity.getEffect(ModEffect.CURSE_OF_LONELINESS),
                deadEntity.getEffect(ModEffect.CURSE_OF_DISGUST),
                deadEntity.getEffect(ModEffect.CURSE_OF_JUDGING)
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
}




