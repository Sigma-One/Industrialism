package sigmaone.industrialism.material.metal

import net.fabricmc.fabric.api.`object`.builder.v1.block.FabricBlockSettings
import net.fabricmc.fabric.api.item.v1.FabricItemSettings
import net.fabricmc.fabric.api.tool.attribute.v1.FabricToolTags
import net.minecraft.block.Block
import net.minecraft.entity.EquipmentSlot
import net.minecraft.item.ArmorItem
import net.minecraft.item.Item
import net.minecraft.item.Items
import net.minecraft.item.ToolItem
import net.minecraft.recipe.Ingredient
import net.minecraft.util.Identifier
import sigmaone.industrialism.Industrialism
import sigmaone.industrialism.item.ItemWireSpool
import sigmaone.industrialism.item.tool.*
import sigmaone.industrialism.util.RegistryHelper
import sigmaone.industrialism.util.contentbuilder.BlockBuilder
import sigmaone.industrialism.util.contentbuilder.ItemBuilder
import sigmaone.industrialism.util.datagen.model.ModelGenerator
import sigmaone.industrialism.util.datagen.recipe.CraftingRecipeGenerator
import sigmaone.industrialism.util.datagen.tag.TagGenerator

class Metal(private val name: String) {
    // Intermediates
    var ingot: Item? = null
    var nugget: Item? = null
    var plate: Item? = null
    var stick: Item? = null
    var gear: Item? = null
    var wire: Item? = null
    var wireSpool: ItemWireSpool? = null

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
        ingot = ItemBuilder.getStandard(name + "_ingot", Item(Item.Settings().group(Industrialism.MATERIALS_TAB)))
        TagGenerator.tagItem(Identifier("c", "${name}_ingots"), ingot!!)
        return this
    }

    fun setIngot(item: Item): Metal {
        ingot = item
        TagGenerator.tagItem(Identifier("c", "${name}_ingots"), ingot!!)
        return this
    }

    fun addNugget(withDefaultRecipe: Boolean = true): Metal {
        nugget = ItemBuilder.getStandard(name + "_nugget", Item(Item.Settings().group(Industrialism.MATERIALS_TAB)))
        TagGenerator.tagItem(Identifier("c", "${name}_nuggets"), nugget!!)
        if (withDefaultRecipe) {
            CraftingRecipeGenerator.generateShapelessRecipe(
                Identifier(Industrialism.MOD_ID, name + "_ingot_nugget_unpack"),
                result = nugget!!,
                resultCount = 9,
                itemIngredients = arrayOf(ingot!!)
            )
            CraftingRecipeGenerator.generateShapedRecipe(
                Identifier(Industrialism.MOD_ID, name + "_nugget_ingot_pack"),
                result = ingot!!,
                tagKeys = hashMapOf(
                    '#' to Identifier("c", "${name}_nuggets")
                ),
                itemKeys = hashMapOf(
                    '=' to nugget!!
                ),
                pattern = arrayOf(
                    "###",
                    "#=#",
                    "###"
                )
            )
        }
        return this
    }

    fun setNugget(item: Item): Metal {
        nugget = item
        TagGenerator.tagItem(Identifier("c", "${name}_nuggets"), nugget!!)
        return this
    }

    fun addStick(withDefaultRecipe: Boolean = true): Metal {
        stick = ItemBuilder.getStandard(name + "_stick", Item(Item.Settings().group(Industrialism.MATERIALS_TAB)))
        TagGenerator.tagItem(Identifier("c", "${name}_sticks"), stick!!)
        if (withDefaultRecipe) {
            CraftingRecipeGenerator.generateShapedRecipe(
                Identifier(Industrialism.MOD_ID, name + "_stick"),
                result = stick!!,
                resultCount = 4,
                tagKeys = hashMapOf(
                    '#' to Identifier("c", "${name}_ingots")
                ),
                pattern = arrayOf(
                    "   ",
                    " # ",
                    " # "
                )
            )
        }
        return this
    }

    fun addPlate(withDefaultRecipe: Boolean = true): Metal {
        plate = ItemBuilder.getStandard(name + "_plate", Item(Item.Settings().group(Industrialism.MATERIALS_TAB)))
        TagGenerator.tagItem(Identifier("c", "${name}_plates"), plate!!)
        if (withDefaultRecipe) {
            CraftingRecipeGenerator.generateShapelessRecipe(
                Identifier(Industrialism.MOD_ID, name + "_plate"),
                result = plate!!,
                itemIngredients = arrayOf(Industrialism.FORGE_HAMMER),
                tagIngredients = arrayOf(Identifier("c", "${name}_ingots")),
                damagesTools = true
            )
        }
        return this
    }

    fun addGear(withDefaultRecipe: Boolean = true): Metal {
        gear = ItemBuilder.getStandard(name + "_gear", Item(Item.Settings().group(Industrialism.MATERIALS_TAB)))
        TagGenerator.tagItem(Identifier("c", "${name}_gears"), gear!!)
        if (withDefaultRecipe) {
            CraftingRecipeGenerator.generateShapedRecipe(
                Identifier(Industrialism.MOD_ID, name + "_gear"),
                result = gear!!,
                resultCount = 4,
                tagKeys = hashMapOf(
                    '#' to Identifier("c", "${name}_plates"),
                    '|' to Identifier("c", "${name}_sticks")
                ),
                pattern = arrayOf(
                    " | ",
                    "|#|",
                    " | "
                )
            )
        }
        return this
    }

    fun addWire(
        maxLength: Int,
        thickness: Float,
        colour: IntArray,
        withDefaultRecipe: Boolean = true,
    ): Metal {
        wire = ItemBuilder.getStandard(name + "_wire", Item(Item.Settings().group(Industrialism.MATERIALS_TAB)))
        TagGenerator.tagItem(Identifier("c", "${name}_wires"), wire!!)
        wireSpool = ItemBuilder.getStandard(name + "_wire_spool", ItemWireSpool(Item.Settings().group(Industrialism.MATERIALS_TAB), maxLength, thickness, colour))
        TagGenerator.tagItem(Identifier("c", "${name}_wire_spools"), wireSpool!!)
        if (withDefaultRecipe) {
            CraftingRecipeGenerator.generateShapelessRecipe(
                Identifier(Industrialism.MOD_ID, name + "_wire"),
                result = wire!!,
                resultCount = 4,
                itemIngredients = arrayOf(Items.SHEARS),
                tagIngredients = arrayOf(Identifier("c", "${name}_plates")),
                damagesTools = true
            )
            CraftingRecipeGenerator.generateShapedRecipe(
                Identifier(Industrialism.MOD_ID, name + "_wire_spool"),
                result = wireSpool!!,
                resultCount = 4,
                tagKeys = hashMapOf(
                    '#' to Identifier("c", "${name}_wires"),
                    '|' to Identifier("c", "wood_sticks")
                ),
                pattern = arrayOf(
                    "###",
                    "#|#",
                    "###"
                )
            )
        }

        return this
    }

    fun addBlock(miningLevel: Int, hardness: Float, withDefaultRecipe: Boolean = true, withDefaultItem: Boolean = true): Metal {
        val builder = BlockBuilder(
            "${name}_block",
            Block(FabricBlockSettings.of(Industrialism.MATERIAL_METAL)
                      .hardness(hardness)
                      .breakByTool(FabricToolTags.PICKAXES, miningLevel)
                      .requiresTool()
            )
        )

        if (withDefaultItem) {
            builder.generateItem(FabricItemSettings().group(Industrialism.MATERIALS_TAB))
        }

        builder.generateModel()
        builder.generateBlockState()

        block = builder.get()

        TagGenerator.tagItem(Identifier("c", "${name}_blocks"), block!!.asItem())
        TagGenerator.tagBlock(Identifier("c", "${name}_blocks"), block!!)

        if (withDefaultRecipe) {
            CraftingRecipeGenerator.generateShapelessRecipe(
                Identifier(Industrialism.MOD_ID, name + "_block_ingot_unpack"),
                result = ingot!!,
                resultCount = 9,
                itemIngredients = arrayOf(block!!.asItem())
            )
            CraftingRecipeGenerator.generateShapedRecipe(
                Identifier(Industrialism.MOD_ID, name + "_ingot_block_pack"),
                result = block!!.asItem(),
                tagKeys = hashMapOf(
                    '#' to Identifier("c", "${name}_ingots")
                ),
                itemKeys = hashMapOf(
                    '=' to ingot!!
                ),
                pattern = arrayOf(
                    "###",
                    "#=#",
                    "###"
                )
            )
        }
        return this
    }

    fun setBlock(item: Block): Metal {
        block = item
        TagGenerator.tagItem(Identifier("c", "${name}_blocks"), block!!.asItem())
        TagGenerator.tagBlock(Identifier("c", "${name}_blocks"), block!!)
        return this
    }

    fun addOre(
        oreName: String,
        miningLevel: Int,
        veins: Int,
        size: Int,
        lowY: Int,
        highY: Int,
        hardness: Float = 3.0f,
        withDefaultRecipe: Boolean = true,
        withDefaultItem: Boolean = true,
    ): Metal {
        val builder = BlockBuilder(
            "${oreName}_ore",
            Block(FabricBlockSettings.of(Industrialism.MATERIAL_STONE)
                      .hardness(hardness)
                      .breakByTool(FabricToolTags.PICKAXES, miningLevel)
                      .requiresTool()
            )
        )

        if (withDefaultItem) {
            builder.generateItem(FabricItemSettings().group(Industrialism.MATERIALS_TAB))
        }

        builder.generateModel()
        builder.generateBlockState()

        ore = builder.get()

        RegistryHelper.registerOreGen(oreName + "ore", 0, ore!!, size, veins, lowY, 0, highY)
        TagGenerator.tagItem(Identifier("c", "${name}_ores"), ore!!.asItem())
        TagGenerator.tagBlock(Identifier("c", "${name}_ores"), ore!!)
        return this
    }

    fun setOre(item: Block): Metal {
        ore = item
        TagGenerator.tagItem(Identifier("c", "${name}_ores"), ore!!.asItem())
        TagGenerator.tagBlock(Identifier("c", "${name}_ores"), ore!!)
        return this
    }

    fun addToolMaterial(baseAttackDamage: Float, durability: Int, miningLevel: Int, miningSpeed: Float, enchantability: Int): Metal {
        toolMaterial = MetalToolMaterial(miningLevel, durability, miningSpeed, (baseAttackDamage - 1f), enchantability) { Ingredient.ofItems(ingot) }
        return this
    }

    fun addPickaxe(attackDamage: Int, attackSpeed: Float, withDefaultRecipe: Boolean = true, withDefaultModel: Boolean = true): Metal {
        val builder = ItemBuilder(
            name + "_pickaxe",
            ToolPickaxe(toolMaterial, attackDamage, attackSpeed - 4.0f, Item.Settings().group(Industrialism.TOOLS_TAB))
        )
        if (withDefaultModel) {
            builder.generateModel(true)
        }
        pickaxe = builder.get()
        TagGenerator.tagItem(Identifier("c", "${name}_pickaxes"), pickaxe!!)
        TagGenerator.tagItem(Identifier("fabric", "pickaxes"), pickaxe!!)
        if (withDefaultRecipe) {
            CraftingRecipeGenerator.generateShapedRecipe(
                Identifier(Industrialism.MOD_ID, name + "_pickaxe"),
                result = pickaxe!!,
                tagKeys = hashMapOf(
                    '#' to Identifier("c", "${name}_ingots"),
                    '|' to Identifier("c", "wood_sticks")
                ),
                pattern = arrayOf(
                    "###",
                    " | ",
                    " | "
                )
            )
        }
        return this
    }

    fun addAxe(attackDamage: Float, attackSpeed: Float, withDefaultRecipe: Boolean = true, withDefaultModel: Boolean = true): Metal {
        val builder = ItemBuilder(
            name + "_axe",
            ToolAxe(toolMaterial, attackDamage, attackSpeed - 4.0f, Item.Settings().group(Industrialism.TOOLS_TAB))
        )
        if (withDefaultModel) {
            builder.generateModel(true)
        }
        axe = builder.get()
        TagGenerator.tagItem(Identifier("c", "${name}_axes"), axe!!)
        TagGenerator.tagItem(Identifier("fabric", "axes"), axe!!)
        if (withDefaultRecipe) {
            CraftingRecipeGenerator.generateShapedRecipe(
                Identifier(Industrialism.MOD_ID, name + "_axe"),
                result = axe!!,
                tagKeys = hashMapOf(
                    '#' to Identifier("c", "${name}_ingots"),
                    '|' to Identifier("c", "wood_sticks")
                ),
                pattern = arrayOf(
                    "## ",
                    "#| ",
                    " | "
                )
            )
        }
        if (withDefaultModel) {
            ModelGenerator.generateToolItemModel(axe!!)
        }
        return this
    }

    fun addShovel(attackDamage: Float, attackSpeed: Float, withDefaultRecipe: Boolean = true, withDefaultModel: Boolean = true): Metal {
        val builder = ItemBuilder(
            name + "_shovel",
            ToolShovel(toolMaterial, attackDamage, attackSpeed - 4.0f, Item.Settings().group(Industrialism.TOOLS_TAB))
        )
        if (withDefaultModel) {
            builder.generateModel(true)
        }
        shovel = builder.get()
        TagGenerator.tagItem(Identifier("c", "${name}_shovels"), shovel!!)
        TagGenerator.tagItem(Identifier("fabric", "shovels"), shovel!!)
        ModelGenerator.generateToolItemModel(shovel!!)
        if (withDefaultRecipe) {
            CraftingRecipeGenerator.generateShapedRecipe(
                Identifier(Industrialism.MOD_ID, name + "_shovel"),
                result = shovel!!,
                tagKeys = hashMapOf(
                    '#' to Identifier("c", "${name}_ingots"),
                    '|' to Identifier("c", "wood_sticks")
                ),
                pattern = arrayOf(
                    " # ",
                    " | ",
                    " | "
                )
            )
        }
        if (withDefaultModel) {
            ModelGenerator.generateToolItemModel(shovel!!)
        }
        return this
    }

    fun addSword(attackDamage: Int, attackSpeed: Float, withDefaultRecipe: Boolean = true, withDefaultModel: Boolean = true): Metal {
        val builder = ItemBuilder(
            name + "_sword",
            ToolSword(toolMaterial, attackDamage, attackSpeed - 4.0f, Item.Settings().group(Industrialism.TOOLS_TAB))
        )
        if (withDefaultModel) {
            builder.generateModel(true)
        }
        sword = builder.get()
        TagGenerator.tagItem(Identifier("c", "${name}_swords"), sword!!)
        TagGenerator.tagItem(Identifier("fabric", "swords"), sword!!)
        if (withDefaultRecipe) {
            CraftingRecipeGenerator.generateShapedRecipe(
                Identifier(Industrialism.MOD_ID, name + "_sword"),
                result = sword!!,
                tagKeys = hashMapOf(
                    '#' to Identifier("c", "${name}_ingots"),
                    '|' to Identifier("c", "wood_sticks")
                ),
                pattern = arrayOf(
                    " # ",
                    " # ",
                    " | "
                )
            )
        }
        if (withDefaultModel) {
            ModelGenerator.generateToolItemModel(sword!!)
        }
        return this
    }

    fun addHoe(attackDamage: Int, attackSpeed: Float, withDefaultRecipe: Boolean = true, withDefaultModel: Boolean = true): Metal {
        val builder = ItemBuilder(
            name + "_hoe",
            ToolHoe(toolMaterial, attackDamage, attackSpeed - 4.0f, Item.Settings().group(Industrialism.TOOLS_TAB))
        )
        if (withDefaultModel) {
            builder.generateModel(true)
        }
        hoe = builder.get()
        TagGenerator.tagItem(Identifier("c", "${name}_hoes"), hoe!!)
        TagGenerator.tagItem(Identifier("fabric", "hoes"), hoe!!)
        if (withDefaultRecipe) {
            CraftingRecipeGenerator.generateShapedRecipe(
                Identifier(Industrialism.MOD_ID, name + "_hoe"),
                result = hoe!!,
                tagKeys = hashMapOf(
                    '#' to Identifier("c", "${name}_ingots"),
                    '|' to Identifier("c", "wood_sticks")
                ),
                pattern = arrayOf(
                    "## ",
                    " | ",
                    " | "
                )
            )
        }
        if (withDefaultModel) {
            ModelGenerator.generateToolItemModel(hoe!!)
        }
        return this
    }

    fun addArmour(durability: IntArray, protection: IntArray, toughness: Float, knockbackResistance: Float, enchantability: Int, skipRecipe: Boolean = false): Metal {
        armourMaterial = MetalArmourMaterial(name, durability, protection, toughness, knockbackResistance, enchantability) { Ingredient.ofItems(ingot) }
        helmet = RegistryHelper.registerItem(name+"_helmet", ArmorItem(armourMaterial, EquipmentSlot.HEAD, Item.Settings().group(Industrialism.TOOLS_TAB)))
        TagGenerator.tagItem(Identifier("c", "${name}_helmets"), helmet!!)
        chestplace = RegistryHelper.registerItem(name+"_chestplate", ArmorItem(armourMaterial, EquipmentSlot.CHEST, Item.Settings().group(Industrialism.TOOLS_TAB)))
        TagGenerator.tagItem(Identifier("c", "${name}_chestplates"), chestplace!!)
        leggings = RegistryHelper.registerItem(name+"_leggings", ArmorItem(armourMaterial, EquipmentSlot.LEGS, Item.Settings().group(Industrialism.TOOLS_TAB)))
        TagGenerator.tagItem(Identifier("c", "${name}_leggings"), leggings!!)
        boots = RegistryHelper.registerItem(name+"_boots", ArmorItem(armourMaterial, EquipmentSlot.FEET, Item.Settings().group(Industrialism.TOOLS_TAB)))
        TagGenerator.tagItem(Identifier("c", "${name}_boots"), boots!!)
        return this
    }
}