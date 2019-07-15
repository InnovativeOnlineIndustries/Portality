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
import com.hrznstudio.titanium.api.client.AssetTypes;
import com.hrznstudio.titanium.api.client.GenericAssetType;
import com.hrznstudio.titanium.api.client.IAsset;
import com.hrznstudio.titanium.api.client.IAssetType;
import com.hrznstudio.titanium.api.client.assets.types.IBackgroundAsset;
import com.hrznstudio.titanium.api.client.assets.types.ITankAsset;
import com.hrznstudio.titanium.client.gui.asset.IAssetProvider;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.awt.*;

public final class TileAssetProvider implements IAssetProvider {

    public static TileAssetProvider PROVIDER = new TileAssetProvider();
    private static final ResourceLocation LOCATION = new ResourceLocation(Portality.MOD_ID, "textures/gui/background.png");
    private final Point HOTBAR_POS = new Point(8, 160);
    private final Point INV_POS = new Point(8, 102);
    private final IAsset PROGRESS_BAR_BORDER = new IAsset() {
        @Override
        public Rectangle getArea() {
            return new Rectangle(211, 1, 11, 56);
        }

        @Override
        public ResourceLocation getResourceLocation() {
            return LOCATION;
        }
    };
    private final IAsset SLOT = new IAsset() {
        @Override
        public Rectangle getArea() {
            return new Rectangle(1, 185, 18, 18);
        }

        @Override
        public ResourceLocation getResourceLocation() {
            return LOCATION;
        }
    };
    private final IAsset ENERGY_BAR = new IAsset() {
        @Override
        public Rectangle getArea() {
            return new Rectangle(177, 94, 18, 46);
        }

        @Override
        public ResourceLocation getResourceLocation() {
            return LOCATION;
        }
    };
    private final IAsset ENERGY_FILL = new IAsset() {
        @Override
        public Rectangle getArea() {
            return new Rectangle(196, 97, 12, 40);
        }

        @Override
        public Point getOffset() {
            return new Point(3, 3);
        }

        @Override
        public ResourceLocation getResourceLocation() {
            return LOCATION;
        }
    };
    private final ITankAsset TANK = new ITankAsset() {
        @Override
        public int getFluidRenderPadding(Direction facing) {
            return 3;
        }

        @Override
        public Rectangle getArea() {
            return new Rectangle(177, 1, 18, 46);
        }

        @Override
        public ResourceLocation getResourceLocation() {
            return LOCATION;
        }
    };
    private final IBackgroundAsset BACKGROUND = new IBackgroundAsset() {
        @Override
        public Point getInventoryPosition() {
            return INV_POS;
        }

        @Override
        public Point getHotbarPosition() {
            return HOTBAR_POS;
        }

        @Override
        public Rectangle getArea() {
            return new Rectangle(0, 0, 176, 184);
        }

        @Override
        public ResourceLocation getResourceLocation() {
            return LOCATION;
        }
    };
    private final IAsset PROGRESS_BAR_BACKGROUND = new IAsset() {
        @Override
        public Rectangle getArea() {
            return new Rectangle(229, 1, 5, 50);
        }

        @Override
        public Point getOffset() {
            return new Point(3, 3);
        }

        @Override
        public ResourceLocation getResourceLocation() {
            return LOCATION;
        }
    };
    private final IAsset PROGRESS_BAR_FILL = new IAsset() {
        @Override
        public Rectangle getArea() {
            return new Rectangle(223, 1, 5, 50);
        }

        @Override
        public Point getOffset() {
            return new Point(3, 3);
        }

        @Override
        public ResourceLocation getResourceLocation() {
            return LOCATION;
        }
    };
    private final IAsset BUTTON_SIDENESS_DISABLED = new IAsset() {
        @Override
        public Rectangle getArea() {
            return new Rectangle(196, 1, 14, 14);
        }

        @Override
        public ResourceLocation getResourceLocation() {
            return LOCATION;
        }
    };
    private final IAsset BUTTON_SIDENESS_ENABLED = new IAsset() {
        @Override
        public Rectangle getArea() {
            return new Rectangle(196, 16, 14, 14);
        }

        @Override
        public ResourceLocation getResourceLocation() {
            return LOCATION;
        }
    };
    private final IAsset BUTTON_SIDENESS_PULL = new IAsset() {
        @Override
        public Rectangle getArea() {
            return new Rectangle(196, 31, 14, 14);
        }

        @Override
        public ResourceLocation getResourceLocation() {
            return LOCATION;
        }
    };
    private final IAsset BUTTON_SIDENESS_PUSH = new IAsset() {

        @Override
        public Rectangle getArea() {
            return new Rectangle(196, 46, 14, 14);
        }

        @Override
        public ResourceLocation getResourceLocation() {
            return LOCATION;
        }
    };
    public static final IAssetType<IAsset> AA_BUTTON_IO_INPUT = new GenericAssetType<>(IAssetProvider.DEFAULT_PROVIDER::getAsset, IAsset.class);
    public static final IAssetType<IAsset> AA_BUTTON_IO_OUTPUT = new GenericAssetType<>(IAssetProvider.DEFAULT_PROVIDER::getAsset, IAsset.class);
    private final IAsset BUTTON_SIDENESS_MANAGER = new IAsset() {
        @Override
        public Rectangle getArea() {
            return new Rectangle(1, 231, 14, 14);
        }

        @Override
        public ResourceLocation getResourceLocation() {
            return LOCATION;
        }
    };
    private final IAsset BUTTON_IO_INPUT = new IAsset() {
        @Override
        public Rectangle getArea() {
            return new Rectangle(178, 141, 14, 14);
        }

        @Override
        public ResourceLocation getResourceLocation() {
            return LOCATION;
        }
    };

