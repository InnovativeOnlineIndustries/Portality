package com.buuz135.portality.block.module;

import com.buuz135.portality.tile.TileController;
import net.minecraft.util.math.BlockPos;

public interface IPortalModule {

    void work(TileController controller, BlockPos pos);

    boolean allowsInterdimensionalTravel();
}
