package sigmaone.industrialism.material.metal;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.tool.attribute.v1.FabricToolTags;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ToolItem;
import net.minecraft.recipe.Ingredient;
import sigmaone.industrialism.Industrialism;
import sigmaone.industrialism.item.tool.*;

import static sigmaone.industrialism.util.RegistryHelper.*;

public class Metal {
    // Misc
    private final String name;

    // Intermediates
    public Item ingot;
    public Item nugget;
    /*
    public Item dust;
    public Item plate;
    public Item wire;
    public Item gear;
     */
    public Item stick;

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

    public Metal(String metal_name) {
        this.name = metal_name;
    }

    public Metal addIngot() {
        this.ingot = registerItem(this.name +"_ingot", new Item(new Item.Settings().group(Industrialism.MATERIALS_TAB)));
        return this;
    }

    public Metal addNugget() {
        this.nugget = registerItem(this.name +"_nugget", new Item(new Item.Settings().group(Industrialism.MATERIALS_TAB)));
        return this;
    }

    public Metal addStick() {
        this.stick = registerItem(this.name+"_stick", new Item(new Item.Settings().group(Industrialism.MATERIALS_TAB)));
        return this;
    }

    public Metal addBlock(int miningLevel, float hardness) {
        this.block = registerBlock(this.name +"_block", new Block(FabricBlockSettings.of(Industrialism.MATERIAL_METAL).hardness(hardness).breakByTool(FabricToolTags.PICKAXES, miningLevel).requiresTool()), Industrialism.MATERIALS_TAB);
        return this;
    }

    public Metal addOre(String ore_name, int miningLevel, int veins, int size, int low_y, int high_y) {
        this.ore = registerBlock(ore_name+"_ore", new Block(FabricBlockSettings.of(Industrialism.MATERIAL_STONE).hardness(3.0f).breakByTool(FabricToolTags.PICKAXES, miningLevel).requiresTool()), Industrialism.MATERIALS_TAB);
        registerOreGen(ore_name+"ore", 0, this.ore, size, veins, low_y, 0, high_y);
        return this;
    }

    public Metal addToolMaterial(int baseAttackDamage, int durability, int miningLevel, float miningSpeed, int enchantability) {
        this.toolMaterial = new MetalToolMaterial(miningLevel, durability, miningSpeed, baseAttackDamage-1, enchantability, () -> {return Ingredient.ofItems(this.ingot);});
        return this;
    }

    public Metal addPickaxe(int attackDamage, float attackSpeed) {
        this.pickaxe = registerItem(this.name +"_pickaxe", new ToolPickaxe(this.toolMaterial, attackDamage, attackSpeed-4.0f, new Item.Settings().group(Industrialism.TOOLS_TAB)));
        return this;
    }
    public Metal addAxe(float attackDamage, float attackSpeed) {
        this.axe = registerItem(this.name +"_axe", new ToolAxe(this.toolMaterial, attackDamage, attackSpeed-4.0f, new Item.Settings().group(Industrialism.TOOLS_TAB)));
        return this;
    }
    public Metal addShovel(float attackDamage, float attackSpeed) {
        this.shovel = registerItem(this.name +"_shovel", new ToolShovel(this.toolMaterial, attackDamage, attackSpeed-4.0f, new Item.Settings().group(Industrialism.TOOLS_TAB)));
        return this;
    }
    public Metal addSword(int attackDamage, float attackSpeed) {
        this.sword = registerItem(this.name +"_sword", new ToolSword(this.toolMaterial, attackDamage, attackSpeed-4.0f, new Item.Settings().group(Industrialism.TOOLS_TAB)));
        return this;
    }
    public Metal addHoe(int attackDamage, float attackSpeed) {
        this.hoe = registerItem(this.name +"_hoe", new ToolHoe(this.toolMaterial, attackDamage, attackSpeed-4.0f, new Item.Settings().group(Industrialism.TOOLS_TAB)));
        return this;
    }
}
