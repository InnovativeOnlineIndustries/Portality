package com.buuz135.portality.gui;

import com.buuz135.portality.Portality;
import com.buuz135.portality.network.PortalNetworkMessage;
import com.buuz135.portality.network.PortalPrivacyToggleMessage;
import com.buuz135.portality.proxy.CommonProxy;
import com.buuz135.portality.util.BlockPosUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;

import java.util.Arrays;
import java.util.List;

public class GuiController extends GuiContainer {

    private static final ResourceLocation BG = new ResourceLocation(Portality.MOD_ID, "textures/gui/controller.png");

    public GuiController(ContainerController inventorySlotsIn) {
        super(inventorySlotsIn);
    }

    @Override
    public void initGui() {
        super.initGui();
        this.addButton(new GuiButton(0, this.guiLeft + 5, this.guiTop + 96, 166, 20, "Call Portal") {
            @Override
            public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
                if (isMouseOver()) {
                    Portality.NETWORK.sendToServer(new PortalNetworkMessage.Request(false));
                    ContainerController containerController = (ContainerController) GuiController.this.inventorySlots;
                    BlockPos pos = containerController.getController().getPos();
                    mc.player.openGui(Portality.INSTANCE, 2, containerController.getController().getWorld(), pos.getX(), pos.getY(), pos.getZ());
                }
                return super.mousePressed(mc, mouseX, mouseY);
            }
        });
        this.addButton(new GuiButton(1, this.guiLeft + 5, this.guiTop + 96 + 22, 166, 20, "Single Portal Call"));
        this.addButton(new GuiButton(2, this.guiLeft + 5, this.guiTop + 96 + 22 * 2, 166, 20, "Force Portal Call"));
        this.addButton(new TooltipGuiButton(3, this.guiLeft - 21, this.guiTop + 17, 20, 20, "") {
            @Override
            public List<String> getTooltip() {
                return Arrays.asList("Change name");
            }

            @Override
            public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
                if (isMouseOver()) {
                    ContainerController containerController = (ContainerController) GuiController.this.inventorySlots;
                    BlockPos pos = containerController.getController().getPos();
                    mc.player.openGui(Portality.INSTANCE, 1, containerController.getController().getWorld(), pos.getX(), pos.getY(), pos.getZ());
                }
                return super.mousePressed(mc, mouseX, mouseY);
            }
        });
        this.addButton(new TooltipGuiButton(4, this.guiLeft - 21, this.guiTop + 17 + 21, 20, 20, "") {

            @Override
            public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
                if (isMouseOver()) {
                    ContainerController containerController = (ContainerController) GuiController.this.inventorySlots;
                    Portality.NETWORK.sendToServer(new PortalPrivacyToggleMessage(containerController.getController().getPos().toLong()));
                }
                return super.mousePressed(mc, mouseX, mouseY);
            }

            @Override
            public List<String> getTooltip() {
                ContainerController containerController = (ContainerController) GuiController.this.inventorySlots;
                return containerController.getController().isPrivate() ? Arrays.asList("Make portal public") : Arrays.asList("Make portal private");
            }
        });
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
        fontRenderer.drawString("Power: " + 0, 10, 21 + (fontRenderer.FONT_HEIGHT + 1) * 4, 0xFFFFFF);

        this.itemRender.renderItemAndEffectIntoGUI(new ItemStack(Items.SIGN), -19, 19);
        this.itemRender.renderItemAndEffectIntoGUI(new ItemStack(Blocks.TRIPWIRE_HOOK), -19, 19 + 20);

        for (GuiButton button : this.buttonList) {
            if (button instanceof TooltipGuiButton && button.isMouseOver()) {
                this.drawHoveringText(((TooltipGuiButton) button).getTooltip(), mouseX - this.guiLeft, mouseY - this.guiTop);
            }
        }
    }
}
