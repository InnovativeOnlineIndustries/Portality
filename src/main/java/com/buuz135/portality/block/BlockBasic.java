package com.buuz135.portality.block;

import com.buuz135.portality.Portality;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IForgeRegistry;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class BlockBasic extends Block {

    public static final List<BlockBasic> BLOCKS = new ArrayList<>();

    public BlockBasic(String name, Material materialIn) {
        super(materialIn);
        this.setRegistryName(new ResourceLocation(Portality.MOD_ID, name));
        this.setUnlocalizedName(getRegistryName().toString());
        this.setCreativeTab(Portality.TAB);
        this.setHardness(1.5F);
        this.setResistance(10.0F);
        BLOCKS.add(this);
    }

    public void registerObject(IForgeRegistry<Block> registry) {
        registry.register(this);
    }

    @SideOnly(Side.CLIENT)
    public void registerRender() {
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, new ModelResourceLocation(this.getRegistryName(), "inventory"));
    }

    @Nullable
    @Override
    public String getHarvestTool(IBlockState state) {
        return "pickaxe";
    }

    @Override
    public int getHarvestLevel(IBlockState state) {
        return 1;
    }

    @Override
    public BlockRenderLayer getBlockLayer() {
        return BlockRenderLayer.CUTOUT;
    }
}
