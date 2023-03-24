package com.example.roboytesapp01.ElementyInterfejsu

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.example.roboytesapp01.R
import com.example.roboytesapp01.TAG
import com.google.android.material.button.MaterialButton



enum class ConnectionState{
    DISCONNECTED,
    CONNECTING,
    CONNECTED,
    CONNECTION_ERROR,
    EDIT
}

class EiPolaczenie  (
    context: Context,
    attrs: AttributeSet
    //newDevice: BluetoothDevice
) : LinearLayout(context, attrs) {

    private var state: ConnectionState = ConnectionState.DISCONNECTED

    var btnPolaczenie: MaterialButton
    var tvStatus: TextView
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

/*
        sp.onItemSelectedListener   = object  : AdapterView.OnItemSelectedListener{
            @SuppressLint("MissingPermission")
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedDevice = sp.adapter.getItem(position) as BluetoothDevice
                Log.d(TAG,"(CustomConnection)(spinner selection) : ${selectedDevice!!.address}  , name : ${selectedDevice!!.name}")
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                Log.d(TAG,"Bluetooth device - not selected.")
            }
        }

 */
        //selectedDevice = newDevice
    }

    fun getValue(): Int {
        return 0
    }

    //bylo setDevicesList
    @SuppressLint("MissingPermission")
    fun setDevice(newDevice : BluetoothDevice){
        selectedDevice = newDevice;
        Log.d(TAG,"(EiPoloczenie) ustawione urzadzenie :  ${selectedDevice!!.address}  , name : ${selectedDevice!!.name}")
    }

    fun interfaceConnected() {
        btnPolaczenie.isEnabled = true
        btnPolaczenie.text = resources.getString(R.string.btnPolaczRobot_rozlacz)
        tvStatus.text = resources.getString(R.string.stateConnected)
        btnUstawienia.isEnabled = true
    }

    fun interfaceConnecting() {
        btnPolaczenie.isEnabled = false
        btnPolaczenie.text = resources.getString(R.string.btnPolaczRobot_polacz)
        tvStatus.text = resources.getString(R.string.stateConnecting)
        btnUstawienia.isEnabled = false
    }

    fun interfaceDisconnected() {
        btnPolaczenie.isEnabled = true
        btnPolaczenie.text = resources.getString(R.string.btnPolaczRobot_polacz)
        tvStatus.text = resources.getString((R.string.stateDisconnected))
        btnUstawienia.isEnabled = false
    }

    fun interfaceConnectionError() {
        btnPolaczenie.isEnabled = true
        btnPolaczenie.text = resources.getString(R.string.btnPolaczRobot_polacz)
        tvStatus.text = resources.getString(R.string.stateConnectionError)
        btnUstawienia.isEnabled = false
    }

    fun interfaceEdycja(){
        btnPolaczenie.isEnabled = true
        btnPolaczenie.text = resources.getString(R.string.btnPolaczRobot_rozlacz)
        tvStatus.text = resources.getString(R.string.stateEdition)
        btnUstawienia.isEnabled = false
    }

    fun setState(newState: ConnectionState) {
        state = newState
        when (state) {
            ConnectionState.CONNECTED -> interfaceConnected()
            ConnectionState.CONNECTING -> interfaceConnecting()
            ConnectionState.DISCONNECTED -> interfaceDisconnected()
            ConnectionState.CONNECTION_ERROR -> interfaceConnectionError()
            ConnectionState.EDIT -> interfaceEdycja()
        }
    }

    fun getState(): ConnectionState {
        return state
    }


    fun hide() {
        btnPolaczenie.visibility = View.GONE
        tvStatus.visibility = View.GONE
        btnUstawienia.visibility = View.GONE
    }

    fun show() {
        btnPolaczenie.visibility = View.VISIBLE
        tvStatus.visibility = View.VISIBLE
        btnUstawienia.visibility = View.VISIBLE
    }
}