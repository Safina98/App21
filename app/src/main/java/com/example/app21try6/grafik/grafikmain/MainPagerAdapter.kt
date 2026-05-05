package com.example.app21try6.grafik.grafikmain

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.app21try6.grafik.grafikproduct.GraphicFragment
import com.example.app21try6.grafik.grafikcustomer.GraphicCustomerFragment
import com.example.app21try6.grafik.grafikprofit.GrapichProfitFragment
import com.example.app21try6.grafik.graphictrend.GraphicProductTrendFragment

class MainPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int {
        return 4 // Number of tabs
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> GraphicFragment()
            1-> GraphicProductTrendFragment()
            2 -> GrapichProfitFragment()
            3 -> GraphicCustomerFragment()
            else -> GraphicFragment()
        }
    }
}
