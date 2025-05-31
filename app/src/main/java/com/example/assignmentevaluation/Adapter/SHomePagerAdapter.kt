package com.example.assignmentevaluation.Adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.assignmentevaluation.Fragment.SAssignmentFragment
import com.example.assignmentevaluation.Fragment.SCourseFragment
import com.example.assignmentevaluation.Fragment.SSubmittedAssignmentFragment
import com.example.assignmentevaluation.Fragment.TAssignmentFragment
import com.example.assignmentevaluation.Fragment.TSubmitAssignmentFragment


class SHomePagerAdapter(
    fragmentActivity: FragmentActivity
) : FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
        val fragment = when (position) {
            0 -> SCourseFragment()
            1 -> SAssignmentFragment()
            2 -> SSubmittedAssignmentFragment()
            else -> throw IllegalStateException("Invalid position: $position")
        }

        return fragment
    }
}

