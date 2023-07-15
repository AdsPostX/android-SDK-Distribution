package com.adspostx.adspostxdemo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView


enum class PresentationStyle {
    POPUP, FULLSCREEN
}

class MainActivity : AppCompatActivity() {
    private var bottomNavigationMenu: BottomNavigationView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.adspostx.adspostxdemo.R.layout.activity_main)

        bottomNavigationMenu = findViewById(R.id.bottomNavigationMenu)

        title = "AdsPostX Native Android"

        val standardUIFragment = StandardUIFragment()
        val nativeUIFragment = NativeUIFragment()
        makeCurrentFragment(standardUIFragment)

        bottomNavigationMenu?.setOnItemSelectedListener { menuItem ->
            val fragment = when (menuItem.itemId) {
                R.id.page_1 -> standardUIFragment
                R.id.page_2 -> nativeUIFragment
                else -> null
            }
            fragment?.let { makeCurrentFragment(it) }
            true
        }
    }


    fun makeCurrentFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.ContainerView, fragment)
            commit()
        }
    }


}

