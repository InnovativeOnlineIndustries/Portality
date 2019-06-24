package com.buuz135.portality.gui.button;

import com.hrznstudio.titanium.api.IFactory;
import com.hrznstudio.titanium.api.client.GenericAssetType;
import com.hrznstudio.titanium.api.client.IAsset;
import com.hrznstudio.titanium.api.client.IAssetType;
import com.hrznstudio.titanium.api.client.IGuiAddon;
import com.hrznstudio.titanium.block.tile.button.PosButton;
import com.hrznstudio.titanium.client.gui.addon.StateButtonAddon;
import com.hrznstudio.titanium.client.gui.addon.StateButtonInfo;
import com.hrznstudio.titanium.client.gui.asset.IAssetProvider;

import java.util.Collections;
import java.util.List;

public abstract class PortalSettingButton extends PosButton {

    public static final IAssetType<IAsset> RENAME = new GenericAssetType<>(IAssetProvider.DEFAULT_PROVIDER::getAsset, IAsset.class);
    public static final IAssetType<IAsset> PRIVATE = new GenericAssetType<>(IAssetProvider.DEFAULT_PROVIDER::getAsset, IAsset.class);
    public static final IAssetType<IAsset> PUBLIC = new GenericAssetType<>(IAssetProvider.DEFAULT_PROVIDER::getAsset, IAsset.class);
    public static final IAssetType<IAsset> NAME_HIDDEN = new GenericAssetType<>(IAssetProvider.DEFAULT_PROVIDER::getAsset, IAsset.class);
    public static final IAssetType<IAsset> NAME_SHOWN = new GenericAssetType<>(IAssetProvider.DEFAULT_PROVIDER::getAsset, IAsset.class);
    public static final IAssetType<IAsset> BOTH_DIRECTION = new GenericAssetType<>(IAssetProvider.DEFAULT_PROVIDER::getAsset, IAsset.class);
    public static final IAssetType<IAsset> SEND = new GenericAssetType<>(IAssetProvider.DEFAULT_PROVIDER::getAsset, IAsset.class);
    public static final IAssetType<IAsset> RECEIVE = new GenericAssetType<>(IAssetProvider.DEFAULT_PROVIDER::getAsset, IAsset.class);

    private StateButtonInfo[] infos;

    public PortalSettingButton(int posX, int posY, StateButtonInfo... infos) {
        super(posX, posY, 20, 20);
        this.infos = infos;
    }

    @Override
    public List<IFactory<? extends IGuiAddon>> getGuiAddons() {
        return Collections.singletonList(() -> new StateButtonAddon(this, infos) {
            @Override
            public int getState() {
                return PortalSettingButton.this.getState();
            }
        });
    }

    public abstract int getState();

    public StateButtonInfo[] getInfos() {
        return infos;
    }
}
