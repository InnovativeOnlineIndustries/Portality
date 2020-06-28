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
package com.buuz135.portality.gui.button;


import com.buuz135.portality.Portality;
import com.buuz135.portality.data.PortalInformation;
import com.buuz135.portality.gui.PortalsScreen;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.button.ImageButton;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;

public class GuiButtonImagePortal extends ImageButton {

    private PortalInformation information;
    private PortalsScreen portals;

    public GuiButtonImagePortal(PortalsScreen guiPortals, PortalInformation information, int x, int y, int xSize, int ySize, int textureX, int textureY, int offset, ResourceLocation location) {
        super(x, y, xSize, ySize, textureX, textureY, offset, location, p_onPress_1_ -> {
        });
        this.information = information;
        this.portals = guiPortals;
    }

    @Override
    public void func_230431_b_(MatrixStack stack, int p_renderButton_1_, int p_renderButton_2_, float p_renderButton_3_) {
        RenderSystem.color4f(1, 1, 1, 1);
        super.func_230431_b_(stack, p_renderButton_1_, p_renderButton_2_, p_renderButton_3_);
        RenderSystem.pushMatrix();
        //RenderSystem.setupGui3DDiffuseLighting();
        Minecraft.getInstance().getItemRenderer().renderItemAndEffectIntoGUI(information.getDisplay(), field_230690_l_ + 5, field_230691_m_ + 3);
        FontRenderer fontRenderer = Minecraft.getInstance().fontRenderer;
        TextFormatting color = TextFormatting.RESET;
        if (information.isPrivate()) color = TextFormatting.GOLD;
        if (information.isActive()) color = TextFormatting.RED;
        fontRenderer.func_238405_a_(stack, color + information.getName().substring(0, Math.min(information.getName().length(), 25)), field_230690_l_ + 28, 7 + field_230691_m_, func_231047_b_(p_renderButton_1_, p_renderButton_2_) ? 16777120 : 0xFFFFFFFF);
        //fontRenderer.drawString(color + (information.isPrivate() ? I18n.format("portality.display.private") : I18n.format("portality.display.public")), x + 40, 10 + (fontRenderer.FONT_HEIGHT + 1) * 1 + y, 0xFFFFFF);
        RenderSystem.color4f(1, 1, 1, 1);
        if (information.isPrivate()) {
            RenderSystem.disableDepthTest();
            Minecraft.getInstance().getTextureManager().bindTexture(new ResourceLocation(Portality.MOD_ID, "textures/gui/lock.png"));
            AbstractGui.func_238463_a_(stack, field_230690_l_ + 4, field_230691_m_ + 14, 0, 0, 8, 8, 8, 8);
            RenderSystem.enableDepthTest();
        }
        RenderHelper.disableStandardItemLighting();
        if (portals.getSelectedPortal() == information) {
            Minecraft.getInstance().getTextureManager().bindTexture(new ResourceLocation(Portality.MOD_ID, "textures/gui/portals.png"));
            Minecraft.getInstance().currentScreen.func_238474_b_(stack, field_230690_l_, field_230691_m_, 0, 210, 157, 22);
        }
        RenderSystem.popMatrix();
    }

    public PortalInformation getInformation() {
        return information;
    }
}
