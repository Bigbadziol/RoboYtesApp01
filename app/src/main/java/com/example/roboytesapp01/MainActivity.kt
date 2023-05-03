package com.example.roboytesapp01
/*
    Modyfikacja seekbara -> https://www.geeksforgeeks.org/creating-custom-seekbar-in-android/
TODO: OBSLUZYC socket.connect  , kiedy robot  wylaczony
 */
import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.roboytesapp01.ElementyInterfejsu.StanPolaczenia
import com.example.roboytesapp01.databinding.ActivityMainBinding
import com.google.gson.JsonObject
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.*

const val TAG = "DEBUG"
const val ARG_DATA="ROBOTMAIN"


class MainActivity : AppCompatActivity(),ICommunicator {
    private lateinit var bind : ActivityMainBinding

    //BT
/// wczesniejsze podejscie , przesylanie danych pomiedzy aktywnosciami.
///    private val MESSAGE_READ = 10
///    private val MESSAGE_TOAST = 20
    //uuid- ten klucz to publiczny, nie szyfrowany , pasmo poszukiwania urzadzen
    private val uuid: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
    private val ENDDATA_SIGNATURE = "#$!#" //Ma być to samo co w esp
    private val ROBOTNAME_SIGNATURE = "YtesRobot"
    //nazwa : YtesRobot01
    //mac :
    //pin   : 0987
    //private val ROBOT_MAC="AC:67:B2:2D:16:92"  //Ytes1
    private val ROBOT_MAC="C8:F0:9E:F4:C6:3E"  //Ytes2 - podlutowany zyroskop

    private lateinit var bluetoothManager: BluetoothManager
    private lateinit var bluetoothAdapter : BluetoothAdapter
    private lateinit var bluetoothRobot : BluetoothDevice
    private var isBluetoothEnabled = false

    private val robotSparowany : Boolean = false
    lateinit var deviceSocket: BluetoothSocket
    var incDataBuffer =""               // data buffer
    var aktualneUstawieniaRobota=""     // w stringu obiekt json reprezentujacy komplet ustawien
///    lateinit var dataHandler: Handler   //  handle  incomming controller data
    lateinit var stateHandler : Handler // to refresh CustomConnction -state

