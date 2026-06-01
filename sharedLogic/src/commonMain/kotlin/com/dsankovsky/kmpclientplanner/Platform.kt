package com.dsankovsky.kmpclientplanner

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform