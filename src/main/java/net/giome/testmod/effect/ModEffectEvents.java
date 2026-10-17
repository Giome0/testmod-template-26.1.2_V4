package net.giome.testmod.effect;

import net.giome.testmod.TestMod;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

@EventBusSubscriber(modid = TestMod.MODID)
public class ModEffectEvents {

    // Fusion de Curse of Death et Blessing of Love.
    @SubscribeEvent
    public static void onEntityTick(EntityTickEvent.Post event) {
        Entity target = event.getEntity();

        if (!(target instanceof LivingEntity entity)) {
            return;
        }

        if (entity.level().isClientSide()) {
            return;
        }

        // Ne pas fusionner une deuxième fois.
        if (entity.hasEffect(ModEffect.CURSE_OF_LONELINESS)) {
            return;
        }
//aa
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

        // Retirer les deux effets d'origine.
        entity.removeEffect(ModEffect.CURSE_OF_DEATH);
        entity.removeEffect(ModEffect.BLESSING_OF_LOVE);

        // Appliquer l'effet fusionné.
        entity.addEffect(new MobEffectInstance(
                ModEffect.CURSE_OF_LONELINESS,
                duration,
                amplifier
        ));
    }

    // Transmission des malédictions à l'entité qui tue.
    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        LivingEntity victim = event.getEntity();

        if (victim.level().isClientSide()) {
            return;
        }

        Entity attacker = event.getSource().getEntity();

        // Le responsable doit être une entité vivante différente
        // de la victime.
        if (!(attacker instanceof LivingEntity killer)
                || killer == victim) {
            return;
        }

        // Transmettre uniquement les malédictions.
        transferCurse(victim, killer, ModEffect.CURSE_OF_DEATH);
        transferCurse(victim, killer, ModEffect.CURSE_OF_HATRED);
        transferCurse(victim, killer, ModEffect.CURSE_OF_LONELINESS);
    }

    private static void transferCurse(
            LivingEntity victim,
            LivingEntity killer,
            Holder<MobEffect> curse
    ) {
        MobEffectInstance instance = victim.getEffect(curse);

        if (instance == null) {
            return;
        }

        killer.addEffect(new MobEffectInstance(
                curse,
                instance.getDuration(),
                instance.getAmplifier()
        ));
    }
}