    //----------------------------------------------------------------------------------------------
    private inner class ConnectedThread(private val mmSocket: BluetoothSocket) : Thread() {

        private val mmInStream: InputStream = mmSocket.inputStream
        private val mmOutStream: OutputStream = mmSocket.outputStream
        private val mmBuffer: ByteArray = ByteArray(2048) // mmBuffer store for the stream

        override fun run() {
            var numBytes: Int // ilosc odczytanych danych, ma trafic do glownej aktywnosci
            var pcgSize: Int
            var thisMessage: String
            // Keep listening to the InputStream until an exception occurs.
            while (true) {
                // Read from the InputStream.
                numBytes = try {
                    pcgSize = mmInStream.read(mmBuffer)
                    thisMessage  = String(mmBuffer, 0, pcgSize)
                    handleIncommingEspData(thisMessage)
                    //Log.d(TAG,"Readed bytes : $pcgSize")
                    Log.d(TAG,"")
                } catch (e: IOException) {
                    Log.d(TAG, "Input stream was disconnected", e)
                    break
                }
                // Prześlij dane do głównej aktywności
///                val readMsg = dataHandler.obtainMessage(MESSAGE_READ, numBytes, -1, mmBuffer)
///                readMsg.sendToTarget()
            }
        }

        // Call this from the main activity to send data to the remote device.
        fun write(bytes: ByteArray) {
            try {
                mmOutStream.write(bytes)
            } catch (e: IOException) {
                Log.e(TAG, "Error occurred when sending data", e)

                // Prześlij informacje o nie powodzeniu
///                val writeErrorMsg = dataHandler.obtainMessage(MESSAGE_TOAST)
/////                val bundle = Bundle().apply {
/////                    putString("toast", "Couldn't send data to the other device")
/////                }
///                writeErrorMsg.data = bundle
///                dataHandler.sendMessage(writeErrorMsg)
                return
            }
        }

        // Call this method from the main activity to shut down the connection.
        fun cancel() {
            try {
                if (!mmSocket.isConnected){
                    Log.d(TAG,"[Connected Thread] -> Trying close not connected socket.")
                }
                mmSocket.close()
                Log.d(TAG,"[Connected Thread] -> socked is closed.")
            } catch (e: IOException) {
                Log.e(TAG, "[Connected Thread] -> Could not close the socket : ", e)
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun handleIncommingEspData(thisData : String){
        runOnUiThread {
            if (!deviceSocket.isConnected) return@runOnUiThread
            incDataBuffer += thisData
            if (incDataBuffer.contains(ENDDATA_SIGNATURE)){
                incDataBuffer = incDataBuffer.substringBefore(ENDDATA_SIGNATURE)
                Log.d(TAG,"(incData) : , end of data signature")
                bind.connection.selectedDevice?.let{
                    when {
                        it.name.contains(ROBOTNAME_SIGNATURE,ignoreCase = true)->{
                            Log.d(TAG,"(incData), RobotYtes")
                            aktualneUstawieniaRobota = incDataBuffer
                            replaceFragment(SterowanieFragment(),incDataBuffer)
                        }
                        else->{
                            Log.d(TAG,"(incData), nieznane urzadzenie")
                        }
                    }
                    Log.d(TAG,"(incData) buffer clear")
                    incDataBuffer=""
                    stateHandler.post {
                        bind.connection.ustawStan(StanPolaczenia.POLACZONO)
                    }
                }
            }//contain end data signature
        }//ui thread
    }

    @SuppressLint("MissingPermission")
    private inner class ConnectThread(device: BluetoothDevice) : Thread() {
        private val mmSocket: BluetoothSocket? by lazy(LazyThreadSafetyMode.NONE) {
            device.createInsecureRfcommSocketToServiceRecord(uuid)
        }

        @SuppressLint("MissingPermission")
        override fun run() {
            // Wyszukiwanie urządzeń spowalnia transfer, zatem przerywamy profilaktycznie.
            bluetoothAdapter?.cancelDiscovery()
            mmSocket?.let { socket ->
                Log.d(TAG, "[Connect Thread] - > connecting to socket")
                stateHandler.post {
                    bind.connection.ustawStan(StanPolaczenia.LACZE_SIE)
                }

                try {
                    socket.connect()
                }catch (exception: IOException){
                    Log.d(TAG,"(connect thread)(run) - soket nie polaczony")
                    stateHandler.post {
                        bind.connection.ustawStan(StanPolaczenia.BLAD_POLACZENIA)
                    }
                }
                if (socket.isConnected) {
                    Log.d(TAG, "[Connect Thread] - > socked is connected.")
                    deviceSocket = mmSocket as BluetoothSocket
                    stateHandler.post {
                        //tu raczej nic, lepiej po pobraniu danych
                    }
                    ConnectedThread(socket).start()

                    val cmdWelcome = JsonObject()
                    cmdWelcome.addProperty("cmd", "DANE_PROSZE")
                    if (deviceSocket.isConnected) {
                        //Uwaga znak '\n' jest niezbedny , poniewaz informuje kontroler o koncu komunikatu
                        val cmd = cmdWelcome.toString() + '\n'
                        Log.d(TAG, "[Connect Thread] welcome -> $cmd")
                        ConnectedThread(deviceSocket).write(cmd.toByteArray())
                    } else {
                        Log.d(TAG, "[Connect Thread] welcome -> Socket is not connected!")
                        stateHandler.post {
                            bind.connection.ustawStan(StanPolaczenia.BLAD_POLACZENIA)
                        }
                    }
                }
            }
        }
        // Closes the client socket and causes the thread to finish.
        fun cancel() {
            try {
                mmSocket?.close()
                Log.d(TAG,"[Connect Thread] - > socked is closed.")
            } catch (e: IOException) {
                Log.e(TAG, "[Connect Thread] -> Could not close the client socket : ", e)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        bind = ActivityMainBinding.inflate(layoutInflater)
        setContentView(bind.root)

        Log.d(TAG,"Main activity :  1.24")
        Log.d(TAG,"25.03.2023")
        gotBTPerms(this,true)
        replaceFragment(WitajFragment(),"") //Fragment powitalny

        bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothAdapter = bluetoothManager.adapter
        bluetoothRobot = bluetoothAdapter.getRemoteDevice(ROBOT_MAC)

        Log.d(TAG,"Robot sparowany : ${czyRobotSparowany()}")

        stateHandler = Handler(Looper.getMainLooper())
 ///       dataHandler = Handler(Looper.getMainLooper())

        val enableBluetoothLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { /* Not needed */ }

        val permissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { perms ->
            val canEnableBluetooth = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                Log.d(TAG,"perm.luncher.res dla : Android 12,13,.. ")
                gotBTPerms(this,true)//selftest
                perms[Manifest.permission.BLUETOOTH_CONNECT] == true
            } else{
                Log.d(TAG,"perm.luncher.res dla : Android 10,11 ")
                gotBTPerms(this,true)//selftest
                perms[Manifest.permission.ACCESS_FINE_LOCATION] == true
            }

            if(canEnableBluetooth && !isBluetoothEnabled) {
                enableBluetoothLauncher.launch(
                    Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                )
            }
        }

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) { //android 12,13,...
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.BLUETOOTH_SCAN,
                    Manifest.permission.BLUETOOTH_CONNECT,
                )
            )
        }else{
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,

                )
            )
        }

