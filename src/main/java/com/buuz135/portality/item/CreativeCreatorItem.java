package com.buuz135.portality.item;

import com.buuz135.portality.Portality;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

public class CreativeCreatorItem extends Item {

    public static CreativeCreatorItem INSTANCE = new CreativeCreatorItem();

    public CreativeCreatorItem() {
        this.setRegistryName(new ResourceLocation(Portality.MOD_ID, "creative_creator"));
        this.setTranslationKey(getRegistryName().toString());
        this.setCreativeTab(Portality.TAB);
    }

    @SideOnly(Side.CLIENT)
    public void registerRender() {
        ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation(this.getRegistryName(), "inventory"));
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        tooltip.add("Makes a portal creative:");
        tooltip.add(" - The portal doesn't consume power");
        tooltip.add(" - The portal can't be broken");
        tooltip.add("(Right Click a controller to use)");
    }
}
