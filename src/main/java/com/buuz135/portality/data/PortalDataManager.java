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
package com.buuz135.portality.data;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.saveddata.SavedData;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PortalDataManager extends SavedData {

    public static final String NAME = "Portality";
    private List<PortalInformation> informationList;

    public PortalDataManager(String name) {
        super(name);
        informationList = new ArrayList<>();
    }

    public PortalDataManager() {
        this(NAME);
    }

    public static void addInformation(Level world, PortalInformation information) {
        if (world instanceof ServerLevel) {
            PortalDataManager dataManager = getData(world);
            dataManager.getInformationList().add(information);
            dataManager.setDirty();
        }
    }

    public static void removeInformation(LevelAccessor world, BlockPos blockPos) {
        if (world instanceof ServerLevel) {
            PortalDataManager dataManager = getData(world);
            dataManager.getInformationList().removeIf(information1 -> information1.getLocation().equals(blockPos));
            dataManager.setDirty();
        }
    }

    @Nullable
    public static PortalInformation getInfoFromID(Level world, UUID uuid) {
        PortalDataManager dataManager = getData(world);
        for (PortalInformation information : dataManager.getInformationList()) {
            if (information.getId().equals(uuid)) return information;
        }
        return null;
    }

    @Nullable
    public static PortalInformation getInfoFromPos(Level world, BlockPos pos) {
        PortalDataManager dataManager = getData(world);
        for (PortalInformation information : dataManager.getInformationList()) {
            if (information.getLocation().equals(pos)) return information;
        }
        return null;
    }

    @Nullable
    public static PortalInformation getInfoFromLink(Level world, PortalLinkData data) {
        PortalDataManager dataManager = getData(world);
        for (PortalInformation information : dataManager.getInformationList()) {
            if (information.getDimension() == data.getDimension() && information.getLocation().equals(data.getPos()))
                return information;
        }
        return null;
    }

    public static void setPortalPrivacy(Level world, BlockPos pos, boolean privacy) {
        PortalDataManager dataManager = getData(world);
        for (PortalInformation information : dataManager.getInformationList()) {
            if (information.getLocation().equals(pos)) {
                information.setPrivate(privacy);
                dataManager.setDirty();
            }
        }
    }

    public static void setPortalName(Level world, BlockPos pos, String name) {
        PortalDataManager dataManager = getData(world);
        for (PortalInformation information : dataManager.getInformationList()) {
            if (information.getLocation().equals(pos)) {
                information.setName(name);
                dataManager.setDirty();
            }
        }
    }

    public static void setPortalInterdimensional(Level world, BlockPos pos, boolean interdimensional) {
        PortalDataManager dataManager = getData(world);
        for (PortalInformation information : dataManager.getInformationList()) {
            if (information.getLocation().equals(pos)) {
                information.setInterdimensional(interdimensional);
                dataManager.setDirty();
            }
        }
    }

    public static void setPortalDisplay(Level world, BlockPos pos, ItemStack stack) {
        PortalDataManager dataManager = getData(world);
        for (PortalInformation information : dataManager.getInformationList()) {
            if (information.getLocation().equals(pos)) {
                information.setDisplay(stack);
                dataManager.setDirty();
            }
        }
    }

    @Nullable
    public static PortalDataManager getData(LevelAccessor world) {
        if (world instanceof ServerLevel) {
            ServerLevel serverWorld = ((ServerLevel) world).getServer().getLevel(Level.OVERWORLD);
            PortalDataManager data = serverWorld.getDataStorage().computeIfAbsent(PortalDataManager::new, NAME);
            //if (data == null) {
            //    data = new PortalDataManager();
            //    world.getMapStorage().func_212424_a(world.getDimension().getType(), NAME, data);
            //}
            return data;
        }
        return null;
    }

    public static void setActiveStatus(Level world, BlockPos pos, boolean active) {
        PortalDataManager dataManager = getData(world);
        for (PortalInformation information : dataManager.getInformationList()) {
            if (information.getLocation().equals(pos)) {
                information.setActive(active);
                dataManager.setDirty();
            }
        }
    }

    @Override
    public void load(CompoundTag nbt) {
        informationList.clear();
        CompoundTag root = nbt.getCompound(NAME);
        for (String key : root.getAllKeys()) {
            CompoundTag info = root.getCompound(key);
            informationList.add(PortalInformation.readFromNBT(info));
        }
    }

    @Override
    public CompoundTag save(CompoundTag compound) {
        CompoundTag tag = new CompoundTag();
        for (PortalInformation information : informationList) {
            tag.put(information.getId().toString(), information.writetoNBT());
        }
        compound.put(NAME, tag);
        return compound;
    }

    public List<PortalInformation> getInformationList() {
        return informationList;
    }

}
