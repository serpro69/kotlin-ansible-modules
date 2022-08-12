#!/usr/bin/env -S kotlinc -script --

// WANT_JSON

@file:DependsOn("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.7.10")
@file:DependsOn("com.fasterxml.jackson.core:jackson-databind:2.13.3")
@file:DependsOn("com.fasterxml.jackson.module:jackson-module-kotlin:2.13.3")

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import kotlin.system.exitProcess
import java.io.File

//@JsonIgnoreProperties(ignoreUnknown = true)
data class ModuleArgs(
    val name: String = "World",
)

data class Response(
    val msg: String,
    val changed: Boolean,
    val failed: Boolean,
)

fun returnResponse(response: Response) {
    mapper.writeValue(System.out, response)
    if (response.failed) exitProcess(1) else exitProcess(0)
}

val mapper by lazy {
    ObjectMapper().also {
        it.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
    }
}

val moduleArgs: ModuleArgs = mapper.readValue(File(args[0]), ModuleArgs::class.java)

returnResponse(Response("Hello, ${moduleArgs.name}", false, false))
