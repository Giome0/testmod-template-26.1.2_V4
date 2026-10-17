package net.giome.testmod.network;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;

import java.util.ArrayList;
import java.util.List;

public class DelayedExplosion {
    private static final List<ExplosionData> pending = new ArrayList<>();

    public static void schedule(ServerLevel level, double x, double y, double z, float power, int delayTicks) {
        pending.add(new ExplosionData(level, x, y, z, power, delayTicks));
    }

    @SubscribeEvent
    public static void onTick(LevelTickEvent.Post event) {
        if (event.getLevel() instanceof ServerLevel serverLevel) {
            pending.removeIf(data -> {
                if (data.level == serverLevel) {
                    data.ticksLeft--;
                    if (data.ticksLeft <= 0) {
                        data.level.explode(null, data.x, data.y, data.z, data.power, Level.ExplosionInteraction.TNT);
                        return true;
                    }
                }
                return false;
            });
        }
    }

    public static void scheduleCrater(ServerLevel level, double x, double y, double z, float radius, int delay) {
        for (int dx = -(int)radius; dx <= (int)radius; dx += 3) {
            for (int dy = -(int)radius; dy <= 0; dy += 3) {
                for (int dz = -(int)radius; dz <= (int)radius; dz += 3) {
                    double distance = Math.sqrt(dx*dx + dy*dy + dz*dz);
                    if (distance <= radius) {
                        schedule(level, x + dx, y + dy, z + dz, 10f, delay);
                    }
                }
            }
        }
    }

    static class ExplosionData {
        ServerLevel level;
        double x, y, z;
        float power;
        int ticksLeft;

        ExplosionData(ServerLevel level, double x, double y, double z, float power, int ticksLeft) {
            this.level = level;
            this.x = x;
            this.y = y;
            this.z = z;
            this.power = power;
            this.ticksLeft = ticksLeft;
        }
    }
}
