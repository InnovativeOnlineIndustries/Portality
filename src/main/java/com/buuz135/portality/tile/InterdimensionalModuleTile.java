package com.buuz135.portality.tile;

import com.buuz135.portality.proxy.CommonProxy;
import com.hrznstudio.titanium.block.BasicTileBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nonnull;

public class InterdimensionalModuleTile extends FrameTile<InterdimensionalModuleTile> {

    public InterdimensionalModuleTile(BlockPos pos, BlockState state) {
        super((BasicTileBlock<InterdimensionalModuleTile>) CommonProxy.BLOCK_INTERDIMENSIONAL_MODULE.block().get(), CommonProxy.BLOCK_INTERDIMENSIONAL_MODULE.type().get(), pos, state);
    }

    @Nonnull
    @Override
    public InterdimensionalModuleTile getSelf() {
        return this;
    }
}
