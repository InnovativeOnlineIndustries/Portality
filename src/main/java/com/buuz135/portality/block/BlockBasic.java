package com.buuz135.portality.block;

import com.buuz135.portality.Portality;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.ArrayList;
import java.util.List;

public class BlockBasic extends Block {

    public static final List<BlockBasic> BLOCKS = new ArrayList<>();

    public BlockBasic(String name, Material materialIn) {
        super(materialIn);
        this.setRegistryName(new ResourceLocation(Portality.MOD_ID, name));
        this.setUnlocalizedName(getRegistryName().toString());
        this.setCreativeTab(Portality.TAB);
        BLOCKS.add(this);
    }

    public void registerObject(IForgeRegistry<Block> registry) {
        registry.register(this);
    }

    public void registerRender() {
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, new ModelResourceLocation(this.getRegistryName(), "inventory"));
    }
}
