package com.buuz135.portality;

import com.buuz135.portality.block.ControllerBlock;
import com.buuz135.portality.block.FrameBlock;
import com.buuz135.portality.block.GeneratorBlock;
import com.buuz135.portality.block.InterdimensionalModuleBlock;
import com.buuz135.portality.block.module.CapabilityEnergyModuleBlock;
import com.buuz135.portality.block.module.CapabilityFluidModuleBlock;
import com.buuz135.portality.block.module.CapabilityItemModuleBlock;
import com.buuz135.portality.datagen.PortalityBlockTagsProvider;
import com.buuz135.portality.item.TeleportationTokenItem;
import com.buuz135.portality.network.*;
import com.buuz135.portality.proxy.CommonProxy;
import com.buuz135.portality.proxy.PortalitySoundHandler;
import com.buuz135.portality.proxy.client.ClientProxy;
import com.buuz135.portality.tile.BasicFrameTile;
import com.buuz135.portality.tile.EnergyModuleTile;
import com.hrznstudio.titanium.event.handler.EventManager;
import com.hrznstudio.titanium.module.ModuleController;
import com.hrznstudio.titanium.network.NetworkHandler;
import com.hrznstudio.titanium.reward.Reward;
import com.hrznstudio.titanium.reward.RewardGiver;
import com.hrznstudio.titanium.reward.RewardManager;
import com.hrznstudio.titanium.tab.TitaniumTab;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.UUID;
import java.util.stream.Collectors;

@Mod(Portality.MOD_ID)
public class Portality extends ModuleController {

    public static final String MOD_ID = "portality";
    public static final NetworkHandler NETWORK = new NetworkHandler(MOD_ID);
    public static final TitaniumTab TAB = new TitaniumTab(ResourceLocation.fromNamespaceAndPath(MOD_ID, "main"));

    public static CommonProxy proxy;

    public Portality(Dist dist, IEventBus modBus, ModContainer container) {
        super(container);
        NETWORK.registerMessage("portal_privacy", PortalPrivacyToggleMessage.class);
        NETWORK.registerMessage("portal_rename", PortalRenameMessage.class);
        NETWORK.registerMessage("portal_network_response", PortalNetworkMessage.Response.class);
        NETWORK.registerMessage("portal_link", PortalLinkMessage.class);
        NETWORK.registerMessage("portal_close", PortalCloseMessage.class);
        NETWORK.registerMessage("portal_teleport", PortalTeleportMessage.class);
        NETWORK.registerMessage("portal_display_toggle", PortalDisplayToggleMessage.class);
        NETWORK.registerMessage("portal_change_color", PortalChangeColorMessage.class);

        proxy = dist.isClient() ? new ClientProxy() : new CommonProxy();
        EventManager.mod(FMLCommonSetupEvent.class).process(this::onCommon).subscribe();
        EventManager.mod(FMLClientSetupEvent.class).process(this::onClient).subscribe();
        EventManager.mod(RegisterCapabilitiesEvent.class).process(event -> {
            event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, CommonProxy.BLOCK_CAPABILITY_ENERGY_MODULE.type().get(), (tile, direction) -> tile instanceof EnergyModuleTile energyModuleTile ? energyModuleTile.getEnergyStorage() : null);
        }).subscribe();

