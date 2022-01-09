package com.buuz135.portality.data;

import com.buuz135.portality.proxy.CommonProxy;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.UUID;

public class TokenPortalInformation extends PortalInformation{

    public TokenPortalInformation(UUID owner, ResourceKey<Level> dimension, BlockPos location, String name) {
        super(UUID.randomUUID(), owner, false, false, dimension, location, name, new ItemStack(CommonProxy.TELEPORTATION_TOKEN_ITEM), true);
        setToken(true);
    }

}
