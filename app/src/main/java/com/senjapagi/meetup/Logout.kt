package com.senjapagi.meetup

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Handler
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import cn.pedant.SweetAlert.SweetAlertDialog
import com.senjapagi.shsd.Preference.Preference

class Logout : AppCompatActivity {
    lateinit var mContext: Context
    lateinit var pDialog: SweetAlertDialog

    constructor(context: Context) {
        this.mContext = context
    }

    fun logoutDialog() {
        pDialog = SweetAlertDialog(mContext, SweetAlertDialog.WARNING_TYPE)
        pDialog.titleText = "Do you want to end your session ?"
        pDialog.setCancelable(true)
        pDialog.confirmText = "Yes"
        pDialog.setConfirmClickListener {
            pDialog.dismissWithAnimation()
            val pref = Preference(mContext)
            pref.clearPreferences()
            Handler().postDelayed({
                val intent = Intent(mContext, MainActivity::class.java)
                mContext.startActivity(intent)
            }, 700)

        }
        pDialog.showCancelButton(true)
        pDialog.cancelText = "Cancel"
        pDialog.setCancelClickListener {
            pDialog.dismissWithAnimation()
        }
        pDialog.show()
    }

    fun logoutDialogAdminWeb() {
        pDialog = SweetAlertDialog(mContext, SweetAlertDialog.WARNING_TYPE)
        pDialog.titleText = "Do you want to end your session ?"
        pDialog.setCancelable(true)
        pDialog.confirmText = "Yes"
        pDialog.setConfirmClickListener {
            pDialog.dismissWithAnimation()
            val pref = Preference(mContext)
            pref.clearPreferences()
            Handler().postDelayed({
                val intent = Intent(mContext, admin_website::class.java)
                mContext.startActivity(intent)
            }, 700)

        }
        pDialog.showCancelButton(true)
        pDialog.cancelText = "Cancel"
        pDialog.setCancelClickListener {
            pDialog.dismissWithAnimation()
        }
        pDialog.show()
    }

}