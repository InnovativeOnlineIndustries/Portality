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
package com.buuz135.portality.gui;

import com.buuz135.portality.Portality;
import com.buuz135.portality.network.PortalRenameMessage;
import com.buuz135.portality.tile.ControllerTile;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class RenameControllerScreen extends Screen {

    private final ControllerTile controller;
    private TextFieldWidget textFieldWidget;
    private Button confirm;

    public RenameControllerScreen(ControllerTile controller) {
        super(new TranslationTextComponent("portality.gui.controller.rename"));
        this.controller = controller;
    }

    @Override
    protected void init() {
        super.init();
        int textFieldWidth = 140;
        textFieldWidget = new TextFieldWidget(Minecraft.getInstance().fontRenderer, width / 2 - textFieldWidth / 2, height / 2 - 10, textFieldWidth, 18, new StringTextComponent(""));
        textFieldWidget.setCanLoseFocus(false);
        textFieldWidget.setMaxStringLength(28);
        textFieldWidget.setSelectionPos(0);
        textFieldWidget.setText(this.controller.getPortalDisplayName());
        textFieldWidget.setFocused2(true);
        addButton(textFieldWidget);
        //this.setFocused(this.textFieldWidget);

        confirm = new Button(width / 2 + textFieldWidth / 2 + 5, height / 2 - 10, 50, 18, new StringTextComponent("Confirm"), button -> {
            Portality.NETWORK.get().sendToServer(new PortalRenameMessage(textFieldWidget.getText(), controller.getPos()));
            Minecraft.getInstance().displayGuiScreen(new ControllerScreen(controller));
        });
        addButton(confirm);
    }

    @Override
    public void render(MatrixStack matrixStack, int p_render_1_, int p_render_2_, float p_render_3_) {
        this.renderBackground(matrixStack, 0);//draw tinted background
        super.render(matrixStack, p_render_1_, p_render_2_, p_render_3_);
        //textFieldWidget.render(p_render_1_, p_render_2_, p_render_3_);
        String rename = new TranslationTextComponent("portality.gui.controller.rename").getString();
        Minecraft.getInstance().fontRenderer.drawStringWithShadow(matrixStack, rename, width / 2 - Minecraft.getInstance().fontRenderer.getStringWidth(rename) / 2, height / 2 - 30, 0xFFFFFF);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void onClose() { //onClose

    }
}
