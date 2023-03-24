package com.example.roboytesapp01

//BŁĘDY MOZE powodować interupt poszeczegolnych monitorow przyciskow
// Zmiana koncepcji , StrzalkiMonitor przestaje nasłuchiwać poszczególnych przycisków
// jako wątek , teraz metodą : stanSterowanieToJson() zwraca jsona na rządanie
import android.util.Log
import android.widget.ImageButton
import com.example.roboytesapp01.ui.main.*

class StrzalkiMonitor(
    ibLewoGora : ImageButton,
    ibGora : ImageButton,
    ibPrawoGora : ImageButton,
    ibLewoDol : ImageButton,
    ibDol : ImageButton,
    ibPrawoDol : ImageButton,
    buttonCheckTime : Long) {
    private val monitorLewoPrzod : ImageButtonMonitor
    private val monitorPrzod : ImageButtonMonitor
    private val monitorPrawoPrzod : ImageButtonMonitor
    private val monitorLewoTyl : ImageButtonMonitor
    private val monitorTyl : ImageButtonMonitor
    private val monitorPrawoTyl : ImageButtonMonitor
    private val stanyPrzyciskowSterowania = Array(6){ "it = $it" }
    init{
        //Jeśli przycisk jest przytrzymywany zwraca swoją sygnaturę np. "LG"(lewo góra),
        //jeśli nie jest , zwraca stringa "0"
        //Parametry zdefiniowane w Stale.
        monitorLewoPrzod = ImageButtonMonitor(ibLewoGora,buttonCheckTime, PARAM_LEWO_PRZOD)
        monitorPrzod = ImageButtonMonitor(ibGora,buttonCheckTime, PARAM_PRZOD)
        monitorPrawoPrzod = ImageButtonMonitor(ibPrawoGora,buttonCheckTime, PARAM_PRAWO_PRZOD)
        monitorLewoTyl = ImageButtonMonitor(ibLewoDol,buttonCheckTime, PARAM_LEWO_TYL)
        monitorTyl = ImageButtonMonitor(ibDol,buttonCheckTime, PARAM_TYL)
        monitorPrawoTyl = ImageButtonMonitor(ibPrawoDol,buttonCheckTime, PARAM_PRAWO_TYL)
        monitorLewoPrzod.start()
        monitorPrzod.start()
        monitorPrawoPrzod.start()
        monitorLewoTyl.start()
        monitorTyl.start()
        monitorPrawoTyl.start()
    }

    /*
        Jeśli przycisk sterujący jest przytrzymywany przez urzytkownika
        zatanie zwrócony string : {"ruch":{"PT":1}} w przecinwym przypadku ""
     */
    fun stanSterowanieToJson() : String {
        //Pobierz aktualne stany przyciskow
        stanyPrzyciskowSterowania[0] = monitorLewoPrzod.wezSygnature()
        stanyPrzyciskowSterowania[1] = monitorPrzod.wezSygnature()
        stanyPrzyciskowSterowania[2] = monitorPrawoPrzod.wezSygnature()
        stanyPrzyciskowSterowania[3] = monitorLewoTyl.wezSygnature()
        stanyPrzyciskowSterowania[4] = monitorTyl.wezSygnature()
        stanyPrzyciskowSterowania[5] = monitorPrawoTyl.wezSygnature()

         for (tenStan : String in stanyPrzyciskowSterowania){
            if (!tenStan.contentEquals("0"))
                //Uwaga , znak informujacy o koncu komunikatu '\n' doklejany jest przed samym wyslaniem
                return "{\"ruch\":{\""+tenStan+"\":1}}"
                //return tenStan // testy  : inne znaki dla apki bluetooth rc controller
        }
        return ""
    }

    fun anuluj(){
        Log.d(TAG,"StrzalkiMonitor, zatrzymuje monitorowanie poszczegolnych przyciskow.")
        monitorLewoPrzod.anuluj()
        //monitorLewoGora.interrupt();
        monitorPrzod.anuluj()
        //monitorGora.interrupt()
        monitorPrawoPrzod.anuluj()
        //monitorPrawoGora.interrupt()
        monitorLewoTyl.anuluj()
        //monitorLewoDol.interrupt()
        monitorTyl.anuluj()
        //monitorDol.interrupt()
        monitorPrawoTyl.anuluj()
        //monitorPrawoDol.interrupt()
    }
}