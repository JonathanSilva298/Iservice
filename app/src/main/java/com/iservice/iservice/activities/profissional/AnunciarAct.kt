package com.iservice.iservice.activities.profissional

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.iservice.iservice.R
import com.iservice.iservice.classes.Servico
import com.iservice.iservice.classes.Users
import java.util.*

class AnunciarAct : AppCompatActivity() {
    private lateinit var spn_tipo: Spinner
    private lateinit var btn_cadastrar: Button
    private lateinit var me: Users
    private lateinit var txt_detalhes: EditText
    private lateinit var txt_servicoName: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.act_anunciar)
        spn_tipo = findViewById(R.id.spn_anunciar_tipo)
        btn_cadastrar = findViewById(R.id.btn_anunciar_cadastrar)
        txt_detalhes = findViewById(R.id.txt_anunciar_detalhes)
        txt_servicoName = findViewById(R.id.txt_anunciar_nome)

        spn_tipo.adapter = ArrayAdapter(
            this, android.R.layout.simple_spinner_dropdown_item,
            listOf("Instalações elétricas", "Obras de construção", "Eventos")
        )

        btn_cadastrar.setOnClickListener {
            FirebaseFirestore.getInstance().collection("/usersP")
                .document(FirebaseAuth.getInstance().uid!!)
                .get()
                .addOnSuccessListener {
                    me = it.toObject(Users::class.java)!!
                    createAnuncio()
                }

        }
    }

    private fun createAnuncio() {
        val nomeServico = txt_servicoName.text.toString()
        val tipo = spn_tipo.selectedItem as String
        val detalhes = txt_detalhes.text.toString()
        val uid = me.uuid
        val filename = UUID.randomUUID().toString()//

        if (nomeServico.isEmpty() || detalhes.isEmpty() || tipo.isEmpty()) {
            Toast.makeText(this, "Todos campos devem devem ser preenchidos!", Toast.LENGTH_SHORT)
                .show()
            return
        }
        val anuncio = Servico(filename, nomeServico, uid, tipo, detalhes)

        FirebaseFirestore.getInstance().collection("anuncios")
            .document(filename)
            .set(anuncio)
            .addOnSuccessListener {
                val intent = Intent(this, MainPAct::class.java)

                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or (Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
            .addOnFailureListener {
                Toast.makeText(this, it.message.toString(), Toast.LENGTH_SHORT).show()
            }
    }
}