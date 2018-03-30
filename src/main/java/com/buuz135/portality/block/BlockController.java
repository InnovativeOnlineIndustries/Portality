package com.buuz135.portality.block;

import com.buuz135.portality.proxy.client.render.TESRPortal;
import com.buuz135.portality.tile.TileController;
import net.minecraft.block.material.Material;
import net.minecraftforge.fml.client.registry.ClientRegistry;

public class BlockController extends BlockTileHorizontal<TileController> {

    public BlockController() {
        super("controller", TileController.class, Material.ROCK);
    }

    @Override
    public void registerRender() {
        super.registerRender();
        ClientRegistry.bindTileEntitySpecialRenderer(TileController.class, new TESRPortal());
    }
}
