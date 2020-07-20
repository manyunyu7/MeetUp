package com.senjapagi.meetup

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONArrayRequestListener
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.senjapagi.meetup.Beautifier.DialogBuilder
import com.senjapagi.meetup.Preference.URL
import com.senjapagi.shsd.Preference.Preference
import com.senjapagi.shsd.Preference.prefConstant
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject

class MainActivity : AppCompatActivity() {
    lateinit var sPreference: Preference
    lateinit var dialogBuilder: DialogBuilder
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        sPreference = Preference(this)
        dialogBuilder = DialogBuilder(this, window.decorView)

        prefCheck()
        btnLogin.setOnClickListener {
            checkLogin()
        }

        btnLoginAdmin.setOnClickListener {
            startActivity(Intent(this@MainActivity,admin_website::class.java))
            finish()
        }

    }

    private fun prefCheck() {
        val log = sPreference.getPrefBool(prefConstant.IS_LOGIN)
        if (log!!) {
            val level = sPreference.getPrefString(prefConstant.LEVEL)
            if (level.equals("user")) {
                if (level.equals("user")) {
                    val intent = Intent(this@MainActivity, dashboard_client::class.java)
                        .apply {
                        }
                    startActivity(intent)
                    finish()
                }
                if (level.equals("admin")) {
                    val intent = Intent(this@MainActivity, admin_website::class.java)
                        .apply {
                        }
                    startActivity(intent)
                    finish()
                }
            }
        } else {
            //DO NOTHING
        }
    }

    private fun checkLogin() {
        if (et_username.text.toString().isEmpty()) {
            et_username.error = "Please complete this form first"
        } else if (et_password.text.toString().isEmpty()) {
            et_password.error = "Please complete this form first"
        } else {
            login(et_username.text.toString(), et_password.text.toString())
        }
    }

    private fun login(username: String, password: String) {
        dialogBuilder.progressType(
            "Loading", "Loading", "Checking Your Credential"
        )
        AndroidNetworking.post(URL.LOGIN)
            .addBodyParameter("email", username)
            .addBodyParameter("password", password)
            .setPriority(Priority.HIGH)
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener {
                override fun onResponse(response: JSONObject?) {
//                    TODO("already implemented")
                    val status = response?.getBoolean("sukses")
                    if (status!!) {
                        dialogBuilder.destroyAll()
                        val data = response.getJSONObject("data")
                        val level = data.getString("level")
                        val nama = data.getString("nama")
                        val id = data.getString("user_id")
                        val token = data.getString("token")
                        makeToast(level!!)
                        sPreference.save(prefConstant.IS_LOGIN, true)
                        sPreference.save(prefConstant.LEVEL, prefConstant.USER)
                        sPreference.save(prefConstant.TOKEN, token!!)
                        sPreference.save(prefConstant.USER_ID, id!!)
                        sPreference.save(prefConstant.USERNAME, username)
                        sPreference.save(prefConstant.NAME, nama)

                        if (level.equals("user")) {
                            val intent = Intent(
                                this@MainActivity,
                                dashboard_client::class.java
                            )
                            makeToast("Login Success")
                            startActivity(intent)
                            finish()
                        } else if (level == ("admin")) {
                            dialogBuilder.destroyAll()
                            dialogBuilder.neutralWarning(
                                "Error", "Username or Password not found", "Try Again"
                            )
                        }

                    } else {
                        dialogBuilder.destroyAll()
                        dialogBuilder.neutralWarning(
                            "Error", "Username or Password not found", "Try Again"
                        )
                    }

                }

                override fun onError(anError: ANError?) {
                    //                   TODO("Not yet implemented")
                    makeToast(anError?.localizedMessage.toString())
                }

            })
    }

    fun makeToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}