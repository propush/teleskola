package com.skolopendra.teleskola.output

import com.skolopendra.teleskola.input.TelemetryValue
import com.skolopendra.teleskola.output.exception.TelemetryWriterException

interface TelemetryWriter {

    @Throws(TelemetryWriterException::class)
    fun init()

    @Throws(TelemetryWriterException::class)
    fun writeData(data: List<TelemetryValue>)

    @Throws(TelemetryWriterException::class)
    fun finish()

}
