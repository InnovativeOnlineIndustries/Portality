package com.buuz135.portality.block;

import com.buuz135.portality.block.module.IPortalModule;
import com.buuz135.portality.tile.TileController;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockInterdimensionalModule extends BlockBasic implements IPortalModule {

    public BlockInterdimensionalModule() {
        super("module_interdimensional", Material.ROCK);
    }

    @Override
    public void work(TileController controller, BlockPos pos) {

    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerRender() {
        super.registerRender();
    }

    @Override
    public boolean allowsInterdimensionalTravel() {
        return true;
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        return false;
    }
}
