package com.example.app21try6.database.tables

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey


@Entity(
    tableName = "detail_warna_table",
    foreignKeys = [
        ForeignKey(
            entity = SubProduct::class,
            parentColumns = ["sub_id"],
            childColumns = ["subId"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["ref"], unique = true)] // Add this index
)
data class DetailWarnaTable(
    @PrimaryKey(autoGenerate = true)
    var id:Int=0,
    @ColumnInfo(name="subId")
    var subId:Int=0,
    @ColumnInfo(name="batchCount")
    var batchCount:Double=0.0,
    @ColumnInfo(name="net")
    var net:Double=0.0,
    @ColumnInfo(name="ket")
    var ket:String="",
    @ColumnInfo(name="ref")
    var ref: String=""
)

/*
add bool to track
when tracked:
    stock minus 1
    log barang keluar
    add into merchandice retail-> detail ref, detail isi, batchount 1

kapan mulai tracking?
   pas buka baru, setiap transaksi detail warna, di potong barang?=>dipotong
bagaimana kalau bon?
    merchandise retail bisa dikurangi manual.
perlu log untuk merchandise retail?
    modif log supaya bisa untuk retail dan roll=> TIDAK, terlalu banyak log, buat frgmnet untuk track detail warna saja
ada barang roll dan barang pcs, bagaimana merchandise retailnya
    ???
apakah tracking manual atau otomatis?

    get merchandises by sub id
        if merchandise is more than one:
            if transactionDetail qty more than five
                pop up apakah ada sambungan di kain atau tidak
                kalau sambung, pilih yang mana yang di sambung
                lalu pilih retail selanjutnya
                lalu di retail selanjutnya dikurangi 20 cm

net di retail bisa ditambah dan di kurangi manual, bisa juga tambah retail manual (tidak dikurangi dari detail warna)



buat page untuk trancking TransDetail, untuk tahu kapan barang keluar

 */