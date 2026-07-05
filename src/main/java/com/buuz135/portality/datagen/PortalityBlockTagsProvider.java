package com.buuz135.portality.datagen;

import com.buuz135.portality.Portality;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class PortalityBlockTagsProvider extends BlockTagsProvider {

    public PortalityBlockTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, String modId, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, modId, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        BuiltInRegistries.BLOCK.stream()
                .filter(block -> Portality.MOD_ID.equals(BuiltInRegistries.BLOCK.getKey(block).getNamespace()))
                .forEach(block -> this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(block));
    }
}
