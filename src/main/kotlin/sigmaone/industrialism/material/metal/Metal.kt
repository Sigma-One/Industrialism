package sigmaone.industrialism.material.metal

import net.fabricmc.fabric.api.`object`.builder.v1.block.FabricBlockSettings
import net.fabricmc.fabric.api.item.v1.FabricItemSettings
import net.fabricmc.fabric.api.tool.attribute.v1.FabricToolTags
import net.minecraft.block.Block
import net.minecraft.entity.EquipmentSlot
import net.minecraft.item.ArmorItem
import net.minecraft.item.Item
import net.minecraft.item.ToolItem
import net.minecraft.recipe.Ingredient
import sigmaone.industrialism.Industrialism
import sigmaone.industrialism.item.tool.*
import sigmaone.industrialism.util.RegistryHelper

class Metal(  // Misc
        private val name: String) {
    // Intermediates
    var ingot: Item? = null
    var nugget: Item? = null

    /*
    public Item dust;
    public Item wire;
    public Item gear;
     */
    var plate: Item? = null
    var stick: Item? = null

    // Blocks
    var block: Block? = null
    var ore: Block? = null

    // Armour
    var armourMaterial: MetalArmourMaterial? = null
    var helmet: Item? = null
    var chestplace: Item? = null
    var leggings: Item? = null
    var boots: Item? = null

    // Tools
    var toolMaterial: MetalToolMaterial? = null
    var pickaxe: ToolItem? = null
    var axe: ToolItem? = null
    var shovel: ToolItem? = null
    var sword: ToolItem? = null
    var hoe: ToolItem? = null
    fun addIngot(): Metal {
        ingot = RegistryHelper.registerItem(name + "_ingot", Item(Item.Settings().group(Industrialism.MATERIALS_TAB)))
        return this
    }

    fun addNugget(): Metal {
        nugget = RegistryHelper.registerItem(name + "_nugget", Item(Item.Settings().group(Industrialism.MATERIALS_TAB)))
        return this
    }

    fun addStick(): Metal {
        stick = RegistryHelper.registerItem(name + "_stick", Item(Item.Settings().group(Industrialism.MATERIALS_TAB)))
        return this
    }

    fun addPlate(): Metal {
        plate = RegistryHelper.registerItem(name + "_plate", Item(Item.Settings().group(Industrialism.MATERIALS_TAB)))
        return this
    }

    fun addBlock(miningLevel: Int, hardness: Float): Metal {
        block = RegistryHelper.registerBlock(
                name + "_block",
                Block(FabricBlockSettings.of(Industrialism.MATERIAL_METAL)
                        .hardness(hardness)
                        .breakByTool(FabricToolTags.PICKAXES, miningLevel)
                        .requiresTool()),
                FabricItemSettings().group(Industrialism.MATERIALS_TAB)
        )
        return this
    }

    fun addOre(oreName: String, miningLevel: Int, veins: Int, size: Int, lowY: Int, highY: Int): Metal {
        ore = RegistryHelper.registerBlock(
                oreName + "_ore",
                Block(FabricBlockSettings.of(Industrialism.MATERIAL_STONE)
                        .hardness(3.0f)
                        .breakByTool(FabricToolTags.PICKAXES, miningLevel)
                        .requiresTool()),
                FabricItemSettings().group(Industrialism.MATERIALS_TAB)
        )
        RegistryHelper.registerOreGen(oreName + "ore", 0, ore!!, size, veins, lowY, 0, highY)
        return this
    }

    fun addToolMaterial(baseAttackDamage: Int, durability: Int, miningLevel: Int, miningSpeed: Float, enchantability: Int): Metal {
        toolMaterial = MetalToolMaterial(miningLevel, durability, miningSpeed, (baseAttackDamage - 1).toFloat(), enchantability) { Ingredient.ofItems(ingot) }
        return this
    }

    fun addPickaxe(attackDamage: Int, attackSpeed: Float): Metal {
        pickaxe = RegistryHelper.registerItem(name + "_pickaxe", ToolPickaxe(toolMaterial, attackDamage, attackSpeed - 4.0f, Item.Settings().group(Industrialism.TOOLS_TAB)))
        return this
    }

    fun addAxe(attackDamage: Float, attackSpeed: Float): Metal {
        axe = RegistryHelper.registerItem(name + "_axe", ToolAxe(toolMaterial, attackDamage, attackSpeed - 4.0f, Item.Settings().group(Industrialism.TOOLS_TAB)))
        return this
    }

    fun addShovel(attackDamage: Float, attackSpeed: Float): Metal {
        shovel = RegistryHelper.registerItem(name + "_shovel", ToolShovel(toolMaterial, attackDamage, attackSpeed - 4.0f, Item.Settings().group(Industrialism.TOOLS_TAB)))
        return this
    }

    fun addSword(attackDamage: Int, attackSpeed: Float): Metal {
        sword = RegistryHelper.registerItem(name + "_sword", ToolSword(toolMaterial, attackDamage, attackSpeed - 4.0f, Item.Settings().group(Industrialism.TOOLS_TAB)))
        return this
    }

    fun addHoe(attackDamage: Int, attackSpeed: Float): Metal {
        hoe = RegistryHelper.registerItem(name + "_hoe", ToolHoe(toolMaterial, attackDamage, attackSpeed - 4.0f, Item.Settings().group(Industrialism.TOOLS_TAB)))
        return this
    }

    fun addArmour(durability: IntArray, protection: IntArray, toughness: Float, knockbackResistance: Float, enchantability: Int): Metal {
        armourMaterial = MetalArmourMaterial(name, durability, protection, toughness, knockbackResistance, enchantability) { Ingredient.ofItems(ingot) }
        helmet = RegistryHelper.registerItem(name+"_helmet", ArmorItem(armourMaterial, EquipmentSlot.HEAD, Item.Settings().group(Industrialism.TOOLS_TAB)))
        chestplace = RegistryHelper.registerItem(name+"_chestplate", ArmorItem(armourMaterial, EquipmentSlot.CHEST, Item.Settings().group(Industrialism.TOOLS_TAB)))
        leggings = RegistryHelper.registerItem(name+"_leggings", ArmorItem(armourMaterial, EquipmentSlot.LEGS, Item.Settings().group(Industrialism.TOOLS_TAB)))
        boots = RegistryHelper.registerItem(name+"_boots", ArmorItem(armourMaterial, EquipmentSlot.FEET, Item.Settings().group(Industrialism.TOOLS_TAB)))
        return this
    }
}