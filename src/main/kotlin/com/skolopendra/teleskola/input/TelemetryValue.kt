package com.skolopendra.teleskola.input

sealed class TelemetryValue {

    data class TextTelemetryValue(val text: String) : TelemetryValue() {
        override fun toString(): String = text
    }

}
