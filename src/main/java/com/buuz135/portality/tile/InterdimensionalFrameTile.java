package com.buuz135.portality.tile;

import com.buuz135.portality.proxy.CommonProxy;
import com.hrznstudio.titanium.block.BasicTileBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nonnull;

public class InterdimensionalFrameTile extends FrameTile<InterdimensionalFrameTile> {

    public InterdimensionalFrameTile(BlockPos pos, BlockState state) {
        super((BasicTileBlock<InterdimensionalFrameTile>) CommonProxy.BLOCK_INTERDIMENSIONAL_MODULE.getLeft().get(), CommonProxy.BLOCK_INTERDIMENSIONAL_MODULE.getRight().get(), pos, state);
    }

    @Nonnull
    @Override
    public InterdimensionalFrameTile getSelf() {
        return this;
    }
}
