package com.skolopendra.teleskola.input

import com.skolopendra.teleskola.configuration.Configuration

class TelemetryReaderFactory {

    companion object {
        @JvmStatic
        fun getTelemetryReader(
            inputFileName: String,
            telemetryFormat: TelemetryFormat,
            configuration: Configuration
        ): TelemetryReader =
            when (telemetryFormat) {
                TelemetryFormat.OPENTX -> OpenTxTelemetryReaderImpl(inputFileName, configuration)
            }
    }

}
