package com.example.app21try6.bookkeeping.grafik

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.app21try6.database.ProductDao
import com.example.app21try6.database.SummaryDbDao

class GrafikViewModel(val database: SummaryDbDao,
                      val database2: ProductDao,
                      application: Application,
                      val date:Array<String>): AndroidViewModel(application)  {

}