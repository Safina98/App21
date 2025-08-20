package com.example.app21try6

import java.text.SimpleDateFormat
import java.util.Locale

object Constants {

    // Units
    val UNITS: List<String> = listOf("-", "LSN", "BKS", "ROLL", "DOS")

    object BARANGLOGKET {
        const val masuk = "MASUK"
        const val keluar = "KELUAR"
    }

    // Date formats
    const val DETAILED_DATE_FORMAT = "dd-MM-yyyy HH:mm"
    val DETAILED_DATE_FORMATTER = SimpleDateFormat(DETAILED_DATE_FORMAT, Locale.getDefault())

    const val SIMPLE_DATE_FORMAT = "dd-MM-yyyy"
    val SIMPLE_DATE_FORMATTER = SimpleDateFormat(SIMPLE_DATE_FORMAT, Locale.getDefault())

    object ITEMUNIT {
        const val NONE = "NONE"
        const val LSN = "LSN"
        const val ROLL = "ROLL"
        const val BKS = "BKS"
        const val DOS = "DOS"
        const val KARUNG = "KARUNG"
    }

    object DISCTYPE {
        const val CASHBACK_PRINTED = "Printed"
        const val CASHBACK_NOT_PRINTED = "Not Printed"
        const val UBAH_HARGA = "Ubah Harga"
    }

    object MODELTYPE {
        const val BRAND = "Brand"
        const val PRODUCT = "Product"
        const val SUB_PRODUCT = "Sub Product"
    }

    enum class Code(val text: String) {
        ZERO(""),
        LONGPLUS("Tambah"),
        LONGSUBS("Kurang"),
        TEXTITEM("Update Nama Barang"),
        TEXTPRICE("Update Harga barang"),
        UNITQTY("ISI"),
        DUPLICATE("ISI")
    }
}
