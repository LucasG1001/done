package com.done.app.navigation

import kotlinx.serialization.Serializable

@Serializable
data object TodayRoute

@Serializable
data class DetailRoute(val habitId: Long)

@Serializable
data class ManageRoute(val habitId: Long = 0L)

@Serializable
data object StatsRoute
