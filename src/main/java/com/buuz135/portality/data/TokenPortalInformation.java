package com.buuz135.portality.data;

import com.buuz135.portality.proxy.CommonProxy;
import net.minecraft.item.ItemStack;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.UUID;

public class TokenPortalInformation extends PortalInformation{

    public TokenPortalInformation(UUID owner, RegistryKey<World> dimension, BlockPos location, String name) {
        super(UUID.randomUUID(), owner, false, false, dimension, location, name, new ItemStack(CommonProxy.TELEPORTATION_TOKEN_ITEM), true);
        setToken(true);
    }

}
