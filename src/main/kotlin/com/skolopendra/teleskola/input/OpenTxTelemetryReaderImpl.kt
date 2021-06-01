package com.skolopendra.teleskola.input

import com.skolopendra.teleskola.configuration.Configuration
import com.skolopendra.teleskola.configuration.TelemetryField
import com.skolopendra.teleskola.configuration.TelemetryFieldPosition
import com.skolopendra.teleskola.configuration.TelemetryFieldType
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File

class OpenTxTelemetryReaderImpl(
    private val inputFileName: String,
    private val configuration: Configuration
) : TelemetryReader {

    private val log: Logger = LoggerFactory.getLogger(this::class.java)

    private val inputReader = File(inputFileName).bufferedReader()
    private val headerHolder: List<TelemetryField> = readHeader()

    private fun readHeader(): List<TelemetryField> =
        parseHeader(inputReader.readLine())

    private fun parseHeader(line: String): List<TelemetryField> =
        line
            .split(',')
            .map(::nameToTelemetryField)

    private fun nameToTelemetryField(name: String): TelemetryField =
        configuration.telemetryFields.singleOrNull { it.name == name } ?: defaultField(name)

    private fun defaultField(name: String): TelemetryField =
        TelemetryField(name, name, false, TelemetryFieldType.TEXT, TelemetryFieldPosition(0, 0))
            .also {
                log.warn("Unknown field: $it")
            }

    override fun header(): List<TelemetryField> = headerHolder

    override fun values(): Sequence<List<TelemetryValue>> =
        sequence {
            while (inputReader.ready()) {
                val line = inputReader.readLine()
                log.debug("Read line: $line")
                yield(readValues(line))
            }
        }

    private fun readValues(data: String): List<TelemetryValue> =
        data.split(',').mapIndexed { index, s -> dataToTelemetryValue(index, s) }

    private fun dataToTelemetryValue(index: Int, data: String): TelemetryValue {
        val telemetryField = headerHolder[index]
        return getTelemetryValue(telemetryField, data)
    }

    private fun getTelemetryValue(telemetryField: TelemetryField, data: String): TelemetryValue =
        when (telemetryField.type) {
            TelemetryFieldType.TEXT -> TelemetryValue.TextTelemetryValue(data)
        }

}
