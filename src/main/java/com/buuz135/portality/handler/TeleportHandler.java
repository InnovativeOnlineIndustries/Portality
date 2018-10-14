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
package com.buuz135.portality.handler;

import com.buuz135.portality.Portality;
import com.buuz135.portality.block.BlockController;
import com.buuz135.portality.data.PortalLinkData;
import com.buuz135.portality.network.PortalTeleportMessage;
import com.buuz135.portality.proxy.PortalityConfig;
import com.buuz135.portality.proxy.PortalitySoundHandler;
import com.buuz135.portality.tile.TileController;
import com.buuz135.portality.util.TeleportUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.MobEffects;
import net.minecraft.network.play.server.SPacketCustomSound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
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
        if (!(controller.getWorld().getBlockState(controller.getPos()).getBlock() instanceof BlockController)) {
            controller.closeLink();
            return;
        }
        EnumFacing facing = controller.getWorld().getBlockState(controller.getPos()).getValue(BlockController.FACING).getOpposite();
        Random random = controller.getWorld().rand;
        BlockPos offset = controller.getPos().offset(facing);
        double mult = controller.getLength() / 20D;
        controller.getWorld().spawnParticle(EnumParticleTypes.END_ROD, offset.getX() + 0.5 + random.nextDouble() * (controller.getWidth() + 2) - (controller.getWidth() + 2) / 2D, offset.getY() + controller.getHeight() / 2D + random.nextDouble() * (controller.getHeight() - 2) - (controller.getHeight() - 2) / 2D, offset.getZ() + 0.5 + random.nextDouble() * 2 - 1, facing.getDirectionVec().getX() * mult, facing.getDirectionVec().getY() * mult, facing.getDirectionVec().getZ() * mult);
        List<Entity> entityRemove = new ArrayList<>();
        for (Map.Entry<Entity, TeleportData> entry : entityTimeToTeleport.entrySet()) {
            if (entry.getKey().isDead || !controller.getWorld().getEntitiesWithinAABB(Entity.class, controller.getPortalArea()).contains(entry.getKey())) {
                entityRemove.add(entry.getKey());
                continue;
            }
            if (entry.getKey() instanceof EntityPlayer && entry.getKey().isSneaking()) {
                entityRemove.add(entry.getKey());
                continue;
            }
            BlockPos destinationPos = controller.getPos().add(0, controller.getHeight() / 2, 0).offset(facing, controller.getLength() - 1);
            Vec3d destination = new Vec3d(destinationPos).addVector(0.5, 0, 0.5);
            double distance = destinationPos.getDistance(entry.getKey().getPosition().getX(), entry.getKey().getPosition().getY(), entry.getKey().getPosition().getZ());
            destination = destination.subtract(entry.getKey().posX, entry.getKey().posY, entry.getKey().posZ).scale((entry.getValue().time += 0.05) / distance);
            System.out.println(distance);
            if (distance <= 1.5) {
                if (!entry.getKey().world.isRemote) {
                    if (controller.getEnergy().extractEnergyInternal(PortalityConfig.TELEPORT_ENERGY_AMOUNT, true) == PortalityConfig.TELEPORT_ENERGY_AMOUNT) {
                        World tpWorld = entry.getKey().world.getMinecraftServer().getWorld(entry.getValue().data.getDimension());
                        EnumFacing tpFacing = tpWorld.getBlockState(entry.getValue().data.getPos()).getValue(BlockController.FACING);
                        BlockPos pos = entry.getValue().data.getPos().offset(tpFacing);
                        Entity entity = TeleportUtil.teleportEntity(entry.getKey(), entry.getValue().data.getDimension(), pos.getX() + 0.5, pos.getY() + 2, pos.getZ() + 0.5, tpFacing.getHorizontalAngle(), 0);
                        entitesTeleported.put(entity, new TeleportedEntityData(entry.getValue().data));
                        controller.getEnergy().extractEnergyInternal(PortalityConfig.TELEPORT_ENERGY_AMOUNT, false);
                        if (entry.getKey() instanceof EntityPlayerMP)
                            Portality.NETWORK.sendTo(new PortalTeleportMessage(tpFacing.getIndex(), controller.getLength()), (EntityPlayerMP) entry.getKey());
                        if (controller.teleportedEntity()) {
                            return;
                        }
                    } else {
                        if (entry.getKey() instanceof EntityLivingBase && PortalityConfig.HURT_PLAYERS) {
                            ((EntityLivingBase) entry.getKey()).addPotionEffect(new PotionEffect(MobEffects.WITHER, 5 * 20, 1));
                        }
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
                if (entry.getKey().world.isRemote)
                    entry.getKey().world.getEntitiesWithinAABB(EntityPlayerMP.class, new AxisAlignedBB(entry.getKey().posX, entry.getKey().posY, entry.getKey().posZ, entry.getKey().posX, entry.getKey().posY, entry.getKey().posZ).grow(16)).forEach(entityPlayer -> entityPlayer.connection.sendPacket(new SPacketCustomSound(PortalitySoundHandler.PORTAL_TP.getSoundName().toString(), SoundCategory.BLOCKS, entry.getKey().posX, entry.getKey().posY, entry.getKey().posZ, 0.5f, 1f)));
                entry.getValue().moved = true;
                World tpWorld = entry.getKey().world;
                if (tpWorld.getBlockState(entry.getValue().data.getPos()).getBlock() instanceof BlockController) {
                    EnumFacing tpFacing = tpWorld.getBlockState(entry.getValue().data.getPos()).getValue(BlockController.FACING);
                    Vec3d vec3d = new Vec3d(tpFacing.getDirectionVec()).scale(2 * controller.getLength() / (double) PortalityConfig.MAX_PORTAL_LENGTH);
                    entry.getKey().motionX = vec3d.x;
                    entry.getKey().motionY = vec3d.y;
                    entry.getKey().motionZ = vec3d.z;
                    entry.getKey().setRotationYawHead(tpFacing.getHorizontalAngle());
                }
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
