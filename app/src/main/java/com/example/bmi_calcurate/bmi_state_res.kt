package com.example.bmi_calcurate

import androidx.compose.ui.graphics.Color

class bmi_state_res (bmi : Double) {
    var bmi = bmi

    fun bmi_cal() : String{
        val bmi_cal = when{
            bmi >= 35 -> "고도 비만"
            bmi >= 30 -> "비만"
            bmi >= 25 -> "과체중"
            bmi >= 20 -> "정상"
            else -> "저체중"
        }

        return bmi_cal
    }

    fun bmi_state_color() : List<Color>{
        val bmi_state_color = when{
            bmi >= 35 -> listOf(Color.Black , Color(0xFF464545))
            bmi >= 30 -> listOf(Color.Red , Color(0xFFF76262))
            bmi >= 25 -> listOf(Color(0xFFFF5722), Color(0xFFF76638))
            bmi >= 20 -> listOf(Color.Blue , Color(0xFF7E8EEB))
            else -> listOf(Color.Green , Color(0xFF98C069))
        }

        return bmi_state_color
    }

    fun imageRes() : Int {
        val imageRes = when{
            bmi >= 35 -> R.drawable.ic_superhigh_weight
            bmi >= 30 -> R.drawable.ic_high_overweight
            bmi >= 25 -> R.drawable.ic_overweight
            bmi >= 20 -> R.drawable.ic_normal
            else -> R.drawable.ic_row_weight
        }

        return imageRes
    }
}