package info.pinkhouse.tidbits

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import me.shedaniel.cloth.api.datagen.v1.DataGeneratorHandler
import me.shedaniel.cloth.api.datagen.v1.LootTableData
import me.shedaniel.cloth.api.datagen.v1.ModelStateData
import me.shedaniel.cloth.api.datagen.v1.RecipeData
import net.fabricmc.fabric.api.`object`.builder.v1.block.FabricBlockSettings
import net.minecraft.advancement.criterion.ImpossibleCriterion
import net.minecraft.block.Block
import net.minecraft.block.Material
import net.minecraft.client.render.model.json.JsonUnbakedModel
import net.minecraft.data.server.recipe.ShapedRecipeJsonFactory
import net.minecraft.item.BlockItem
import net.minecraft.item.Item
import net.minecraft.item.ItemGroup
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry
import java.nio.file.Paths

class CompressedBlock(val ogBlock: String, val compressionLevel: Int) {
    companion object {
        fun fromName(name: String): CompressedBlock {
            var regex = Regex("compressed_(\\s+)_(\\s+)_(\\d)x")
            var matches = regex.findAll(name).toList().first().groupValues
            var (namespace, path, compressionLevel) = matches
            return CompressedBlock(
                Identifier(
                    namespace,
                    path
                ).toString(), compressionLevel.toInt()
            )
        }
        fun fromIdentifier(ident: Identifier): CompressedBlock {
            return CompressedBlock.fromName(ident.path)
        }
        fun forAll(f: (CompressedBlock) -> Unit) {
            for (inBlock in listOf("minecraft:cobblestone", "minecraft:dirt")) {
                for (compressionLevel in (1..5)) {
                    f(CompressedBlock(inBlock, compressionLevel))
                }
            }
        }
    }

    val ogBlockIdent: Identifier
        get() = Identifier(
            ogBlock
        )

    val ident: Identifier
        get() = Identifier(
            "tidbits",
            "compressed_${ogBlockIdent.namespace}_${ogBlockIdent.path}_${compressionLevel}x"
        )
    val subIdent: Identifier
        get() = if (compressionLevel > 1) Identifier(
            "tidbits",
            "compressed_${ogBlockIdent.namespace}_${ogBlockIdent.path}_${compressionLevel - 1}x"
        ) else ogBlockIdent

    val recipe: JsonObject
        get() {
        var out = JsonObject();
        out.addProperty("type", "minecraft:crafting_shaped")

        var pattern = JsonArray()
        pattern.add("AAA")
        pattern.add("AAA")
        pattern.add("AAA")
        out.add("pattern", pattern)

        var key = JsonObject()
        var keyEntry = JsonObject()
        keyEntry.addProperty("item", subIdent.toString())
        key.add("A", keyEntry)
        out.add("key", key)

        var result = JsonObject()
        result.addProperty("item", ident.toString())
        result.addProperty("count", 1)
        out.add("result", result)

        return out
    }

    val model: JsonUnbakedModel
        get() {
        var out = JsonUnbakedModel.deserialize(
            """
            {
              "parent": "block/cube_all",
              "textures": {
                "all": "tidbits:block/compressed_cobblestone_1x"
              }
            }
        """.trimIndent()
        )
        out.id = ident.toString()
        return out
    }
    val blockstate: String get() {
        return """
            {
              "parent": "block/cube_all",
              "textures": {
                "all": "tidbits:block/compressed_cobblestone_1x"
              }
            }""".trimIndent()
    }


    private fun registerRecipe(recipes: RecipeData) {
        ShapedRecipeJsonFactory.create(Registry.ITEM.get(ident))
            .pattern("AAA")
            .pattern("AAA")
            .pattern("AAA")
            .input('A', Registry.ITEM.get(subIdent))
            .criterion("impossible",
                ImpossibleCriterion.Conditions()
            )
            .offerTo(recipes)
        IDataProvider.
    }

    private fun registerModelState(modelStates: ModelStateData) {
        modelStates.addSingletonCubeAll(Registry.BLOCK.get(ident))
    }

    private fun registerLootTable(lootTable: LootTableData) {
        lootTable.registerBlockDropSelf(Registry.BLOCK.get(ident))
    }

    fun registerDatagen(handler: DataGeneratorHandler) {
//        registerRecipe(handler.recipes)
        registerModelState(handler.modelStates)
//        registerLootTable(handler.lootTables)
    }

    fun register() {
        var block = Block(
            FabricBlockSettings.of(
                Material.METAL
            ).hardness(5.0f)
        )
        Registry.register(
            Registry.BLOCK,
            ident,
            block
        )
        Registry.register(
            Registry.ITEM,
            ident,
            BlockItem(
                block,
                Item.Settings()
                    .group(ItemGroup.MISC)
            )
        )

    }
}