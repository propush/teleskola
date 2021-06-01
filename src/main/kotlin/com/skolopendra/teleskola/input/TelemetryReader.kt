package com.skolopendra.teleskola.input

import com.skolopendra.teleskola.configuration.TelemetryField

interface TelemetryReader {

    fun header(): List<TelemetryField>
    fun values(): Sequence<List<TelemetryValue>>

}
