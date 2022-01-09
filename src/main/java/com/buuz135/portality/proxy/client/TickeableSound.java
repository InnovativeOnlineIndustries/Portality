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
import net.minecraft.client.resources.sounds.AbstractSoundInstance;
import net.minecraft.client.resources.sounds.TickableSoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TickeableSound extends AbstractSoundInstance implements TickableSoundInstance {

    private boolean done;
    private Level world;

    public TickeableSound(Level world, BlockPos pos, SoundEvent soundIn) {
        super(soundIn, SoundSource.BLOCKS);
        this.world = world;
        this.x = pos.getX();
        this.y = pos.getY();
        this.z = pos.getZ();
        this.looping = true;
        this.done = false;
        this.volume = 0.35f;
        this.pitch = 0f;
        this.relative = false;
    }

    @Override
    public boolean isStopped() {
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
        if (world.getBlockEntity(new BlockPos(x, y, z)) == null) {
            setDone();
        }
        double distance = Minecraft.getInstance().player.blockPosition().distManhattan(new BlockPos(this.x, this.y, this.z));
        if (distance > 16) {
            this.volume = 0;
        } else {
            if (distance == 0) distance = 1;
            this.volume = (float) (0.35 * (1F / distance));
        }
    }
}
