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
package com.buuz135.portality.tile;

import com.buuz135.portality.proxy.client.IPortalColor;
import com.hrznstudio.titanium.block.BasicTileBlock;
import com.hrznstudio.titanium.block.tile.ActiveTile;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;

public abstract class FrameTile<T extends FrameTile<T>> extends ActiveTile<T> implements IPortalColor {

    private BlockPos controllerPos;
    private int color;

    public FrameTile(BasicTileBlock<T> base, BlockPos pos, BlockState state) {
        super(base, pos, state);
        this.color = Integer.parseInt("0094ff", 16); //Default Blue
    }

    @Override
    protected void saveAdditional(CompoundTag compoundTag) {
        super.saveAdditional(compoundTag);
        if (controllerPos != null) {
            compoundTag.putInt("X", controllerPos.getX());
            compoundTag.putInt("Y", controllerPos.getY());
            compoundTag.putInt("Z", controllerPos.getZ());
        }
        compoundTag.putInt("Color", color);
    }

    @Override
    public void load(CompoundTag compound) {
        super.load(compound);
        if (compound.contains("X")) {
            controllerPos = new BlockPos(compound.getInt("X"), compound.getInt("Y"), compound.getInt("Z"));
        }
        if (compound.contains("Color")) {
            if (this.color != compound.getInt("Color") && this.level != null) {
                this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 1);
            }
            this.color = compound.getInt("Color");
        }
    }

    public BlockPos getControllerPos() {
        return controllerPos;
    }

    public void setControllerPos(BlockPos controllerPos) {
        this.controllerPos = controllerPos;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
        markForUpdate();
    }
}