    private final IAsset BUTTON_IO_OUTPUT = new IAsset() {
        @Override
        public Rectangle getArea() {
            return new Rectangle(178, 156, 14, 14);
        }

        @Override
        public ResourceLocation getResourceLocation() {
            return LOCATION;
        }
    };

    TileAssetProvider() {
    }

    @Nullable
    @Override
    public <T extends IAsset> T getAsset(IAssetType<T> assetType) {
        if (assetType == AssetTypes.BACKGROUND)
            return assetType.castOrDefault(BACKGROUND);
        if (assetType == AssetTypes.ENERGY_BACKGROUND)
            return assetType.castOrDefault(ENERGY_BAR);
        if (assetType == AssetTypes.ENERGY_BAR)
            return assetType.castOrDefault(ENERGY_FILL);
        if (assetType == AssetTypes.PROGRESS_BAR_BACKGROUND)
            return assetType.castOrDefault(PROGRESS_BAR_BACKGROUND);
        if (assetType == AssetTypes.PROGRESS_BAR)
            return assetType.castOrDefault(PROGRESS_BAR_FILL);
        if (assetType == AssetTypes.SLOT)
            return assetType.castOrDefault(SLOT);
        if (assetType == AssetTypes.TANK)
            return assetType.castOrDefault(TANK);
        if (assetType == AssetTypes.PROGRESS_BAR_BORDER)
            return assetType.castOrDefault(PROGRESS_BAR_BORDER);
        if (assetType == AssetTypes.BUTTON_SIDENESS_DISABLED)
            return assetType.castOrDefault(BUTTON_SIDENESS_DISABLED);
        if (assetType == AssetTypes.BUTTON_SIDENESS_ENABLED)
            return assetType.castOrDefault(BUTTON_SIDENESS_ENABLED);
        if (assetType == AssetTypes.BUTTON_SIDENESS_PULL)
            return assetType.castOrDefault(BUTTON_SIDENESS_PULL);
        if (assetType == AssetTypes.BUTTON_SIDENESS_PUSH)
            return assetType.castOrDefault(BUTTON_SIDENESS_PUSH);
        if (assetType == AssetTypes.BUTTON_SIDENESS_MANAGER)
            return assetType.castOrDefault(BUTTON_SIDENESS_MANAGER);
        if (assetType == AA_BUTTON_IO_INPUT)
            return assetType.castOrDefault(BUTTON_IO_INPUT);
        if (assetType == AA_BUTTON_IO_OUTPUT)
            return assetType.castOrDefault(BUTTON_IO_OUTPUT);
        return null;
    }
}