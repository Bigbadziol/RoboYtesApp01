package com.example.roboytesapp01.ElementyInterfejsu

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.widget.LinearLayout
import android.widget.TextView
import com.example.roboytesapp01.R
import com.example.roboytesapp01.TAG
import com.google.android.material.button.MaterialButton


enum class StanPolaczenia{
    ROZLACZONO,
    LACZE_SIE,
    POLACZONO,
    BLAD_POLACZENIA,
    EDYCJA
}

class EiPolaczenie  (
    context: Context,
    attrs: AttributeSet
) : LinearLayout(context, attrs) {

    private var state: StanPolaczenia = StanPolaczenia.ROZLACZONO

    var btnPolaczenie: MaterialButton
    private var tvStatus: TextView
    var btnUstawienia : MaterialButton
    var selectedDevice: BluetoothDevice? = null

    init {
        inflate(context, R.layout.ei_polaczenie, this)
        val customAttributesStyle = context.obtainStyledAttributes(
            attrs,
            R.styleable.EiPolaczenie, 0, 0
        )

        btnPolaczenie = findViewById<MaterialButton>(R.id.btnPolaczRobot)
        tvStatus = findViewById<TextView>(R.id.tvStatusRobot)
        btnUstawienia = findViewById<MaterialButton>(R.id.btnUstawieniaRobot)

        try {
        } finally {
            customAttributesStyle.recycle()
        }
    }

    fun getValue(): Int {
        return 0
    }

    //bylo setDevicesList
    @SuppressLint("MissingPermission")
    fun setDevice(newDevice : BluetoothDevice){
        selectedDevice = newDevice
        Log.d(TAG,"(EiPoloczenie) ustawione urzadzenie :  ${selectedDevice!!.address}  , name : ${selectedDevice!!.name}")
    }

    private fun interfejsPolaczono() {
        btnPolaczenie.isEnabled = true
        btnPolaczenie.text = resources.getString(R.string.btnPolaczRobot_rozlacz)
        tvStatus.text = resources.getString(R.string.stateConnected)
        btnUstawienia.isEnabled = true
        Log.d(TAG,"(Polaczenie) - polaczony")
    }

    private fun interfejsLaczeSie() {
        btnPolaczenie.isEnabled = false
        btnPolaczenie.text = resources.getString(R.string.btnPolaczRobot_polacz)
        tvStatus.text = resources.getString(R.string.stateConnecting)
        btnUstawienia.isEnabled = false
        Log.d(TAG,"(Polaczenie) - lacze sie")
    }

    private fun interfejsRozlaczono() {
        btnPolaczenie.isEnabled = true
        btnPolaczenie.text = resources.getString(R.string.btnPolaczRobot_polacz)
        tvStatus.text = resources.getString((R.string.stateDisconnected))
        btnUstawienia.isEnabled = false
        Log.d(TAG,"(Polaczenie) - rozlaczono")
    }

    private fun interfejsBladPolaczenia() {
        btnPolaczenie.isEnabled = true
        btnPolaczenie.text = resources.getString(R.string.btnPolaczRobot_polacz)
        tvStatus.text = resources.getString(R.string.stateConnectionError)
        btnUstawienia.isEnabled = false
        Log.d(TAG,"(Polaczenie) - blad polaczenia")
    }

    private fun interfejsEdycja(){
        btnPolaczenie.isEnabled = false
        btnPolaczenie.text = resources.getString(R.string.btnPolaczRobot_rozlacz)
        tvStatus.text = resources.getString(R.string.stateEdition)
        btnUstawienia.isEnabled = false
        Log.d(TAG,"(Polaczenie) - tryb edycji")
    }

    fun ustawStan(newState: StanPolaczenia) {
        state = newState
        when (state) {
            StanPolaczenia.POLACZONO -> interfejsPolaczono()
            StanPolaczenia.LACZE_SIE -> interfejsLaczeSie()
            StanPolaczenia.ROZLACZONO -> interfejsRozlaczono()
            StanPolaczenia.BLAD_POLACZENIA -> interfejsBladPolaczenia()
            StanPolaczenia.EDYCJA -> interfejsEdycja()
        }
    }

    fun wezStan(): StanPolaczenia {
        return state
    }
}