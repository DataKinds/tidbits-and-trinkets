package info.pinkhouse.tidbits

import com.google.gson.JsonObject
import net.minecraft.client.render.model.json.JsonUnbakedModel
import net.minecraft.util.Identifier

// For support join https://discord.gg/v6v4pMv

val COMPRESSED_BLOCKS: MutableList<CompressedBlock> = mutableListOf()
val RECIPES: Map<Identifier, JsonObject> get() = COMPRESSED_BLOCKS.groupBy({ it.ident }, { it.recipe }).mapValues { it.value.first() }
val MODELS: Map<Identifier, JsonUnbakedModel> get() = COMPRESSED_BLOCKS.groupBy({ it.ident }, { it.model }).mapValues { it.value.first() }

@Suppress("unused")
fun init() {
    // This code runs as soon as Minecraft is in a mod-load-ready state.
    // However, some things (like resources) may still be uninitialized.
    // Proceed with mild caution.

    println("Hello Fabric world!")
    CompressedBlock.forAll {
        println("Registering ${it.ident.toString()} with subIdent = ${it.subIdent.toString()}, ogBlockIdent = ${it.ogBlockIdent.toString()}, compressionLevel = ${it.compressionLevel}")
        it.register()
    }
}

