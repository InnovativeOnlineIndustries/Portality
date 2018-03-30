package com.buuz135.portality.block;

import com.buuz135.litterboxlib.api.IRegisterable;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.registries.IForgeRegistry;

public class BlockBasic extends Block implements IRegisterable<Block> {

    public BlockBasic(ResourceLocation resourceLocation, Material materialIn) {
        super(materialIn);
        this.setRegistryName(resourceLocation);
        this.setUnlocalizedName(resourceLocation.toString());
    }

    @Override
    public void registerObject(IForgeRegistry<Block> registry) {
        registry.register(this);
    }

    @Override
    public void registerRender() {
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, new ModelResourceLocation(this.getRegistryName(), "inventory"));
    }
}
