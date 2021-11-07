package com.buuz135.portality.item;

import com.buuz135.portality.Portality;
import com.hrznstudio.titanium.item.BasicItem;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import org.apache.commons.lang3.text.WordUtils;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Locale;

public class TeleportationTokenItem extends BasicItem {

    public TeleportationTokenItem() {
        super("portality:teleportation_token",new Properties().maxStackSize(1).group(Portality.TAB));
    }

    @Override
    public ActionResultType onItemUse(ItemUseContext context) {
        CompoundNBT compoundNBT = context.getItem().getOrCreateTag();
        compoundNBT.putString("Dimension", context.getWorld().getDimensionKey().getLocation().toString());
        compoundNBT.putInt("X", context.getPos().getX());
        compoundNBT.putInt("Y", context.getPos().getY());
        compoundNBT.putInt("Z", context.getPos().getZ());
        compoundNBT.putString("Direction", context.getPlacementHorizontalFacing().name());
        return ActionResultType.SUCCESS;
    }

    @Override
    public boolean hasTooltipDetails(@Nullable Key key) {
        return key == null;
    }

    @Override
    public void addTooltipDetails(@Nullable Key key, ItemStack stack, List<ITextComponent> tooltip, boolean advanced) {
        super.addTooltipDetails(key, stack, tooltip, advanced);
        if (key == null && stack.hasTag()){
            String dimension =  stack.getOrCreateTag().getString("Dimension");
            if (dimension.contains(":")){
                dimension = dimension.split(":")[1];
            }
            tooltip.add(new TranslationTextComponent("portality.display.dimension").append(new StringTextComponent(WordUtils.capitalize(dimension))).mergeStyle(TextFormatting.GRAY));
            tooltip.add(new TranslationTextComponent("portality.display.position").append(new StringTextComponent( stack.getOrCreateTag().getInt("X") + ", " + stack.getOrCreateTag().getInt("Y") + ", " +stack.getOrCreateTag().getInt("Z"))).mergeStyle(TextFormatting.GRAY));
            tooltip.add(new TranslationTextComponent("portality.display.direction").append(new StringTextComponent( WordUtils.capitalize(stack.getOrCreateTag().getString("Direction").toLowerCase(Locale.ROOT)))).mergeStyle(TextFormatting.GRAY));

        }
    }
}
