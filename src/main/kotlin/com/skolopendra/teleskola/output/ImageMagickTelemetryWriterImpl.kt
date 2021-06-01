package com.skolopendra.teleskola.output

import com.skolopendra.teleskola.configuration.Configuration
import com.skolopendra.teleskola.configuration.TelemetryField
import com.skolopendra.teleskola.configuration.TelemetryFieldPosition
import com.skolopendra.teleskola.input.TelemetryValue
import com.skolopendra.teleskola.output.exception.TelemetryWriterException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File
import java.math.RoundingMode
import java.nio.file.Files.createTempDirectory

class ImageMagickTelemetryWriterImpl(
    private val configuration: Configuration,
    private val header: List<TelemetryField>,
    private val videoOutput: String
) : TelemetryWriter {

    private val log: Logger = LoggerFactory.getLogger(this::class.java)

    private lateinit var tempDir: File
    private lateinit var tempDirPath: String
    private var outputCounter: Long = 0
    private val dimensionsStr = with(configuration.video.dimensions) { "${x}x${y}" }

    init {
        init()
    }

    @Throws(TelemetryWriterException::class)
    override fun init() {
        outputCounter = 0
        tempDir = createTempDirectory(configuration.tempDir).toFile()
        tempDir.deleteOnExit()
        tempDirPath = tempDir.absolutePath
        log.info("Temp directory set to ${tempDir.absolutePath}")
    }

    @Throws(TelemetryWriterException::class)
    override fun writeData(data: List<TelemetryValue>) {
        val fileName = "${tempDirPath}/image${outputCounter.toString().padStart(6, '0')}.png"
        outputCounter++
        log.debug("Output file: $fileName")
        val telemetryStr = getTelemetryStr(data)
        val process = ProcessBuilder()
            .command(
                mutableListOf(
                    "convert",
                    "-size", dimensionsStr,
                    "xc:${configuration.generalFieldSettings.color.background}",
                    "-fill", configuration.generalFieldSettings.color.foreground,
                    "-pointsize", configuration.generalFieldSettings.fontSize.toString()
                ).apply {
                    addAll(telemetryStr)
                    add(fileName)
                }
            )
            .redirectOutput(ProcessBuilder.Redirect.INHERIT)
            .redirectError(ProcessBuilder.Redirect.INHERIT)
            .start()
        waitAndCheckResponseCode(process)
    }

    private fun getTelemetryStr(data: List<TelemetryValue>): List<String> =
        data
            .flatMapIndexed(::fieldToStr)


    private fun fieldToStr(index: Int, telemetryValue: TelemetryValue): List<String> {
        val telemetryField = header[index]
        if (!telemetryField.visible) {
            return emptyList()
        }
        return when (telemetryValue) {
            is TelemetryValue.TextTelemetryValue ->
                listOf(
                    "-draw",
                    "text ${posToStr(telemetryField.position)} '${valueToText(telemetryField, telemetryValue)}'"
                )
        }
    }

    private fun valueToText(
        telemetryField: TelemetryField,
        telemetryValue: TelemetryValue
    ) = when (telemetryField.visibleName?.isNotEmpty() == true) {
        true -> "${telemetryField.visibleName}: $telemetryValue"
        false -> telemetryValue.toString()
    }

    private fun posToStr(position: TelemetryFieldPosition): String = "${position.x},${position.y}"

    @Throws(TelemetryWriterException::class)
    override fun finish() {
        val frameRate =
            (1.0 / configuration.telemetryInterval).toBigDecimal().setScale(1, RoundingMode.HALF_UP).toString()
        log.info("Running finish process - ffmpeg, framerate = $frameRate")
        val process = ProcessBuilder()
            .command(
                listOf(
                    "ffmpeg",
                    "-y",
                    "-framerate",
                    frameRate,
                    "-pattern_type",
                    "glob",
                    "-i",
                    "*.png",
                    "-c:v",
                    "libx264",
                    "-r",
                    "25",
                    "-pix_fmt",
                    "yuv420p",
                    videoOutput
                )
            )
            .directory(tempDir)
            .redirectOutput(ProcessBuilder.Redirect.INHERIT)
            .redirectError(ProcessBuilder.Redirect.INHERIT)
            .start()
        waitAndCheckResponseCode(process)
        tempDir.deleteRecursively()
    }

    private fun waitAndCheckResponseCode(process: Process) {
        val rc = process.waitFor()
        if (rc != 0) {
            log.error("rc: $rc")
            log.error("err: ${process.errorStream.bufferedReader().readText()}")
            log.error("out: ${process.inputStream.bufferedReader().readText()}")
            throw TelemetryWriterException("Error running convert, rc=$rc")
        }
    }

}
