package com.iservice.iservice.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.iservice.iservice.activities.profissional.LoginPAct
import com.iservice.iservice.activities.contratante.MainCAct
import com.iservice.iservice.activities.profissional.MainPAct
import com.iservice.iservice.R
import com.iservice.iservice.classes.Users

class MainAct : AppCompatActivity() {
    private lateinit var btn_buscarprofissional: Button
    private lateinit var btn_souprofissional: Button
    private var me: Users? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.act_main)
        verifyAuthentication()

        btn_buscarprofissional = findViewById(R.id.btn_main_buscarprossifional)
        btn_souprofissional = findViewById(R.id.btn_main_souprossifional)

        btn_buscarprofissional.setOnClickListener {
            val intent = Intent(this, MainCAct::class.java)
            //val intent = Intent(this, MenuPAct::class.java)
            startActivity(intent)
        }

        btn_souprofissional.setOnClickListener {
            val intent = Intent(this, LoginPAct::class.java)
            startActivity(intent)
        }
    }

    private fun verifyAuthentication() {
        if (FirebaseAuth.getInstance().uid != null) {
            FirebaseFirestore.getInstance().collection("/usersP")
                .document(FirebaseAuth.getInstance().uid!!)
                .get()
                .addOnSuccessListener {
                    me = it.toObject(Users::class.java)!!
                    if (me != null) {
                        if (me!!.accountType == "Profissional") {
                            val intent = Intent(this, MainPAct::class.java)

                            intent.flags =
                                Intent.FLAG_ACTIVITY_CLEAR_TASK or (Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                        } else {
                            val intent = Intent(this, MainCAct::class.java)

                            intent.flags =
                                Intent.FLAG_ACTIVITY_CLEAR_TASK or (Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                        }
                    }

                }
        }
    }
}