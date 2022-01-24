package com.buuz135.portality.tile;

import com.buuz135.portality.proxy.CommonProxy;
import com.hrznstudio.titanium.block.BasicTileBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nonnull;

public class BasicFrameTile extends FrameTile<BasicFrameTile> {

    public BasicFrameTile(BlockPos pos, BlockState state) {
        super((BasicTileBlock<BasicFrameTile>) CommonProxy.BLOCK_FRAME.get(), pos, state);
    }

    @Nonnull
    @Override
    public BasicFrameTile getSelf() {
        return this;
    }
}
