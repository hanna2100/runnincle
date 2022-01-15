package com.example.runnincle

data class IntervalProgram(
    val warmingUp: Int,
    val setBoost: Int,
    val setCoolDown: Int,
    val retryTime: Int,
    val coolDown: Int,
    val isSkipLastSetCoolDown: Boolean
)
