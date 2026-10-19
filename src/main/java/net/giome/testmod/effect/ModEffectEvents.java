
package net.giome.testmod.effect;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import net.giome.testmod.TestMod;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetSubtitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitlesAnimationPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
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
import net.minecraft.ChatFormatting;

@EventBusSubscriber(modid = TestMod.MODID)
public class ModEffectEvents {

    private static final int CHANCE_EFFET = 5;
    private static final int EXPLOSION_DELAY = 60;

    // Compte à rebours avant l'explosion.
    private static final Map<UUID, Integer> explosionCountdown =
            new HashMap<>();

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

    // Compte uniquement les bénédictions.
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

        // Gère le compte à rebours déjà commencé.
        Integer remaining = explosionCountdown.get(entity.getUUID());

        if (remaining != null) {
            remaining--;

            if (remaining <= 0) {
                explosionCountdown.remove(entity.getUUID());

                // On conserve Rebirth jusqu'à l'événement de mort.
                boolean hasRebirth =
                        entity.hasEffect(ModEffect.BLESSING_OF_REBIRTH);

                serverLevel.explode(
                        entity,
                        entity.getX(),
                        entity.getY(),
                        entity.getZ(),
                        4.0F,
                        Level.ExplosionInteraction.MOB
                );

                // Sans Rebirth, on force la mort si l'explosion
                // n'a pas déjà tué l'entité.
                if (!hasRebirth && entity.isAlive()) {
                    entity.kill(serverLevel);
                }
            } else {
                explosionCountdown.put(entity.getUUID(), remaining);
            }

            return;
        }

        // Trois bénédictions ou plus : avertissement, puis explosion.
        for (ServerPlayer player : serverLevel.players()) {
            player.connection.send(
                    new ClientboundSetTitlesAnimationPacket(0, 60, 0)
            );

            // Titre : "holy power" en or et en gras.
            Component title = Component.literal("The ")
                    .withStyle(ChatFormatting.WHITE)
                    .append(
                            Component.literal("holy power")
                                    .withStyle(
                                            ChatFormatting.GOLD,
                                            ChatFormatting.BOLD
                                    )
                    )
                    .append(
                            Component.literal(" is too much")
                                    .withStyle(ChatFormatting.WHITE)
                    );

            // Sous-titre : "mere human" en rouge et en gras.
            Component subtitle = Component.literal("for a ")
                    .withStyle(ChatFormatting.WHITE)
                    .append(
                            Component.literal("mere human")
                                    .withStyle(
                                            ChatFormatting.RED,
                                            ChatFormatting.BOLD
                                    )
                    )
                    .append(
                            Component.literal(" to hold")
                                    .withStyle(ChatFormatting.WHITE)
                    );

            player.connection.send(
                    new ClientboundSetTitleTextPacket(title)
            );

            player.connection.send(
                    new ClientboundSetSubtitleTextPacket(subtitle)
            );
        }

        // Particules autour des entités avec Curse of Death.
        MobEffectInstance deathCurse =
                entity.getEffect(ModEffect.CURSE_OF_DEATH);

        if (deathCurse != null && entity.tickCount % 2 == 0) {
            double radius = deathCurse.getAmplifier() + 2.0;
            int horizontalPoints = 15;
            int verticalPoints = 6;

            double centerY =
                    entity.getY() + entity.getBbHeight() / 2.0;

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

                int amplifier =
                        disgust.getAmplifier()
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

                int amplifier =
                        death.getAmplifier()
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

        // Blessing of Rebirth : sauve l'entité une fois.
        if (deadEntity.hasEffect(ModEffect.BLESSING_OF_REBIRTH)) {
            int amplifier = deadEntity
                    .getEffect(ModEffect.BLESSING_OF_REBIRTH)
                    .getAmplifier();

            event.setCanceled(true);

            deadEntity.removeEffect(ModEffect.BLESSING_OF_REBIRTH);
            deadEntity.setHealth(1.0F);

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

        // Transfert des malédictions au tueur direct.
        Entity attacker = event.getSource().getEntity();

        if (!(attacker instanceof LivingEntity killer)) {
            return;
        }

        if (deadEntity == killer) {
            return;
        }

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






