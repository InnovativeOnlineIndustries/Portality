/*
 * This file is part of Worldgen Indicators.
 *
 * Copyright 2018, Buuz135
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in the
 * Software without restriction, including without limitation the rights to use, copy,
 * modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the
 * following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies
 * or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.buuz135.portality.block.module;

import com.buuz135.portality.block.BlockFrame;
import com.buuz135.portality.tile.TileController;
import com.buuz135.portality.tile.TileFrame;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;
import java.util.stream.Collectors;

public abstract class BlockCapabilityModule<T, S extends TileFrame> extends BlockFrame<S> implements IPortalModule {

    public static PropertyBool INPUT = PropertyBool.create("input");


    public BlockCapabilityModule(String name, Class<S> tileClass) {
        super(name, tileClass);
        this.setDefaultState(this.blockState.getBaseState().withProperty(INPUT, true));

    }

    @Override
    public void work(TileController controller, BlockPos blockPos) {
        if (controller.getLinkData() == null) return;
        TileEntity other = controller.getWorld().getMinecraftServer().getWorld(controller.getLinkData().getDimension()).getTileEntity(controller.getLinkData().getPos());
        if (other instanceof TileController && this.isInput(controller.getWorld().getBlockState(blockPos))) {
            TileController otherController = (TileController) other;
            internalWork(controller.getWorld(), blockPos, other.getWorld(), otherController.getModules().stream().filter(pos -> otherController.getWorld().getBlockState(pos).getBlock() instanceof BlockCapabilityModule
                    && !((BlockCapabilityModule) otherController.getWorld().getBlockState(pos).getBlock()).isInput(otherController.getWorld().getBlockState(pos))
                    && ((BlockCapabilityModule) otherController.getWorld().getBlockState(pos).getBlock()).getCapability().equals(this.getCapability())).collect(Collectors.toList()));
        }
    }

    public abstract Capability<T> getCapability();

    public boolean isInput(IBlockState state) {
        return state.getValue(INPUT);
    }

    abstract void internalWork(World current, BlockPos myself, World otherWorld, List<BlockPos> compatibleBlockPos);

    @Override
    public boolean allowsInterdimensionalTravel() {
        return false;
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        return false;
    }

    public IBlockState getStateFromMeta(int meta) {
        return meta == 0 ? this.getDefaultState().withProperty(INPUT, true) : this.getDefaultState().withProperty(INPUT, false);
    }

    public int getMetaFromState(IBlockState state) {
        return state.getValue(INPUT) ? 0 : 1;
    }

    @Override
    public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> items) {
        items.add(new ItemStack(this, 1, 0));
        items.add(new ItemStack(this, 1, 1));
    }

    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, INPUT);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerRender() {
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, new ModelResourceLocation(this.getRegistryName(), "input=true"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 1, new ModelResourceLocation(this.getRegistryName(), "input=false"));
    }

    @Override
    public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
        return new ItemStack(this, 1, this.getMetaFromState(state));
    }
}
