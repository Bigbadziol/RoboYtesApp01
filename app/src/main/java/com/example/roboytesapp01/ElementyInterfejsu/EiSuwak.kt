package com.example.roboytesapp01.ElementyInterfejsu

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.TextView
import com.example.roboytesapp01.R

class EiSuwak  (
    context: Context,
    attrs: AttributeSet
) : LinearLayout(context, attrs) {

    var llLine : LinearLayout
    var tvDescription : TextView
    var tvValue : TextView
    var sb : SeekBar

    init {
        inflate(context, R.layout.ei_suwak, this)
        val customAttributesStyle = context.obtainStyledAttributes(attrs,
            R.styleable.EiSuwak,0,0)

        llLine = findViewById<LinearLayout>(R.id.param_val_line)
        tvDescription = findViewById<TextView>(R.id.param_val_description)
        tvValue = findViewById<TextView>(R.id.param_val_value)
        sb = findViewById<SeekBar>(R.id.param_val_seekbar)

        val tmpMin = (customAttributesStyle.getString(R.styleable.EiSuwak_param_val_min))?.toInt() ?: 1 //default min
        val tmpMax = (customAttributesStyle.getString(R.styleable.EiSuwak_param_val_max))?.toInt() ?:10 //default max
        val tmpVal = (customAttributesStyle.getString(R.styleable.EiSuwak_param_val_value))?.toInt() ?:1 //default seekbar progress position
        try {
            tvDescription.text = customAttributesStyle.getString((R.styleable.EiSuwak_param_val_description))
            tvValue.text = tmpVal.toString()
            sb.min=tmpMin
            sb.max=tmpMax
            sb.progress = tmpVal
        } finally {
            customAttributesStyle.recycle()
        }

        sb.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(sb: SeekBar?, progress: Int, fromUser: Boolean) {
                tvValue.text = progress.toString()
            }
            override fun onStartTrackingTouch(sb: SeekBar?) {}
            override fun onStopTrackingTouch(sb: SeekBar?) {}
        })
    }

    fun getValue():Int{
        return sb.progress
    }

    fun set(desc: String,value:Int,min:Int,max:Int){
        show()
        tvDescription.text = desc
        tvValue.text = value.toString()
        sb.min = min
        sb.max = max
        sb.progress = value
    }

    fun set(value : Int, min : Int , max: Int){
        show()
        sb.min = min
        sb.max = max
        sb.progress = value
    }

    fun set(value : Int){
        show()
        sb.progress = value
    }

    fun hide(){
        llLine.visibility = View.GONE
        tvDescription.visibility= View.GONE
        tvValue.visibility = View.GONE
        sb.visibility = View.GONE
    }

    fun show(){
        llLine.visibility = View.VISIBLE
        tvDescription.visibility = View.VISIBLE
        tvValue.visibility = View.VISIBLE
        sb.visibility = View.VISIBLE
    }
}