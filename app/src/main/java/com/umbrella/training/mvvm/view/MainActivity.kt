package com.umbrella.training.mvvm.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import com.ble.pos.sdk.blereader.BLEReader
import com.ble.pos.sdk.blereader.IBLEReader_Callback
import com.ble.pos.sdk.blereader.WDBluetoothDevice
import com.umbrella.training.mvvm.R

class MainActivity : AppCompatActivity() {

    var goneTestContainer: LinearLayout? = null
    var goneTestButton: Button? = null
    private var goneTestText: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        goneTestContainer = findViewById(R.id.gone_test_container)
        goneTestButton = findViewById(R.id.gone_test_button)
        goneTestText = findViewById(R.id.gone_test_text)


        findViewById<Button>(R.id.gone_test_parent_gone).setOnClickListener {

        }
        findViewById<Button>(R.id.gone_test_child_visible).setOnClickListener {

        }

        setBLEReaderCallback()


    }

    /**
     * 设置蓝牙读卡器回调
     */
    private fun setBLEReaderCallback() {
        BLEReader.getInstance().set_callback(object : IBLEReader_Callback{
            override fun onLeScan(p0: MutableList<WDBluetoothDevice>?) {
            }

            override fun onConnectGatt(p0: Int, p1: Any?) {
            }

            override fun onServicesDiscovered(p0: Int, p1: Any?) {
            }

            override fun onCharacteristicChanged(p0: Int, p1: Any?) {
            }

            override fun onReadRemoteRssi(p0: Int) {
            }

            override fun onOTA(p0: Int, p1: Any?) {
            }

            override fun onChangeBLEParameter(): Int {
                return 0
            }
        })
    }

}
