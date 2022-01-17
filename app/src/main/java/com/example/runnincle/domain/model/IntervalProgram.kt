package com.example.runnincle.domain.model

import java.io.Serializable

data class IntervalProgram (
    val warmingUp: Int,
    val setBoost: Int,
    val setCoolDown: Int,
    val retryTime: Int,
    val coolDown: Int,
    val isSkipLastSetCoolDown: Boolean
): Serializable