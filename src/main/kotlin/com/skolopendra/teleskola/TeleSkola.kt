package com.skolopendra.teleskola

import com.skolopendra.teleskola.base.objectMapper
import com.skolopendra.teleskola.configuration.ConfigReader
import com.skolopendra.teleskola.input.TelemetryFormat
import com.skolopendra.teleskola.input.TelemetryReaderFactory
import com.skolopendra.teleskola.output.ImageMagickTelemetryWriterImpl
import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.cli.default
import kotlinx.cli.required

fun main(args: Array<String>) {
    val parser = ArgParser("teleskola")
    val telemetryInput by parser.option(
        ArgType.String, shortName = "i", description = "Telemetry input file"
    ).required()
    val telemetryInputFormat by parser.option(
        ArgType.Choice<TelemetryFormat>(), shortName = "f",
        description = "Format for output file"
    ).default(TelemetryFormat.OPENTX)
    val videoOutputTemp by parser.option(
        ArgType.String, shortName = "o", description = "Video output file"
    )
    val configFile by parser.option(
        ArgType.String, shortName = "c", description = "Configuration file"
    ).default("teleskola.json")
    parser.parse(args)
    val videoOutput = videoOutputTemp ?: changeExtension(telemetryInput, "tele.mp4")
    println(
        """Configuration file: $configFile
        |Telemetry input: [$telemetryInputFormat] $telemetryInput
        |Video output: $videoOutput
        """.trimMargin()
    )
    processTelemetry(configFile, telemetryInput, telemetryInputFormat, videoOutput)
}

fun processTelemetry(
    configFile: String,
    telemetryInput: String,
    telemetryInputFormat: TelemetryFormat,
    videoOutput: String
) {
    val configuration = ConfigReader(objectMapper).readConfig(configFile)
    println("Configuration read: $configuration")
    val telemetryReader = TelemetryReaderFactory.getTelemetryReader(telemetryInput, telemetryInputFormat, configuration)
    println("Read headers:\n${telemetryReader.header()}")
    val telemetryWriter = ImageMagickTelemetryWriterImpl(configuration, telemetryReader.header(), videoOutput)
    telemetryReader.values().forEach(telemetryWriter::writeData)
    telemetryWriter.finish()
    println("Done.")
}

fun changeExtension(fileName: String, newExtension: String): String =
    fileName.replaceAfterLast('.', newExtension, "$fileName.$newExtension")
