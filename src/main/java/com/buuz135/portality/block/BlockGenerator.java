package com.buuz135.portality.block;

import com.buuz135.portality.tile.TileGenerator;
import com.hrznstudio.titanium.api.IFactory;
import com.hrznstudio.titanium.block.BlockRotation;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

import javax.annotation.Nonnull;

public class BlockGenerator extends BlockRotation<TileGenerator> {

    public BlockGenerator() {
        super("generator", Block.Properties.create(Material.ROCK), TileGenerator.class);
    }

    @Override
    public IFactory<TileGenerator> getTileEntityFactory() {
        return TileGenerator::new;
    }

    @Nonnull
    @Override
    public RotationType getRotationType() {
        return RotationType.FOUR_WAY;
    }
}
