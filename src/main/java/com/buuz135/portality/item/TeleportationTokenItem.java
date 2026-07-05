package com.buuz135.portality.item;

import com.buuz135.portality.Portality;
import com.buuz135.portality.tile.ControllerTile;
import com.hrznstudio.titanium.item.BasicItem;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.LevelReader;
import org.apache.commons.lang3.text.WordUtils;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Locale;

public class TeleportationTokenItem extends BasicItem {

    public TeleportationTokenItem() {
        super(new Properties().stacksTo(1));
        this.setItemGroup(Portality.TAB);
    }

    public static boolean hasTokenData(ItemStack stack) {
        return stack.has(DataComponents.CUSTOM_DATA);
    }

    @Override
    public boolean hasTooltipDetails(@Nullable Key key) {
        return key == null;
    }

    public static CompoundTag getTokenData(ItemStack stack) {
        return stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        if (context.getLevel().getBlockEntity(context.getClickedPos()) instanceof ControllerTile){
            return InteractionResult.PASS;
        }
        CompoundTag compoundNBT = getTokenData(context.getItemInHand());
        compoundNBT.putString("Dimension", context.getLevel().dimension().location().toString());
        compoundNBT.putInt("X", context.getClickedPos().getX());
        compoundNBT.putInt("Y", context.getClickedPos().getY());
        compoundNBT.putInt("Z", context.getClickedPos().getZ());
        compoundNBT.putString("Direction", context.getClickedFace().name());
        context.getItemInHand().set(DataComponents.CUSTOM_DATA, CustomData.of(compoundNBT));
        return InteractionResult.SUCCESS;
    }

    @Override
    public boolean doesSneakBypassUse(ItemStack stack, LevelReader world, BlockPos pos, Player player) {
        return world.getBlockEntity(pos) instanceof ControllerTile;
    }

    @Override
    public void addTooltipDetails(@Nullable Key key, ItemStack stack, List<Component> tooltip, boolean advanced) {
        super.addTooltipDetails(key, stack, tooltip, advanced);
        if (key == null && hasTokenData(stack)) {
            CompoundTag data = getTokenData(stack);
            String dimension = data.getString("Dimension");
            if (dimension.contains(":")){
                dimension = dimension.split(":")[1];
            }
            tooltip.add(Component.translatable("portality.display.dimension").append(Component.literal(WordUtils.capitalize(dimension))).withStyle(ChatFormatting.GRAY));
            tooltip.add(Component.translatable("portality.display.position").append(Component.literal(data.getInt("X") + ", " + data.getInt("Y") + ", " + data.getInt("Z"))).withStyle(ChatFormatting.GRAY));
            tooltip.add(Component.translatable("portality.display.direction").append(Component.literal(WordUtils.capitalize(data.getString("Direction").toLowerCase(Locale.ROOT)))).withStyle(ChatFormatting.GRAY));
        }
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return hasTokenData(stack);
    }
}
