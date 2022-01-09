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
package com.buuz135.portality;

import com.buuz135.portality.network.*;
import com.buuz135.portality.proxy.CommonProxy;
import com.buuz135.portality.proxy.PortalitySoundHandler;
import com.buuz135.portality.proxy.client.ClientProxy;
import com.buuz135.portality.proxy.client.render.AuraRender;
import com.hrznstudio.titanium.TitaniumClient;
import com.hrznstudio.titanium.event.handler.EventManager;
import com.hrznstudio.titanium.module.Feature;
import com.hrznstudio.titanium.module.Module;
import com.hrznstudio.titanium.module.ModuleController;
import com.hrznstudio.titanium.network.NetworkHandler;
import com.hrznstudio.titanium.reward.Reward;
import com.hrznstudio.titanium.reward.RewardGiver;
import com.hrznstudio.titanium.reward.RewardManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.UUID;
import java.util.stream.Collectors;

@Mod("portality")
public class Portality extends ModuleController {

    public static final String MOD_ID = "portality";
    public static NetworkHandler NETWORK = new NetworkHandler(MOD_ID);
    public static final CreativeModeTab TAB = new CreativeModeTab(MOD_ID) {
        @Override
        public ItemStack makeIcon() {
            return new ItemStack(CommonProxy.BLOCK_CONTROLLER);
        }
    };

    public static CommonProxy proxy;

    public Portality() {
        NETWORK.registerMessage(PortalPrivacyToggleMessage.class);
        NETWORK.registerMessage(PortalPrivacyToggleMessage.class);
        NETWORK.registerMessage(PortalRenameMessage.class);
        NETWORK.registerMessage(PortalNetworkMessage.Response.class);
        NETWORK.registerMessage(PortalLinkMessage.class);
        NETWORK.registerMessage(PortalCloseMessage.class);
        NETWORK.registerMessage(PortalTeleportMessage.class);
        NETWORK.registerMessage(PortalDisplayToggleMessage.class);
        NETWORK.registerMessage(PortalChangeColorMessage.class);
        proxy = DistExecutor.runForDist(() -> ClientProxy::new, () -> CommonProxy::new);
        EventManager.mod(FMLCommonSetupEvent.class).process(this::onCommon).subscribe();
        EventManager.mod(FMLClientSetupEvent.class).process(this::onClient).subscribe();
        RewardGiver giver = RewardManager.get().getGiver(UUID.fromString("d28b7061-fb92-4064-90fb-7e02b95a72a6"), "Buuz135");
        try {
            giver.addReward(new Reward(new ResourceLocation(Portality.MOD_ID, "aura"), new URL("https://raw.githubusercontent.com/Buuz135/Industrial-Foregoing/master/contributors.json"), () -> dist -> {
                if (dist == Dist.CLIENT) {
                    registerAura();
                }
            }, Arrays.stream(AuraType.values()).map(Enum::toString).collect(Collectors.toList()).toArray(new String[]{})));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void initModules() {
        addModule(Module.builder("core").force()
                .feature(Feature.builder("core")
                        .content(Block.class, CommonProxy.BLOCK_CONTROLLER)
                        .content(Block.class, CommonProxy.BLOCK_FRAME)
                        .content(Block.class, CommonProxy.BLOCK_CAPABILITY_ENERGY_MODULE)
                        .content(Block.class, CommonProxy.BLOCK_CAPABILITY_FLUID_MODULE)
                        .content(Block.class, CommonProxy.BLOCK_CAPABILITY_ITEM_MODULE)
                        .content(Block.class, CommonProxy.BLOCK_INTERDIMENSIONAL_MODULE)
                        .content(Block.class, CommonProxy.BLOCK_GENERATOR)
                        .content(Item.class, CommonProxy.TELEPORTATION_TOKEN_ITEM)
                        .content(SoundEvent.class, PortalitySoundHandler.PORTAL)
                        .content(SoundEvent.class, PortalitySoundHandler.PORTAL_TP)
                        .force()));
    }

    public void onCommon(FMLCommonSetupEvent event) {
        proxy.onCommon();
    }

    public void onClient(FMLClientSetupEvent event) {
        proxy.onClient(event.getMinecraftSupplier().get());
    }

    public enum AuraType {
        PORTAL(new ResourceLocation(Portality.MOD_ID, "textures/blocks/player_render.png"), true),
        FORCE_FIELD(new ResourceLocation("textures/misc/forcefield.png"), true),
        UNDERWATER(new ResourceLocation("textures/misc/underwater.png"), true),
        SPOOK(new ResourceLocation("textures/misc/pumpkinblur.png"), false),
        END(new ResourceLocation("textures/environment/end_sky.png"), true),
        CLOUDS(new ResourceLocation("textures/environment/clouds.png"), true),
        RAIN(new ResourceLocation("textures/environment/rain.png"), true),
        SGA(new ResourceLocation("textures/font/ascii_sga.png"), true),
        ENCHANTED(new ResourceLocation("textures/misc/enchanted_item_glint.png"), true),
        BARS(new ResourceLocation("textures/gui/bars.png"), true),
        RECIPE_BOOK(new ResourceLocation("textures/gui/recipe_book.png"), true),
        END_PORTAL(new ResourceLocation("textures/entity/end_portal.png"), true),
        MOON(new ResourceLocation("textures/environment/moon_phases.png"), true);

        private final ResourceLocation resourceLocation;
        private final boolean enableBlend;

        AuraType(ResourceLocation resourceLocation, boolean enableBlend) {
            this.resourceLocation = resourceLocation;
            this.enableBlend = enableBlend;
        }

        public ResourceLocation getResourceLocation() {
            return resourceLocation;
        }

        public boolean isEnableBlend() {
            return enableBlend;
        }
    }

    @OnlyIn(Dist.CLIENT)
    private void registerAura() {
        Minecraft instance = Minecraft.getInstance();
        EntityRenderDispatcher manager = instance.getEntityRenderDispatcher();
        manager.getSkinMap().get("default").addLayer(new AuraRender(TitaniumClient.getPlayerRenderer(instance)));
        manager.getSkinMap().get("slim").addLayer(new AuraRender(TitaniumClient.getPlayerRenderer(instance)));
    }
}
