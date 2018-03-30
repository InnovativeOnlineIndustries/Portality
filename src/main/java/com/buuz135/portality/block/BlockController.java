package com.buuz135.portality.block;

import com.buuz135.portality.tile.TileController;
import net.minecraft.block.material.Material;

public class BlockController extends BlockTileHorizontal<TileController> {

    public BlockController() {
        super("controller", TileController.class, Material.ROCK);
    }
}
