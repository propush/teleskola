package com.skolopendra.teleskola.configuration

data class Configuration(
    val tempDir: String,
    val telemetryInterval: Int,
    val video: VideoSettings,
    val generalFieldSettings: GeneralFieldSettings,
    val telemetryFields: List<TelemetryField>
)

data class VideoSettings(
    val dimensions: Dimensions
)

data class GeneralFieldSettings(
    val fontSize: Int,
    val color: ColorSettings
)

data class ColorSettings(
    val background: String,
    val foreground: String
)

