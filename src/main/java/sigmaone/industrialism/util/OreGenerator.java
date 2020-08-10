package sigmaone.industrialism.util;

import net.minecraft.block.Block;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.decorator.Decorator;
import net.minecraft.world.gen.decorator.RangeDecoratorConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.OreFeatureConfig;

public class OreGenerator {
    public void generate(int dimension, Biome biome, Block ore, int size, int veins, int bottom_offset, int top_offset, int max_y) {
        switch (dimension) {
            case 0: generateOverworld(biome, ore, size, veins, bottom_offset, top_offset, max_y);
        }
    }

    private void generateOverworld(Biome biome, Block ore, int size, int veins, int bottom_offset, int top_offset, int max_y) {
        if (biome.getCategory() != Biome.Category.NETHER && biome.getCategory() != Biome.Category.THEEND) {
            biome.addFeature(
                    GenerationStep.Feature.UNDERGROUND_ORES,
                    Feature.ORE.configure(
                            new OreFeatureConfig(OreFeatureConfig.Target.NATURAL_STONE, ore.getDefaultState(), size)
                    ).createDecoratedFeature(Decorator.COUNT_RANGE.configure(new RangeDecoratorConfig(veins, bottom_offset, top_offset, max_y)))
            );
        }
    }
}
