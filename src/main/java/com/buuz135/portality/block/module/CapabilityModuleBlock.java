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
package com.buuz135.portality.block.module;

import com.buuz135.portality.Portality;
import com.buuz135.portality.block.FrameBlock;
import com.buuz135.portality.tile.ControllerTile;
import com.buuz135.portality.tile.FrameTile;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.stream.Collectors;

public abstract class CapabilityModuleBlock<T, S extends FrameTile<S>> extends FrameBlock<S> implements IPortalModule {

    public static BooleanProperty INPUT = BooleanProperty.create("input");

    public CapabilityModuleBlock(String name, Class<S> tileClass) {
        super(name, tileClass);
        this.registerDefaultState(this.defaultBlockState().setValue(INPUT, true));
        setItemGroup(Portality.TAB);
    }

    @Override
    public void work(ControllerTile controller, BlockPos blockPos) {
        if (controller.getLinkData() == null) return;
        BlockEntity other = controller.getLevel().getServer().getLevel(controller.getLinkData().getDimension()).getBlockEntity(controller.getLinkData().getPos());
        if (other instanceof ControllerTile && this.isInput(controller.getLevel().getBlockState(blockPos))) {
            ControllerTile otherController = (ControllerTile) other;
            internalWork(controller.getLevel(), blockPos, other.getLevel(), otherController.getModules().stream().filter(pos -> otherController.getLevel().getBlockState(pos).getBlock() instanceof CapabilityModuleBlock
                    && !((CapabilityModuleBlock) otherController.getLevel().getBlockState(pos).getBlock()).isInput(otherController.getLevel().getBlockState(pos))
                    && ((CapabilityModuleBlock) otherController.getLevel().getBlockState(pos).getBlock()).getCapability().equals(this.getCapability())).collect(Collectors.toList()));
        }
    }

    @Nonnull
    @Override
    public RotationType getRotationType() {
        return RotationType.FOUR_WAY;
    }

    public abstract Capability<T> getCapability();

    public boolean isInput(BlockState state) {
        return state.getValue(INPUT);
    }

    abstract void internalWork(Level current, BlockPos myself, Level otherWorld, List<BlockPos> compatibleBlockPos);

    @Override
    public boolean allowsInterdimensionalTravel() {
        return false;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(INPUT);
    }

    @Override
    public ItemStack getPickBlock(BlockState state, HitResult target, BlockGetter world, BlockPos pos, Player player) {
        return new ItemStack(this, 1);
    }

}
