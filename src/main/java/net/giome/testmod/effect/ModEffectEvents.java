package net.giome.testmod.effect;

import net.giome.testmod.TestMod;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;

@EventBusSubscriber(modid = TestMod.MODID)
public class ModEffectEvents {

    @SubscribeEvent
    public static void onEffectAdded(MobEffectEvent.Added event) {
        LivingEntity entity = event.getEntity();

        // Prevent merging if the combined effect is already active.
        if (entity.hasEffect(ModEffect.CURSE_OF_LONELINESS)) {
            return;
        }

        // Check whether the entity has both required effects.
        MobEffectInstance death =
                entity.getEffect(ModEffect.CURSE_OF_DEATH);

        MobEffectInstance love =
                entity.getEffect(ModEffect.BLESSING_OF_LOVE);

        if (death == null || love == null) {
            return;
        }

        // Keep the longest duration and the highest amplifier.
        int duration = Math.max(
                death.getDuration(),
                love.getDuration()
        );

        int amplifier = death.getAmplifier() + love.getAmplifier() + 1;

        // Remove the original effects.
        entity.removeEffect(ModEffect.CURSE_OF_DEATH);
        entity.removeEffect(ModEffect.BLESSING_OF_LOVE);

        // Apply the combined effect immediately.
        entity.addEffect(new MobEffectInstance(
                ModEffect.CURSE_OF_LONELINESS,
                duration,
                amplifier
        ));
    }
}