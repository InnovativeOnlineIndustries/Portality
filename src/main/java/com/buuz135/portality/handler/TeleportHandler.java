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
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        Direction facing = controller.getTeleportDirection();
        Direction right = controller.getPortalRight();
        Direction up = controller.getPortalUp();
        RandomSource random = controller.getLevel().random;
        BlockPos offset = controller.getBlockPos().relative(facing);
        double mult = controller.getLength() / 20D;
        double widthSpread = Math.max(1D, controller.getWidth() * 2D - 1D);
        double heightSpread = Math.max(1D, controller.getHeight() - 1D);
        Vec3 particle = Vec3.atCenterOf(offset)
                .add(Vec3.atLowerCornerOf(right.getNormal()).scale(random.nextDouble() * widthSpread - widthSpread / 2D))
                .add(Vec3.atLowerCornerOf(up.getNormal()).scale(0.5D + random.nextDouble() * heightSpread));
        controller.getLevel().addParticle(ParticleTypes.END_ROD, particle.x, particle.y, particle.z, facing.getNormal().getX() * mult, facing.getNormal().getY() * mult, facing.getNormal().getZ() * mult);
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
            double planeHeightOffset = controller.getFacing().getAxis().isVertical() ? controller.getHeight() / 2D : controller.getHeight() / 2D - 1.5;
            Vec3 destinationPos = Vec3.atCenterOf(controller.getBlockPos()).add(Vec3.atLowerCornerOf(up.getNormal()).scale(planeHeightOffset)).add(Vec3.atLowerCornerOf(facing.getNormal()).scale(3 - 1));
            if (controller.getFacing() == Direction.UP) {
                destinationPos = new Vec3(entry.getKey().getX(), controller.getBlockPos().getY() - controller.getLength() + 1, entry.getKey().getZ());
            }
            double distance = destinationPos.distanceTo(entry.getKey().position());
            Vec3 destination = destinationPos.subtract(entry.getKey().position()).scale((entry.getValue().time += 0.05) / distance);
            if (destinationPos.distanceTo(entry.getKey().position()) < 1.5) {
                if (!entry.getKey().level().isClientSide) {
                    if (controller.getEnergyStorage().getEnergyStored() >= PortalityConfig.TELEPORT_ENERGY_AMOUNT) {
                        Level tpWorld = entry.getKey().level().getServer().getLevel(entry.getValue().data.getDimension());
                        Direction tpFacing = Direction.NORTH;
                        ControllerTile targetController = null;
                        if (controller.getLinkData().isToken()){
                            tpFacing = Direction.byName(controller.getTeleportationTokens().get(controller.getLinkData().getName()).getString("Direction"));
                        } else {
                            BlockEntity blockEntity = tpWorld.getBlockEntity(entry.getValue().data.getPos());
                            if (blockEntity instanceof ControllerTile controllerTile) {
                                targetController = controllerTile;
                                tpFacing = controllerTile.getFacing();
                            }
                        }
                        Vec3 pos = getExitPosition(entry.getValue().data.getPos(), tpFacing, targetController);
                        if (controller.getFacing() == Direction.UP || controller.getFacing() == Direction.DOWN)
                            pos = pos.add(0, 0, -0.5);
                        Entity entity = TeleportationUtils.teleportEntity(entry.getKey(), entry.getValue().data.getDimension(), pos.x, pos.y, pos.z, tpFacing.toYRot(), getPortalPitch(tpFacing));
                        entitesTeleported.put(entity, new TeleportedEntityData(entry.getValue().data));
                        controller.getEnergyStorage().extractEnergy(PortalityConfig.TELEPORT_ENERGY_AMOUNT, false);
                        if (entry.getKey() instanceof ServerPlayer serverPlayer)
                            Portality.NETWORK.sendTo(new PortalTeleportMessage(tpFacing.get3DDataValue(), controller.getLength()), serverPlayer);
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
            if (controller.getFacing() != Direction.UP)
                entry.getKey().setDeltaMovement(destination.x, destination.y, destination.z);
        }
        for (Entity entity : entityRemove) {
            entityTimeToTeleport.remove(entity);
        }
        entityRemove.clear();
        for (Map.Entry<Entity, TeleportedEntityData> entry : entitesTeleported.entrySet()) {
            entry.getValue().ticks++;
            if (entry.getValue().ticks > 2 && !entry.getValue().moved) {
                if (!entry.getKey().level().isClientSide)
                    entry.getKey().level().getEntitiesOfClass(ServerPlayer.class, new AABB(entry.getKey().blockPosition()).inflate(16)).forEach(entityPlayer -> entityPlayer.playNotifySound(PortalitySoundHandler.PORTAL_TP.get(), SoundSource.BLOCKS, 0.5f, 1f));
                entry.getValue().moved = true;
                Level tpWorld = entry.getKey().level();
                Direction tpFacing = Direction.NORTH;
                if (controller.getLinkData().isToken()){
                    tpFacing = Direction.byName(controller.getTeleportationTokens().get(controller.getLinkData().getName()).getString("Direction"));
                } else if (tpWorld.getBlockState(entry.getValue().data.getPos()).getBlock() instanceof ControllerBlock){
                    BlockEntity blockEntity = tpWorld.getBlockEntity(entry.getValue().data.getPos());
                    if (blockEntity instanceof ControllerTile controllerTile) {
                        tpFacing = controllerTile.getFacing();
                    }
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

    private Direction getPortalUp(Direction facing) {
        if (facing.getAxis().isVertical()) {
            return facing == Direction.UP ? Direction.NORTH : Direction.SOUTH;
        }
        return Direction.UP;
    }

    private float getPortalPitch(Direction facing) {
        if (facing == Direction.UP) {
            return -90;
        }
        if (facing == Direction.DOWN) {
            return 90;
        }
        return 0;
    }

    private Vec3 getExitPosition(BlockPos pos, Direction facing, ControllerTile targetController) {
        Vec3 exit = Vec3.atCenterOf(pos).add(Vec3.atLowerCornerOf(facing.getNormal()).scale(2));
        if (targetController != null) {
            if (facing.getAxis().isVertical()) {
                return exit
                        .add(Vec3.atLowerCornerOf(targetController.getPortalUp().getNormal()).scale(targetController.getHeight() / 2D));
            }
            return exit.add(Vec3.atLowerCornerOf(targetController.getPortalUp().getNormal()).scale(1.5));
        }
        if (facing.getAxis().isHorizontal()) {
            return exit.add(Vec3.atLowerCornerOf(getPortalUp(facing).getNormal()).scale(1.5));
        }
        return exit;
    }
}