        if (gotBTPerms(this,false)) {
            bind.connection.ustawStan(StanPolaczenia.ROZLACZONO)
            bluetoothRobot?.let {
                Log.d(TAG,"Ustawiam, naszego robota do polaczenia.")
                bind.connection.setDevice(it)
            }

            bind.connection.btnPolaczenie.setOnClickListener {
                Log.d(TAG, "btn Polaczenie - klik")
                //Teraz  domyslnie uruchamia
                //replaceFragment(SterowanieFragment(), "") // Fragment sterowania
                if (bind.connection.selectedDevice != null){
                    when (bind.connection.wezStan()){
                        StanPolaczenia.ROZLACZONO ->{
                            ConnectThread(bind.connection.selectedDevice!!).start()
                        }
                        StanPolaczenia.BLAD_POLACZENIA ->{
                            ConnectThread(bind.connection.selectedDevice!!).start()
                        }
                        StanPolaczenia.POLACZONO->{
                            replaceFragment(WitajFragment(),"") //Set welcome screen
                            //przeladuj witaj ???
                            val cmdPapa = JsonObject()
                            cmdPapa.addProperty("cmd", "PAPA")
                            val cmd = cmdPapa.toString() + '\n'

                            if (deviceSocket.isConnected) {
                                //Uwaga znak '\n' jest niezbedny , poniewaz informuje kontroler o koncu komunikatu
                                Log.d(TAG, "[Rozlaczanie] -> $cmd")
                                ConnectedThread(deviceSocket).write(cmd.toByteArray())
                                stateHandler.post {
                                    bind.connection.ustawStan(StanPolaczenia.ROZLACZONO)
                                }
                                ConnectedThread(deviceSocket).cancel()
                            } else {
                                Log.d(TAG, "[Rozlaczanie] -> Socket nie jest polaczony")
                                stateHandler.post {
                                    bind.connection.ustawStan(StanPolaczenia.BLAD_POLACZENIA)
                                }
                                ConnectedThread(deviceSocket).cancel()
                            }

                        }
                        StanPolaczenia.LACZE_SIE->{
                            //pojecia nie mam
                        }
                        StanPolaczenia.EDYCJA->{
                            //...pojecia nie mam
                        }
                    }
                }else{
                    Log.d(TAG,"Wybrane urzadzenie to null !")
                }

            }

            bind.connection.btnUstawienia.setOnClickListener {
                Log.d(TAG, "btn Ustawienia - klik")
                replaceFragment(UstawieniaFragment(), aktualneUstawieniaRobota) // Fragment ustawienia
                bind.connection.ustawStan(StanPolaczenia.EDYCJA)
            }
/*
            // PRZYCISK TESTOWY
            bind.btnTest1.setOnClickListener {
                Log.d(TAG,"Test1 - click!")
                replaceFragment(UstawieniaFragment(),"");
                /*
                //Spamowanie na pałe pakietem
                if (deviceSocket.isConnected){
                    // Uwaga ! znak \n - jest znakiem informujacym kontroler o koncu komunikatu
                    val msg="<{\"audio\":{\"PN\":3}}\n"
                    //val msg="F"
                    ConnectedThread(deviceSocket).write(msg.toByteArray())
                    Log.d(TAG,"$msg")
                }else{
                    Log.d(TAG,"(MainActivity)(ch:sterowanie)-> Socket nie polaczony!")
                }
               */
            }
*/

        }

    }

    /**
     * Zamiana zawartości głównego kontenera fragmentów wskazanym.
     */
    private fun replaceFragment(fragment: Fragment, data : String){
        val fragmentManager = supportFragmentManager
        val bundle = Bundle()
        bundle.putString(ARG_DATA,data) // <-- put test data

        fragment.arguments = bundle
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.setCustomAnimations(R.anim.enter_from_bottom, 0, 0, R.anim.exit_to_bottom)
        fragmentTransaction.replace(R.id.mainFragmentContainer,fragment)
        fragmentTransaction.commit()
    }

    /**
     * Obsługa informacji zwrotnej z fragmentu odpowiedzialnego za sterowanie robotem
     */
    override fun kanalSterowanie(msg: String) {
        Log.d(TAG,"(MainActivity) wiadomosc od(sterowanie): $msg")
        if (!::deviceSocket.isInitialized) {
            Log.d(TAG,"Zmienna deviceSoket nie została zainicjalizowana.")
            return
        }
        if (deviceSocket.isConnected){
            //Uwaga! znak '\n' informuje kontroler o koncu komunikatu, niezbedny
            val msgEnd = msg + '\n'
            ConnectedThread(deviceSocket).write(msgEnd.toByteArray())
        }else{
            Log.d(TAG,"(MainActivity)(ch:sterowanie)-> Socket nie polaczony!")
            bind.connection.ustawStan(StanPolaczenia.ROZLACZONO)
        }
    }

    /**
     * Obsługa informacji zwrotnej od fragmentu odpowiedzialnego za ustawienia.
     */
    override fun kanalUstawienia(msg: String) {
        Log.d(TAG,"(MainActivity) wiadomosc od(ustawienia): $msg")
        //zamknij - bez modyfikacji danych
        if (msg.equals("{}")) {
            bind.connection.ustawStan(StanPolaczenia.POLACZONO)
            return
        }
        //wyslij nowe ustawienia
        if (!::deviceSocket.isInitialized) {
            Log.d(TAG,"Zmienna deviceSoket nie została zainicjalizowana.")
            return
        }
        if (deviceSocket.isConnected){
            //Powracajace dane z fragmentu uaktualniaja nasze ustawienia
            aktualneUstawieniaRobota = msg
            //Uwaga! znak '\n' informuje kontroler o koncu komunikatu, niezbedny
            val msgEnd = msg + '\n'
            ConnectedThread(deviceSocket).write(msgEnd.toByteArray())
            bind.connection.ustawStan(StanPolaczenia.POLACZONO)
        }else{
            Log.d(TAG,"(MainActivity)(ch:ustawienia)-> Socket nie polaczony!")
            bind.connection.ustawStan(StanPolaczenia.ROZLACZONO)
        }
    }

    /**
     * Z względu ma różny poziom uprawnień(normalne/real time) BlueTooth, który się zmienia wraz z androidami,
     *  inaczej jest do Androida 11 a inaczej od Androida 12 sprawdzamy czy mamy wszystkie
     *  uprawnienia i testujemy wynik. True - teoretycznie mamy wszystko
     */
    private fun gotBTPerms(context: Context, showTestInLogs :Boolean ): Boolean {

        var gotAllPerms = true
        val permissionsRequired =
            if (Build.VERSION.SDK_INT <= 30) { //android 11 lub nizej
                arrayOf(
                    Manifest.permission.BLUETOOTH,
                    Manifest.permission.BLUETOOTH_ADMIN,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            } else {
                arrayOf(
                    Manifest.permission.BLUETOOTH, //BUG XIAOMI
                    Manifest.permission.BLUETOOTH_CONNECT,
                    Manifest.permission.BLUETOOTH_SCAN
                )
            }
        var permNum = 0
        if (showTestInLogs) {
            val sdk = Build.VERSION.SDK_INT
            Log.d(TAG,"BT wlaczane : $isBluetoothEnabled")
            Log.d(TAG, "Testuje uprawnienia ${permissionsRequired.size} entries , SDK :  $sdk")
        }
        permissionsRequired.forEach { requiredPermission ->
            permNum++
            if (ContextCompat.checkSelfPermission(
                    context,
                    requiredPermission
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                if (showTestInLogs) {
                    Log.d(TAG, "$permNum ) $requiredPermission -> JEST")
                }
            } else {
                Log.d(TAG, "$permNum ) $requiredPermission -> BRAK")
                gotAllPerms = false
            }
        }
        if (showTestInLogs) {
            Log.d(TAG, "Wynik testu : $gotAllPerms")
        }
        return gotAllPerms
    }
    /**
     *
     */
    @SuppressLint("MissingPermission")
    private fun czyRobotSparowany(): Boolean{
        val listaUrzadzen = bluetoothAdapter?.bondedDevices
        if (listaUrzadzen == null) return false
        for ( dev : BluetoothDevice  in listaUrzadzen){
            if (dev.address.equals(ROBOT_MAC,ignoreCase = true)) return true
        }
        return false
    }
}