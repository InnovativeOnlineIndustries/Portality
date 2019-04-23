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
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class GuiController extends GuiAddonScreen implements ITileContainer<TileController> {

    private TileController controller;
    private int x;
    private int y;

    public GuiController(TileController controller) {
        super(PortalityAssetProvider.PROVIDER, true);
        this.controller = controller;
        IBackgroundAsset background = (IBackgroundAsset) IAssetProvider.getAsset(PortalityAssetProvider.PROVIDER, AssetTypes.BACKGROUND);
        this.x = this.width / 2 - background.getArea().width / 2;
        this.y = this.height / 2 - background.getArea().height / 2;
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        super.render(mouseX, mouseY, partialTicks);
        IBackgroundAsset background = (IBackgroundAsset) IAssetProvider.getAsset(PortalityAssetProvider.PROVIDER, AssetTypes.BACKGROUND);
        this.x = this.width / 2 - background.getArea().width / 2;
        this.y = this.height / 2 - background.getArea().height / 2;

        String name = new TextComponentTranslation(CommonProxy.BLOCK_CONTROLLER.getTranslationKey()).getFormattedText();
        fontRenderer.drawString(TextFormatting.DARK_AQUA + name, this.x + background.getArea().width / 2 - fontRenderer.getStringWidth(name) / 2, this.y + 3, 0x000000);

        fontRenderer.drawString(I18n.format("portality.gui.controller") + " " + controller.getPortalDisplayName().substring(0, Math.min(controller.getPortalDisplayName().length(), 28)), this.x + 10, this.y + 21, 0xFFFFFF);
        fontRenderer.drawString(I18n.format("portality.gui.controller.private") + " " + controller.isPrivate(), this.x + 10, this.y + 21 + (fontRenderer.FONT_HEIGHT + 1) * 1, 0xFFFFFF);
        fontRenderer.drawString(I18n.format("portality.gui.controller.max_distance") + " " + BlockPosUtils.getMaxDistance(controller.getLength()), this.x + 10, this.y + 21 + (fontRenderer.FONT_HEIGHT + 1) * 2, 0xFFFFFF);
        fontRenderer.drawString(I18n.format("portality.gui.controller.interdimensional") + " " + controller.isInterdimensional(), this.x + 10, this.y + 21 + (fontRenderer.FONT_HEIGHT + 1) * 3, 0xFFFFFF);
        fontRenderer.drawString(I18n.format("portality.gui.controller.power") + " " + new DecimalFormat().format(controller.getEnergyStorage().getEnergyStored()) + " FE", this.x + 10, this.y + 21 + (fontRenderer.FONT_HEIGHT + 1) * 4, 0xFFFFFF);
        fontRenderer.drawString(I18n.format("portality.gui.controller.link") + " " + (controller.isActive() ? I18n.format("portality.gui.controller.link_active") : I18n.format("portality.gui.controller.link_missing")), this.x + 10, this.y + 21 + (fontRenderer.FONT_HEIGHT + 1) * 5, 0xFFFFFF);
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
            public void drawGuiContainerBackgroundLayer(GuiScreen guiScreen, IAssetProvider iAssetProvider, int i, int i1, int i2, int i3, float v) {
                IBackgroundAsset background = (IBackgroundAsset) IAssetProvider.getAsset(PortalityAssetProvider.PROVIDER, AssetTypes.BACKGROUND);
                Minecraft.getInstance().getTextureManager().bindTexture(background.getResourceLocation());
                drawTexturedModalRect(x - 25, y + 9, 0, 110, 25, 97);
            }

            @Override
            public void drawGuiContainerForegroundLayer(GuiScreen guiScreen, IAssetProvider iAssetProvider, int i, int i1, int i2, int i3) {

            }
        });
        controller.getGuiAddons().forEach(iFactory -> addons.add((IFactory<IGuiAddon>) iFactory));
        return addons;
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    @Override
    public TileController getTile() {
        return controller;
    }
}
