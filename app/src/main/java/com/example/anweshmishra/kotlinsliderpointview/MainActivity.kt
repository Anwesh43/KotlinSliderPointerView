package com.example.anweshmishra.kotlinsliderpointview

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.example.sliderpointview.SliderPointView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SliderPointView.create(this)
    }
}
