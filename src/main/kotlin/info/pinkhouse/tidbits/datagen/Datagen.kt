package info.pinkhouse.tidbits.datagen

import info.pinkhouse.tidbits.CompressedBlock
import me.shedaniel.cloth.api.datagen.v1.DataGeneratorHandler
import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint
import java.nio.file.Paths
import kotlin.system.exitProcess

val dataHandler = DataGeneratorHandler.create(Paths.get("../tidbits/src/generated/resources"))

class DatagenTask : PreLaunchEntrypoint {
//    companion object {
//        @JvmStatic()
//        @JvmName("main")
//        fun main(args: Array<String>) {
//
//        }
//    }

    override fun onPreLaunch() {
        println("Datagen output at ${dataHandler.output.toString()}")
        CompressedBlock.forAll {
            println("Running datagen for ${it.ident.toString()} with subIdent = ${it.subIdent.toString()}, ogBlockIdent = ${it.ogBlockIdent.toString()}, compressionLevel = ${it.compressionLevel}")
            it.registerDatagen(dataHandler)
        }
        dataHandler.run()
        exitProcess(0)
    }
}