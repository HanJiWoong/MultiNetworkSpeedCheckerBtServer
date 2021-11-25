package com.exs.gaonnetworkspeedcheckerserver

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import java.util.*
import kotlin.math.roundToLong

class ResultActivity : AppCompatActivity() {

    private val FUNCTION_TYPE_DOWNLOAD = "download"
    private val FUNCTION_TYPE_UPLOAD = "upload"
    private val FUNCTION_TYPE_DELAY = "delayTime"
    private val FUNCTION_TYPE_JITTER = "jitter"

    // ************
    // UI 관련 멤버 변수
    // ************

    private lateinit var mBtnMeasureSpeed: Button

    private lateinit var mBtnTabDownload: Button
    private lateinit var mBtnTabUpload: Button
    private lateinit var mBtnTabDelayTime: Button
    private lateinit var mBtnTabJitter: Button

    private lateinit var mTVTextLTE: TextView
    private lateinit var mTVText5G: TextView
    private lateinit var mTVTextWifi6e: TextView
    private lateinit var mTVTextWifi6: TextView
    private lateinit var mTVTextWifi5: TextView

    private lateinit var mTVTextLTEUnit: TextView
    private lateinit var mTVText5GUnit: TextView
    private lateinit var mTVTextWifi6eUnit: TextView
    private lateinit var mTVTextWifi6Unit: TextView
    private lateinit var mTVTextWifi5Unit: TextView

    private lateinit var mLineChart: LineChart

    // ************
    // 표시할 데이터
    // ************
    private var mArrWifi6e: ArrayList<RecivedSpeedData> = ArrayList()
    private var mArrWifi6: ArrayList<RecivedSpeedData> = ArrayList()
    private var mArrWifi5: ArrayList<RecivedSpeedData> = ArrayList()
    private var mArr5g: ArrayList<RecivedSpeedData> = ArrayList()
    private var mArrLTE: ArrayList<RecivedSpeedData> = ArrayList()

    // ***********
    // 동작 관련 멤버 변수
    // ***********
    private var mStrFuncType: String = FUNCTION_TYPE_DOWNLOAD

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        mArrLTE =
            intent.getSerializableExtra(getString(R.string.intent_name_arr_lte)) as ArrayList<RecivedSpeedData>
        mArr5g =
            intent.getSerializableExtra(getString(R.string.intent_name_arr_5g)) as ArrayList<RecivedSpeedData>
        mArrWifi5 =
            intent.getSerializableExtra(getString(R.string.intent_name_arr_wifi5)) as ArrayList<RecivedSpeedData>
        mArrWifi6 =
            intent.getSerializableExtra(getString(R.string.intent_name_arr_wifi6)) as ArrayList<RecivedSpeedData>
        mArrWifi6e =
            intent.getSerializableExtra(getString(R.string.intent_name_arr_wifi6e)) as ArrayList<RecivedSpeedData>

        initUI()
        setUI()
        setListener()

