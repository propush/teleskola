package com.skolopendra.teleskola.input

import com.skolopendra.teleskola.base.objectMapper
import com.skolopendra.teleskola.configuration.ConfigReader
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test


internal class OpenTxTelemetryReaderImplTest {

    private lateinit var openTxTelemetryReaderImpl: OpenTxTelemetryReaderImpl

    @BeforeEach
    fun setUp() {
        openTxTelemetryReaderImpl = OpenTxTelemetryReaderImpl(
            this::class.java.classLoader.getResource("test_opentx_telemetry.csv")!!.file,
            ConfigReader(objectMapper).readConfig("teleskola.json")
        )
    }

    @Test
    fun header() {
        assertEquals(
            46,
            openTxTelemetryReaderImpl.header().size
        )
    }

    @Test
    fun values() {
        var counter = 0
        openTxTelemetryReaderImpl.values().forEach {
            assertEquals(
                "2021-05-26",
                it[0].toString()
            )
            counter++
        }
        assertEquals(4, counter)
    }

}
