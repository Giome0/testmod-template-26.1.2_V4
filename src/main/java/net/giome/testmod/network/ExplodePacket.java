package net.giome.testmod.network;

import net.giome.testmod.TestMod;
import net.giome.testmod.block.entity.custom.terminalEntity;
import net.giome.testmod.item.missils.IMissil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.transfer.item.ItemResource;

public record ExplodePacket(BlockPos pos) implements CustomPacketPayload {
    public static final Type<ExplodePacket> TYPE = new Type<>(Identifier.fromNamespaceAndPath(TestMod.MODID, "explode"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ExplodePacket> STREAM_CODEC =
            StreamCodec.composite(BlockPos.STREAM_CODEC, ExplodePacket::pos, ExplodePacket::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(ExplodePacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();
            ServerLevel level = (ServerLevel) player.level();

            System.out.println("Terminal trouvé !");
            if (level.getBlockEntity(packet.pos()) instanceof terminalEntity te) {
                ItemStack stick = te.inventory.getResource(1).toStack();
                if (stick.getItem() instanceof IMissil missil) {
                    CompoundTag tag = stick.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
                    if (tag.contains("x")) {
                        int x = tag.getInt("x").orElse(0);
                        int y = tag.getInt("y").orElse(0);
                        int z = tag.getInt("z").orElse(0);
                        DelayedExplosion.scheduleCrater(level, x + 0.5, y, z + 0.5, missil.getPower(), missil.getDelay());
                        te.inventory.set(1, ItemResource.EMPTY, 0);
                    }
                }
            }
        });
    }
}
