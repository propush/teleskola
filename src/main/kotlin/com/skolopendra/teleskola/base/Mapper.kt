package com.skolopendra.teleskola.base

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule

val objectMapper = ObjectMapper().registerKotlinModule()
