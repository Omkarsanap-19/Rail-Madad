package com.example.railmadad.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.railmadad.Admin.fragments.departmentPage

class pagerAdapter(fragmentManager: FragmentManager,lifecycle: Lifecycle, val listDepart : List<String>): FragmentStateAdapter(fragmentManager,lifecycle) {
    override fun createFragment(position: Int): Fragment {
        val category = listDepart[position]
        return departmentPage.newInstance(category)
    }

    override fun getItemCount(): Int {
        return listDepart.size
    }

}