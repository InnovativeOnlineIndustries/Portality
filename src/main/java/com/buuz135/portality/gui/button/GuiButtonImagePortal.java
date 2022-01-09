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
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.resources.ResourceLocation;

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
    public void renderButton(PoseStack stack, int p_renderButton_1_, int p_renderButton_2_, float p_renderButton_3_) {
        RenderSystem.color4f(1, 1, 1, 1);
        super.renderButton(stack, p_renderButton_1_, p_renderButton_2_, p_renderButton_3_);
        RenderSystem.pushMatrix();
        //RenderSystem.setupGui3DDiffuseLighting();
        Minecraft.getInstance().getItemRenderer().renderAndDecorateItem(information.getDisplay(), x + 5, y + 3);
        Font fontRenderer = Minecraft.getInstance().font;
        ChatFormatting color = ChatFormatting.RESET;
        if (information.isPrivate()) color = ChatFormatting.GOLD;
        if (information.isActive()) color = ChatFormatting.RED;
        fontRenderer.drawShadow(stack, color + information.getName().substring(0, Math.min(information.getName().length(), 25)), x + 28, 7 + y, isMouseOver(p_renderButton_1_, p_renderButton_2_) ? 16777120 : 0xFFFFFFFF);
        //fontRenderer.drawString(color + (information.isPrivate() ? I18n.format("portality.display.private") : I18n.format("portality.display.public")), x + 40, 10 + (fontRenderer.FONT_HEIGHT + 1) * 1 + y, 0xFFFFFF);
        RenderSystem.color4f(1, 1, 1, 1);
        if (information.isPrivate()) {
            RenderSystem.disableDepthTest();
            Minecraft.getInstance().getTextureManager().bind(new ResourceLocation(Portality.MOD_ID, "textures/gui/lock.png"));
            GuiComponent.blit(stack, x + 4, y + 14, 0, 0, 8, 8, 8, 8);
            RenderSystem.enableDepthTest();
        }
        Lighting.turnOff();
        if (portals.getSelectedPortal() == information) {
            Minecraft.getInstance().getTextureManager().bind(new ResourceLocation(Portality.MOD_ID, "textures/gui/portals.png"));
            Minecraft.getInstance().screen.blit(stack, x, y, 0, 210, 157, 22);
        }
        RenderSystem.popMatrix();
    }

    public PortalInformation getInformation() {
        return information;
    }
}
