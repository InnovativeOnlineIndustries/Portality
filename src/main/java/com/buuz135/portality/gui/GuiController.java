package com.buuz135.portality.gui;

import com.buuz135.portality.proxy.CommonProxy;
import com.buuz135.portality.tile.TileController;
import com.buuz135.portality.util.BlockPosUtils;
import com.hrznstudio.titanium.api.IFactory;
import com.hrznstudio.titanium.api.client.AssetTypes;
import com.hrznstudio.titanium.api.client.IGuiAddon;
import com.hrznstudio.titanium.api.client.assets.types.IBackgroundAsset;
import com.hrznstudio.titanium.client.gui.GuiAddonScreen;
import com.hrznstudio.titanium.client.gui.ITileContainer;
import com.hrznstudio.titanium.client.gui.addon.BasicGuiAddon;
import com.hrznstudio.titanium.client.gui.asset.IAssetProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class GuiController extends GuiAddonScreen implements ITileContainer<TileController> {

    private TileController controller;

    public GuiController(TileController controller) {
        super(PortalityAssetProvider.PROVIDER, true);
        this.controller = controller;
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        super.render(mouseX, mouseY, partialTicks);
    }


    @Override
    public void renderBackground(int mouseX, int mouseY, float partialTicks) {
        super.renderBackground(mouseX, mouseY, partialTicks);
    }

    @Override
    public void renderForeground(int mouseX, int mouseY, float partialTicks) {
        IBackgroundAsset background = (IBackgroundAsset) IAssetProvider.getAsset(PortalityAssetProvider.PROVIDER, AssetTypes.BACKGROUND);
        String name = new TranslationTextComponent(CommonProxy.BLOCK_CONTROLLER.getTranslationKey()).getFormattedText();
        FontRenderer fontRenderer = Minecraft.getInstance().fontRenderer;
        fontRenderer.drawString(TextFormatting.DARK_AQUA + name, this.x + background.getArea().width / 2 - fontRenderer.getStringWidth(name) / 2, this.y + 3, 0x000000);
        fontRenderer.drawString(I18n.format("portality.gui.controller") + " " + controller.getPortalDisplayName().substring(0, Math.min(controller.getPortalDisplayName().length(), 26)), this.x + 10, this.y + 21, 0xFFFFFF);
        fontRenderer.drawString(I18n.format("portality.gui.controller.private") + " " + controller.isPrivate(), this.x + 10, this.y + 21 + (fontRenderer.FONT_HEIGHT + 1) * 1, 0xFFFFFF);
        fontRenderer.drawString(I18n.format("portality.gui.controller.max_distance") + " " + BlockPosUtils.getMaxDistance(controller.getLength()), this.x + 10, this.y + 21 + (fontRenderer.FONT_HEIGHT + 1) * 2, 0xFFFFFF);
        fontRenderer.drawString(I18n.format("portality.gui.controller.interdimensional") + " " + controller.isInterdimensional(), this.x + 10, this.y + 21 + (fontRenderer.FONT_HEIGHT + 1) * 3, 0xFFFFFF);
        fontRenderer.drawString(I18n.format("portality.gui.controller.power") + " " + new DecimalFormat().format(controller.getEnergyStorage().getEnergyStored()) + " FE", this.x + 10, this.y + 21 + (fontRenderer.FONT_HEIGHT + 1) * 4, 0xFFFFFF);
        fontRenderer.drawString(I18n.format("portality.gui.controller.link") + " " + (controller.isActive() ? I18n.format("portality.gui.controller.link_active") : I18n.format("portality.gui.controller.link_missing")), this.x + 10, this.y + 21 + (fontRenderer.FONT_HEIGHT + 1) * 5, 0xFFFFFF);
        super.renderForeground(mouseX, mouseY, partialTicks);
    }

    @Override
    public List<IFactory<IGuiAddon>> guiAddons() {
        List<IFactory<IGuiAddon>> addons = new ArrayList<>();
        addons.add(() -> new BasicGuiAddon(x - 25, y + 9) {
            @Override
            public int getXSize() {
                return 0;
            }

            @Override
            public int getYSize() {
                return 0;
            }

            @Override
            public void drawGuiContainerBackgroundLayer(Screen guiScreen, IAssetProvider iAssetProvider, int i, int i1, int i2, int i3, float v) {
                IBackgroundAsset background = (IBackgroundAsset) IAssetProvider.getAsset(PortalityAssetProvider.PROVIDER, AssetTypes.BACKGROUND);
                Minecraft.getInstance().getTextureManager().bindTexture(background.getResourceLocation());
                guiScreen.blit(x - 25, y + 9, 0, 110, 25, 97);
            }

            @Override
            public void drawGuiContainerForegroundLayer(Screen guiScreen, IAssetProvider iAssetProvider, int i, int i1, int i2, int i3) {

            }
        });
        controller.getGuiAddons().forEach(iFactory -> addons.add((IFactory<IGuiAddon>) iFactory));
        return addons;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public TileController getTile() {
        return controller;
    }
}
