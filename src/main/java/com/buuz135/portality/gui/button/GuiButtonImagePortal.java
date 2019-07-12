package com.buuz135.portality.gui.button;


import com.buuz135.portality.Portality;
import com.buuz135.portality.data.PortalInformation;
import com.buuz135.portality.gui.GuiPortals;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.button.ImageButton;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;

public class GuiButtonImagePortal extends ImageButton {

    private PortalInformation information;
    private GuiPortals portals;

    public GuiButtonImagePortal(GuiPortals guiPortals, PortalInformation information, int x, int y, int xSize, int ySize, int textureX, int textureY, int offset, ResourceLocation location) {
        super(x, y, xSize, ySize, textureX, textureY, offset, location, p_onPress_1_ -> {
        });
        this.information = information;
        this.portals = guiPortals;
    }

    @Override
    public void renderButton(int p_renderButton_1_, int p_renderButton_2_, float p_renderButton_3_) {
        GlStateManager.color4f(1, 1, 1, 1);
        super.renderButton(p_renderButton_1_, p_renderButton_2_, p_renderButton_3_);
        GlStateManager.pushMatrix();
        RenderHelper.enableGUIStandardItemLighting();
        Minecraft.getInstance().getItemRenderer().renderItemAndEffectIntoGUI(information.getDisplay(), x + 5, y + 3);
        FontRenderer fontRenderer = Minecraft.getInstance().fontRenderer;
        TextFormatting color = TextFormatting.RESET;
        if (information.isPrivate()) color = TextFormatting.GOLD;
        if (information.isActive()) color = TextFormatting.RED;
        fontRenderer.drawString(color + information.getName().substring(0, Math.min(information.getName().length(), 28)), x + 28, 7 + y, isMouseOver(p_renderButton_1_, p_renderButton_2_) ? 16777120 : 0xFFFFFFFF);
        //fontRenderer.drawString(color + (information.isPrivate() ? I18n.format("portality.display.private") : I18n.format("portality.display.public")), x + 40, 10 + (fontRenderer.FONT_HEIGHT + 1) * 1 + y, 0xFFFFFF);
        GlStateManager.color4f(1, 1, 1, 1);
        if (information.isPrivate()) {
            GlStateManager.disableDepthTest();
            Minecraft.getInstance().getTextureManager().bindTexture(new ResourceLocation(Portality.MOD_ID, "textures/gui/lock.png"));
            blit(x + 4, y + 14, 0, 0, 8, 8, 8, 8);
            GlStateManager.enableDepthTest();
        }
        RenderHelper.disableStandardItemLighting();
        if (portals.getSelectedPortal() == information) {
            Minecraft.getInstance().getTextureManager().bindTexture(new ResourceLocation(Portality.MOD_ID, "textures/gui/portals.png"));
            blit(x, y, 0, 210, 157, 22);
        }
        GlStateManager.popMatrix();
    }

    public PortalInformation getInformation() {
        return information;
    }
}
