package com.buuz135.portality.gui;

import com.buuz135.portality.Portality;
import com.buuz135.portality.proxy.CommonProxy;
import com.buuz135.portality.util.BlockPosUtils;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentTranslation;

public class GuiController extends GuiContainer {

    private static final ResourceLocation BG = new ResourceLocation(Portality.MOD_ID, "textures/gui/controller.png");

    public GuiController(ContainerController inventorySlotsIn) {
        super(inventorySlotsIn);
        this.addButton(new GuiButton(0, this.guiLeft, this.guiTop, "Call Portal"));
    }

    @Override
    public void initGui() {
        super.initGui();
        this.addButton(new GuiButton(0, this.guiLeft + 5, this.guiTop + 96, 166, 20, "Call Portal"));
        this.addButton(new GuiButton(1, this.guiLeft + 5, this.guiTop + 96 + 22, 166, 20, "Single Portal Call"));
        this.addButton(new GuiButton(2, this.guiLeft + 5, this.guiTop + 96 + 22 * 2, 166, 20, "Force Portal Call"));
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        this.drawDefaultBackground();
        GlStateManager.color(1, 1, 1, 1);
        mc.getTextureManager().bindTexture(BG);
        int x = (width - xSize) / 2;
        int y = (height - ySize) / 2;
        drawTexturedModalRect(x, y, 0, 0, xSize, ySize);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
        String name = new TextComponentTranslation(CommonProxy.BLOCK_CONTROLLER.getUnlocalizedName()).getFormattedText();
        fontRenderer.drawString(name, xSize / 2 - fontRenderer.getStringWidth(name) / 2, 6, 0x404040);

        ContainerController containerController = (ContainerController) this.inventorySlots;
        fontRenderer.drawString("Name: " + containerController.getController().getName().substring(0, Math.min(containerController.getController().getName().length(), 28)), 10, 21, 0xFFFFFF);
        fontRenderer.drawString("Private: " + containerController.getController().isPrivate(), 10, 21 + (fontRenderer.FONT_HEIGHT + 1) * 1, 0xFFFFFF);
        fontRenderer.drawString("Max Distance: " + BlockPosUtils.getMaxDistance(containerController.getController().getLength()), 10, 21 + (fontRenderer.FONT_HEIGHT + 1) * 2, 0xFFFFFF);
        fontRenderer.drawString("Interdimensional: " + containerController.getController().isPrivate(), 10, 21 + (fontRenderer.FONT_HEIGHT + 1) * 3, 0xFFFFFF);
        fontRenderer.drawString("Power: " + containerController.getController().isPrivate(), 10, 21 + (fontRenderer.FONT_HEIGHT + 1) * 4, 0xFFFFFF);
    }
}
