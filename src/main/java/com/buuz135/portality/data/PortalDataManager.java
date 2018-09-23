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
package com.buuz135.portality.data;

import net.minecraft.item.ItemStack;
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
        dataManager.getInformationList().removeIf(information1 -> information1.getLocation().equals(blockPos));
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

    @Nullable
    public static PortalInformation getInfoFromPos(World world, BlockPos pos) {
        PortalDataManager dataManager = getData(world);
        for (PortalInformation information : dataManager.getInformationList()) {
            if (information.getLocation().equals(pos)) return information;
        }
        return null;
    }

    @Nullable
    public static PortalInformation getInfoFromLink(World world, PortalLinkData data) {
        PortalDataManager dataManager = getData(world);
        for (PortalInformation information : dataManager.getInformationList()) {
            if (information.getDimension() == data.getDimension() && information.getLocation().equals(data.getPos()))
                return information;
        }
        return null;
    }

    public static void setPortalPrivacy(World world, BlockPos pos, boolean privacy) {
        PortalDataManager dataManager = getData(world);
        for (PortalInformation information : dataManager.getInformationList()) {
            if (information.getLocation().equals(pos)) {
                information.setPrivate(privacy);
                dataManager.markDirty();
            }
        }
    }

    public static void setPortalName(World world, BlockPos pos, String name) {
        PortalDataManager dataManager = getData(world);
        for (PortalInformation information : dataManager.getInformationList()) {
            if (information.getLocation().equals(pos)) {
                information.setName(name);
                dataManager.markDirty();
            }
        }
    }

    public static void setPortalInterdimensional(World world, BlockPos pos, boolean interdimensional) {
        PortalDataManager dataManager = getData(world);
        for (PortalInformation information : dataManager.getInformationList()) {
            if (information.getLocation().equals(pos)) {
                information.setInterdimensional(interdimensional);
                dataManager.markDirty();
            }
        }
    }

    public static void setPortalDisplay(World world, BlockPos pos, ItemStack stack) {
        PortalDataManager dataManager = getData(world);
        for (PortalInformation information : dataManager.getInformationList()) {
            if (information.getLocation().equals(pos)) {
                information.setDisplay(stack);
                dataManager.markDirty();
            }
        }
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

    public static void setActiveStatus(World world, BlockPos pos, boolean active) {
        PortalDataManager dataManager = getData(world);
        for (PortalInformation information : dataManager.getInformationList()) {
            if (information.getLocation().equals(pos)) {
                information.setActive(active);
                dataManager.markDirty();
            }
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        informationList.clear();
        NBTTagCompound root = nbt.getCompoundTag(NAME);
        for (String key : root.getKeySet()) {
            NBTTagCompound info = root.getCompoundTag(key);
            informationList.add(PortalInformation.readFromNBT(info));
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        NBTTagCompound tag = new NBTTagCompound();
        for (PortalInformation information : informationList) {
            tag.setTag(information.getId().toString(), information.writetoNBT());
        }
        compound.setTag(NAME, tag);
        return compound;
    }

    public List<PortalInformation> getInformationList() {
        return informationList;
    }

}
