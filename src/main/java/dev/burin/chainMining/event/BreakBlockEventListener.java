package dev.burin.chainMining.event;

import dev.burin.chainMining.ChainMiningMod;
import lombok.Setter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.GameMasterBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.EventHooks;
import net.neoforged.neoforge.event.level.BlockEvent;

import java.util.function.Consumer;

@EventBusSubscriber(modid = ChainMiningMod.MOD_ID)
public class BreakBlockEventListener {
    private static final Direction[] HORIZONTAL_DIRECTIONS = new Direction[]{
        Direction.SOUTH,
        Direction.WEST,
        Direction.EAST,
        Direction.NORTH
    };
    private static final Direction[] VERTICAL_DIRECTIONS = new Direction[]{
        Direction.UP,
        Direction.DOWN
    };
    private static final Direction[][] CORNER_DIRECTIONS = new Direction[][]{
        {Direction.EAST, Direction.NORTH}, {Direction.EAST, Direction.SOUTH},
        {Direction.WEST, Direction.NORTH}, {Direction.WEST, Direction.SOUTH},
    };

    @Setter
    private static boolean onPressed = false;

    @SubscribeEvent
    public static void breakBlock(BlockEvent.BreakEvent event) {
        Player player = event.getPlayer();
        LevelAccessor level = event.getLevel();
        BlockPos pos = event.getPos();
        BlockState state = event.getState();
        if (onPressed) {
            event.setCanceled(true);
            BlockPos.breadthFirstTraversal(pos, Integer.MAX_VALUE, 64,
                BreakBlockEventListener::acceptDirections,
                (blockPos) -> {
                BlockState blockState = level.getBlockState(blockPos);
                if (blockState.is(state.getBlock())) {
                    destroyBlock((Level) level, blockPos, level.getBlockState(blockPos), (ServerPlayer) player);
                    return true;
                }
                return pos.equals(blockPos);
            });
        }
    }

    private static void acceptDirections(BlockPos blockPos, Consumer<BlockPos> blockPosConsumer) {
        for (Direction direction : Direction.values()) {
            blockPosConsumer.accept(blockPos.relative(direction));
        }
        for (Direction horizontal : HORIZONTAL_DIRECTIONS) {
            for (Direction vertical : VERTICAL_DIRECTIONS) {
                blockPosConsumer.accept(blockPos.relative(horizontal).relative(vertical));
            }
        }
        for (Direction[] corner : CORNER_DIRECTIONS) {
            BlockPos pos1 = blockPos;
            for (Direction direction : corner) {
                pos1 = pos1.relative(direction);
            }
            for (Direction verticalDirection : VERTICAL_DIRECTIONS) {
                pos1 = pos1.relative(verticalDirection);
                blockPosConsumer.accept(pos1);
            }
        }
    }

    private static void destroyBlock(Level level, BlockPos pos, BlockState state, ServerPlayer player) {
        GameType gameModeForPlayer = player.gameMode.getGameModeForPlayer();
        BlockEntity blockEntity = level.getBlockEntity(pos);
        Block block = state.getBlock();
        if (block instanceof GameMasterBlock && !player.canUseGameMasterBlocks()) {
            level.sendBlockUpdated(pos, state, state, 3);
        } else if (!player.blockActionRestricted(level, pos, gameModeForPlayer)) {
            BlockState state1 = block.playerWillDestroy(level, pos, state, player);
            if (player.gameMode.isCreative()) {
                removeBlock(level, pos, state1, true, player);
            } else {
                ItemStack stack = player.getMainHandItem();
                ItemStack stack1 = stack.copy();
                boolean b = state1.canHarvestBlock(level, pos, player);
                stack.mineBlock(level, state1, pos, player);
                boolean b1 = removeBlock(level, pos, state1, b, player);

                if (b && b1) {
                    if (level instanceof ServerLevel serverLevel) {
                        LootParams.Builder builder = new LootParams.Builder(serverLevel)
                            .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(pos))
                            .withParameter(LootContextParams.TOOL, stack1)
                            .withOptionalParameter(LootContextParams.THIS_ENTITY, player)
                            .withOptionalParameter(LootContextParams.BLOCK_ENTITY, blockEntity);
                        state1.getDrops(builder).stream()
                            .map((item) -> new ItemEntity(level, player.position().x, player.position().y, player.position().z, item))
                            .forEach(level::addFreshEntity);
                    }
                }

                if (stack.isEmpty() && !stack1.isEmpty()) {
                    EventHooks.onPlayerDestroyItem(player, stack1, InteractionHand.MAIN_HAND);
                }
            }
        }
    }

    private static boolean removeBlock(Level level, BlockPos pos, BlockState state, boolean canHarvest, ServerPlayer player) {
        boolean removed = state.onDestroyedByPlayer(level, pos, player, canHarvest, level.getFluidState(pos));
        if (removed)
            state.getBlock().destroy(level, pos, state);
        return removed;
    }
}