        setResultText()
        setChart()
    }

    private fun initUI() {
        mBtnMeasureSpeed = findViewById(R.id.BtnMeasureSpeed)

        mBtnTabDownload = findViewById(R.id.BtnResultTabDownload)
        mBtnTabUpload = findViewById(R.id.BtnResultTabUpload)
        mBtnTabDelayTime = findViewById(R.id.BtnResultTabDelayTime)
        mBtnTabJitter = findViewById(R.id.BtnResultTabJitter)

        mTVTextLTE = findViewById(R.id.TVResultTextDataLTEValue)
        mTVText5G = findViewById(R.id.TVResultTextData5GValue)
        mTVTextWifi5 = findViewById(R.id.TVResultTextDataWifi5Value)
        mTVTextWifi6 = findViewById(R.id.TVResultTextDataWifi6Value)
        mTVTextWifi6e = findViewById(R.id.TVResultTextDataWifi6eValue)

        mTVTextLTEUnit = findViewById(R.id.TVResultTextDataLTEUnit)
        mTVText5GUnit = findViewById(R.id.TVResultTextData5GUnit)
        mTVTextWifi6eUnit = findViewById(R.id.TVResultTextDataWifi6eUnit)
        mTVTextWifi6Unit = findViewById(R.id.TVResultTextDataWifi6Unit)
        mTVTextWifi5Unit = findViewById(R.id.TVResultTextDataWifi5Unit)

        mLineChart = findViewById(R.id.ChartResult)
    }

    private fun setListener() {
        mBtnTabDownload.setOnClickListener {
            if (mStrFuncType != FUNCTION_TYPE_DOWNLOAD) {
                mStrFuncType = FUNCTION_TYPE_DOWNLOAD

                setUI()
                setResultText()
                setChart()
            }
        }

        mBtnTabUpload.setOnClickListener {
            if (mStrFuncType != FUNCTION_TYPE_UPLOAD) {
                mStrFuncType = FUNCTION_TYPE_UPLOAD

                setUI()
                setResultText()
                setChart()
            }
        }

        mBtnTabDelayTime.setOnClickListener {
            if (mStrFuncType != FUNCTION_TYPE_DELAY) {
                mStrFuncType = FUNCTION_TYPE_DELAY

                setUI()
                setResultText()
                setChart()
            }
        }

        mBtnTabJitter.setOnClickListener {
            if (mStrFuncType != FUNCTION_TYPE_JITTER) {
                mStrFuncType = FUNCTION_TYPE_JITTER

                setUI()
                setResultText()
                setChart()
            }
        }

        mBtnMeasureSpeed.setOnClickListener {
            finish()
        }
    }

    private fun setUI() {

        mBtnTabDownload.background = getDrawable(R.drawable.bg_tab_non_selected)
        mBtnTabUpload.background = getDrawable(R.drawable.bg_tab_non_selected)
        mBtnTabDelayTime.background = getDrawable(R.drawable.bg_tab_non_selected)
        mBtnTabJitter.background = getDrawable(R.drawable.bg_tab_non_selected)

        if (mStrFuncType == FUNCTION_TYPE_DOWNLOAD) {
            mBtnTabDownload.background = getDrawable(R.drawable.bg_tab_selected)
        } else if (mStrFuncType == FUNCTION_TYPE_UPLOAD) {
            mBtnTabUpload.background = getDrawable(R.drawable.bg_tab_selected)
        } else if (mStrFuncType == FUNCTION_TYPE_DELAY) {
            mBtnTabDelayTime.background = getDrawable(R.drawable.bg_tab_selected)
        } else if (mStrFuncType == FUNCTION_TYPE_JITTER) {
            mBtnTabJitter.background = getDrawable(R.drawable.bg_tab_selected)
        }
    }

    private fun setResultText() {
        if (mStrFuncType == FUNCTION_TYPE_DOWNLOAD) {
            mTVTextLTEUnit.text = "Mbps"
            mTVText5GUnit.text = "Mbps"
            mTVTextWifi5Unit.text = "Mbps"
            mTVTextWifi6Unit.text = "Mbps"
            mTVTextWifi6eUnit.text = "Mbps"

            var maxLTEValue: Int = 0
            var max5GValue: Int = 0
            var maxWifi5Value: Int = 0
            var maxWifi6Value: Int = 0
            var maxWifi6eValue: Int = 0

            for (item in mArrLTE) {
                if (maxLTEValue < item.downSpeed) {
                    maxLTEValue = item.downSpeed
                }
            }

            for (item in mArr5g) {
                if (max5GValue < item.downSpeed) {
                    max5GValue = item.downSpeed
                }
            }

            for (item in mArrWifi5) {
                if (maxWifi5Value < item.downSpeed) {
                    maxWifi5Value = item.downSpeed
                }
            }

            for (item in mArrWifi6) {
                if (maxWifi6Value < item.downSpeed) {
                    maxWifi6Value = item.downSpeed
                }
            }

            for (item in mArrWifi6e) {
                if (maxWifi6eValue < item.downSpeed) {
                    maxWifi6eValue = item.downSpeed
                }
            }

            mTVTextLTE.text = "${(maxLTEValue / 1000).toFloat()}"
            mTVText5G.text = "${(max5GValue / 1000).toFloat()}"
            mTVTextWifi5.text = "${(maxWifi5Value / 1000).toFloat()}"
            mTVTextWifi6.text = "${(maxWifi6Value / 1000).toFloat()}"
            mTVTextWifi6e.text = "${(maxWifi6eValue / 1000).toFloat()}"

        } else if (mStrFuncType == FUNCTION_TYPE_UPLOAD) {
            mTVTextLTEUnit.text = "Mbps"
            mTVText5GUnit.text = "Mbps"
            mTVTextWifi5Unit.text = "Mbps"
            mTVTextWifi6Unit.text = "Mbps"
            mTVTextWifi6eUnit.text = "Mbps"

            var maxLTEValue: Int = 0
            var max5GValue: Int = 0
            var maxWifi5Value: Int = 0
            var maxWifi6Value: Int = 0
            var maxWifi6eValue: Int = 0

            for (item in mArrLTE) {
                if (maxLTEValue < item.upSpeed) {
                    maxLTEValue = item.upSpeed
                }
            }

            for (item in mArr5g) {
                if (max5GValue < item.upSpeed) {
                    max5GValue = item.upSpeed
                }
            }

            for (item in mArrWifi5) {
                if (maxWifi5Value < item.upSpeed) {
                    maxWifi5Value = item.upSpeed
                }
            }

            for (item in mArrWifi6) {
                if (maxWifi6Value < item.upSpeed) {
                    maxWifi6Value = item.upSpeed
                }
            }

            for (item in mArrWifi6e) {
                if (maxWifi6eValue < item.upSpeed) {
                    maxWifi6eValue = item.upSpeed
                }
            }

            mTVTextLTE.text = "${(maxLTEValue / 1000).toFloat()}"
            mTVText5G.text = "${(max5GValue / 1000).toFloat()}"
            mTVTextWifi5.text = "${(maxWifi5Value / 1000).toFloat()}"
            mTVTextWifi6.text = "${(maxWifi6Value / 1000).toFloat()}"
            mTVTextWifi6e.text = "${(maxWifi6eValue / 1000).toFloat()}"

        } else if (mStrFuncType == FUNCTION_TYPE_DELAY) {
            mTVTextLTEUnit.text = "ms"
            mTVText5GUnit.text = "ms"
            mTVTextWifi5Unit.text = "ms"
            mTVTextWifi6Unit.text = "ms"
            mTVTextWifi6eUnit.text = "ms"

            var averLTEValue: Int = 0
            var aver5GValue: Int = 0
            var averWifi5Value: Int = 0
            var averWifi6Value: Int = 0
            var averWifi6eValue: Int = 0

            for (item in mArrLTE) {
                averLTEValue += item.delayTime
            }

            for (item in mArr5g) {
                aver5GValue += item.delayTime
            }

            for (item in mArrWifi5) {
                averWifi5Value += item.delayTime
            }

            for (item in mArrWifi6) {
                averWifi6Value += item.delayTime
            }

            for (item in mArrWifi6e) {
                averWifi6eValue += item.delayTime
            }

            mTVTextLTE.text = "${(averLTEValue / mArrLTE.count()).toFloat()}"
            mTVText5G.text = "${(aver5GValue / mArr5g.count()).toFloat()}"
            mTVTextWifi5.text = "${(averWifi5Value / mArrWifi5.count()).toFloat()}"
            mTVTextWifi6.text = "${(averWifi6Value / mArrWifi6.count()).toFloat()}"
            mTVTextWifi6e.text = "${(averWifi6eValue / mArrWifi6e.count()).toFloat()}"
        } else if (mStrFuncType == FUNCTION_TYPE_JITTER) {
            mTVTextLTEUnit.text = "ms"
            mTVText5GUnit.text = "ms"
            mTVTextWifi5Unit.text = "ms"
            mTVTextWifi6Unit.text = "ms"
            mTVTextWifi6eUnit.text = "ms"

            var averLTEValue: Int = 0
            var aver5GValue: Int = 0
            var averWifi5Value: Int = 0
            var averWifi6Value: Int = 0
            var averWifi6eValue: Int = 0

            for (item in mArrLTE) {
                if (item.jitter < 0) {
                    averLTEValue += (item.jitter * -1)
                } else {
                    averLTEValue += item.jitter
                }
            }

            for (item in mArr5g) {
                if (item.jitter < 0) {
                    aver5GValue += (item.jitter * -1)
                } else {
                    aver5GValue += item.jitter
                }

            }

            for (item in mArrWifi5) {
                if (item.jitter < 0) {
                    averWifi5Value += (item.jitter * -1)
                } else {
                    averWifi5Value += item.jitter
                }

            }

            for (item in mArrWifi6) {
                if (item.jitter < 0) {
                    averWifi6Value += (item.jitter * -1)
                } else {
                    averWifi6Value += item.jitter
                }

            }

            for (item in mArrWifi6e) {
                if (item.jitter < 0) {
                    averWifi6eValue += (item.jitter * -1)
                } else {
                    averWifi6eValue += item.jitter
                }

            }

            mTVTextLTE.text = "${(averLTEValue / mArrLTE.count()).toFloat()}"
            mTVText5G.text = "${(aver5GValue / mArr5g.count()).toFloat()}"
            mTVTextWifi5.text = "${(averWifi5Value / mArrWifi5.count()).toFloat()}"
            mTVTextWifi6.text = "${(averWifi6Value / mArrWifi6.count()).toFloat()}"
            mTVTextWifi6e.text = "${(averWifi6eValue / mArrWifi6e.count()).toFloat()}"
        }
    }

    // ************
    // 시작 시 statusBar, 탐색 메뉴 등의 버튼을 보이지 않게 하기 위한 부분
    // ************

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) hideSystemUI()
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

    // ************************
    // 차트 관련 메소드
    // ************************

    private fun setChart() {
        var min: Long = 0
        var max: Long = 0

        if(mStrFuncType == FUNCTION_TYPE_DOWNLOAD) {
            for (item in mArrWifi6e) {
                if(item.downSpeed > max) {
                    max = item.downSpeed.toLong()
                }
            }

            for (item in mArrWifi6) {
                if(item.downSpeed > max) {
                    max = item.downSpeed.toLong()
                }
            }

            for (item in mArrWifi5) {
                if(item.downSpeed > max) {
                    max = item.downSpeed.toLong()
                }
            }

            for (item in mArr5g) {
                if(item.downSpeed > max) {
                    max = item.downSpeed.toLong()
                }
            }

            for (item in mArrLTE) {
                if(item.downSpeed > max) {
                    max = item.downSpeed.toLong()
                }
            }

            max = ((max/1000) * 1.5).toLong()

        } else if (mStrFuncType == FUNCTION_TYPE_UPLOAD) {
            for (item in mArrWifi6e) {
                if(item.upSpeed > max) {
                    max = item.upSpeed.toLong()
                }
            }

            for (item in mArrWifi6) {
                if(item.upSpeed > max) {
                    max = item.upSpeed.toLong()
                }
            }

            for (item in mArrWifi5) {
                if(item.upSpeed > max) {
                    max = item.upSpeed.toLong()
                }
            }

            for (item in mArr5g) {
                if(item.upSpeed > max) {
                    max = item.upSpeed.toLong()
                }
            }

            for (item in mArrLTE) {
                if(item.upSpeed > max) {
                    max = item.upSpeed.toLong()
                }
            }

            max = ((max/1000) * 1.5).toLong()

        } else if (mStrFuncType == FUNCTION_TYPE_DELAY) {
            for (item in mArrWifi6e) {
                if(item.delayTime > max) {
                    max = item.delayTime.toLong()
                }
            }

            for (item in mArrWifi6) {
                if(item.delayTime > max) {
                    max = item.delayTime.toLong()
                }
            }

            for (item in mArrWifi5) {
                if(item.delayTime > max) {
                    max = item.delayTime.toLong()
                }
            }

            for (item in mArr5g) {
                if(item.delayTime > max) {
                    max = item.delayTime.toLong()
                }
            }

            for (item in mArrLTE) {
                if(item.delayTime > max) {
                    max = item.delayTime.toLong()
                }
            }
        } else if(mStrFuncType == FUNCTION_TYPE_JITTER) {
            for (item in mArrWifi6e) {
                var jitter:Int = item.jitter
                if (jitter < 0) jitter = jitter * (-1)

                if(jitter > max) {
                    max = jitter.toLong()
                }
            }

            for (item in mArrWifi6) {
                var jitter:Int = item.jitter
                if (jitter < 0) jitter = jitter * (-1)

                if(jitter > max) {
                    max = jitter.toLong()
                }
            }

            for (item in mArrWifi5) {
                var jitter:Int = item.jitter
                if (jitter < 0) jitter = jitter * (-1)

                if(jitter > max) {
                    max = jitter.toLong()
                }
            }

            for (item in mArr5g) {
                var jitter:Int = item.jitter
                if (jitter < 0) jitter = jitter * (-1)

                if(jitter > max) {
                    max = jitter.toLong()
                }
            }

            for (item in mArrLTE) {
                var jitter:Int = item.jitter
                if (jitter < 0) jitter = jitter * (-1)

                if(jitter > max) {
                    max = jitter.toLong()
                }
            }
        }




        mLineChart.setViewPortOffsets(0f, 0f, 0f, 0f)
        mLineChart.setBackgroundColor(Color.parseColor("#00ffffff"))

        // no description text
        mLineChart.getDescription().setEnabled(true)

        // enable touch gestures
        mLineChart.setTouchEnabled(true)

        // enable scaling and dragging
        mLineChart.setDragEnabled(true)
        mLineChart.setScaleEnabled(true)
        mLineChart.setDrawGridBackground(false)
        mLineChart.setDrawMarkers(true)
        mLineChart.setMaxHighlightDistance(100f)
        mLineChart.setClickable(false)


        // if disabled, scaling can be done on x- and y-axis separately
        mLineChart.setPinchZoom(true)
        mLineChart.setDragXEnabled(true)
        mLineChart.setDragYEnabled(true)

        val x: XAxis = mLineChart.getXAxis()
        x.removeAllLimitLines()

        x.typeface = ResourcesCompat.getFont(this, R.font.notosanscjkkr_medium)
        x.textColor = Color.parseColor("#00484848")
        x.position = XAxis.XAxisPosition.BOTTOM_INSIDE
        x.setDrawGridLines(true)
        x.gridColor = Color.parseColor("#ff0d52")
        x.axisLineColor = Color.parseColor("#ff0d52")
        x.setLabelCount(60, true)
        val y: YAxis = mLineChart.getAxisLeft()
        y.removeAllLimitLines()
        y.typeface = ResourcesCompat.getFont(this, R.font.notosanscjkkr_medium)
        y.setLabelCount(7, true)
        y.textColor = Color.parseColor("#FFFFFF")
        y.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART)
        y.setDrawGridLines(true)
        y.gridColor = Color.parseColor("#ff0d52")
        y.axisLineColor = Color.BLACK
        y.setDrawZeroLine(false)
        y.axisMinimum = min.toFloat()
        y.axisMaximum = max.toFloat()
        mLineChart.getAxisRight().setEnabled(false)
        mLineChart.getLegend().setEnabled(false)
        mLineChart.animateXY(100, 100)
        mLineChart.description.text = ""

        mLineChart.invalidate()
        setData()
    }


    private fun setData() {
        val values = ArrayList<ArrayList<Entry>>()

        var iter = 0

        if (mStrFuncType == FUNCTION_TYPE_DOWNLOAD) {

            val valueLTE = ArrayList<Entry>()
            for (item in mArrLTE) {
                valueLTE.add(Entry(iter.toFloat(), (item.downSpeed / 1000).toFloat()))
                iter++
            }
            values.add(valueLTE)

            iter=0
            val value5G = ArrayList<Entry>()
            for (item in mArr5g) {
                value5G.add(Entry(iter.toFloat(), (item.downSpeed / 1000).toFloat()))
                iter++
            }
            values.add(value5G)

            iter=0
            val valueWifi5 = ArrayList<Entry>()
            for (item in mArrWifi5) {
                valueWifi5.add(Entry(iter.toFloat(), (item.downSpeed / 1000).toFloat()))
                iter++
            }
            values.add(valueWifi5)

            iter=0
            val valueWifi6 = ArrayList<Entry>()
            for (item in mArrWifi6) {
                valueWifi6.add(Entry(iter.toFloat(), (item.downSpeed / 1000).toFloat()))
                iter++
            }
            values.add(valueWifi6)

            iter=0
            val valueWifi6e = ArrayList<Entry>()
            for (item in mArrWifi6e) {
                valueWifi6e.add(Entry(iter.toFloat(), (item.downSpeed / 1000).toFloat()))
                iter++
            }
            values.add(valueWifi6e)


        } else if (mStrFuncType == FUNCTION_TYPE_UPLOAD) {
            val valueLTE = ArrayList<Entry>()
            for (item in mArrLTE) {
                valueLTE.add(Entry(iter.toFloat(), (item.upSpeed / 1000).toFloat()))
                iter++
            }
            values.add(valueLTE)

            iter=0
            val value5G = ArrayList<Entry>()
            for (item in mArr5g) {
                value5G.add(Entry(iter.toFloat(), (item.upSpeed / 1000).toFloat()))
                iter++
            }
            values.add(value5G)

            iter=0
            val valueWifi5 = ArrayList<Entry>()
            for (item in mArrWifi5) {
                valueWifi5.add(Entry(iter.toFloat(), (item.upSpeed / 1000).toFloat()))
                iter++
            }
            values.add(valueWifi5)

            iter=0
            val valueWifi6 = ArrayList<Entry>()
            for (item in mArrWifi6) {
                valueWifi6.add(Entry(iter.toFloat(), (item.upSpeed / 1000).toFloat()))
                iter++
            }
            values.add(valueWifi6)

            iter=0
            val valueWifi6e = ArrayList<Entry>()
            for (item in mArrWifi6e) {
                valueWifi6e.add(Entry(iter.toFloat(), (item.upSpeed / 1000).toFloat()))
                iter++
            }
            values.add(valueWifi6e)
        } else if (mStrFuncType == FUNCTION_TYPE_DELAY) {
            val valueLTE = ArrayList<Entry>()
            for (item in mArrLTE) {
                valueLTE.add(Entry(iter.toFloat(), item.delayTime.toFloat()))
                iter++
            }
            values.add(valueLTE)

            iter=0
            val value5G = ArrayList<Entry>()
            for (item in mArr5g) {
                value5G.add(Entry(iter.toFloat(), item.delayTime.toFloat()))
                iter++
            }
            values.add(value5G)

            iter=0
            val valueWifi5 = ArrayList<Entry>()
            for (item in mArrWifi5) {
                valueWifi5.add(Entry(iter.toFloat(), item.delayTime.toFloat()))
                iter++
            }
            values.add(valueWifi5)

            iter=0
            val valueWifi6 = ArrayList<Entry>()
            for (item in mArrWifi6) {
                valueWifi6.add(Entry(iter.toFloat(), item.delayTime.toFloat()))
                iter++
            }
            values.add(valueWifi6)

            iter=0
            val valueWifi6e = ArrayList<Entry>()
            for (item in mArrWifi6e) {
                valueWifi6e.add(Entry(iter.toFloat(), item.delayTime.toFloat()))
                iter++
            }
            values.add(valueWifi6e)
        } else if (mStrFuncType == FUNCTION_TYPE_JITTER) {
            val valueLTE = ArrayList<Entry>()
            for (item in mArrLTE) {
                var jitter = item.jitter
                if (jitter < 0) valueLTE.add(Entry(iter.toFloat(), (jitter * -1).toFloat()))
                else valueLTE.add(Entry(iter.toFloat(), jitter.toFloat()))
                iter++
            }
            values.add(valueLTE)

            iter=0
            val value5G = ArrayList<Entry>()
            for (item in mArr5g) {
                var jitter = item.jitter
                if (jitter < 0) value5G.add(Entry(iter.toFloat(), (jitter * -1).toFloat()))
                else value5G.add(Entry(iter.toFloat(), jitter.toFloat()))
                iter++
            }
            values.add(value5G)

            iter=0
            val valueWifi5 = ArrayList<Entry>()
            for (item in mArrWifi5) {
                var jitter = item.jitter
                if (jitter < 0) valueWifi5.add(Entry(iter.toFloat(), (jitter * -1).toFloat()))
                else valueWifi5.add(Entry(iter.toFloat(), jitter.toFloat()))
                iter++
            }
            values.add(valueWifi5)

            iter=0
            val valueWifi6 = ArrayList<Entry>()
            for (item in mArrWifi6) {
                var jitter = item.jitter
                if (jitter < 0) valueWifi6.add(Entry(iter.toFloat(), (jitter * -1).toFloat()))
                else valueWifi6.add(Entry(iter.toFloat(), jitter.toFloat()))
                iter++
            }
            values.add(valueWifi6)

            iter=0
            val valueWifi6e = ArrayList<Entry>()
            for (item in mArrWifi6e) {
                var jitter = item.jitter
                if (jitter < 0) valueWifi6e.add(Entry(iter.toFloat(), (jitter * -1).toFloat()))
                else valueWifi6e.add(Entry(iter.toFloat(), jitter.toFloat()))
                iter++
            }
            values.add(valueWifi6e)
        }

        val dataSets = ArrayList<ILineDataSet>()
        for (i in values.indices.reversed()) {
            val value = values[i]
            val set = LineDataSet(value, "" + i)

            set.mode = LineDataSet.Mode.CUBIC_BEZIER
            set.cubicIntensity = 0.2f
            set.setDrawFilled(true)
            set.setDrawCircles(true)
            set.lineWidth = 1.8f
            set.circleRadius = 4f

            set.setCircleColor(Color.parseColor("#0010deff"))
            set.circleHoleColor = Color.parseColor("#0010deff")

            set.fillColor = Color.parseColor("#000000")

            if(i == 0) {
                set.color = Color.parseColor("#ff8c34")
            } else if (i == 1) {
                set.color = Color.parseColor("#00ffb1")
            } else if (i == 2) {
                set.color = Color.parseColor("#0cc1e6")
            } else if (i  == 3) {
                set.color = Color.parseColor("#6910de")
            } else if (i == 4) {
                set.color = Color.parseColor("#ff6b6b")
            } else {
                set.color = Color.parseColor("#000000")
            }

            set.fillAlpha = 40
            set.setDrawHorizontalHighlightIndicator(false)
            set.setDrawVerticalHighlightIndicator(false)

            set.values = value
            dataSets.add(set)
        }
        val data = LineData(dataSets)
        data.setDrawValues(false)
        data.setValueTypeface(ResourcesCompat.getFont(this, R.font.notosanscjkkr_medium))
        data.setValueTextSize(9f)
        data.setDrawValues(false)

        // set data
        mLineChart.setData(data)
//        mChartCowDetailRealTimeActivitySituationGraph.highlightValue(15.0f, 0);
    }
}