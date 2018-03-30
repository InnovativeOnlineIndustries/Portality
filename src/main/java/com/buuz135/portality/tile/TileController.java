package com.buuz135.portality.tile;

import com.buuz135.portality.block.BlockController;
import com.buuz135.portality.block.BlockFrame;
import com.buuz135.portality.util.BlockPosUtils;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

import java.util.List;

public class TileController extends TileBase implements ITickable {

    private static String NBT_FORMED = "Formed";
    private static String NBT_TICK = "Tick";
    private static String NBT_LENGTH = "Length";

    private boolean isFormed = false;
    private int tick = 0;
    private int length = 0;

    @Override
    public void update() {
        if (world.isRemote) return;
        ++tick;
        if (tick >= 10) {
            tick = 0;

            length = checkArea();
            markForUpdate();
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound = super.writeToNBT(compound);
        compound.setBoolean(NBT_FORMED, isFormed);
        compound.setInteger(NBT_TICK, tick);
        compound.setInteger(NBT_LENGTH, length);
        return compound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        isFormed = compound.getBoolean(NBT_FORMED);
        tick = compound.getInteger(NBT_TICK);
        length = compound.getInteger(NBT_LENGTH);
        super.readFromNBT(compound);
    }

    private int checkArea() {
        BlockPos center = this.pos.offset(EnumFacing.UP, 2);
        EnumFacing facing = world.getBlockState(this.pos).getValue(BlockController.FACING);
        boolean isSouthNorth = facing == EnumFacing.NORTH || facing == EnumFacing.SOUTH;
        int length = 0;
        while (true) {
            AxisAlignedBB box = new AxisAlignedBB(center.getX() + (isSouthNorth ? 2 : 0), center.getY() + 2, center.getZ() + (!isSouthNorth ? 2 : 0),
                    center.getX() + (isSouthNorth ? -2 : 0), center.getY() - 2, center.getZ() + (!isSouthNorth ? -2 : 0));
            List<BlockPos> blocks = BlockPosUtils.getBlockPosInAABB(box);
            BlockPos finalCenter = center;
            blocks.removeIf(pos1 -> BlockPosUtils.getBlockPosInAABB(new AxisAlignedBB(finalCenter.getX() + (isSouthNorth ? 1 : 0), finalCenter.getY() + 1, finalCenter.getZ() + (!isSouthNorth ? 1 : 0),
                    finalCenter.getX() + (isSouthNorth ? -1 : 0), finalCenter.getY() - 1, finalCenter.getZ() + (!isSouthNorth ? -1 : 0))).contains(pos1));
            for (BlockPos pos : blocks) {
                if (length == 0 && (pos.getX() == center.getX() && pos.getY() == center.getY() - 2 && pos.getZ() == center.getZ()) && this.world.getBlockState(pos).getBlock() instanceof BlockController)
                    continue;
                if (this.world.getBlockState(pos).getBlock() instanceof BlockFrame) continue;
                return length;
            }
            ++length;
            if (length > 16) return length;
            center = center.offset(facing.getOpposite());
        }
    }


}
