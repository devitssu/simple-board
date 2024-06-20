package com.teamsparta.simpleboard

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class SimpleBoardApplication

fun main(args: Array<String>) {
    runApplication<SimpleBoardApplication>(*args)
}
