package com.example.app21try6.grafik.grafikmain

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.example.app21try6.R
import com.example.app21try6.databinding.FragmentGraphicMainBinding
import com.example.app21try6.grafik.GraphicViewModel
import com.google.android.material.tabs.TabLayoutMediator


class GraphicMainFragment : Fragment() {
    private val viewModel: GraphicViewModel by  activityViewModels { GraphicViewModel.Factory }
    private lateinit var binding: FragmentGraphicMainBinding
    private val tabStateViewModel: TabStateViewModel by activityViewModels { TabStateViewModel.Factory() }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentGraphicMainBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        val pagerAdapter = MainPagerAdapter(this)
        binding.viewPager.adapter = pagerAdapter

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Stok"
                1 -> "Product Trend"
                2 -> "Omzet"
                3 -> "Profit"
                else -> "Customer"
            }
        }.attach()
        binding.viewPager.registerOnPageChangeCallback(object : androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                // Notify the ViewModel or a shared mechanism of which tab is active
                tabStateViewModel.setActiveGraphTab(position)
            }
        })
        return binding.root
    }
}