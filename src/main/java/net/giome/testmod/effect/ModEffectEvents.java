
package net.giome.testmod.effect;

import net.giome.testmod.TestMod;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

@EventBusSubscriber(modid = TestMod.MODID)
public class ModEffectEvents {

    @SubscribeEvent
    public static void onEntityTick(EntityTickEvent.Post event) {
        Entity target = event.getEntity();

        if (!(target instanceof LivingEntity entity)) {
            return;
        }

        // Exécuter uniquement côté serveur.
        if (entity.level().isClientSide()) {
            return;
        }

        // Ne pas fusionner une deuxième fois.
        if (entity.hasEffect(ModEffect.CURSE_OF_LONELINESS)) {
            return;
        }

        MobEffectInstance death =
                entity.getEffect(ModEffect.CURSE_OF_DEATH);

        MobEffectInstance love =
                entity.getEffect(ModEffect.BLESSING_OF_LOVE);

        if (death == null || love == null) {
            return;
        }

        int duration = Math.max(
                death.getDuration(),
                love.getDuration()
        );

        int amplifier =
                death.getAmplifier() + love.getAmplifier() + 1;

        entity.removeEffect(ModEffect.CURSE_OF_DEATH);
        entity.removeEffect(ModEffect.BLESSING_OF_LOVE);

        entity.addEffect(new MobEffectInstance(
                ModEffect.CURSE_OF_LONELINESS,
                duration,
                amplifier
        ));
    }
}