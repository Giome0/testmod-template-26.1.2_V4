package net.giome.testmod.effect;

import net.giome.testmod.TestMod;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.Mob;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModEffect {
    public static final DeferredRegister<MobEffect> MOB_EFFECTS =
            DeferredRegister.create(BuiltInRegistries.MOB_EFFECT, TestMod.MODID);

    public static final Holder<MobEffect> CURSE_OF_DEATH = MOB_EFFECTS.register("curse_of_death",
            () -> new CurseOfDeath(MobEffectCategory.NEUTRAL, 000000));

    public static void register(IEventBus eventBus) {
        MOB_EFFECTS.register(eventBus);
    }
}
