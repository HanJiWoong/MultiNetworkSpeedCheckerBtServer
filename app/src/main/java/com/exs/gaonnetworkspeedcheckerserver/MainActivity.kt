package com.exs.gaonnetworkspeedcheckerserver

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.SystemClock
import android.util.Log
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import com.afra55.speedometer.SpeedometerDialog
import com.example.bluetoothserver.net.BTConstant
import com.example.bluetoothserver.net.BluetoothServer
import com.example.bluetoothserver.net.SocketListener
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import java.util.ArrayList

class MainActivity : AppCompatActivity() {

    private val NETWORK_TYPE_WIFI6E = "wifi6e"
    private val NETWORK_TYPE_WIFI6 = "wifi6"
    private val NETWORK_TYPE_WIFI5 = "wifi5"
    private val NETWORK_TYPE_5G = "5g"
    private val NETWORK_TYPE_LTE = "lte"

    private val STORED_COUNT:Int = 180

    // ************
    // UI 관련 멤버 변수
    // ************
    private lateinit var mBtnResultCompare: Button
    private lateinit var mBtnAction: Button

    private lateinit var mSpeedoMeterWifi6E: SpeedometerDialog
    private lateinit var mSpeedoMeter5G: SpeedometerDialog
    private lateinit var mSpeedoMeterWifi6: SpeedometerDialog
    private lateinit var mSpeedometerLTE: SpeedometerDialog
    private lateinit var mSpeedometerWifi5: SpeedometerDialog

    private lateinit var mTVWifi6EDownload: TextView
    private lateinit var mTVWifi6EUpload: TextView
    private lateinit var mTVWifi6EDelayTime: TextView
    private lateinit var mTVWifi6EZitter: TextView

    private lateinit var mTV5GDownload: TextView
    private lateinit var mTV5GUpload: TextView
    private lateinit var mTV5GDelayTime: TextView
    private lateinit var mTV5GZitter: TextView

    private lateinit var mTVWifi6Download: TextView
    private lateinit var mTVWifi6Upload: TextView
    private lateinit var mTVWifi6DelayTime: TextView
    private lateinit var mTVWifi6Zitter: TextView

    private lateinit var mTVLTEDownload: TextView
    private lateinit var mTVLTEUpload: TextView
    private lateinit var mTVLTEDelayTime: TextView
    private lateinit var mTVLTEZitter: TextView

    private lateinit var mTVWifi5Download: TextView
    private lateinit var mTVWifi5Upload: TextView
    private lateinit var mTVWifi5DelayTime: TextView
    private lateinit var mTVWifi5Zitter: TextView

    private lateinit var mTVComplete:TextView


    // ************
    // 블루투스 관련 멤버 변수
    // ************

    private var handler: Handler = Handler()
    private var sbLog = StringBuilder()
    private var btServer: BluetoothServer = BluetoothServer()

