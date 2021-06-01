package com.skolopendra.teleskola.configuration

import com.fasterxml.jackson.databind.ObjectMapper
import java.io.File

class ConfigReader(
    private val mapper: ObjectMapper
) {

    fun readConfig(configFile: String): Configuration =
        mapper.readValue(File(configFile), Configuration::class.java)

}
