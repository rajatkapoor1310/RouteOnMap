package com.example.routeview

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.navigationonmap.R

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        replaceFragment(
            R.id.fragment_container,
            MapFragment.newInstance()
        )
    }

    fun replaceFragment(id: Int, fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(id, fragment, fragment.javaClass.simpleName)
        transaction.commitAllowingStateLoss()
    }
}