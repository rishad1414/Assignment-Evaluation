package com.example.assignmentevaluation.Adapter

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.assignmentevaluation.Fragment.TAssignmentFragment
import com.example.assignmentevaluation.Fragment.TSubmitAssignmentFragment



class TCoursePagerAdapter(
    fragmentActivity: FragmentActivity
) : FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        val fragment = when (position) {
            0 -> TAssignmentFragment()
            1 -> TSubmitAssignmentFragment()
            else -> throw IllegalStateException("Invalid position: $position")
        }

        return fragment
    }
}

