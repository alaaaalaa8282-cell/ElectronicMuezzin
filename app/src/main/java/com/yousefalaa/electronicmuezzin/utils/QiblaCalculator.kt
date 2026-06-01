package com.yousefalaa.electronicmuezzin.utils

import kotlin.math.*

object QiblaCalculator {

    private const val MAKKAH_LAT = 21.4225
    private const val MAKKAH_LNG = 39.8262

    fun getQiblaDirection(lat: Double, lng: Double): Double {
        val lat1 = Math.toRadians(lat)
        val lat2 = Math.toRadians(MAKKAH_LAT)
        val deltaLng = Math.toRadians(MAKKAH_LNG - lng)

        val x = sin(deltaLng) * cos(lat2)
        val y = cos(lat1) * sin(lat2) - sin(lat1) * cos(lat2) * cos(deltaLng)

        val bearing = Math.toDegrees(atan2(x, y))
        return (bearing + 360) % 360
    }

    fun getDistanceToMakkah(lat: Double, lng: Double): Double {
        val R = 6371000.0
        val lat1 = Math.toRadians(lat)
        val lat2 = Math.toRadians(MAKKAH_LAT)
        val deltaLat = Math.toRadians(MAKKAH_LAT - lat)
        val deltaLng = Math.toRadians(MAKKAH_LNG - lng)

        val a = sin(deltaLat / 2).pow(2) +
                cos(lat1) * cos(lat2) * sin(deltaLng / 2).pow(2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))

        return R * c
    }
}
