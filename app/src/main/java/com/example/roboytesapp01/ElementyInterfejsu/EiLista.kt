package com.example.roboytesapp01.ElementyInterfejsu

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import com.example.roboytesapp01.Adaptery.StringListAdapter
import com.example.roboytesapp01.R
import com.example.roboytesapp01.TAG

class EiLista  (
    context: Context,
    attrs: AttributeSet
) : LinearLayout(context, attrs) {
    var llLine : LinearLayout
    var tvDescription : TextView
    var sp : Spinner
    val entryList : ArrayList<String> = ArrayList()
    init{
        inflate(context, R.layout.ei_lista, this)
        val customAttributesStyle = context.obtainStyledAttributes(attrs,
            R.styleable.EiLista,0,0)
        llLine = findViewById(R.id.param_list_line)
        tvDescription = findViewById(R.id.param_list_description)
        sp = findViewById(R.id.param_list_spinner)
        //entryList.add("Custom 1")
        //entryList.add("Custom 2")
        //entryList.add("Custom 3")

        //val x = customAttributesStyle.get
        try {
            //tu jest bug malo upierdliwy ale zawsze.
            //wskazanie w komponencie cechy :  app:param_list_values="@array/commonPaleteList"
            //nie wplywa na wartosc - nie potrafie tego skopiowac w TO miejsce -
            //na ten moment aby sie nie krzaczylo pobierane sa dane z....
            //plik param_list.xml :
            //android:entries="@array/TestCustomParameterList"
            //to wskazanie na inna tablice stringow ale nie o to chodzi : (bedzie chcialo zmiany konstruktora)
            //sp.adapter = StringListAdapter(context,resources.getStringArray(R.array.TestCustomParameterList))
            //to diala np,jak dodamy wartosci : Test 1 , Test 2 , Test 3
            sp.adapter = StringListAdapter(context,entryList)
        } finally {
            customAttributesStyle.recycle()
        }
    }
    fun getIdex(): Int{
        return sp.selectedItemPosition
    }

    fun getValue() : String {
        return sp.adapter.getItem(sp.selectedItemPosition).toString()
    }

    fun set(description: String, values: ArrayList<String>){
        llLine.visibility = View.VISIBLE
        tvDescription.visibility = View.VISIBLE
        sp.visibility = View.VISIBLE
        tvDescription.text = description
        entryList.clear()
        entryList.addAll(values)
        sp.adapter = StringListAdapter(context,values)
    }

    fun set(description: String, values : Array<String>){
        llLine.visibility = View.VISIBLE
        tvDescription.visibility = View.VISIBLE
        sp.visibility = View.VISIBLE
        tvDescription.text = description

        val arrList: java.util.ArrayList<String>?
        arrList = values.toCollection(ArrayList())
        entryList.clear()
        entryList.addAll(arrList)
        sp.adapter = StringListAdapter(context,arrList)
    }


    fun setIndex(index : Int){
        if (index >=0 && index < entryList.size) {
            sp.setSelection(index)
        }else{
            Log.d(TAG,"CustomParamList , index out of range.")
        }
    }

    fun setIndexForName(name : String){
        var index = entryList.indexOfFirst {
            it == name
        }
        if (index == -1){
            index = 0
            Log.d(TAG,"Custom ParamList , entry name not found on list, set on first item")
        }
        sp.setSelection(index)
    }

    fun hide(){
        llLine.visibility = View.GONE
        tvDescription.visibility = View.GONE
        sp.visibility = View.GONE
    }

    fun show(){
        llLine.visibility = View.VISIBLE
        tvDescription.visibility = View.VISIBLE
        sp.visibility = View.VISIBLE
    }
}