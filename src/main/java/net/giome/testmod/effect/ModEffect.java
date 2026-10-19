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
            () -> new CurseOfDeath(MobEffectCategory.NEUTRAL, 0x000000));

    public static final Holder<MobEffect> CURSE_OF_HATRED = MOB_EFFECTS.register("curse_of_hatred",
            () -> new CurseOfHatred(MobEffectCategory.NEUTRAL, 0x8B0000));

    public static final Holder<MobEffect> BLESSING_OF_LOVE = MOB_EFFECTS.register("blessing_of_love",
            () -> new BlessingOfLove(MobEffectCategory.BENEFICIAL, 0xFF0000));

    public static final Holder<MobEffect> CURSE_OF_LONELINESS = MOB_EFFECTS.register("curse_of_loneliness",
            () -> new CurseOfLoneliness(MobEffectCategory.NEUTRAL, 0x4B0082));

    public static final Holder<MobEffect> CURSE_OF_DISGUST = MOB_EFFECTS.register("curse_of_disgust",
            () -> new CurseOfDisgust(MobEffectCategory.NEUTRAL, 0x004705));

    public static final Holder<MobEffect> CURSE_OF_JUDGING = MOB_EFFECTS.register("curse_of_judging",
            () -> new CurseOfJudging(MobEffectCategory.NEUTRAL, 0x696969));

    public static final Holder<MobEffect> BLESSING_OF_THE_MOON = MOB_EFFECTS.register("blessing_of_the_moon",
            () -> new BlessingOfTheMoon(MobEffectCategory.BENEFICIAL, 0x6633CC));

    public static final Holder<MobEffect> BLESSING_OF_THE_SUN = MOB_EFFECTS.register("blessing_of_the_sun",
            () -> new BlessingOfTheSun(MobEffectCategory.BENEFICIAL, 0xFFD700));



    public static void register(IEventBus eventBus) {
        MOB_EFFECTS.register(eventBus);
    }
}
