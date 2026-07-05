/**
 * MIT License
 *
 * Copyright (c) 2018
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.buuz135.portality.block;

import com.buuz135.portality.Portality;
import com.buuz135.portality.data.PortalDataManager;
import com.buuz135.portality.data.PortalInformation;
import com.buuz135.portality.item.TeleportationTokenItem;
import com.buuz135.portality.proxy.CommonProxy;
import com.buuz135.portality.tile.ControllerTile;
import com.hrznstudio.titanium.block.RotatableBlock;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.UUID;

public class ControllerBlock extends RotatableBlock<ControllerTile> {

    public static final IntegerProperty ROLL = IntegerProperty.create("roll", 0, 3);

    public ControllerBlock() {
        super("controller", Block.Properties.ofFullCopy(Blocks.IRON_BLOCK), ControllerTile.class);
        setItemGroup(Portality.TAB);
    }

    @Override
    public void setPlacedBy(Level worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        PortalInformation information = new PortalInformation(UUID.randomUUID(), placer.getUUID(), false, false, worldIn.dimension(), pos, "X: " + pos.getX() + " Y: " + pos.getY() + " Z: " + pos.getZ(), new ItemStack(CommonProxy.BLOCK_FRAME.block().get()), false);
        PortalDataManager.addInformation(worldIn, information);
        super.setPlacedBy(worldIn, pos, state, placer, stack);
    }

    @Override
    public void destroy(LevelAccessor worldIn, BlockPos pos, BlockState state) {
        super.destroy(worldIn, pos, state);
        PortalDataManager.removeInformation(worldIn, pos);
    }

    @Override
    public void wasExploded(Level worldIn, BlockPos pos, Explosion explosionIn) {
        super.wasExploded(worldIn, pos, explosionIn);
        PortalDataManager.removeInformation(worldIn, pos);
    }

    private static int getRollForUp(Direction facing, Direction up) {
        for (int roll = 0; roll < 4; roll++) {
            if (ControllerTile.getPortalUp(facing, roll) == up) {
                return roll;
            }
        }
        return 0;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction facing = context.getClickedFace();
        int roll = facing.getAxis().isVertical() ? getRollForPlacement(facing, context.getNearestLookingDirections()) : 0;
        return this.defaultBlockState().setValue(FACING_ALL, facing).setValue(ROLL, roll);
    }

    @Nonnull
    @Override
    public RotationType getRotationType() {
        return RotationType.SIX_WAY;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(ROLL);
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        Direction oldFacing = state.getValue(FACING_ALL);
        Direction oldUp = ControllerTile.getPortalUp(oldFacing, state.getValue(ROLL));
        BlockState rotated = state.setValue(FACING_ALL, rotation.rotate(oldFacing));
        return rotated.setValue(ROLL, getRollForUp(rotated.getValue(FACING_ALL), rotation.rotate(oldUp)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        Direction oldFacing = state.getValue(FACING_ALL);
        Direction oldUp = ControllerTile.getPortalUp(oldFacing, state.getValue(ROLL));
        Rotation rotation = mirror.getRotation(oldFacing);
        BlockState mirrored = state.setValue(FACING_ALL, rotation.rotate(oldFacing));
        return mirrored.setValue(ROLL, getRollForUp(mirrored.getValue(FACING_ALL), rotation.rotate(oldUp)));
    }

    private int getRollForPlacement(Direction facing, Direction[] nearestLookingDirections) {
        for (Direction direction : nearestLookingDirections) {
            if (direction.getAxis() != facing.getAxis()) {
                return getRollForUp(facing, direction);
            }
        }
        return 0;
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level worldIn, BlockPos pos, Player playerIn, InteractionHand hand, BlockHitResult ray) {
        BlockEntity tile = worldIn.getBlockEntity(pos);
        if (tile instanceof ControllerTile) {
            ControllerTile controller = (ControllerTile) tile;
            if (!worldIn.isClientSide()) {
                if (!controller.isFormed()) {
                    playerIn.displayClientMessage(Component.translatable("portality.controller.error.size").withStyle(ChatFormatting.RED), true);
                    return ItemInteractionResult.SUCCESS;
                }
                if (controller.isPrivate() && !controller.getOwner().equals(playerIn.getUUID())) {
                    playerIn.displayClientMessage(Component.translatable("portality.controller.error.privacy").withStyle(ChatFormatting.RED), true);
                    return ItemInteractionResult.SUCCESS;
                }
                if (playerIn.isCrouching() && controller.getOwner().equals(playerIn.getUUID()) && !stack.isEmpty() && !ItemStack.isSameItemSameComponents(stack, controller.getDisplay())) {
                    if (stack.getItem() instanceof TeleportationTokenItem) {
                        if (TeleportationTokenItem.hasTokenData(stack)) {
                            controller.addTeleportationToken(stack);
                            playerIn.displayClientMessage(Component.translatable("portility.controller.info.added_token").withStyle(ChatFormatting.GREEN), true);
                        }
                        return ItemInteractionResult.SUCCESS;
                    }
                    playerIn.displayClientMessage(Component.translatable("portility.controller.info.icon_changed").withStyle(ChatFormatting.GREEN), true);
                    controller.setDisplayNameEnabled(stack);
                    return ItemInteractionResult.SUCCESS;
                }
            } else if (controller.isFormed()) {
                if (controller.isPrivate() && !controller.getOwner().equals(playerIn.getUUID()))
                    return ItemInteractionResult.SUCCESS;
                Minecraft.getInstance().submitAsync(() -> {
                    ControllerTile.OpenGui.open(0, (ControllerTile) tile);
                });
                return ItemInteractionResult.SUCCESS;
            }
        }
        return super.useItemOn(stack, state, worldIn, pos, playerIn, hand, ray);
    }

    @Override
    public void onRemove(BlockState state, Level worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        BlockEntity entity = worldIn.getBlockEntity(pos);
        if (entity instanceof ControllerTile) {
            ((ControllerTile) entity).breakController();
        }
        super.onRemove(state, worldIn, pos, newState, isMoving);
    }

    @Override
    public BlockEntityType.BlockEntitySupplier<?> getTileEntityFactory() {
        return ControllerTile::new;
    }


}
