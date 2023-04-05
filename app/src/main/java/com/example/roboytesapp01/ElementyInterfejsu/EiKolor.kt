package com.example.roboytesapp01.ElementyInterfejsu

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.example.roboytesapp01.R
import com.example.roboytesapp01.intToJsonColor
import com.google.android.material.button.MaterialButton
import com.google.gson.JsonObject
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener

class EiKolor  (
    context: Context,
    attrs: AttributeSet
) : LinearLayout(context, attrs) {

    var llLine : LinearLayout
    var button : MaterialButton
    var tv : TextView
    init{
        inflate(context, R.layout.ei_kolor, this)
        val customAttributesStyle = context.obtainStyledAttributes(attrs,
            R.styleable.EiKolor,0,0)
        llLine = findViewById<LinearLayout>(R.id.param_color_line)
        button = findViewById<MaterialButton>(R.id.param_color_button)
        tv = findViewById<TextView>(R.id.param_color_textview)
        try {
            button.text = customAttributesStyle.getString(R.styleable.EiKolor_param_color_description)
            val stringColor = customAttributesStyle.getString(R.styleable.EiKolor_param_color_value)
            val intColor = Color.parseColor(stringColor)
            //Log.d(TAG,"tmpColor : $stringColor , parsed : $intColor")
            tv.setBackgroundColor(intColor)
        } finally {
            customAttributesStyle.recycle()
        }

        button.setOnClickListener{
            //Log.d(TAG,"Wywolanie buildera color picka.")
            com.skydoves.colorpickerview.ColorPickerDialog.Builder(context)
                .setTitle(R.string.dlColorTitle)
                .setPreferenceName("prefStripColor1")

                .setPositiveButton(context.getString(R.string.dlColorBtnOk), ColorEnvelopeListener { envelope, _ ->
                    val color = envelope.color
                    tv.setBackgroundColor(color)

                })
                .attachAlphaSlideBar(false) // the default value is true.
                .attachBrightnessSlideBar(true) // the default value is true.
                //.setBottomSpace(12) // avaible in : 2.2.4
                .show()
        }
    }
    fun getValueInt() : Int{
        val col = tv.background as ColorDrawable
        return col.color
    }
    fun getValueJson() : JsonObject {
        val col = tv.background as ColorDrawable
        return intToJsonColor(col.color)
    }

    fun set(desc : String, value : Int ){
        show()
        button.text = desc
        tv.setBackgroundColor(value)
    }
    fun set( value : Int){
        show()
        tv.setBackgroundColor(value)
    }

    fun hide(){
        llLine.visibility = View.GONE
        tv.visibility = View.GONE
        button.visibility = View.GONE
    }

    fun show(){
        llLine.visibility = View.VISIBLE
        tv.visibility = View.VISIBLE
        button.visibility = View.VISIBLE
    }
}