        RewardGiver giver = RewardManager.get().getGiver(UUID.fromString("d28b7061-fb92-4064-90fb-7e02b95a72a6"), "Buuz135");
        try {
            giver.addReward(new Reward(ResourceLocation.fromNamespaceAndPath(Portality.MOD_ID, "aura"), new URL("https://raw.githubusercontent.com/Buuz135/Industrial-Foregoing/master/contributors.json"), () -> rewardDist -> {
            }, Arrays.stream(AuraType.values()).map(Enum::toString).collect(Collectors.toList()).toArray(new String[]{})));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void initModules() {
        this.addCreativeTab("main", () -> CommonProxy.BLOCK_CONTROLLER == null ? ItemStack.EMPTY : new ItemStack(CommonProxy.BLOCK_CONTROLLER.block().get()), MOD_ID, TAB);
        CommonProxy.BLOCK_CONTROLLER = getRegistries().registerBlockWithTile("controller", ControllerBlock::new, TAB);
        CommonProxy.BLOCK_FRAME = getRegistries().registerBlockWithTile("frame", () -> new FrameBlock<BasicFrameTile>("frame", BasicFrameTile.class), TAB);
        CommonProxy.BLOCK_CAPABILITY_ENERGY_MODULE = getRegistries().registerBlockWithTile("module_energy", CapabilityEnergyModuleBlock::new, TAB);
        CommonProxy.BLOCK_CAPABILITY_FLUID_MODULE = getRegistries().registerBlockWithTile("module_fluids", CapabilityFluidModuleBlock::new, TAB);
        CommonProxy.BLOCK_CAPABILITY_ITEM_MODULE = getRegistries().registerBlockWithTile("module_items", CapabilityItemModuleBlock::new, TAB);
        CommonProxy.BLOCK_INTERDIMENSIONAL_MODULE = getRegistries().registerBlockWithTile("module_interdimensional", InterdimensionalModuleBlock::new, TAB);
        CommonProxy.BLOCK_GENERATOR = getRegistries().registerBlockWithTile("generator", GeneratorBlock::new, TAB);
        CommonProxy.TELEPORTATION_TOKEN_ITEM = getRegistries().registerGeneric(Registries.ITEM, "teleportation_token", TeleportationTokenItem::new);

        PortalitySoundHandler.PORTAL = getRegistries().registerTyped(Registries.SOUND_EVENT, "portal", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Portality.MOD_ID, "portal")));
        PortalitySoundHandler.PORTAL_TP = getRegistries().registerTyped(Registries.SOUND_EVENT, "portal_teleport", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Portality.MOD_ID, "portal_teleport")));
    }

    public void onCommon(FMLCommonSetupEvent event) {
        proxy.onCommon();
    }

    public void onClient(FMLClientSetupEvent event) {
        proxy.onClient(Minecraft.getInstance());
    }

    @Override
    public void addDataProvider(GatherDataEvent event) {
        super.addDataProvider(event);
        event.getGenerator().addProvider(true, new PortalityBlockTagsProvider(event.getGenerator().getPackOutput(), event.getLookupProvider(), MOD_ID, event.getExistingFileHelper()));
    }

    public enum AuraType {
        PORTAL(ResourceLocation.fromNamespaceAndPath(Portality.MOD_ID, "textures/blocks/player_render.png"), true),
        FORCE_FIELD(ResourceLocation.withDefaultNamespace("textures/misc/forcefield.png"), true),
        UNDERWATER(ResourceLocation.withDefaultNamespace("textures/misc/underwater.png"), true),
        SPOOK(ResourceLocation.withDefaultNamespace("textures/misc/pumpkinblur.png"), false),
        END(ResourceLocation.withDefaultNamespace("textures/environment/end_sky.png"), true),
        CLOUDS(ResourceLocation.withDefaultNamespace("textures/environment/clouds.png"), true),
        RAIN(ResourceLocation.withDefaultNamespace("textures/environment/rain.png"), true),
        SGA(ResourceLocation.withDefaultNamespace("textures/font/ascii_sga.png"), true),
        ENCHANTED(ResourceLocation.withDefaultNamespace("textures/misc/enchanted_item_glint.png"), true),
        BARS(ResourceLocation.withDefaultNamespace("textures/gui/bars.png"), true),
        RECIPE_BOOK(ResourceLocation.withDefaultNamespace("textures/gui/recipe_book.png"), true),
        END_PORTAL(ResourceLocation.withDefaultNamespace("textures/entity/end_portal.png"), true),
        MOON(ResourceLocation.withDefaultNamespace("textures/environment/moon_phases.png"), true);

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
}
