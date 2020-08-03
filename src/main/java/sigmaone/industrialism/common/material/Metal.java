package sigmaone.industrialism.common.material;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.tool.attribute.v1.FabricToolTags;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.item.Item;
import sigmaone.industrialism.Industrialism;

import static sigmaone.industrialism.common.RegistryHelper.*;

public class Metal {
    public Item ingot;
    public Item nugget;
    /*
    public static Item dust;
    public static Item plate;
    public static Item wire;
    public static Item gear;
    public static Item rod;
    */

    public Block block;
    public Block ore;

    public Metal(String name, float hardness) {
        ingot = registerItem(name+"_ingot", new Item(new Item.Settings().group(Industrialism.INDUSTRIALISM_MATERIALS)));
        nugget = registerItem(name+"_nugget", new Item(new Item.Settings().group(Industrialism.INDUSTRIALISM_MATERIALS)));
        block = registerBlock(name+"_block", new Block(FabricBlockSettings.of(Material.METAL).hardness(hardness).breakByTool(FabricToolTags.PICKAXES, 1)), Industrialism.INDUSTRIALISM_MATERIALS);
    }

    public Metal addOre(String name, int miningLevel, int veins, int size, int low_y, int high_y) {
        ore = registerBlock(name+"_ore", new Block(FabricBlockSettings.of(Material.STONE).hardness(3.0f).breakByTool(FabricToolTags.PICKAXES, miningLevel)), Industrialism.INDUSTRIALISM_MATERIALS);
        registerOreGen(0, ore, size, veins, low_y, 0, high_y);
        return this;
    }
}
