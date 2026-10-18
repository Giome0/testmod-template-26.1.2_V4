
package net.giome.testmod.effect;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import net.giome.testmod.TestMod;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

@EventBusSubscriber(modid = TestMod.MODID)
public class ModEffectEvents {

    // Chance d'attribuer un effet à une entité qui apparaît.
    // 50 = 50 %, 100 = 100 %, 10 = 10 %, 0 = désactivé.
    private static final int CHANCE_EFFET = 5;

    // Liste des effets personnalisés pouvant être attribués aléatoirement.
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

    @SubscribeEvent
    public static void onEntityJoinLevel(EntityJoinLevelEvent event) {
        // Ne traiter que les entités vivantes.
        if (!(event.getEntity() instanceof LivingEntity entity)) {
            return;
        }

        // L'attribution doit se faire côté serveur.
        if (!(event.getLevel() instanceof ServerLevel)) {
            return;
        }

        // Ne pas réattribuer un effet aux entités rechargées depuis le monde.
        if (event.loadedFromDisk()) {
            return;
        }

        // Éviter de remplacer un effet personnalisé déjà présent.
        List<Holder<MobEffect>> effects = getRandomEffects();

        if (entity.getActiveEffects().stream()
                .anyMatch(activeEffect ->
                        effects.contains(activeEffect.getEffect()))) {
            return;
        }

        // Vérifier la chance.
        if (ThreadLocalRandom.current().nextInt(100) >= CHANCE_EFFET) {
            return;
        }

        // Choisir un effet au hasard.
        Holder<MobEffect> selectedEffect = effects.get(
                ThreadLocalRandom.current().nextInt(effects.size())
        );

        // Durée -1 : effet permanent.
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

        // Sphère de fumée autour de Curse of Death.
        MobEffectInstance deathCurse =
                entity.getEffect(ModEffect.CURSE_OF_DEATH);

        // Actualisation toutes les 2 ticks.
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

        // Chercher l'entité responsable de la mort.
        Entity attacker = event.getSource().getEntity();

        LivingEntity killer = null;

        // Si l'attaquant possède Curse of Death, il reçoit les malédictions.
        if (attacker instanceof LivingEntity livingAttacker
                && livingAttacker.hasEffect(ModEffect.CURSE_OF_DEATH)) {
            killer = livingAttacker;
        }

        // Sinon, conserver le comportement habituel :
        // l'attaquant direct reçoit les malédictions.
        else if (attacker instanceof LivingEntity livingAttacker) {
            killer = livingAttacker;
        }

        if (killer == null || killer == deadEntity) {
            return;
        }

        // Malédictions transmises au tueur.
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


