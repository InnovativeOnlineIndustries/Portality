package com.buuz135.portality.util;

import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;

public class BlockPosUtils {

    public static List<BlockPos> getBlockPosInAABB(AxisAlignedBB axisAlignedBB) {
        List<BlockPos> blocks = new ArrayList<BlockPos>();
        for (double y = axisAlignedBB.minY; y <= axisAlignedBB.maxY; ++y) {
            for (double x = axisAlignedBB.minX; x <= axisAlignedBB.maxX; ++x) {
                for (double z = axisAlignedBB.minZ; z <= axisAlignedBB.maxZ; ++z) {
                    blocks.add(new BlockPos(x, y, z));
                }
            }
        }
        return blocks;
    }

    public static int getMaxDistance(int length) {
        return length * 200;
    }
}
