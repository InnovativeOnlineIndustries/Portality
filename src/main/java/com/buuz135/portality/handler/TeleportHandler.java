/**
 * MIT License
 *
 * Copyright (c) 2018
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.buuz135.portality.handler;

import com.buuz135.portality.Portality;
import com.buuz135.portality.block.ControllerBlock;
import com.buuz135.portality.data.PortalLinkData;
import com.buuz135.portality.network.PortalTeleportMessage;
import com.buuz135.portality.proxy.PortalityConfig;
import com.buuz135.portality.proxy.PortalitySoundHandler;
import com.buuz135.portality.tile.ControllerTile;
import com.hrznstudio.titanium.util.TeleportationUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.game.ClientboundCustomSoundPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkDirection;

import java.util.*;

public class TeleportHandler {

    private HashMap<Entity, TeleportData> entityTimeToTeleport;
    private HashMap<Entity, TeleportedEntityData> entitesTeleported;
    private ControllerTile controller;

    public TeleportHandler(ControllerTile controller) {
        entityTimeToTeleport = new HashMap<>();
        entitesTeleported = new HashMap<>();
        this.controller = controller;
    }

    public void addEntityToTeleport(Entity entity, PortalLinkData data) {
        if (!entityTimeToTeleport.containsKey(entity)) entityTimeToTeleport.put(entity, new TeleportData(data));
    }

    public void tick() {
        if (!(controller.getLevel().getBlockState(controller.getBlockPos()).getBlock() instanceof ControllerBlock)) {
            controller.closeLink();
            return;
        }
        Direction facing = controller.getLevel().getBlockState(controller.getBlockPos()).getValue(ControllerBlock.FACING_HORIZONTAL).getOpposite();
        Random random = controller.getLevel().random;
        BlockPos offset = controller.getBlockPos().relative(facing);
        double mult = controller.getLength() / 20D;
        controller.getLevel().addParticle(ParticleTypes.END_ROD, offset.getX() + 0.5 + random.nextDouble() * (controller.getWidth() + 2) - (controller.getWidth() + 2) / 2D, offset.getY() + controller.getHeight() / 2D + random.nextDouble() * (controller.getHeight() - 2) - (controller.getHeight() - 2) / 2D, offset.getZ() + 0.5 + random.nextDouble() * 2 - 1, facing.getNormal().getX() * mult, facing.getNormal().getY() * mult, facing.getNormal().getZ() * mult);
        List<Entity> entityRemove = new ArrayList<>();
        for (Map.Entry<Entity, TeleportData> entry : entityTimeToTeleport.entrySet()) {
            if (!entry.getKey().isAlive() || !controller.getLevel().getEntitiesOfClass(Entity.class, controller.getPortalArea()).contains(entry.getKey())) {
                entityRemove.add(entry.getKey());
                continue;
            }
            if (entry.getKey() instanceof Player && entry.getKey().isCrouching()) {
                entityRemove.add(entry.getKey());
                continue;
            }
            BlockPos destinationPos = controller.getBlockPos().offset(0.5, controller.getHeight() / 2D - 0.75, 0.5).relative(facing, controller.getLength() - 1);
            Vec3 destination = new Vec3(destinationPos.getX(), destinationPos.getY(), destinationPos.getZ()).add(0.5, 0, 0.5);
            double distance = destinationPos.distManhattan(new Vec3i(entry.getKey().blockPosition().getX(), entry.getKey().blockPosition().getY(), entry.getKey().blockPosition().getZ()));
            destination = destination.subtract(entry.getKey().blockPosition().getX(), entry.getKey().blockPosition().getY(), entry.getKey().blockPosition().getZ()).scale((entry.getValue().time += 0.05) / distance);
            if (destinationPos.closerThan(new Vec3i(entry.getKey().blockPosition().getX(), entry.getKey().blockPosition().getY(), entry.getKey().blockPosition().getZ()), 1.5)) {
                if (!entry.getKey().level.isClientSide) {
                    if (controller.getEnergyStorage().getEnergyStored() >= PortalityConfig.TELEPORT_ENERGY_AMOUNT) {
                        Level tpWorld = entry.getKey().level.getServer().getLevel(entry.getValue().data.getDimension());
                        Direction tpFacing = Direction.NORTH;
                        if (controller.getLinkData().isToken()){
                            tpFacing = Direction.byName(controller.getTeleportationTokens().get(controller.getLinkData().getName()).getString("Direction"));
                        } else {
                            tpFacing = tpWorld.getBlockState(entry.getValue().data.getPos()).getValue(ControllerBlock.FACING_HORIZONTAL);
                        }
                        BlockPos pos = entry.getValue().data.getPos().relative(tpFacing, 2);
                        Entity entity = TeleportationUtils.teleportEntity(entry.getKey(), entry.getValue().data.getDimension(), pos.getX() + 0.5, pos.getY() + 2, pos.getZ() + 0.5, tpFacing.toYRot(), 0);
                        entitesTeleported.put(entity, new TeleportedEntityData(entry.getValue().data));
                        controller.getEnergyStorage().extractEnergy(PortalityConfig.TELEPORT_ENERGY_AMOUNT, false);
                        if (entry.getKey() instanceof ServerPlayer)
                            Portality.NETWORK.get().sendTo(new PortalTeleportMessage(tpFacing.get3DDataValue(), controller.getLength()), ((ServerPlayer) entry.getKey()).connection.connection, NetworkDirection.PLAY_TO_CLIENT);
                        if (controller.teleportedEntity()) {
                            return;
                        }
                    } else {
                        if (entry.getKey() instanceof LivingEntity && PortalityConfig.HURT_PLAYERS) {
                            ((LivingEntity) entry.getKey()).addEffect(new MobEffectInstance(MobEffects.WITHER, 5 * 20, 1));
                        }
                    }
                }
                entityRemove.add(entry.getKey());
                continue;
            }
            entry.getKey().setDeltaMovement(destination.x, destination.y, destination.z);
        }
        for (Entity entity : entityRemove) {
            entityTimeToTeleport.remove(entity);
        }
        entityRemove.clear();
        for (Map.Entry<Entity, TeleportedEntityData> entry : entitesTeleported.entrySet()) {
            entry.getValue().ticks++;
            if (entry.getValue().ticks > 2 && !entry.getValue().moved) {
                if (entry.getKey().level.isClientSide)
                    entry.getKey().level.getEntitiesOfClass(ServerPlayer.class, new AABB(entry.getKey().blockPosition().getX(), entry.getKey().blockPosition().getY(), entry.getKey().blockPosition().getZ(), entry.getKey().blockPosition().getX(), entry.getKey().blockPosition().getY(), entry.getKey().blockPosition().getZ()).inflate(16)).forEach(entityPlayer -> entityPlayer.connection.send(new ClientboundCustomSoundPacket(PortalitySoundHandler.PORTAL_TP.get().getRegistryName(), SoundSource.BLOCKS, new Vec3(entry.getKey().blockPosition().getX(), entry.getKey().blockPosition().getY(), entry.getKey().blockPosition().getZ()), 0.5f, 1f)));
                entry.getValue().moved = true;
                Level tpWorld = entry.getKey().level;
                Direction tpFacing = Direction.NORTH;
                if (controller.getLinkData().isToken()){
                    tpFacing = Direction.byName(controller.getTeleportationTokens().get(controller.getLinkData().getName()).getString("Direction"));
                } else if (tpWorld.getBlockState(entry.getValue().data.getPos()).getBlock() instanceof ControllerBlock){
                    tpFacing = tpWorld.getBlockState(entry.getValue().data.getPos()).getValue(ControllerBlock.FACING_HORIZONTAL);
                }
                Vec3 vec3d = new Vec3(tpFacing.getNormal().getX(), tpFacing.getNormal().getY(), tpFacing.getNormal().getZ()).scale(2 * controller.getLength() / (double) PortalityConfig.MAX_PORTAL_LENGTH);
                entry.getKey().setDeltaMovement(vec3d.x, vec3d.y, vec3d.z);
                entry.getKey().setYHeadRot(tpFacing.toYRot());
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
