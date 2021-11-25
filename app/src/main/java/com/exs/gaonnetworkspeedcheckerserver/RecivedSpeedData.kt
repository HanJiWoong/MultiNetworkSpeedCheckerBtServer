package com.exs.gaonnetworkspeedcheckerserver

import java.io.Serializable
import java.util.*

data class RecivedSpeedData(val downSpeed:Int, val upSpeed:Int, val delayTime:Int, val jitter:Int) : Serializable
