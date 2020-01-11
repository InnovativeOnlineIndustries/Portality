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
import com.buuz135.portality.gui.button.PortalSettingButton;
import com.hrznstudio.titanium.api.client.AssetTypes;
import com.hrznstudio.titanium.api.client.IAsset;
import com.hrznstudio.titanium.api.client.IAssetType;
import com.hrznstudio.titanium.api.client.assets.types.IBackgroundAsset;
import com.hrznstudio.titanium.client.screen.asset.IAssetProvider;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.awt.*;

public class PortalityAssetProvider implements IAssetProvider {

    public static PortalityAssetProvider PROVIDER = new PortalityAssetProvider();
    private static ResourceLocation LOCATION = new ResourceLocation(Portality.MOD_ID, "textures/gui/controller.png");

    private final IBackgroundAsset BACKGROUND = new IBackgroundAsset() {
        @Override
        public Point getInventoryPosition() {
            return new Point(0, 0);
        }

        @Override
        public Point getHotbarPosition() {
            return new Point(0, 0);
        }

        @Override
        public Rectangle getArea() {
            return new Rectangle(0, 0, 175, 110);
        }

        @Override
        public ResourceLocation getResourceLocation() {
            return LOCATION;
        }
    };

    private final IAsset RENAME = new IAsset() {
        @Override
        public Rectangle getArea() {
            return new Rectangle(176, 0, 20, 20);
        }

        @Override
        public ResourceLocation getResourceLocation() {
            return LOCATION;
        }
    };
    private final IAsset PRIVATE = new IAsset() {
        @Override
        public Rectangle getArea() {
            return new Rectangle(176, 21, 20, 20);
        }

        @Override
        public ResourceLocation getResourceLocation() {
            return LOCATION;
        }
    };
    private final IAsset PUBLIC = new IAsset() {
        @Override
        public Rectangle getArea() {
            return new Rectangle(197, 21, 20, 20);
        }

        @Override
        public ResourceLocation getResourceLocation() {
            return LOCATION;
        }
    };
    private final IAsset NAME_HIDDEN = new IAsset() {
        @Override
        public Rectangle getArea() {
            return new Rectangle(176, 42, 20, 20);
        }

        @Override
        public ResourceLocation getResourceLocation() {
            return LOCATION;
        }
    };
    private final IAsset NAME_SHOWN = new IAsset() {
        @Override
        public Rectangle getArea() {
            return new Rectangle(197, 42, 20, 20);
        }

        @Override
        public ResourceLocation getResourceLocation() {
            return LOCATION;
        }
    };
    private final IAsset BOTH_DIRECTION = new IAsset() {
        @Override
        public Rectangle getArea() {
            return new Rectangle(176, 63, 20, 20);
        }

        @Override
        public ResourceLocation getResourceLocation() {
            return LOCATION;
        }
    };
    private final IAsset SEND = new IAsset() {
        @Override
        public Rectangle getArea() {
            return new Rectangle(197, 63, 20, 20);
        }

        @Override
        public ResourceLocation getResourceLocation() {
            return LOCATION;
        }
    };
    private final IAsset RECEIVE = new IAsset() {
        @Override
        public Rectangle getArea() {
            return new Rectangle(218, 63, 20, 20);
        }

        @Override
        public ResourceLocation getResourceLocation() {
            return LOCATION;
        }
    };

    @Nullable
    @Override
    public <T extends IAsset> T getAsset(IAssetType<T> assetType) {
        if (assetType == AssetTypes.BACKGROUND)
            return assetType.castOrDefault(BACKGROUND);
        if (assetType == PortalSettingButton.RENAME)
            return assetType.castOrDefault(RENAME);
        if (assetType == PortalSettingButton.PRIVATE)
            return assetType.castOrDefault(PRIVATE);
        if (assetType == PortalSettingButton.PUBLIC)
            return assetType.castOrDefault(PUBLIC);
        if (assetType == PortalSettingButton.NAME_HIDDEN)
            return assetType.castOrDefault(NAME_HIDDEN);
        if (assetType == PortalSettingButton.NAME_SHOWN)
            return assetType.castOrDefault(NAME_SHOWN);
        if (assetType == PortalSettingButton.BOTH_DIRECTION)
            return assetType.castOrDefault(BOTH_DIRECTION);
        if (assetType == PortalSettingButton.SEND)
            return assetType.castOrDefault(SEND);
        if (assetType == PortalSettingButton.RECEIVE)
            return assetType.castOrDefault(RECEIVE);

        return null;
    }
}
