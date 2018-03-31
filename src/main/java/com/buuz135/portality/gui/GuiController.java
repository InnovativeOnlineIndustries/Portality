package com.buuz135.portality.gui;

import com.buuz135.portality.Portality;
import com.buuz135.portality.proxy.CommonProxy;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentTranslation;

public class GuiController extends GuiContainer {

    private static final ResourceLocation BG = new ResourceLocation(Portality.MOD_ID, "textures/gui/controller.png");

    public GuiController(ContainerController inventorySlotsIn) {
        super(inventorySlotsIn);
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
        fontRenderer.drawString("Name: " + containerController.getController().getName(), 10, 21, 0xFFFFFF);
    }
}
