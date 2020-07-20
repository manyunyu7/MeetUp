package com.senjapagi.meetup

import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import cn.pedant.SweetAlert.SweetAlertDialog
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.google.android.material.navigation.NavigationView
import com.senjapagi.meetup.Preference.URL
import com.senjapagi.shsd.Preference.Preference
import com.senjapagi.shsd.Preference.prefConstant
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_dasboard_admin.*
import kotlinx.android.synthetic.main.activity_dasboard_client.*
import kotlinx.android.synthetic.main.activity_dasboard_client.userDrawerLayout
import kotlinx.android.synthetic.main.user_app_bar.*
import kotlinx.android.synthetic.main.user_header.*
import org.json.JSONObject

class dashboard_admin : AppCompatActivity() , NavigationView.OnNavigationItemSelectedListener {

    lateinit var fragmentUserDashboard: user_dashboard
    lateinit var fragmentCatalog: room_catalog
    lateinit var fragmentBookHistory: user_book_history
    lateinit var fragmentBookRoom: book_room
    lateinit var fragmentCurrentBook: user_active_booking
    lateinit var fragmentProfile : user_profile
    lateinit var pref : Preference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dasboard_client)
        setSupportActionBar(userToolbar)
        pref = Preference(applicationContext)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            val w = window
            w.setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
            )
        }


        val c = Handler()
        c.postDelayed({
            nav_header_anggota_nama.text = pref.getPrefString(prefConstant.NAME)
            nav_header_anggota_email.text = pref.getPrefString(prefConstant.USERNAME)
            getBaseProfile()
        },3000)

        val actionBar = supportActionBar
//        var organizationName = "Anggota HMSI"
//        actionBar?.title = "Anggota HMSI"
//        titleAppbar.text = organizationName
        toggle_drawer.setOnClickListener() {
            drawerController()
        }



        adminNavView.setCheckedItem(R.id.catalog);
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
        fragmentCurrentBook = user_active_booking()
        fragmentProfile = user_profile()


            supportFragmentManager
                .beginTransaction()
                .replace(R.id.userFrameLayout, fragmentCatalog)
                .commit()



        adminNavView.setNavigationItemSelectedListener(this)
    }

    override fun onBackPressed() {
        drawerController()
        val logout = Logout(this)
        logout.logoutDialog()
    }

    //the when expression for nav draw menu listener
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
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

    fun getBaseProfile(){
        AndroidNetworking.get(URL.USER_DATA)
            .addHeaders("user_id",pref.getPrefString(prefConstant.USER_ID))
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener{
                override fun onResponse(response: JSONObject?) {
                    if(response?.getBoolean("sukses")!!){

                        val photo = response.getJSONObject("data").getString("photo")
                        Picasso.get()
                            .load(URL.USER_THUMBNAIL+photo)
                            .memoryPolicy(MemoryPolicy.NO_CACHE)
                            .error(R.drawable.profile)
                            .networkPolicy(NetworkPolicy.NO_CACHE)
                            .into(header_user)

                    }else{
                        makeToast("Unexpected Response from Server")
                    }
                }

                override fun onError(anError: ANError?) {
                    makeToast("Error Response From Server , Please try again later")
                }

            })
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

