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
import com.buuz135.portality.tile.BasicFrameTile;
import com.buuz135.portality.tile.ControllerTile;
import com.buuz135.portality.tile.FrameTile;
import com.hrznstudio.titanium.api.IFactory;
import com.hrznstudio.titanium.block.RotatableBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class FrameBlock<T extends FrameTile<T>> extends RotatableBlock<T> {

    public FrameBlock(String name, Class<T> tileClass) {
        super(Block.Properties.from(Blocks.IRON_BLOCK), tileClass);
        setRegistryName(Portality.MOD_ID, name);
        setItemGroup(Portality.TAB);
    }

    @Override
    public void onReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        TileEntity tileEntity = worldIn.getTileEntity(pos);
        if (tileEntity instanceof FrameTile && ((FrameTile) tileEntity).getControllerPos() != null) {
            TileEntity controller = worldIn.getTileEntity(((FrameTile) tileEntity).getControllerPos());
            if (controller instanceof ControllerTile) {
                ((ControllerTile) controller).setShouldCheckForStructure(true);
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
                return (T) new BasicFrameTile();
            }
        };
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(IBlockReader worldIn) {
        return new BasicFrameTile();
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        super.fillStateContainer(builder);
    }
}
