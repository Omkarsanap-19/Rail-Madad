package com.example.railmadad.Admin.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.viewpager2.widget.ViewPager2
import com.example.railmadad.R
import com.example.railmadad.adapter.pagerAdapter
import com.example.railmadad.viewmodels.authViewModel
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlin.getValue


class departmentFragment : Fragment() {

    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout
    val departments = listOf(
        "Operating Department",
        "Mechanical (C&W) Department",
        "IT & Electrical Department",
        "Catering Department",
        "Traffic Control Department",
        "Commercial Department",
        "Railway Protection Force (RPF)"
    )
    private val viewModel: authViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_department, container, false)

        tabLayout = view.findViewById<TabLayout>(R.id.tablayout)
        viewPager = view.findViewById<ViewPager2>(R.id.viewPager)
        val pagerAdapter =
            pagerAdapter(childFragmentManager, viewLifecycleOwner.lifecycle, departments)
        viewPager.adapter = pagerAdapter

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = departments[position] // Default tab text; no custom view yet
        }.attach()

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                val custom = LayoutInflater.from(context).inflate(R.layout.custom_tab, null)
                val textView = custom.findViewById<TextView>(R.id.tab_text)
                textView.text = tab.text
                tab.customView = custom
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {
                tab.customView = null // Revert to default styling
            }

            override fun onTabReselected(tab: TabLayout.Tab) {
                // optional
            }
        })

// Manually trigger first tab to be custom-selected
        tabLayout.getTabAt(tabLayout.selectedTabPosition)?.let {
            it.customView = LayoutInflater.from(context).inflate(R.layout.custom_tab, null).apply {
                findViewById<TextView>(R.id.tab_text).text = it.text
            }
        }


            return view
        }


}