/*
 * This file is part of Titanium
 * Copyright (C) 2019, Horizon Studio <contact@hrznstudio.com>.
 *
 * This code is licensed under GNU Lesser General Public License v3.0, the full license text can be found in LICENSE.txt
 */

package com.buuz135.portality.gui;

import com.buuz135.portality.Portality;
import com.hrznstudio.titanium.api.client.AssetTypes;
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
    private final Point HOTBAR_POS = new Point(8, 160);
    private final Point INV_POS = new Point(8, 102);
    private final IAsset PROGRESS_BAR_BORDER = () -> new Rectangle(211, 1, 11, 56);
    ResourceLocation LOCATION = new ResourceLocation(Portality.MOD_ID, "textures/gui/background.png");
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
        return null;
    }
}