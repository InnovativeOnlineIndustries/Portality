package com.buuz135.portality.proxy.client;

import net.minecraft.client.audio.ITickableSound;
import net.minecraft.client.audio.PositionedSound;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TickeableSound extends PositionedSound implements ITickableSound {

    private boolean done;

    public TickeableSound(BlockPos pos, SoundEvent soundIn) {
        super(soundIn, SoundCategory.BLOCKS);
        this.xPosF = pos.getX();
        this.yPosF = pos.getY();
        this.zPosF = pos.getZ();
        this.repeat = true;
        this.done = false;
        this.volume = 0.5f;
        this.pitch = 0f;
    }

    @Override
    public boolean isDonePlaying() {
        return done;
    }

    @Override
    public void update() {

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
}
