package net.giome.testmod.block.entity.custom;

import net.giome.testmod.block.custom.terminal;
import net.giome.testmod.block.entity.ModBlockEntities;
import net.giome.testmod.item.ModItems;
import net.giome.testmod.menu.custom.terminalMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;

import javax.annotation.Nullable;

public class terminalEntity extends BlockEntity implements MenuProvider{
    public final ItemStacksResourceHandler inventory = new ItemStacksResourceHandler(2) {
        @Override
        protected void onContentsChanged(int index, ItemStack previousContents) {
            super.onContentsChanged(index, previousContents);
            terminalEntity.this.setChanged();

            if(!level.isClientSide()) {
                boolean isEmpty = inventory.getResource(0).isEmpty();
                BlockState currentState = level.getBlockState(getBlockPos());
                BlockState newState = currentState.setValue(terminal.ACTIVATED, !isEmpty);
                level.setBlock(getBlockPos(), newState, 3);
                level.sendBlockUpdated(getBlockPos(), currentState, newState, 3);

                if (index == 0 || index == 1) {
                    ItemStack resonite = inventory.getResource(0).toStack();
                    ItemStack stick = inventory.getResource(1).toStack();

                    if (!resonite.isEmpty() && !stick.isEmpty()) {
                        CustomData data = resonite.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
                        CustomData stickData = stick.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);

                        if (!data.isEmpty() && !data.equals(stickData)) {
                            stick.set(DataComponents.CUSTOM_DATA, data);
                            inventory.set(1, ItemResource.of(stick), 1);
                        }
                    }
                }
            }
        }

        @Override
        public boolean isValid(int index, ItemResource resource) {
            if (index == 0) {
                return resource.is(ModItems.ENCAPSULATED_RESONITE.get());
            }
            return true;
        }

        @Override
        protected int getCapacity(int index, ItemResource resource) {
            return 1;
        }
    };

    public terminalEntity(BlockPos worldPosition, BlockState blockState) {
        super(ModBlockEntities.TERMINAL_BE.get(), worldPosition, blockState);
    }


    public void clearContents() {
        inventory.set(0, ItemResource.EMPTY, 0);
    }

    public void drops() {
        SimpleContainer inv = new SimpleContainer(inventory.size());
        for(int i = 0; i < inventory.size(); i++) {
            ItemAccess itemAccess = ItemAccess.forHandlerIndex(inventory, 0);
            inv.setItem(i, new ItemStack(itemAccess.getResource().getItem(), itemAccess.getAmount()));
        }
        Containers.dropContents(this.level, this.worldPosition, inv);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putChild("inventory", inventory);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        input.child("inventory").ifPresent(inventory::deserialize);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.testmod.terminal");
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
        return new terminalMenu(containerId, inventory, this, this.inventory);
    }


    /* BLOCK ENTITY SYNC STUFF */
    @Override
    public @Nullable Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return saveWithoutMetadata(registries);
    }

}
