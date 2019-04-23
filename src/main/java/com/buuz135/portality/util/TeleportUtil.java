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
package com.buuz135.portality.util;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.common.util.ITeleporter;

/**
 * Created by brandon3055.
 * <p>
 * Using the Dont Be A Jerk License that allows me to use parts of code
 * <p>
 * https://github.com/brandon3055/BrandonsCore/blob/master/src/main/java/com/brandon3055/brandonscore/lib/TeleportUtils.java
 */
public class TeleportUtil {

    /**
     * Universal method for teleporting entities of all shapes and sizes!
     * This method will teleport an entity and any entity it is riding or that are ring it recursively. If riding the riding entity will be re mounted on the other side.
     * <p>
     * Note: When teleporting riding entities it is the rider that must be teleported and the mount will follow automatically.
     * As long as you teleport the rider you should not need wo worry about the mount.
     *
     * @return the entity. This may be a new instance so be sure to keep that in mind.
     */
    public static Entity teleportEntity(Entity entity, DimensionType dimension, BlockPos pos, float yaw, float pitch) {
        return entity.changeDimension(dimension, new PortalTeleporter(pos));
//        if (entity == null || entity.world.isRemote) {
//            return entity;
//        }
//
//        MinecraftServer server = entity.getServer();
//        int sourceDim = entity.world.provider.getDimension();
//
//        if (!entity.isBeingRidden() && !entity.isRiding()) {
//            return handleEntityTeleport(entity, server, sourceDim, dimension, xCoord, yCoord, zCoord, yaw, pitch);
//        }
//
//        Entity rootEntity = entity.getLowestRidingEntity();
//        PassengerHelper passengerHelper = new PassengerHelper(rootEntity);
//        PassengerHelper rider = passengerHelper.getPassenger(entity);
//        if (rider == null) {
//            return entity;
//        }
//        passengerHelper.teleport(server, sourceDim, dimension, xCoord, yCoord, zCoord, yaw, pitch);
//        passengerHelper.remountRiders();
//        passengerHelper.updateClients();
//
//        return rider.entity;
    }

    public static class PortalTeleporter implements ITeleporter {

        private final BlockPos targetPos;

        private PortalTeleporter(BlockPos targetPos) {
            this.targetPos = targetPos;
        }

        @Override
        public void placeEntity(World world, Entity entity, float yaw) {
            entity.moveToBlockPosAndAngles(targetPos, yaw, entity.rotationPitch);
        }
    }
//
//    /**
//     * Convenience method that does not require pitch and yaw.
//     */
//    public static Entity teleportEntity(Entity entity, int dimension, double xCoord, double yCoord, double zCoord) {
//        return teleportEntity(entity, dimension, xCoord, yCoord, zCoord, entity.rotationYaw, entity.rotationPitch);
}

/**
 * This is the base teleport method that figures out how to handle the teleport and makes it happen!
 */
