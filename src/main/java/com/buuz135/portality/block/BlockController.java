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
package com.buuz135.portality.block;

import com.buuz135.portality.Portality;
import com.buuz135.portality.data.PortalDataManager;
import com.buuz135.portality.data.PortalInformation;
import com.buuz135.portality.proxy.CommonProxy;
import com.buuz135.portality.tile.TileController;
import com.hrznstudio.titanium.api.IFactory;
import com.hrznstudio.titanium.block.BlockRotation;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.UUID;

public class BlockController extends BlockRotation<TileController> {

    public BlockController() {
        super("controller", Block.Properties.create(Material.ROCK), TileController.class);
        setItemGroup(Portality.TAB);
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        PortalInformation information = new PortalInformation(UUID.randomUUID(), placer.getUniqueID(), false, false, worldIn.getDimension().getType().getId(), pos, "Dim: " + worldIn.getDimension().getType().getRegistryName() + " X: " + pos.getX() + " Y: " + pos.getY() + " Z: " + pos.getZ(), new ItemStack(CommonProxy.BLOCK_FRAME), false);
        PortalDataManager.addInformation(worldIn, information);
        super.onBlockPlacedBy(worldIn, pos, state, placer, stack);
    }

    @Override
    public void onPlayerDestroy(IWorld worldIn, BlockPos pos, BlockState state) {
        super.onPlayerDestroy(worldIn, pos, state);
        PortalDataManager.removeInformation(worldIn.getWorld(), pos);
    }

    @Override
    public void onExplosionDestroy(World worldIn, BlockPos pos, Explosion explosionIn) {
        super.onExplosionDestroy(worldIn, pos, explosionIn);
        PortalDataManager.removeInformation(worldIn, pos);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        return this.getDefaultState().with(FACING, context.getPlacementHorizontalFacing().getOpposite());
    }

    @Nonnull
    @Override
    public RotationType getRotationType() {
        return RotationType.FOUR_WAY;
    }

    @Override
    public boolean onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity playerIn, Hand hand, BlockRayTraceResult ray) {
        TileEntity tile = worldIn.getTileEntity(pos);
        if (worldIn.isRemote()) return true;
        if (tile instanceof TileController) {
            TileController controller = (TileController) tile;
            if (!controller.isFormed()) {
                playerIn.sendStatusMessage(new TranslationTextComponent(TextFormatting.RED + I18n.format("portality.controller.error.size")), true);
                return true;
            }
            if (controller.isPrivate() && !controller.getOwner().equals(playerIn.getUniqueID())) {
                playerIn.sendStatusMessage(new TranslationTextComponent(TextFormatting.RED + I18n.format("portality.controller.error.privacy")), true);
                return true;
            }
            if (playerIn.isSneaking() && controller.getOwner().equals(playerIn.getUniqueID()) && !playerIn.getHeldItem(hand).isEmpty() && !playerIn.getHeldItem(hand).isItemEqual(controller.getDisplay())) {
                playerIn.sendStatusMessage(new TranslationTextComponent(TextFormatting.GREEN + I18n.format("portility.controller.info.icon_changed")), true);
                controller.setDisplayNameEnabled(playerIn.getHeldItem(hand));
                return true;
            }
        }
        return super.onBlockActivated(state, worldIn, pos, playerIn, hand, ray);
    }

    @Override
    public void onReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        TileEntity entity = worldIn.getTileEntity(pos);
        if (entity instanceof TileController) {
            ((TileController) entity).breakController();
        }
        super.onReplaced(state, worldIn, pos, newState, isMoving);
    }

    @Override
    public IFactory<TileController> getTileEntityFactory() {
        return TileController::new;
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(IBlockReader worldIn) {
        return new TileController();
    }

    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT;
    }
}
