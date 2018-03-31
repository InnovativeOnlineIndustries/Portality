package com.buuz135.portality.data;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.storage.WorldSavedData;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PortalDataManager extends WorldSavedData {

    public static final String NAME = "Portality";
    private List<PortalInformation> informationList;

    public PortalDataManager(String name) {
        super(name);
        informationList = new ArrayList<>();
    }

    public PortalDataManager() {
        this(NAME);
    }

    public static void addInformation(World world, PortalInformation information) {
        PortalDataManager dataManager = getData(world);
        dataManager.getInformationList().add(information);
        dataManager.markDirty();
    }

    public static void removeInformation(World world, BlockPos blockPos) {
        PortalDataManager dataManager = getData(world);
        dataManager.getInformationList().removeIf(information1 -> information1.getLocation().equals(blockPos) || information1.getLocation().equals(BlockPos.ORIGIN));
        dataManager.markDirty();
    }

    @Nullable
    public static PortalInformation getInfoFromID(World world, UUID uuid) {
        PortalDataManager dataManager = getData(world);
        for (PortalInformation information : dataManager.getInformationList()) {
            if (information.getId().equals(uuid)) return information;
        }
        return null;
    }

    @Nonnull
    public static PortalDataManager getData(World world) {
        PortalDataManager data = (PortalDataManager) world.getMapStorage().getOrLoadData(PortalDataManager.class, NAME);
        if (data == null) {
            data = new PortalDataManager();
            world.getMapStorage().setData(NAME, data);
        }
        return data;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        informationList.clear();
        NBTTagCompound root = nbt.getCompoundTag(NAME);
        for (String key : root.getKeySet()) {
            NBTTagCompound info = root.getCompoundTag(key);
            informationList.add(new PortalInformation(info.getUniqueId("ID"), info.getUniqueId("Owner"), info.getBoolean("Active"), info.getBoolean("Private"),
                    info.getInteger("Dimension"), BlockPos.fromLong(info.getLong("Position"))));
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        NBTTagCompound tag = new NBTTagCompound();
        for (PortalInformation information : informationList) {
            NBTTagCompound infoTag = new NBTTagCompound();
            infoTag.setUniqueId("ID", information.getId());
            infoTag.setUniqueId("Owner", information.getOwner());
            infoTag.setBoolean("Active", information.isActive());
            infoTag.setBoolean("Private", information.isPrivate());
            infoTag.setInteger("Dimension", information.getDimension());
            infoTag.setLong("Position", information.getLocation().toLong());
            tag.setTag(information.getId().toString(), infoTag);
        }
        compound.setTag(NAME, tag);
        return compound;
    }

    public List<PortalInformation> getInformationList() {
        return informationList;
    }
}
