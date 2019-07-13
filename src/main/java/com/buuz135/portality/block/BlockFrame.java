/*
 * This file is part of Worldgen Indicators.
 *
 * Copyright 2018, Buuz135
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in the
 * Software without restriction, including without limitation the rights to use, copy,
 * modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the
 * following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies
 * or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.buuz135.portality.block;

import com.buuz135.portality.Portality;
import com.buuz135.portality.proxy.CommonProxy;
import com.buuz135.portality.tile.TileController;
import com.buuz135.portality.tile.TileFrame;
import com.hrznstudio.titanium.api.IFactory;
import com.hrznstudio.titanium.block.BlockRotation;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BlockFrame<T extends TileFrame> extends BlockRotation<T> {

    public BlockFrame(String name, Class<T> tileClass) {
        super(name, Block.Properties.create(Material.ROCK), tileClass);
        setItemGroup(Portality.TAB);
    }

    @Override
    public void onReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        TileEntity tileEntity = worldIn.getTileEntity(pos);
        if (tileEntity instanceof TileFrame && ((TileFrame) tileEntity).getControllerPos() != null) {
            TileEntity controller = worldIn.getTileEntity(((TileFrame) tileEntity).getControllerPos());
            if (controller instanceof TileController) {
                ((TileController) controller).setShouldCheckForStructure(true);
            }
        }
        super.onReplaced(state, worldIn, pos, newState, isMoving);
    }

    @Nonnull
    @Override
    public RotationType getRotationType() {
        return RotationType.NONE;
    }

    @Override
    public IFactory<T> getTileEntityFactory() {
        return new IFactory<T>() {
            @Nonnull
            @Override
            public T create() {
                return (T) new TileFrame(CommonProxy.BLOCK_FRAME);
            }
        };
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(IBlockReader worldIn) {
        return new TileFrame(CommonProxy.BLOCK_FRAME);
    }

    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT;
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        super.fillStateContainer(builder);
    }
}
