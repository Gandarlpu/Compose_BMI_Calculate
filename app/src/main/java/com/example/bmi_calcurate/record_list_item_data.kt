package com.example.bmi_calcurate

data class record_list_item_data(
    val bmi : Double,
    val time : String
){
    init {
        this.bmi
        this.time
    }
}
