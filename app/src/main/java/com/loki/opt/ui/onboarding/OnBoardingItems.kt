package com.loki.opt.ui.onboarding

import com.loki.opt.R

data class OnBoardingItems(
    val image: Int,
    val title: Int,
    val description: Int
) {

    companion object {
        fun getData(): List<OnBoardingItems> {
            return listOf(
                OnBoardingItems(
                    image = R.drawable.late_night,
                    title = R.string.bedtime_companion,
                    description = R.string.description_1
                ),
                OnBoardingItems(
                    image = R.drawable.lock,
                    title = R.string.lock_your_screen,
                    description = R.string.description_2
                ),
                OnBoardingItems(
                    image = R.drawable.time,
                    title = R.string.schedule_time_locks,
                    description = R.string.description_3
                ),
            )
        }
    }
}
