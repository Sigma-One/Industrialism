package sigmaone.industrialism.common.material.metal;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.tool.attribute.v1.FabricToolTags;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ToolItem;
import net.minecraft.recipe.Ingredient;
import sigmaone.industrialism.Industrialism;
import sigmaone.industrialism.common.item.tool.*;

import static sigmaone.industrialism.common.RegistryHelper.*;

public class Metal {
    // Misc
    private String name;

    // Intermediates
    public Item ingot;
    public Item nugget;
    /*
    public static Item dust;
    public static Item plate;
    public static Item wire;
    public static Item gear;
    public static Item rod;
    */

    // Blocks
    public Block block;
    public Block ore;

    // Tools
    public MetalToolMaterial toolMaterial;
    public ToolItem pickaxe;
    public ToolItem axe;
    public ToolItem shovel;
    public ToolItem sword;
    public ToolItem hoe;

    public Metal(String metal_name, float hardness) {
        name = metal_name;
        ingot = registerItem(name+"_ingot", new Item(new Item.Settings().group(Industrialism.MATERIALS_TAB)));
        nugget = registerItem(name+"_nugget", new Item(new Item.Settings().group(Industrialism.MATERIALS_TAB)));
        block = registerBlock(name+"_block", new Block(FabricBlockSettings.of(Industrialism.MATERIAL_METAL).hardness(hardness).breakByTool(FabricToolTags.PICKAXES, 1).requiresTool()), Industrialism.MATERIALS_TAB);
    }

    public Metal addOre(String ore_name, int miningLevel, int veins, int size, int low_y, int high_y) {
        ore = registerBlock(ore_name+"_ore", new Block(FabricBlockSettings.of(Industrialism.MATERIAL_STONE).hardness(3.0f).breakByTool(FabricToolTags.PICKAXES, miningLevel).requiresTool()), Industrialism.MATERIALS_TAB);
        registerOreGen(0, ore, size, veins, low_y, 0, high_y);
        return this;
    }

    public Metal addToolMaterial(int baseAttackDamage, int durability, int miningLevel, float miningSpeed, int enchantability) {
        toolMaterial = new MetalToolMaterial(miningLevel, durability, miningSpeed, baseAttackDamage-1, enchantability, () -> {return Ingredient.ofItems(this.ingot);});
        return this;
    }

    public Metal addPickaxe(int attackDamage, float attackSpeed) {
        pickaxe = (ToolItem) registerItem(name+"_pickaxe", new ToolPickaxe(this.toolMaterial, attackDamage, attackSpeed-4.0f, new Item.Settings().group(Industrialism.TOOLS_TAB)));
        return this;
    }
    public Metal addAxe(float attackDamage, float attackSpeed) {
        axe = (ToolItem) registerItem(name+"_axe", new ToolAxe(this.toolMaterial, attackDamage, attackSpeed-4.0f, new Item.Settings().group(Industrialism.TOOLS_TAB)));
        return this;
    }
    public Metal addShovel(float attackDamage, float attackSpeed) {
        shovel = (ToolItem) registerItem(name+"_shovel", new ToolShovel(this.toolMaterial, attackDamage, attackSpeed-4.0f, new Item.Settings().group(Industrialism.TOOLS_TAB)));
        return this;
    }
    public Metal addSword(int attackDamage, float attackSpeed) {
        sword = (ToolItem) registerItem(name+"_sword", new ToolSword(this.toolMaterial, attackDamage, attackSpeed-4.0f, new Item.Settings().group(Industrialism.TOOLS_TAB)));
        return this;
    }
    public Metal addHoe(int attackDamage, float attackSpeed) {
        hoe = (ToolItem) registerItem(name+"_hoe", new ToolHoe(this.toolMaterial, attackDamage, attackSpeed-4.0f, new Item.Settings().group(Industrialism.TOOLS_TAB)));
        return this;
    }
}