    private var mArrWifi6e: ArrayList<RecivedSpeedData> = ArrayList()
    private var mArrWifi6: ArrayList<RecivedSpeedData> = ArrayList()
    private var mArrWifi5: ArrayList<RecivedSpeedData> = ArrayList()
    private var mArr5g: ArrayList<RecivedSpeedData> = ArrayList()
    private var mArrLTE: ArrayList<RecivedSpeedData> = ArrayList()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkPermission()
        }

        AppController.Instance.init(this, btServer)

        initUI()
        setListener()

        btServer.setOnSocketListener(mOnSocketListener)
        btServer.accept()
    }

    override fun onDestroy() {
        super.onDestroy()

        reset()
        AppController.Instance.bluetoothOff()
    }

    private fun initUI() {
        mBtnResultCompare = findViewById(R.id.BtnCompareResult)
        mBtnAction = findViewById(R.id.BtnCheckAction)

        mSpeedoMeterWifi6E = findViewById(R.id.speedometer_wifi6e)
        mSpeedoMeter5G = findViewById(R.id.speedometer_5g)
        mSpeedoMeterWifi6 = findViewById(R.id.speedometer_wifi6)
        mSpeedometerLTE = findViewById(R.id.speedometer_lte)
        mSpeedometerWifi5 = findViewById(R.id.speedometer_wifi5)

        mTVWifi6EDownload = findViewById(R.id.TVCheckDataWifi6eDownload)
        mTVWifi6EUpload = findViewById(R.id.TVCheckDataWifi6eUpload)
        mTVWifi6EDelayTime = findViewById(R.id.TVCheckDataWifi6eDelayTime)
        mTVWifi6EZitter = findViewById(R.id.TVCheckDataWifi6eZitter)

        mTV5GDownload = findViewById(R.id.TVCheckData5gDownload)
        mTV5GUpload = findViewById(R.id.TVCheckData5gUpload)
        mTV5GDelayTime = findViewById(R.id.TVCheckData5gDelayTime)
        mTV5GZitter = findViewById(R.id.TVCheckData5gZitter)

        mTVWifi6Download = findViewById(R.id.TVCheckDataWifi6Download)
        mTVWifi6Upload = findViewById(R.id.TVCheckDataWifi6Upload)
        mTVWifi6DelayTime = findViewById(R.id.TVCheckDataWifi6DelayTime)
        mTVWifi6Zitter = findViewById(R.id.TVCheckDataWifi6Zitter)

        mTVLTEDownload = findViewById(R.id.TVCheckDataLTEDownload)
        mTVLTEUpload = findViewById(R.id.TVCheckDataLTEUpload)
        mTVLTEDelayTime = findViewById(R.id.TVCheckDataLTEDelayTime)
        mTVLTEZitter = findViewById(R.id.TVCheckDataLTEZitter)

        mTVWifi5Download = findViewById(R.id.TVCheckDataWifi5Download)
        mTVWifi5Upload = findViewById(R.id.TVCheckDataWifi5Upload)
        mTVWifi5DelayTime = findViewById(R.id.TVCheckDataWifi5DelayTime)
        mTVWifi5Zitter = findViewById(R.id.TVCheckDataWifi5Zitter)

        mTVComplete = findViewById(R.id.TVMeasureComplete)


        mSpeedoMeterWifi6E.setLimitNumber(3000)
        mSpeedoMeterWifi6E.setMaxNumber(3000F)
        mSpeedoMeterWifi6.setLimitNumber(3000)
        mSpeedoMeterWifi6.setMaxNumber(3000F)
        mSpeedometerWifi5.setLimitNumber(3000)
        mSpeedometerWifi5.setMaxNumber(3000F)
        mSpeedoMeter5G.setLimitNumber(3000)
        mSpeedoMeter5G.setMaxNumber(3000F)
        mSpeedometerLTE.setLimitNumber(3000)
        mSpeedometerLTE.setMaxNumber(3000F)
    }

    private fun setValueInUI(type:String, downSpeed:Int, upSpeed:Int, delayTime:Int, jitter:Int) {
        runOnUiThread {
            if (mBtnAction.isSelected) {
                val resultDownSpeed: Float = (downSpeed / 1000).toFloat()
                val resultUpSpeed: Float = (upSpeed / 1000).toFloat()
                var resultJitter: Int = jitter
                if (resultJitter < 0) {
                    resultJitter *= -1
                }

                if (type == NETWORK_TYPE_WIFI6E) {
                    mSpeedoMeterWifi6E.setCurrentNumber(resultDownSpeed)

                    mTVWifi6EDownload.text = "${resultDownSpeed} Mbps"
                    mTVWifi6EUpload.text = "${resultUpSpeed} Mbps"
                    mTVWifi6EDelayTime.text = "${delayTime} ms"
                    mTVWifi6EZitter.text = "${resultJitter} ms"
                } else if (type == NETWORK_TYPE_WIFI6) {
                    mSpeedoMeterWifi6.setCurrentNumber(resultDownSpeed)

                    mTVWifi6Download.text = "${resultDownSpeed} Mbps"
                    mTVWifi6Upload.text = "${resultUpSpeed} Mbps"
                    mTVWifi6DelayTime.text = "${delayTime} ms"
                    mTVWifi6Zitter.text = "${resultJitter} ms"
                } else if (type == NETWORK_TYPE_WIFI5) {
                    mSpeedometerWifi5.setCurrentNumber(resultDownSpeed)

                    mTVWifi5Download.text = "${resultDownSpeed} Mbps"
                    mTVWifi5Upload.text = "${resultUpSpeed} Mbps"
                    mTVWifi5DelayTime.text = "${delayTime} ms"
                    mTVWifi5Zitter.text = "${resultJitter} ms"
                } else if (type == NETWORK_TYPE_5G) {
                    mSpeedoMeter5G.setCurrentNumber(resultDownSpeed)

                    mTV5GDownload.text = "${resultDownSpeed} Mbps"
                    mTV5GUpload.text = "${resultUpSpeed} Mbps"
                    mTV5GDelayTime.text = "${delayTime} ms"
                    mTV5GZitter.text = "${resultJitter} ms"
                } else if (type == NETWORK_TYPE_LTE) {
                    mSpeedometerLTE.setCurrentNumber(resultDownSpeed)

                    mTVLTEDownload.text = "${resultDownSpeed} Mbps"
                    mTVLTEUpload.text = "${resultUpSpeed} Mbps"
                    mTVLTEDelayTime.text = "${delayTime} ms"
                    mTVLTEZitter.text = "${resultJitter} ms"
                }

            }
        }
    }

    private fun setValueInUIForResult() {
        var type = NETWORK_TYPE_WIFI6E
        var down = 0
        var up = 0
        var delay = 0
        var jitter = 0

        for (data in mArrWifi6e) {
            down += data.downSpeed
            up += data.upSpeed
            delay += data.delayTime
            jitter += data.jitter

            down = down/mArrWifi6e.size
            up = up/mArrWifi6e.size
            delay = delay/mArrWifi6e.size
            jitter = delay/mArrWifi6e.size
        }

        setValueInUI(type, down, up, delay, jitter)

        down = 0
        up = 0
        delay = 0
        jitter = 0

        type = NETWORK_TYPE_WIFI6
        for (data in mArrWifi6) {
            down += data.downSpeed
            up += data.upSpeed
            delay += data.delayTime
            jitter += data.jitter

            down = down/mArrWifi6.size
            up = up/mArrWifi6.size
            delay = delay/mArrWifi6.size
            jitter = delay/mArrWifi6.size
        }


        setValueInUI(type, down, up, delay, jitter)

        down = 0
        up = 0
        delay = 0
        jitter = 0

        type = NETWORK_TYPE_WIFI5
        for (data in mArrWifi5) {
            down += data.downSpeed
            up += data.upSpeed
            delay += data.delayTime
            jitter += data.jitter

            down = down/mArrWifi5.size
            up = up/mArrWifi5.size
            delay = delay/mArrWifi5.size
            jitter = delay/mArrWifi5.size
        }


        setValueInUI(type, down, up, delay, jitter)

        down = 0
        up = 0
        delay = 0
        jitter = 0

        type = NETWORK_TYPE_5G
        for (data in mArr5g) {
            down += data.downSpeed
            up += data.upSpeed
            delay += data.delayTime
            jitter += data.jitter

            down = down/mArr5g.size
            up = up/mArr5g.size
            delay = delay/mArr5g.size
            jitter = delay/mArr5g.size
        }


        setValueInUI(type, down, up, delay, jitter)

        down = 0
        up = 0
        delay = 0
        jitter = 0

        type = NETWORK_TYPE_LTE
        for (data in mArrLTE) {
            down += data.downSpeed
            up += data.upSpeed
            delay += data.delayTime
            jitter += data.jitter

            down = down/mArrLTE.size
            up = up/mArrLTE.size
            delay = delay/mArrLTE.size
            jitter = delay/mArrLTE.size
        }

        setValueInUI(type, down, up, delay, jitter)

    }

    private fun setSpeedometorValue(type:String, downTemp:Int,upTemp:Int) {

        runOnUiThread {
            if (mBtnAction.isSelected) {
                val resultDownSpeed: Float = (downTemp / 1000).toFloat()
                val resultUpSpeed: Float = (upTemp / 1000).toFloat()

                if (type == NETWORK_TYPE_WIFI6E) {
                    mSpeedoMeterWifi6E.setCurrentNumber(resultDownSpeed)

                    mTVWifi6EDownload.text = "${resultDownSpeed} Mbps"
                    mTVWifi6EUpload.text = "${resultUpSpeed} Mbps"
                } else if (type == NETWORK_TYPE_WIFI6) {
                    mSpeedoMeterWifi6.setCurrentNumber(resultDownSpeed)

                    mTVWifi6Download.text = "${resultDownSpeed} Mbps"
                    mTVWifi6Upload.text = "${resultUpSpeed} Mbps"
                } else if (type == NETWORK_TYPE_WIFI5) {
                    mSpeedometerWifi5.setCurrentNumber(resultDownSpeed)

                    mTVWifi5Download.text = "${resultDownSpeed} Mbps"
                    mTVWifi5Upload.text = "${resultUpSpeed} Mbps"
                } else if (type == NETWORK_TYPE_5G) {
                    mSpeedoMeter5G.setCurrentNumber(resultDownSpeed)

                    mTV5GDownload.text = "${resultDownSpeed} Mbps"
                    mTV5GUpload.text = "${resultUpSpeed} Mbps"
                } else if (type == NETWORK_TYPE_LTE) {
                    mSpeedometerLTE.setCurrentNumber(resultDownSpeed)

                    mTVLTEDownload.text = "${resultDownSpeed} Mbps"
                    mTVLTEUpload.text = "${resultUpSpeed} Mbps"
                }
            }
        }
    }

    private fun checkingStoredFinish() : Boolean {

        if(mArrWifi6e.count() >= STORED_COUNT && mArrWifi6.count() >= STORED_COUNT && mArrWifi5.count() >= STORED_COUNT && mArr5g.count() >= STORED_COUNT && mArrLTE.count() >= STORED_COUNT) {
            return true
        }

        return false
    }

    private fun changeCheckingMode() {
        mSpeedoMeterWifi6E.setBackgroundResource(R.drawable.speedometer_bg_wifi6e_checking)
    }

    private fun reset() {

        mArrWifi6e = ArrayList()
        mArrWifi6 = ArrayList()
        mArrWifi5 = ArrayList()
        mArr5g = ArrayList()
        mArrLTE = ArrayList()

        mSpeedoMeterWifi6E.setCurrentNumber(0F)
        mSpeedoMeterWifi6E.setBackgroundResource(R.drawable.speedometer_bg)

        mTVWifi6EDownload.text = "- Mbps"
        mTVWifi6EUpload.text = "- Mbps"
        mTVWifi6EDelayTime.text = "- ms"
        mTVWifi6EZitter.text = "- ms"

        mSpeedoMeterWifi6.setCurrentNumber(0F)

        mTVWifi6Download.text = "- Mbps"
        mTVWifi6Upload.text = "- Mbps"
        mTVWifi6DelayTime.text = "- ms"
        mTVWifi6Zitter.text = "- ms"

        mSpeedometerWifi5.setCurrentNumber(0F)

        mTVWifi5Download.text = "- Mbps"
        mTVWifi5Upload.text = "- Mbps"
        mTVWifi5DelayTime.text = "- ms"
        mTVWifi5Zitter.text = "- ms"

        mSpeedoMeter5G.setCurrentNumber(0F)

        mTV5GDownload.text = "- Mbps"
        mTV5GUpload.text = "- Mbps"
        mTV5GDelayTime.text = "- ms"
        mTV5GZitter.text = "- ms"

        mSpeedometerLTE.setCurrentNumber(0F)

        mTVLTEDownload.text = "- Mbps"
        mTVLTEUpload.text = "- Mbps"
        mTVLTEDelayTime.text = "- ms"
        mTVLTEZitter.text = "- ms"

        mTVComplete.visibility = View.GONE

    }

    // ************
    // 시작 시 statusBar, 탐색 메뉴 등의 버튼을 보이지 않게 하기 위한 부분
    // ************

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) hideSystemUI()

        val layoutMainView: View = this.findViewById<View>(R.id.mainLayout)
        Log.e(
            "MainActivity",
            "Width : ${layoutMainView.width} , height : ${layoutMainView.height}}"
        )
    }

    private fun hideSystemUI() {
        // Enables regular immersive mode.
        // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
        // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_IMMERSIVE
                // Set the content to appear under the system bars so that the
                // content doesn't resize when the system bars hide and show.
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                // Hide the nav bar and status bar
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN)
    }

    // Shows the system bars by removing all the flags
    // except for the ones that make the content appear under the system bars.
    private fun showSystemUI() {
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
    }

    // ************
    // 블루투스 관련 메소드
    // ************

    private fun setListener() {
        mBtnAction.setOnClickListener {
            mBtnAction.isSelected = !mBtnAction.isSelected

            if(mBtnAction.isSelected) {
                reset()

                mBtnAction.background = resources.getDrawable(R.drawable.btn_check_reset, theme)
                mBtnAction.text = "RESET"

                changeCheckingMode()
            } else {
                mBtnAction.background = resources.getDrawable(R.drawable.btn_check_start, theme)
                mBtnAction.text = "START"
            }
        }

        mBtnResultCompare.setOnClickListener {
            val complete = checkingStoredFinish()

            if(complete) {

                val intent:Intent = Intent(this, ResultActivity::class.java)
                intent.putExtra(getString(R.string.intent_name_arr_wifi6e),mArrWifi6e)
                intent.putExtra(getString(R.string.intent_name_arr_wifi6),mArrWifi6)
                intent.putExtra(getString(R.string.intent_name_arr_wifi5),mArrWifi5)
                intent.putExtra(getString(R.string.intent_name_arr_5g),mArr5g)
                intent.putExtra(getString(R.string.intent_name_arr_lte),mArrLTE)
                startActivity(intent)
            } else {
                Toast.makeText(this, "측정이 완료되지 않았거나 실패하였습니다. 다시 측정해보세요.",Toast.LENGTH_LONG).show()

            }
        }
    }

    fun log(message: String) {

        Log.e("recived data", message.toString())

    }

    private val mOnSocketListener: SocketListener = object : SocketListener {
        override fun onConnect() {
            log("Connect!\n")
        }

        override fun onDisconnect() {
            log("Disconnect!\n")
        }

        override fun onError(e: Exception?) {
            e?.let { log(e.toString() + "\n") }
        }

        override fun onReceive(msg: String?) {
            Thread {

                if(mBtnAction.isSelected) {

                    var data:RecivedSpeedData

                    val recivedArr = msg?.split("::")

                    recivedArr?.let {
                        if (it.get(0) ?: "" == NETWORK_TYPE_WIFI6E) {
                            val downSpeed: Int = it.get(1).toInt()
                            val upSpeed: Int = it.get(2).toInt()
                            val tempDown: Int = downSpeed - (0..downSpeed / 3).random()
                            val tempUp: Int = upSpeed - (0..upSpeed / 3).random()

                            setValueInUI(
                                NETWORK_TYPE_WIFI6E,
                                tempDown,
                                tempUp,
                                it.get(3).toInt(),
                                it.get(4).toInt()
                            )
                            SystemClock.sleep(250)


                            setSpeedometorValue(NETWORK_TYPE_WIFI6E, tempDown, tempUp)

                            data = RecivedSpeedData(tempDown,tempUp,it.get(3).toInt(),it.get(4).toInt())
                            mArrWifi6e.add(data)

                        } else if (it.get(0) ?: "" == NETWORK_TYPE_WIFI6) {
                            val downSpeed: Int = it.get(1).toInt()
                            val upSpeed: Int = it.get(2).toInt()
                            val tempDown: Int = downSpeed - (0..downSpeed / 3).random()
                            val tempUp: Int = upSpeed - (0..upSpeed / 3).random()

                            setValueInUI(
                                NETWORK_TYPE_WIFI6,
                                tempDown,
                                tempUp,
                                it.get(3).toInt(),
                                it.get(4).toInt()
                            )
                            SystemClock.sleep(250)

                            setSpeedometorValue(NETWORK_TYPE_WIFI6, tempDown, tempUp)

                            data = RecivedSpeedData(tempDown,tempUp,it.get(3).toInt(),it.get(4).toInt())
                            mArrWifi6.add(data)
                        } else if (it.get(0) ?: "" == NETWORK_TYPE_WIFI5) {
                            val downSpeed: Int = it.get(1).toInt()
                            val upSpeed: Int = it.get(2).toInt()
                            val tempDown: Int = downSpeed - (0..downSpeed / 3).random()
                            val tempUp: Int = upSpeed - (0..upSpeed / 3).random()

                            setValueInUI(
                                NETWORK_TYPE_WIFI5,
                                tempDown,
                                tempUp,
                                it.get(3).toInt(),
                                it.get(4).toInt()
                            )
                            SystemClock.sleep(250)


                            setSpeedometorValue(NETWORK_TYPE_WIFI5, tempDown, tempUp)

                            data = RecivedSpeedData(tempDown,tempUp,it.get(3).toInt(),it.get(4).toInt())
                            mArrWifi5.add(data)

                        } else if (it.get(0) ?: "" == NETWORK_TYPE_5G) {
                            val downSpeed: Int = it.get(1).toInt()
                            val upSpeed: Int = it.get(2).toInt()
                            val tempDown: Int = downSpeed - (0..downSpeed / 3).random()
                            val tempUp: Int = upSpeed - (0..upSpeed / 3).random()

                            setValueInUI(
                                NETWORK_TYPE_5G,
                                tempDown,
                                tempUp,
                                it.get(3).toInt(),
                                it.get(4).toInt()
                            )
                            SystemClock.sleep(250)



                            setSpeedometorValue(NETWORK_TYPE_5G, tempDown, tempUp)

                            data = RecivedSpeedData(tempDown,tempUp,it.get(3).toInt(),it.get(4).toInt())
                            mArr5g.add(data)

                        } else if (it.get(0) ?: "" == NETWORK_TYPE_LTE) {
                            val downSpeed: Int = it.get(1).toInt()
                            val upSpeed: Int = it.get(2).toInt()
                            val tempDown: Int = downSpeed - (0..downSpeed / 3).random()
                            val tempUp: Int = upSpeed - (0..upSpeed / 3).random()

                            setValueInUI(
                                NETWORK_TYPE_LTE,
                                tempDown,
                                tempUp,
                                it.get(3).toInt(),
                                it.get(4).toInt()
                            )
                            SystemClock.sleep(250)


                            setSpeedometorValue(NETWORK_TYPE_LTE, tempDown, tempUp)

                            data = RecivedSpeedData(tempDown,tempUp,it.get(3).toInt(),it.get(4).toInt())
                            mArrLTE.add(data)

                        } else {
                            msg?.let { log("이상한 데이터 : $it") }
                        }

                        val complete = checkingStoredFinish()

                        if(complete) {
                            runOnUiThread {
                                mBtnAction.isSelected = false
                                mBtnAction.background = resources.getDrawable(R.drawable.btn_check_start, theme)
                                mBtnAction.text = "START"

                                mTVComplete.visibility = View.VISIBLE

                                setValueInUIForResult()
                            }
                        }
                    }
                }
            }.start()
        }

        override fun onSend(msg: String?) {
            msg?.let { log("Send : $it\n") }
        }

        override fun onLogPrint(msg: String?) {
            msg?.let { log("$it\n") }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            BTConstant.BT_REQUEST_ENABLE -> if (resultCode == Activity.RESULT_OK) {
                Toast.makeText(applicationContext, "블루투스 활성화", Toast.LENGTH_LONG).show()
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(applicationContext, "취소", Toast.LENGTH_LONG).show()
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun checkPermission() {
        val permissions = arrayOf(
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN
        )

        for (permission in permissions) {
            val chk = checkCallingOrSelfPermission(Manifest.permission.WRITE_CONTACTS)
            if (chk == PackageManager.PERMISSION_DENIED) {
                requestPermissions(permissions, 0)
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 0) {
            for (element in grantResults) {
                if (element == PackageManager.PERMISSION_GRANTED) {
                } else {
                    TedPermission(this)
                        .setPermissionListener(object : PermissionListener {
                            override fun onPermissionGranted() {

                            }

                            override fun onPermissionDenied(deniedPermissions: ArrayList<String?>) {

                            }
                        })
                        .setDeniedMessage("You have permission to set up.")
                        .setPermissions(
                            Manifest.permission.BLUETOOTH,
                            Manifest.permission.BLUETOOTH_ADMIN
                        )
                        .setGotoSettingButton(true)
                        .check();
                }
            }
        }
    }


}