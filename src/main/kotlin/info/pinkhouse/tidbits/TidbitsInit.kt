package info.pinkhouse.tidbits

import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import net.fabricmc.fabric.api.`object`.builder.v1.block.FabricBlockSettings
import net.minecraft.block.Block
import net.minecraft.block.Material
import net.minecraft.client.render.model.json.JsonUnbakedModel
import net.minecraft.item.BlockItem
import net.minecraft.item.Item
import net.minecraft.item.ItemGroup
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry
// For support join https://discord.gg/v6v4pMv

val COMPRESSED_BLOCKS: MutableList<CompressedBlock> = mutableListOf()
val RECIPES: Map<Identifier, JsonObject> get() = COMPRESSED_BLOCKS.groupBy({ it.ident }, { it.recipe }).mapValues { it.value.first() }
val MODELS: Map<Identifier, JsonUnbakedModel> get() = COMPRESSED_BLOCKS.groupBy({ it.ident }, { it.model }).mapValues { it.value.first() }

class CompressedBlock(val ogBlock: String, val compressionLevel: Int) {
    companion object {
        fun fromName(name: String): CompressedBlock {
            var regex = Regex("compressed_(\\s+)_(\\s+)_(\\d)x")
            var matches = regex.findAll(name).toList().first().groupValues
            var (namespace, path, compressionLevel) = matches
            return CompressedBlock(Identifier(namespace, path).toString(), compressionLevel.toInt())
        }
        fun fromIdentifier(ident: Identifier): CompressedBlock {
            return CompressedBlock.fromName(ident.path)
        }
    }

    val ogBlockIdent: Identifier get() = Identifier(ogBlock)

    val ident: Identifier get() = Identifier("tidbits", "compressed_${ogBlockIdent.namespace}_${ogBlockIdent.path}_${compressionLevel}x")
    val subIdent: Identifier get() = if (compressionLevel <= 1) Identifier("tidbits", "compressed_${ogBlockIdent.namespace}_${ogBlockIdent.path}_${compressionLevel - 1}x") else ogBlockIdent

    val recipe: JsonObject get() {
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

    val model: JsonUnbakedModel get() {
        var out = JsonUnbakedModel.deserialize("""
            {
              "parent": "block/cube_all",
              "textures": {
                "all": "tidbits:block/compressed_cobblestone_1x"
              }
            }
        """.trimIndent())
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


    fun register() {
        var block = Block(FabricBlockSettings.of(Material.METAL).hardness(5.0f))
        Registry.register(Registry.BLOCK, ident, block)
        Registry.register(Registry.ITEM, ident, BlockItem(block, Item.Settings().group(ItemGroup.MISC)))
    }
}

@Suppress("unused")
fun init() {
    // This code runs as soon as Minecraft is in a mod-load-ready state.
    // However, some things (like resources) may still be uninitialized.
    // Proceed with mild caution.

    println("Hello Fabric world!")
    for (inBlock in listOf("minecraft:cobblestone", "minecraft:dirt")) {
        for (compressionLevel in (1..5)) {
            var cb = CompressedBlock(inBlock, compressionLevel)
            cb.register()
        }
    }
}

