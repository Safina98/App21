package com.example.app21try6.transaction.transactiondetail

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.util.Log
import java.io.OutputStream
import java.io.IOException
import java.util.*

class BluetoothPrinterService(private val device: BluetoothDevice) {
    private var socket: BluetoothSocket? = null
    private var outputStream: OutputStream? = null

    @SuppressLint("MissingPermission")
    fun connect() {
        try {
            Log.i(tdTag,"BluetoothPrinterService connect")
            socket = device.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"))
            socket?.connect()
            outputStream = socket?.outputStream

        } catch (e: IOException) {
            Log.i(tdTag,"BluetoothPrinterService connect failed")
            e.printStackTrace()
            Log.i(tdTag,"${e}")

        }
    }
//@SuppressLint("MissingPermission")
//fun connect() {
//    try {
//        socket = try {
//            device.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"))
//        } catch (e: Exception) {
//            null
//        }
//        socket?.connect()
//        outputStream = socket?.outputStream
//    } catch (e: IOException) {
//        Log.e(tdTag, "Standard connect failed, trying fallback", e)
//        try {
//            // Fallback: bypass SDP, open channel 1 directly via reflection
//            val method = device.javaClass.getMethod("createRfcommSocket", Int::class.javaPrimitiveType)
//            socket = method.invoke(device, 1) as BluetoothSocket
//            socket?.connect()
//            outputStream = socket?.outputStream
//        } catch (e2: Exception) {
//            Log.e(tdTag, "Fallback connect also failed", e2)
//        }
//    }
//}

    fun disconnect() {
        try {
            outputStream?.close()
            socket?.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun print(data: ByteArray) {
        try {
            Log.i(tdTag,"BluetoothPrinterService print")
            outputStream?.write(data)
            outputStream?.flush()
        } catch (e: IOException) {
            Log.i(tdTag,"BluetoothPrinterService print failed")
            e.printStackTrace()
            Log.i(tdTag,"BluetoothPrinterService ${e}")
        }
    }
}
