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
package com.buuz135.portality.proxy.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ITickableSound;
import net.minecraft.client.audio.LocatableSound;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TickeableSound extends LocatableSound implements ITickableSound {

    private boolean done;
    private World world;

    public TickeableSound(World world, BlockPos pos, SoundEvent soundIn) {
        super(soundIn, SoundCategory.BLOCKS);
        this.world = world;
        this.x = pos.getX();
        this.y = pos.getY();
        this.z = pos.getZ();
        this.repeat = true;
        this.done = false;
        this.volume = 0.35f;
        this.pitch = 0f;
        this.global = false;
    }

    @Override
    public boolean isDonePlaying() {
        return done;
    }

    public void setDone() {
        done = true;
    }

    public void increase() {
        if (this.pitch < 1) {
            this.pitch += 0.03;
        }
    }

    public void decrease() {
        if (this.pitch > 0) {
            this.pitch -= 0.03;
        }
    }

    @Override
    public void tick() {
        if (world.getTileEntity(new BlockPos(x, y, z)) == null) {
            setDone();
        }
        double distance = Minecraft.getInstance().player.func_233580_cy_().manhattanDistance(new BlockPos(this.x, this.y, this.z));
        if (distance > 16) {
            this.volume = 0;
        } else {
            if (distance == 0) distance = 1;
            this.volume = (float) (0.35 * (1F / distance));
        }
    }
}
