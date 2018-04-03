package com.buuz135.portality.block.module;

import com.buuz135.portality.tile.TileEntityItemModule;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

import java.util.List;

public class BlockCapabilityItemModule extends BlockCapabilityModule<IItemHandler, TileEntityItemModule> {

    public BlockCapabilityItemModule() {
        super("module_items", TileEntityItemModule.class);
    }

    @Override
    public Capability<IItemHandler> getCapability() {
        return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY;
    }

    @Override
    void internalWork(World current, BlockPos myself, World otherWorld, List<BlockPos> compatibleBlockPos) {
        if (current.getTileEntity(myself).hasCapability(this.getCapability(), null)) {
            IItemHandler handlerSelf = current.getTileEntity(myself).getCapability(this.getCapability(), null);
            for (BlockPos otherPos : compatibleBlockPos) {
                TileEntity otherTile = otherWorld.getTileEntity(otherPos);
                if (otherTile != null && otherTile.hasCapability(this.getCapability(), null)) {
                    IItemHandler handlerOther = otherTile.getCapability(this.getCapability(), null);
                    for (int i = 0; i < handlerSelf.getSlots(); i++) {
                        ItemStack stack = handlerSelf.getStackInSlot(i);
                        if (stack.isEmpty()) continue;
                        if (ItemHandlerHelper.insertItem(handlerOther, stack, true).isEmpty()) {
                            ItemHandlerHelper.insertItem(handlerOther, stack.copy(), false);
                            handlerSelf.getStackInSlot(i).setCount(0);
                            return;
                        }
                    }
                }
            }
        }

    }
}
