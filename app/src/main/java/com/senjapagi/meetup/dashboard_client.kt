package com.senjapagi.meetup

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_dasboard_client.*
import kotlinx.android.synthetic.main.user_app_bar.*

class dashboard_client : AppCompatActivity() , NavigationView.OnNavigationItemSelectedListener {

    lateinit var fragmentUserDashboard: user_dashboard
    lateinit var fragmentCatalog: room_catalog
    lateinit var fragmentBookHistory: user_book_history
    lateinit var fragmentBookRoom: book_room

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dasboard_client)
        setSupportActionBar(userToolbar)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            window?.setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        }
        val actionBar = supportActionBar
//        var organizationName = "Anggota HMSI"
//        actionBar?.title = "Anggota HMSI"
//        titleAppbar.text = organizationName
        toggle_drawer.setOnClickListener() {
            drawerController()
        }

        userNavView.setNavigationItemSelectedListener(this)

        val drawerToggle: ActionBarDrawerToggle = object : ActionBarDrawerToggle(
            this,
            userDrawerLayout,
            userToolbar,
            (R.string.open),
            (R.string.close)
        ) {
        }

        //all fragment init and defining starting fragment
        fragmentUserDashboard = user_dashboard()
        fragmentCatalog = room_catalog()
        fragmentBookHistory = user_book_history()
        fragmentBookRoom = book_room()

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.userFrameLayout, fragmentUserDashboard)
            .commit()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        drawerController()
    }

    //the when expression for nav draw menu listener
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.home -> {
                transactionFragment(fragmentUserDashboard)
            }
            R.id.catalog -> {
                transactionFragment(fragmentCatalog)
            }
            R.id.book -> {
                transactionFragment(fragmentBookRoom)
            }
            R.id.history -> {
                transactionFragment(fragmentBookHistory)
            }

            R.id.logout -> {
                val logout = Logout(this)
                logout.logoutDialog()
            }
            else -> {
                makeToast("This Menu is Not Supported Yet")
            }
        }
        userDrawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    fun drawerController() {
        // If the navigation drawer is not open then open it, if its already open then close it.
        // Kalau drawer layout dibuka akan ditutup , vice versa
        if (userDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            userDrawerLayout.closeDrawer(GravityCompat.START)
        } else
            userDrawerLayout.openDrawer(GravityCompat.START)
    }

    fun openCloseNavigationDrawer(view: View) {
        if (userDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            userDrawerLayout.closeDrawer(GravityCompat.START)
        } else
            userDrawerLayout.openDrawer(GravityCompat.START)
    }

    private fun transactionFragment(Fragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.userFrameLayout, Fragment)
            .commit()
    }

    fun makeToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}

