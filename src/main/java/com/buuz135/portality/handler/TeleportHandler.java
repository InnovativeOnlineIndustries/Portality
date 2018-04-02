package com.buuz135.portality.handler;

import com.buuz135.portality.block.BlockController;
import com.buuz135.portality.data.PortalLinkData;
import com.buuz135.portality.tile.TileController;
import com.buuz135.portality.util.TeleportUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.*;

public class TeleportHandler {

    private HashMap<Entity, TeleportData> entityTimeToTeleport;
    private HashMap<Entity, TeleportedEntityData> entitesTeleported;
    private TileController controller;

    public TeleportHandler(TileController controller) {
        entityTimeToTeleport = new HashMap<>();
        entitesTeleported = new HashMap<>();
        this.controller = controller;
    }

    public void addEntityToTeleport(Entity entity, PortalLinkData data) {
        if (!entityTimeToTeleport.containsKey(entity)) entityTimeToTeleport.put(entity, new TeleportData(data));
    }

    public void tick() {
        EnumFacing facing = controller.getWorld().getBlockState(controller.getPos()).getValue(BlockController.FACING).getOpposite();
        Random random = controller.getWorld().rand;
        BlockPos offset = controller.getPos().offset(facing);
        double mult = controller.getLength() / 20D;
        controller.getWorld().spawnParticle(EnumParticleTypes.END_ROD, offset.getX() + 0.5 + random.nextDouble() * 2 - 1, offset.getY() + 2.5 + random.nextDouble() * 2 - 1, offset.getZ() + 0.5 + random.nextDouble() * 2 - 1, facing.getDirectionVec().getX() * mult, facing.getDirectionVec().getY() * mult, facing.getDirectionVec().getZ() * mult);
        List<Entity> entityRemove = new ArrayList<>();
        for (Map.Entry<Entity, TeleportData> entry : entityTimeToTeleport.entrySet()) {
            if (entry.getKey().isDead || !controller.getPortalArea().contains(new Vec3d(entry.getKey().getPosition()))) {
                entityRemove.add(entry.getKey());
                continue;
            }
            if (entry.getKey() instanceof EntityPlayer && entry.getKey().isSneaking()) {
                entityRemove.add(entry.getKey());
                continue;
            }
            BlockPos destinationPos = controller.getPos().offset(facing, controller.getLength() - 1);
            Vec3d destination = new Vec3d(destinationPos).addVector(0.5, 1.5, 0.5);
            double distance = destinationPos.add(0.5, 1.5, 0.5).getDistance(entry.getKey().getPosition().getX(), entry.getKey().getPosition().getY(), entry.getKey().getPosition().getZ());
            destination = destination.subtract(entry.getKey().posX, entry.getKey().posY, entry.getKey().posZ).scale((entry.getValue().time += 0.05) / distance);
            if (distance <= 0.1) {
                if (!entry.getKey().world.isRemote) {
                    if (controller.getEnergy().extractEnergyInternal(5000, true) == 5000) {
                        World tpWorld = entry.getKey().world;
                        EnumFacing tpFacing = tpWorld.getBlockState(entry.getValue().data.getPos()).getValue(BlockController.FACING);
                        BlockPos pos = entry.getValue().data.getPos().offset(tpFacing);
                        Entity entity = TeleportUtil.teleportEntity(entry.getKey(), entry.getValue().data.getDimension(), pos.getX() + 0.5, pos.getY() + 2, pos.getZ() + 0.5, tpFacing.getHorizontalAngle(), 0);
                        entitesTeleported.put(entity, new TeleportedEntityData(entry.getValue().data));
                        controller.getEnergy().extractEnergyInternal(5000, false);
                    } else {
                        //TODO Something bad
                    }
                }
                entityRemove.add(entry.getKey());
                continue;
            }
            entry.getKey().motionX = destination.x;
            entry.getKey().motionY = destination.y;
            entry.getKey().motionZ = destination.z;
        }
        for (Entity entity : entityRemove) {
            entityTimeToTeleport.remove(entity);
        }
        entityRemove.clear();
        for (Map.Entry<Entity, TeleportedEntityData> entry : entitesTeleported.entrySet()) {
            entry.getValue().ticks++;
            if (entry.getValue().ticks > 2 && !entry.getValue().moved) {
                if (entry.getKey() instanceof EntityPlayer)
                    entry.getKey().world.playSound((EntityPlayer) entry.getKey(), entry.getKey().getPosition(), new SoundEvent(new ResourceLocation("block.portal.travel")), SoundCategory.AMBIENT, 1, 1);
                entry.getValue().moved = true;
                World tpWorld = entry.getKey().world;
                EnumFacing tpFacing = tpWorld.getBlockState(entry.getValue().data.getPos()).getValue(BlockController.FACING);
                entry.getKey().motionX = tpFacing.getDirectionVec().getX();
                entry.getKey().motionY = tpFacing.getDirectionVec().getY();
                entry.getKey().motionZ = tpFacing.getDirectionVec().getZ();
                entry.getKey().setRotationYawHead(tpFacing.getHorizontalAngle());
            }
            if (entry.getValue().ticks > 40) {
                entityRemove.add(entry.getKey());
            }
        }
        for (Entity entity : entityRemove) {
            entitesTeleported.remove(entity);
        }
    }

    private class TeleportData {
        private PortalLinkData data;
        private double time;

        public TeleportData(PortalLinkData data) {
            this.data = data;
            this.time = 0;
        }
    }

    private class TeleportedEntityData {

        private int ticks;
        private boolean moved;
        private PortalLinkData data;

        public TeleportedEntityData(PortalLinkData data) {
            this.data = data;
            this.ticks = 0;
            this.moved = false;
        }
    }
}
