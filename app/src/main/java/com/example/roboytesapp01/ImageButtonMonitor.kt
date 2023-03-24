package com.example.roboytesapp01
//Aby pojawily sie zmiany grafiki przyciskow podczas przyciskania
//trzeba przerobic tak aby w konstruktorze poarametrem
import android.annotation.SuppressLint
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.ImageButton

@SuppressLint("ClickableViewAccessibility")
class ImageButtonMonitor(private val button: ImageButton,
                         private val msTime : Long,
                         private val sygnatura :String) : Thread() {
    private var wcisniety = false

    override fun run() {
        button.setOnClickListener {
            Log.d(TAG,"Click")
        }
        button.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View, event: MotionEvent): Boolean {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        wcisniety = true
                        sleep(msTime)
                        //button.setImageResource(R.drawable.gora_lewo_on);
                        //button.setBackground(getResources().getDrawable(R.drawable.button_is_on));
                    }
                    MotionEvent.ACTION_UP ->{
                        wcisniety = false
                        sleep(msTime)
                    }
                }
                return true
                //return v.onTouchEvent(event)
            }
        })
    }

    fun jestWcisniety(): Boolean {
        return wcisniety
    }

    fun wezSygnature() : String{
        if  (wcisniety == true) return sygnatura
        else return "0"
    }

    fun anuluj(){
        wcisniety = false
        button.setOnTouchListener(null)
    }
}