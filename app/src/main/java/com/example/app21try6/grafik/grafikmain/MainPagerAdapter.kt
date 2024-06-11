package com.example.app21try6.grafik.grafikmain

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.app21try6.grafik.GraphicFragment
import com.example.app21try6.grafik.grafikprofit.GrapichProfitFragment

class MainPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int {
        return 2 // Number of tabs
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> GraphicFragment()
            1 -> GrapichProfitFragment()
            else -> GraphicFragment()
        }
    }
}
