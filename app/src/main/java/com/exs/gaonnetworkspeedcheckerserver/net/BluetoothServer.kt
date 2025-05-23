package com.example.bluetoothserver.net

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothServerSocket
import android.bluetooth.BluetoothSocket
import android.os.SystemClock
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.io.OutputStream

class BluetoothServer {

    private var btAdapter = BluetoothAdapter.getDefaultAdapter()
    private var acceptThread: AcceptThread? = null
    private var commThread: CommThread? = null
    private var inputStream: InputStream? = null
    private var outputStream: OutputStream? = null
    private var socketListener: SocketListener? = null

    fun setOnSocketListener(listener: SocketListener?) {
        socketListener = listener
    }

    fun onConnect() {
        socketListener?.onConnect()
    }

    fun onDisconnect() {
        socketListener?.onDisconnect()
    }

    fun onLogPrint(message: String?) {
        socketListener?.onLogPrint(message)
    }

    fun onError(e: Exception) {
        socketListener?.onError(e)
    }

    fun onReceive(msg: String) {
        println(msg)
        socketListener?.onReceive(msg)
    }

    fun onSend(msg: String) {
        println(msg)
        socketListener?.onSend(msg)
    }

    fun accept() {
        stop()

        onLogPrint("Waiting for accept the client..")
        acceptThread = AcceptThread()
        acceptThread?.start()
    }

    fun stop() {
        if (acceptThread == null) return

        try {
            acceptThread?.let {
                onLogPrint("Stop accepting")

                it.stopThread()
                it.join(1000)
                it.interrupt()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    inner class AcceptThread : Thread() {
        private var acceptSockets: MutableList<BluetoothServerSocket> = mutableListOf()
        private var sockets: MutableList<BluetoothSocket> = mutableListOf()

        override fun run() {
            while (true) {
                var socket = try {
                    println("Try Accept Start")
                    var acceptSocket = acceptSockets.last()
                    println("Try Accept : ${acceptSocket.toString()}")
                    acceptSocket?.accept()
                } catch (e: Exception) {
                    println("Error Accept")
                    e.printStackTrace()
                    break
                }

                if (socket != null) {
                    var acceptSocket = btAdapter.listenUsingRfcommWithServiceRecord(
                        "bluetoothTest${SystemClock.currentThreadTimeMillis()}",
                        BTConstant.BLUETOOTH_UUID_INSECURE)

                    acceptSockets.add(acceptSocket)

                    onConnect()

                    println("Connection : ${socket.remoteDevice.name}")


                    commThread = CommThread(socket)
                    commThread?.start()

                    sockets.add(socket)



                    accept()

                    break
                }
            }
        }

        fun stopThread() {
            try {

                for (acceptSocket in acceptSockets) {
                    acceptSocket.close()
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        init {
            try {

                var acceptSocket = btAdapter.listenUsingRfcommWithServiceRecord(
                    "bluetoothTest${SystemClock.currentThreadTimeMillis()}",
                    BTConstant.BLUETOOTH_UUID_INSECURE)

                acceptSockets.add(acceptSocket)

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    internal inner class CommThread(private val socket: BluetoothSocket?): Thread() {

        override fun run() {
            try {
                outputStream = socket?.outputStream
                inputStream = socket?.inputStream
            } catch (e: Exception) {
                e.printStackTrace()
            }

            var len: Int
            val buffer = ByteArray(1024)
            val byteArrayOutputStream = ByteArrayOutputStream()

            while (true) {
                try {
                    len = socket?.inputStream?.read(buffer)!!
                    val data = buffer.copyOf(len)
                    byteArrayOutputStream.write(data)

                    socket.inputStream?.available()?.let { available ->

                        if (available == 0) {
                            val dataByteArray = byteArrayOutputStream.toByteArray()
                            val dataString = String(dataByteArray)
                            onReceive(dataString)

                            byteArrayOutputStream.reset()
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    stopThread()
                    accept()
                    break
                }
            }
        }

        fun stopThread() {
            try {
                inputStream?.close()
                outputStream?.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun sendData(msg: String) {
        if (outputStream == null) return

        try {
            outputStream?.let {
                onSend(msg)

                it.write(msg.toByteArray())
                it.flush()
            }
        } catch (e: Exception) {
            onError(e)
            e.printStackTrace()
            stop()
        }
    }
}