package com.example.roboytesapp01.ElementyInterfejsu

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import com.example.roboytesapp01.R

/*
    Klasa nie uzywana , zastapiona wbudowana :
        <com.google.android.material.switchmaterial.SwitchMaterial
 */
class EiWlacznik (
    context: Context,
    attrs: AttributeSet
) : LinearLayout(context, attrs) {

    var llLine : LinearLayout
    var tvDescription : TextView
    var sw : SwitchCompat

    init {
        inflate(context, R.layout.ei_wlacznik, this)
        val customAttributesStyle = context.obtainStyledAttributes(attrs, R.styleable.EiWlacznik, 0, 0)

        llLine = findViewById<LinearLayout>(R.id.param_bool_line)
        tvDescription = findViewById<TextView>(R.id.param_bool_description)
        sw = findViewById<SwitchCompat>(R.id.param_bool_value)
        val tmpValue = (customAttributesStyle.getString(R.styleable.EiWlacznik_param_bool_value))?.toBoolean() ?: false
        try {
            tvDescription.text = customAttributesStyle.getString((R.styleable.EiWlacznik_param_bool_description))
            sw.isChecked = tmpValue
        } finally {
            customAttributesStyle.recycle()
        }
    }

    fun getValue() : Boolean{
        return sw.isChecked
    }

    fun set(desc : String, value : Boolean ){
        llLine.visibility = View.VISIBLE
        tvDescription.visibility = View.VISIBLE
        sw.visibility = View.VISIBLE
        tvDescription.text = desc
        sw.isChecked = value
    }

    fun hide(){
        llLine.visibility = View.GONE
        tvDescription.visibility = View.GONE
        sw.visibility = View.GONE
    }

    fun show(){
        llLine.visibility = View.VISIBLE
        tvDescription.visibility = View.VISIBLE
        sw.visibility = View.VISIBLE
    }

}