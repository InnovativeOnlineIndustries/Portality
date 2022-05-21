package com.buuz135.portality.datagen;

import com.buuz135.portality.Portality;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.tags.BlockTags;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

public class PortalityBlockTagsProvider extends BlockTagsProvider {

    public PortalityBlockTagsProvider(DataGenerator p_126530_, String modId, @Nullable ExistingFileHelper existingFileHelper) {
        super(p_126530_, modId, existingFileHelper);
    }

    @Override
    protected void addTags() {
        ForgeRegistries.BLOCKS.getValues().stream().filter(block -> block.getRegistryName().getNamespace().equals(Portality.MOD_ID)).forEach(block -> this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(block));
    }
}
