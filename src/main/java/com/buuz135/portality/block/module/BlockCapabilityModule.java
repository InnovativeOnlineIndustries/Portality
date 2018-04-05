package com.buuz135.portality.block.module;

import com.buuz135.portality.block.BlockTile;
import com.buuz135.portality.tile.TileController;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
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

import javax.annotation.Nullable;
import java.util.List;
import java.util.stream.Collectors;

public abstract class BlockCapabilityModule<T, S extends TileEntity> extends BlockTile<S> implements IPortalModule {

    public static PropertyBool INPUT = PropertyBool.create("input");


    public BlockCapabilityModule(String name, Class<S> tileClass) {
        super(name, tileClass, Material.ROCK, 0);
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

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        tooltip.add(stack.getMetadata() == 0 ? I18n.format("module.type.input") : I18n.format("module.type.output"));
    }

    @Override
    public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
        return new ItemStack(this, 1, this.getMetaFromState(state));
    }
}
