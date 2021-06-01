package com.skolopendra.teleskola.configuration

data class TelemetryField(
    val name: String,
    val visibleName: String? = name,
    val visible: Boolean = true,
    val type: TelemetryFieldType = TelemetryFieldType.TEXT,
    val position: TelemetryFieldPosition
)
