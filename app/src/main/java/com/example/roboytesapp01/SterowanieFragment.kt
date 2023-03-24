package com.example.roboytesapp01

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.roboytesapp01.databinding.FragmentSterowanieBinding
import com.example.roboytesapp01.ui.main.PARAM_PRAWY_ODTWARZANIE
import java.lang.Thread.sleep


private const val ARG_PARAM_STEROWANIE1 = "param_sterowanie1"
private const val ARG_PARAM_STEROWANIE2 = "param_sterowanie2"

class SterowanieFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var bind : FragmentSterowanieBinding
    private lateinit var strzalkiMonitor: StrzalkiMonitor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM_STEROWANIE1)
            param2 = it.getString(ARG_PARAM_STEROWANIE2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        bind = FragmentSterowanieBinding.inflate(inflater,container,false)
        val view = bind.root
        //return inflater.inflate(R.layout.fragment_sterowanie, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val communicator: ICommunicator = activity as ICommunicator

//https://stackoverflow.com/questions/47149457/android-imagebutton-ontouchlistener-not-working
//https://stackoverflow.com/questions/19538747/how-to-use-both-ontouch-and-onclick-for-an-imagebutton
        //val  bt1 = ImageButtonMonitor(bind.btnGora,100,"G");
        //bt1.start();
        strzalkiMonitor = StrzalkiMonitor(bind.btnLewoGora,
            bind.btnGora,
            bind.btnPrawoGora,
            bind.btnLewoDol,
            bind.btnDol,
            bind.btnPrawoDol,
            100
        )

        val nasluchujPrzyciskow = Thread{
            while(true){
                val odpowiedz = strzalkiMonitor.stanSterowanieToJson()
                if (odpowiedz.isNotBlank()){
                    //Log.d(TAG,"Back  : $odpowiedz")
                    communicator.kanalSterowanie(odpowiedz)
                }
                sleep(200)
            }
        }
        nasluchujPrzyciskow.start() //Nasłuchuj przyciskow sterujacych

        bind.btnFun1.setOnClickListener {
            communicator.kanalSterowanie(audioEfektToJson(1))
        }
        bind.btnFun2.setOnClickListener {
            communicator.kanalSterowanie(audioEfektToJson(2))
        }
        bind.btnFun3.setOnClickListener {
            communicator.kanalSterowanie(audioEfektToJson(3))
        }
        bind.btnFun4.setOnClickListener {
            communicator.kanalSterowanie(audioEfektToJson(4))
        }
        bind.btnFun5.setOnClickListener {
            communicator.kanalSterowanie(audioEfektToJson(5))
        }
        bind.btnFun6.setOnClickListener {
            communicator.kanalSterowanie(audioEfektToJson(6))
        }
        bind.btnFun7.setOnClickListener {
            communicator.kanalSterowanie(audioEfektToJson(7))
        }
        bind.btnFun8.setOnClickListener {
            communicator.kanalSterowanie(audioEfektToJson(8))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        strzalkiMonitor.anuluj()
        //strzalkiMonitor.interrupt()
    }

    private fun audioEfektToJson(numerEfektu: Int):String{
        // Uwaga! znak '\n' informujacy kontroler o koncu komunikatu doklejany
        // przed samym wyslaniem
        return "{\"audio\":{\""+ PARAM_PRAWY_ODTWARZANIE + "\":"+numerEfektu+ "}}"
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SterowanieFragment.
         */

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SterowanieFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM_STEROWANIE1, param1)
                    putString(ARG_PARAM_STEROWANIE2, param2)

                }
            }
    }
}