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
import com.buuz135.portality.block.BlockFrame;
import com.buuz135.portality.tile.TileController;
import com.buuz135.portality.tile.TileFrame;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.stream.Collectors;

public abstract class BlockCapabilityModule<T, S extends TileFrame> extends BlockFrame<S> implements IPortalModule {

    public static BooleanProperty INPUT = BooleanProperty.create("input");

    public BlockCapabilityModule(String name, Class<S> tileClass) {
        super(name, tileClass);
        this.setDefaultState(this.getDefaultState().with(INPUT, true));
        setItemGroup(Portality.TAB);
    }

    @Override
    public void work(TileController controller, BlockPos blockPos) {
        if (controller.getLinkData() == null) return;
        TileEntity other = controller.getWorld().getServer().getWorld(DimensionType.getById(controller.getLinkData().getDimension())).getTileEntity(controller.getLinkData().getPos());
        if (other instanceof TileController && this.isInput(controller.getWorld().getBlockState(blockPos))) {
            TileController otherController = (TileController) other;
            internalWork(controller.getWorld(), blockPos, other.getWorld(), otherController.getModules().stream().filter(pos -> otherController.getWorld().getBlockState(pos).getBlock() instanceof BlockCapabilityModule
                    && !((BlockCapabilityModule) otherController.getWorld().getBlockState(pos).getBlock()).isInput(otherController.getWorld().getBlockState(pos))
                    && ((BlockCapabilityModule) otherController.getWorld().getBlockState(pos).getBlock()).getCapability().equals(this.getCapability())).collect(Collectors.toList()));
        }
    }

    @Nonnull
    @Override
    public RotationType getRotationType() {
        return RotationType.FOUR_WAY;
    }

    public abstract Capability<T> getCapability();

    public boolean isInput(BlockState state) {
        return state.get(INPUT);
    }

    abstract void internalWork(World current, BlockPos myself, World otherWorld, List<BlockPos> compatibleBlockPos);

    @Override
    public boolean allowsInterdimensionalTravel() {
        return false;
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        super.fillStateContainer(builder);
        builder.add(INPUT);
    }

    @Override
    public ItemStack getPickBlock(BlockState state, RayTraceResult target, IBlockReader world, BlockPos pos, PlayerEntity player) {
        return new ItemStack(this, 1);
    }

}
