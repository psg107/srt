package com.srt

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class SrtApplication

fun main(args: Array<String>) {
    runApplication<SrtApplication>(*args)
}
