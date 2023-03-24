package com.example.roboytesapp01

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Toast
import com.example.roboytesapp01.DaneTestowe.daneTestoweOdpowiedz
import com.example.roboytesapp01.databinding.FragmentUstawieniaBinding
import com.example.roboytesapp01.ui.main.*
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlin.collections.ArrayList

// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM_USTAWIENIA1 = "param_ustawienia1"
private const val ARG_PARAM_USTAWIENIA2 = "param_ustawienia2"

/**
 * A simple [Fragment] subclass.
 * Use the [UstawieniaFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class UstawieniaFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var bind: FragmentUstawieniaBinding
    private var indexTorAudio = 0; //0..1
    private var indexTrybAudio = 0;//0..4
    private var lewyGlosnosc = 20; //0..30
    private var prawyGlosnosc = 20;//
    private var wyciszenie = 10;
    private var lewyOdtwarzanie = 0; //0 -losowy, n-numer
    private var utworyLista1 = ArrayList<String>()
    private var utworyLista2 = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM_USTAWIENIA1)
            param2 = it.getString(ARG_PARAM_USTAWIENIA2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        bind =  FragmentUstawieniaBinding.inflate(inflater,container,false)
        val view = bind.root
        return view
        //return inflater.inflate(R.layout.fragment_ustawienia, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val communicator: ICommunicator = activity as ICommunicator
        var torAudioBug = false //Ominięcie problemu polegającemu na tym,że listener nadpisze ustawioną
                                //wcześniej pozycję utworu z innego parametru.
        super.onViewCreated(view, savedInstanceState)

        bind.lstTorAudio.set(resources.getString(R.string.torAudio_desc),
            ArrayList(resources.getStringArray(R.array.arrTorAudio).toMutableList()))

        bind.lstTrybAudio.set(resources.getString(R.string.trybAudio_desc),
            ArrayList(resources.getStringArray(R.array.arrTrybAudio).toMutableList()))
        //wladowane dane testowe ,potem będą nadpisane
        bind.lstUtwory.set(resources.getString(R.string.utwor_desc),
            ArrayList(resources.getStringArray(R.array.arrUtworyTest).toMutableList()))
        uaktualnijInterfejs(daneTestoweOdpowiedz)

        bind.btnUstawieniaPotwierdz.setOnClickListener {
            communicator.kanalUstawienia(noweUstawienia().toString())
            //W przypadku potrzeby fragment-> fragment pamiętać o Bundle
            przejdzDoSterowania()

        }

        bind.btnUstawieniaZamknij.setOnClickListener {
            przejdzDoSterowania()
        }

        bind.lstTorAudio.sp.onItemSelectedListener = object  : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val podmienianaLista = ArrayList<String>()
                when (position) {
                    0 -> {
                        Log.d(TAG,"Tor audio : 0")
                        podmienianaLista.addAll(utworyLista1)
                        bind.lstUtwory.set(resources.getString(R.string.utwor_desc),podmienianaLista)
                        if (!torAudioBug){
                            bind.lstUtwory.setIndex(lewyOdtwarzanie)
                            torAudioBug = true
                        }
                    }
                    1 -> {
                        Log.d(TAG,"Tor audio : 1")
                        podmienianaLista.addAll(utworyLista2)
                        bind.lstUtwory.set(resources.getString(R.string.utwor_desc),podmienianaLista)
                        if (!torAudioBug){
                            bind.lstUtwory.setIndex(lewyOdtwarzanie)
                            torAudioBug = true
                        }
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                Log.d(TAG, "Tor audio - nic nie wybrano.")
            }
        }
    }

    private fun testujParametrLiczba(obiekt : JsonObject, parametr : String, min : Int, max : Int , domyslna : Int) : Int{
        var tmp = domyslna;
        if (!obiekt.has(parametr)){
            Toast.makeText(activity?.applicationContext,
                "Brak parametru : $parametr", Toast.LENGTH_SHORT).show()
            return domyslna
        }
        try {
            tmp = obiekt.get(parametr).asInt
        }catch(e : Exception){
            Toast.makeText(activity?.applicationContext,
                "Błąd konwersji parametru :$parametr", Toast.LENGTH_SHORT).show()
            return domyslna
        }
        if (tmp < min || tmp > max){
            Toast.makeText(activity?.applicationContext,
                "Parametr :$parametr poza zakresem.", Toast.LENGTH_SHORT).show()
            return domyslna
        }
        return tmp;
    };

    private fun testujParametrListaUtworow(obiekt : JsonObject, parametr : String): ArrayList<String>{
        val tmp = ArrayList<String>()
        if (!obiekt.has(parametr)){
            Toast.makeText(activity?.applicationContext,
                "Brak parametru : $parametr", Toast.LENGTH_SHORT).show()
            return tmp
        }
        val utwory = obiekt.getAsJsonArray(parametr)
        tmp.add(resources.getString(R.string.utwor_losowy))
        for (u in utwory){
            tmp.add(u.asString)
        }
        return tmp
    }

    @SuppressLint("SuspiciousIndentation")
    private fun uaktualnijInterfejs(daneWejsciowe: String){
        if (!czytoJson(daneWejsciowe)){
            Log.d(TAG,"(Ustawienia) przychodzace dane nie sa poprawnym jsonem.")
            return
        }
        val dane: JsonObject = Gson().fromJson(daneWejsciowe, JsonObject::class.java)
        if (dane.has("audioOdp")){
            val audio = dane.getAsJsonObject("audioOdp")
            //tor
            indexTorAudio = testujParametrLiczba(audio, PARAM_TOR,0,
                ArrayList(resources.getStringArray(R.array.arrTorAudio).toMutableList()).size -1,0)
                bind.lstTorAudio.setIndex(indexTorAudio)
            //tryb
            indexTrybAudio = testujParametrLiczba(audio,PARAM_TRYB,0,
                ArrayList(resources.getStringArray(R.array.arrTrybAudio).toMutableList()).size -1,0)
            bind.lstTrybAudio.setIndex(indexTrybAudio)
            //wybor utworu
            utworyLista1.addAll(testujParametrListaUtworow(audio, PARAM_LISTA1))
            utworyLista2.addAll(testujParametrListaUtworow(audio, PARAM_LISTA2))
            var rozmiar  = 0;
            when(indexTorAudio){
                0-> {
                        rozmiar = utworyLista1.size
                        bind.lstUtwory.set(resources.getString(R.string.utwor_desc),utworyLista1)
                    }

                1-> {
                    rozmiar = utworyLista2.size
                    bind.lstUtwory.set(resources.getString(R.string.utwor_desc),utworyLista2)
                    }
            }

            lewyOdtwarzanie = testujParametrLiczba(audio, PARAM_LEWY_ODTWAZANIE,0,rozmiar-1,0)
            bind.lstUtwory.setIndex(lewyOdtwarzanie)
            //glosnosc lewy
            lewyGlosnosc = testujParametrLiczba(audio, PARAM_LEWY_GLOSNOSC,0, 30,20)
            bind.suwGlosnoscMuzyka.set(lewyGlosnosc)
            //glosnosc prawy
            prawyGlosnosc = testujParametrLiczba(audio, PARAM_PRAWY_GLOSNOSC,0, 30,20)
            bind.suwGlosnoscEfekty.set(prawyGlosnosc)
            //wyciszenie
            wyciszenie = testujParametrLiczba(audio, PARAM_POZIOM_WYCISZENIA,0,15,10)
            bind.suwWyciszenie.set(wyciszenie)
        }else{
            Log.d(TAG,"(Ustawienia) BLAD : brak obiektu 'audioOdp' !")
        }
    }

    private fun noweUstawienia() : JsonObject{
        val odpowiedz = JsonObject();
        val odpAudio = JsonObject();
        odpAudio.addProperty(PARAM_TOR,bind.lstTorAudio.getIdex())
        odpAudio.addProperty(PARAM_TRYB,bind.lstTrybAudio.getIdex())
        odpAudio.addProperty(PARAM_LEWY_GLOSNOSC,bind.suwGlosnoscMuzyka.getValue())
        odpAudio.addProperty(PARAM_PRAWY_GLOSNOSC,bind.suwGlosnoscEfekty.getValue())
        odpAudio.addProperty(PARAM_POZIOM_WYCISZENIA,bind.suwWyciszenie.getValue())
        odpAudio.addProperty(PARAM_LEWY_ODTWAZANIE,bind.lstUtwory.getIdex())
        odpowiedz.add("audio",odpAudio)
        return odpowiedz
    }

    private fun przejdzDoSterowania(){
        val fragment : Fragment = SterowanieFragment()
        val fragmentTransaction = parentFragmentManager.beginTransaction()
        fragmentTransaction.setCustomAnimations(R.anim.enter_from_right, 0, 0, R.anim.exit_to_right)
        fragmentTransaction.add(R.id.mainFragmentContainer,fragment)
        fragmentTransaction.addToBackStack(null) //TODO: Aby napewno ?
        fragmentTransaction.commit()
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment UstawieniaFragment.
         */
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            UstawieniaFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM_USTAWIENIA1, param1)
                    putString(ARG_PARAM_USTAWIENIA2, param2)
                }
            }
    }
}