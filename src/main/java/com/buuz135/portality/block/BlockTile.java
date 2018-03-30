package com.buuz135.portality.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.IForgeRegistry;

import javax.annotation.Nullable;

public class BlockTile<T extends TileEntity> extends BlockBasic {

    private final Class<T> tileClass;

    public BlockTile(String name, Class<T> tileClass, Material materialIn) {
        super(name, materialIn);
        this.tileClass = tileClass;
    }

    @Override
    public void registerObject(IForgeRegistry<Block> registry) {
        super.registerObject(registry);
        GameRegistry.registerTileEntity(tileClass, this.getRegistryName().toString() + "_tile");
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        try {
            return tileClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        return true;
    }
}
