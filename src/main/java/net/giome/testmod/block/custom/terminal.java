package net.giome.testmod.block.custom;

import com.mojang.serialization.MapCodec;
import net.giome.testmod.block.entity.custom.terminalEntity;
import net.giome.testmod.item.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.transfer.item.ItemResource;

import javax.annotation.Nullable;

public class terminal extends BaseEntityBlock {
    public static final VoxelShape SHAPE = Block.box(0, 0, 0, 16, 12, 16);
    public static final MapCodec<terminal> CODEC = simpleCodec(terminal::new);


    public terminal(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(ACTIVATED, false));
    }

    // SHAPE
    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    /* BLOCK ENTITY */
    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos worldPosition, BlockState blockState) {
        return new terminalEntity(worldPosition, blockState);
    }

    @Override
    public boolean onDestroyedByPlayer(BlockState state, Level level, BlockPos pos, Player player, ItemStack toolStack,
                                       boolean willHarvest, FluidState fluid) {
        if(level.getBlockEntity(pos) instanceof terminalEntity terminalEntity) {
            terminalEntity.drops();
            level.updateNeighbourForOutputSignal(pos, this);
        }
        return super.onDestroyedByPlayer(state, level, pos, player, toolStack, willHarvest, fluid);
    }

    public static final BooleanProperty ACTIVATED = BooleanProperty.create("activated");

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(ACTIVATED);
    }

    /*@Override
    protected InteractionResult useItemOn(ItemStack itemStack, BlockState state, Level level,
                                          BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if(level.getBlockEntity(pos) instanceof terminalEntity terminalEntity) {
            if(player.isCrouching()) {
                player.openMenu(new SimpleMenuProvider(terminalEntity, Component.translatable("block.testmod.terminal")), pos);
                return InteractionResult.SUCCESS;
            }
            boolean isterminalEmpty = terminalEntity.inventory.getResource(0).isEmpty();

            // Insert Logic
                if (isterminalEmpty && !itemStack.isEmpty() && itemStack.is(ModItems.ENCAPSULATED_RESONITE.get())) {
                    terminalEntity.inventory.set(0, ItemResource.of(itemStack), 1);
                    itemStack.shrink(1);
                    level.playSound(player, pos, SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 1f, 2f);
                    level.setBlock(pos, state.setValue(ACTIVATED, true), 3);
                }


                // Extract Logic
                else if (!isterminalEmpty) {
                    ItemStack stackOnterminal = terminalEntity.inventory.getResource(0).toStack();
                    terminalEntity.clearContents();

                    if (!player.getInventory().add(stackOnterminal)) {
                        player.drop(stackOnterminal, false);
                    }

                    level.playSound(player, pos, SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 1f, 1f);
                    level.setBlock(pos, state.setValue(ACTIVATED, false), 3);
                }
            }
        return InteractionResult.SUCCESS;
    }*/

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if(level.getBlockEntity(pos) instanceof terminalEntity terminalEntity) {
                player.openMenu(new SimpleMenuProvider(terminalEntity, Component.translatable("block.testmod.terminal")), pos);
                return InteractionResult.SUCCESS;
        }
        return InteractionResult.SUCCESS;
    }
}
