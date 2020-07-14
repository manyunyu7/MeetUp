package com.senjapagi.meetup

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.senjapagi.meetup.Preference.URL
import com.senjapagi.shsd.Preference.Preference
import com.senjapagi.shsd.Preference.prefConstant
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_user_profile.*
import kotlinx.android.synthetic.main.layout_info_peserta.*
import kotlinx.android.synthetic.main.layout_loading_transparent.*
import kotlinx.android.synthetic.main.user_header.*
import org.json.JSONObject

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


/**
 * A simple [Fragment] subclass.
 * Use the [user_fragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class user_profile : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    lateinit var pref: Preference


    var oldName = ""
    var oldEmail = ""
    var oldPassword = ""
    var oldPhoto = ""

    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()
    }

    override fun onStop() {
        super.onStop()
        (activity as AppCompatActivity?)!!.supportActionBar!!.show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        pref = Preference(activity?.applicationContext!!)


        val userID = pref.getPrefString(prefConstant.USER_ID)

        userName.setText(pref.getPrefString(prefConstant.NAME))
        userEmail.setText(pref.getPrefString(prefConstant.USERNAME))

        getBaseProfile()

        btnUpdate.setOnClickListener {
            updateProfile()
        }


    }

    fun updateProfile() {
        anim_loading.visibility = View.VISIBLE
        var newName = "";
        var newEmail = "";
        var newPassword = ""
        val pref = Preference(context!!)

        newName = etName.text.toString()
        newEmail = etEmail.text.toString()
        newPassword = etNewPassword.text.toString()
        oldPassword = etOldPassword.text.toString()



        AndroidNetworking.post((URL.USER_DATA))
            .addBodyParameter("name", newName)
            .addBodyParameter("email", newEmail)
            .addBodyParameter("oldpassword", oldPassword)
            .addBodyParameter("password", newPassword)
            .addBodyParameter("repassword", newPassword)
            .addHeaders("user_id", pref.getPrefString(prefConstant.USER_ID))
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener {
                override fun onResponse(response: JSONObject?) {
                    anim_loading.visibility = View.GONE
                    val status = response?.getBoolean("sukses")
                    if (status!!) {
                        getBaseProfile()
                        makeToast("Success Updating your account")
                        etNewPassword.setText("")
                        etOldPassword.setText("")
                        pref.save(prefConstant.PASSWORD,oldPassword)
                        pref.save(prefConstant.NAME,newName)
                    } else {
                        makeToast("Sorry , your Old Password is wrong !")
                        etNewPassword.setText("")
                        etOldPassword.setText("")
                    }


                }

                override fun onError(anError: ANError?) {
                    anim_loading.visibility = View.GONE
                    makeToast("Connection to server failed, please try again later")
                    etNewPassword.setText("")
                    etOldPassword.setText("")
                }

            })
    }

    fun getBaseProfile() {
        anim_loading.visibility = View.VISIBLE
        AndroidNetworking.get(URL.USER_DATA)
            .addHeaders("user_id", pref.getPrefString(prefConstant.USER_ID))
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener {
                override fun onResponse(response: JSONObject?) {
                    anim_loading.visibility = View.GONE
                    if (response?.getBoolean("sukses")!!) {
                        val photo = response.getJSONObject("data").getString("photo")
                        val name = response.getJSONObject("data").getString("name")
                        val email = response.getJSONObject("data").getString("email")

                        oldName = name
                        oldEmail = email
                        oldPhoto = photo

                        etName.setText(name)
                        etEmail.setText(email)
                        Picasso.get()
                            .load(URL.USER_THUMBNAIL + photo)
                            .error(R.drawable.profile)
                            .memoryPolicy(MemoryPolicy.NO_CACHE)
                            .networkPolicy(NetworkPolicy.NO_CACHE)
                            .into(userPhoto, object : com.squareup.picasso.Callback {
                                override fun onSuccess() {
                                    //set animations here
                                }

                                override fun onError(e: java.lang.Exception?) {
                                    //do smth when there is picture loading error
                                    makeToast("Error ${e?.localizedMessage.toString()}")
                                }
                            })

                    } else {
                        makeToast("Unexpected Response from Server")
                    }
                }

                override fun onError(anError: ANError?) {
                    anim_loading.visibility = View.GONE
                    makeToast("Error Response from Server , Please Try Again Later")
                }

            })
    }

    fun makeToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user_profile, container, false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment user_fragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            user_profile().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}