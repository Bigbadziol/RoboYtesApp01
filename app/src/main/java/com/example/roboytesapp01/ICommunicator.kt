package com.example.roboytesapp01

interface ICommunicator {
    fun kanalSterowanie(msg : String)
    fun kanalUstawienia(msg : String)
}