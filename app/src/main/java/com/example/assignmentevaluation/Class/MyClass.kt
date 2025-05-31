package com.example.assinment_evaluation.Class

class MyClass {


    private val dates = mutableListOf<String>()
    private val classes = mutableListOf<String>()


    fun getClassAndDate(index: Int): Pair<String, String> {

        for (i in 1..42) {
            dates.add("date$i")
            classes.add("class$i")
        }

        if (index < 1 || index > dates.size) {
            throw IllegalArgumentException("Index out of range. Must be between 1 and 42.")
        }

        return Pair(classes[index - 1], dates[index - 1])
    }


    public fun getLT(batch: String): String{
        if(batch=="12")
            return "4-II"
        else if(batch=="13")
            return "4-I"
        else if(batch=="14")
            return "4-I"
        else if(batch=="15")
            return "3-II"
        else if(batch=="16")
            return "3-I"
        else if(batch=="17")
            return "2-II"
        else if(batch=="18")
            return "2-I"
        else if(batch=="19")
            return "1-II"
        else if(batch=="20")
            return "1-I"
        else
            return "3-I"
    }
}