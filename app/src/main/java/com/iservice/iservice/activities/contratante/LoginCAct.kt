package com.iservice.iservice.activities.contratante

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.iservice.iservice.R
import com.iservice.iservice.classes.Users

class LoginCAct : AppCompatActivity() {

    private lateinit var txt_email: EditText
    private lateinit var txt_senha: EditText
    private lateinit var btn_enter: Button
    private lateinit var txt_account: TextView
    private var me: Users? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.act_login_c)

        txt_email = findViewById(R.id.txt_logincontratante_email)
        txt_senha = findViewById(R.id.txt_logincontratante_senha)
        btn_enter = findViewById(R.id.btn_logincontratante_enter)
        txt_account = findViewById(R.id.txt_logincontratante_account)

        btn_enter.setOnClickListener {
            val email = txt_email.text.toString()
            val senha = txt_senha.text.toString()

            if (email.isEmpty() || senha.isEmpty()) {
                Toast.makeText(
                    this@LoginCAct,
                    "Nome, Email e Senha devem ser preenchidos!",
                    Toast.LENGTH_SHORT
                )
                    .show()
                return@setOnClickListener
            }

            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, senha)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        txt_email.text.clear()
                        txt_senha.text.clear()

                        FirebaseFirestore.getInstance().collection("/usersP")
                            .document(FirebaseAuth.getInstance().uid!!)
                            .get()
                            .addOnSuccessListener {

                                me = it.toObject(Users::class.java)!!
                                if (me!!.accountType != "Contratante") {
                                    Toast.makeText(
                                        this,
                                        "O tipo da conta não corresponde!",
                                        Toast.LENGTH_SHORT
                                    )
                                        .show()
                                    return@addOnSuccessListener
                                }
                            }


                        val intent = Intent(this, MainCAct::class.java)

                        intent.flags =
                            Intent.FLAG_ACTIVITY_CLEAR_TASK or (Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, it.message.toString(), Toast.LENGTH_SHORT)
                        .show()
                }
        }
        txt_account.setOnClickListener {
            val intent = Intent(this, CadCAct::class.java)
            startActivity(intent)
        }
    }
}