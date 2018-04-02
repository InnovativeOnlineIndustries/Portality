package com.buuz135.portality.block.module;

import com.buuz135.portality.tile.TileController;

public interface IPortalModule {

    void work(TileController controller);

    boolean allowsInterdimensionalTravel();
}