//    private static Entity handleEntityTeleport(Entity entity, MinecraftServer server, int sourceDim, int targetDim, double xCoord, double yCoord, double zCoord, float yaw, float pitch) {
//        if (entity == null || entity.world.isRemote) {
//            return entity;
//        }
//
//        boolean interDimensional = sourceDim != targetDim;
//
//        if (interDimensional && !ForgeHooks.onTravelToDimension(entity, targetDim)) {
//            return entity;
//        }
//
//        if (interDimensional) {
//            if (entity instanceof EntityPlayerMP) {
//                return teleportPlayerInterdimentional((EntityPlayerMP) entity, server, sourceDim, targetDim, xCoord, yCoord, zCoord, yaw, pitch);
//            } else {
//                return teleportEntityInterdimentional(entity, server, sourceDim, targetDim, xCoord, yCoord, zCoord, yaw, pitch);
//            }
//        } else {
//            if (entity instanceof EntityPlayerMP) {
//                EntityPlayerMP player = (EntityPlayerMP) entity;
//                player.connection.setPlayerLocation(xCoord, yCoord, zCoord, yaw, pitch);
//                player.setRotationYawHead(yaw);
//            } else {
//                entity.setLocationAndAngles(xCoord, yCoord, zCoord, yaw, pitch);
//                entity.setRotationYawHead(yaw);
//            }
//        }
//
//        return entity;
//    }
//
//    private static Entity teleportEntityInterdimentional(Entity entity, MinecraftServer server, DimensionType sourceDim, DimensionType targetDim, double xCoord, double yCoord, double zCoord, float yaw, float pitch) {
//        if (!entity.isAlive()) {
//            return null;
//        }
//
//        WorldServer sourceWorld = server.getWorld(sourceDim);
//        WorldServer targetWorld = server.getWorld(targetDim);
//
//        //Set the entity dead before calling changeDimension. Still need to call changeDimension for things like minecarts which will drop their contents otherwise.
//        if (entity.isAlive() && entity instanceof EntityMinecart) {
//            entity.removed = true;
//            entity.changeDimension(targetDim, targetDim);
//            entity.isDead = false;
//        }
//
//        entity.dimension = targetDim;
//
//        sourceWorld.removeEntity(entity);
//        entity.isDead = false;
//        entity.setLocationAndAngles(xCoord, yCoord, zCoord, yaw, pitch);
//        sourceWorld.updateEntityWithOptionalForce(entity, false);
//
//        Entity newEntity = EntityList.newEntity(entity.getClass(), targetWorld);
//        if (newEntity != null) {
//            newEntity.copyDataFromOld(entity);
//            newEntity.setLocationAndAngles(xCoord, yCoord, zCoord, yaw, pitch);
//            boolean flag = newEntity.forceSpawn;
//            newEntity.forceSpawn = true;
//            targetWorld.spawnEntity(newEntity);
//            newEntity.forceSpawn = flag;
//            targetWorld.updateEntityWithOptionalForce(newEntity, false);
//        }
//
//        entity.isDead = true;
//        sourceWorld.resetUpdateEntityTick();
//        targetWorld.resetUpdateEntityTick();
//
//        return newEntity;
//    }
//
//    /**
//     * This is the black magic responsible for teleporting players between dimensions!
//     */
//    private static EntityPlayer teleportPlayerInterdimentional(EntityPlayerMP player, MinecraftServer server, int sourceDim, int targetDim, double xCoord, double yCoord, double zCoord, float yaw, float pitch) {
//        WorldServer sourceWorld = server.getWorld(sourceDim);
//        WorldServer targetWorld = server.getWorld(targetDim);
//        PlayerList playerList = server.getPlayerList();
//
//        player.dimension = targetDim;
//        player.connection.sendPacket(new SPacketRespawn(player.dimension, targetWorld.getDifficulty(), targetWorld.getWorldInfo().getTerrainType(), player.interactionManager.getGameType()));
//        playerList.updatePermissionLevel(player);
//        sourceWorld.removeEntityDangerously(player);
//        player.isDead = false;
//
//        //region Transfer to world
//
//        player.setLocationAndAngles(xCoord, yCoord, zCoord, yaw, pitch);
//        player.connection.setPlayerLocation(xCoord, yCoord, zCoord, yaw, pitch);
//        targetWorld.spawnEntity(player);
//        targetWorld.updateEntityWithOptionalForce(player, false);
//        player.setWorld(targetWorld);
//
//        //endregion
//
//        playerList.preparePlayer(player, sourceWorld);
//        player.connection.setPlayerLocation(xCoord, yCoord, zCoord, yaw, pitch);
//        player.interactionManager.setWorld(targetWorld);
//        player.connection.sendPacket(new SPacketPlayerAbilities(player.capabilities));
//
//        playerList.updateTimeAndWeatherForPlayer(player, targetWorld);
//        playerList.syncPlayerInventory(player);
//
//        for (PotionEffect potioneffect : player.getActivePotionEffects()) {
//            player.connection.sendPacket(new SPacketEntityEffect(player.getEntityId(), potioneffect));
//        }
//        net.minecraftforge.fml.common.FMLCommonHandler.instance().firePlayerChangedDimensionEvent(player, sourceDim, targetDim);
//        player.setLocationAndAngles(xCoord, yCoord, zCoord, yaw, pitch);
//
//        return player;
//    }
//
//    public static Entity getHighestRidingEntity(Entity mount) {
//        Entity entity;
//
//        for (entity = mount; entity.getPassengers().size() > 0; entity = entity.getPassengers().get(0)) ;
//
//        return entity;
//    }
//
//    private static class PassengerHelper {
//        public Entity entity;
//        public LinkedList<PassengerHelper> passengers = new LinkedList<>();
//        public double offsetX, offsetY, offsetZ;
//
//        /**
//         * Creates a new passenger helper for the given entity and recursively adds all of the entities passengers.
//         *
//         * @param entity The root entity. If you have multiple stacked entities this would be the one at the bottom of the stack.
//         */
//        public PassengerHelper(Entity entity) {
//            this.entity = entity;
//            if (entity.isRiding()) {
//                offsetX = entity.posX - entity.getRidingEntity().posX;
//                offsetY = entity.posY - entity.getRidingEntity().posY;
//                offsetZ = entity.posZ - entity.getRidingEntity().posZ;
//            }
//            for (Entity passenger : entity.getPassengers()) {
//                passengers.add(new PassengerHelper(passenger));
//            }
//        }
//
//        /**
//         * Recursively teleports the entity and all of its passengers after dismounting them.
//         *
//         * @param server    The minecraft server.
//         * @param sourceDim The source dimension.
//         * @param targetDim The target dimension.
//         * @param xCoord    The target x position.
//         * @param yCoord    The target y position.
//         * @param zCoord    The target z position.
//         * @param yaw       The target yaw.
//         * @param pitch     The target pitch.
//         */
//        public void teleport(MinecraftServer server, int sourceDim, int targetDim, double xCoord, double yCoord, double zCoord, float yaw, float pitch) {
//            entity.removePassengers();
//            entity = handleEntityTeleport(entity, server, sourceDim, targetDim, xCoord, yCoord, zCoord, yaw, pitch);
//            for (PassengerHelper passenger : passengers) {
//                passenger.teleport(server, sourceDim, targetDim, xCoord, yCoord, zCoord, yaw, pitch);
//            }
//        }
//
//        /**
//         * Recursively remounts all of this entities riders and offsets their position relative to their position before teleporting.
//         */
//        public void remountRiders() {
//            if (entity.isRiding()) {
//                entity.setLocationAndAngles(entity.posX + offsetX, entity.posY + offsetY, entity.posZ + offsetZ, entity.rotationYaw, entity.rotationPitch);
//            }
//            for (PassengerHelper passenger : passengers) {
//                passenger.entity.startRiding(entity, true);
//                passenger.remountRiders();
//            }
//        }
//
//        /**
//         * This method sends update packets to any players that were teleported with the entity stack.
//         */
//        public void updateClients() {
//            if (entity instanceof EntityPlayerMP) {
//                updateClient((EntityPlayerMP) entity);
//            }
//            for (PassengerHelper passenger : passengers) {
//                passenger.updateClients();
//            }
//        }
//
//        /**
//         * This is the method that is responsible for actually sending the update to each client.
//         *
//         * @param playerMP The Player.
//         */
//        private void updateClient(EntityPlayerMP playerMP) {
//            if (entity.isBeingRidden()) {
//                playerMP.connection.sendPacket(new SPacketSetPassengers(entity));
//            }
//            for (PassengerHelper passenger : passengers) {
//                passenger.updateClients();
//            }
//        }
//
//        /**
//         * This method returns the helper for a specific entity in the stack.
//         *
//         * @param passenger The passenger you are looking for.
//         * @return The passenger helper for the specified passenger.
//         */
//        public PassengerHelper getPassenger(Entity passenger) {
//            if (this.entity == passenger) {
//                return this;
//            }
//
//            for (PassengerHelper rider : passengers) {
//                PassengerHelper re = rider.getPassenger(passenger);
//                if (re != null) {
//                    return re;
//                }
//            }
//
//            return null;
//        }
//    }


