package com.iservice.iservice.activities.profissional

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.widget.*
import com.google.firebase.firestore.FirebaseFirestore
import com.iservice.iservice.R
import com.iservice.iservice.classes.Animacao
import com.iservice.iservice.classes.Servico

class AnuncioEditAct : AppCompatActivity() {
    private lateinit var spn_tipo: Spinner
    private lateinit var btn_edit: Button
    private lateinit var servico: Servico
    private lateinit var txt_detalhes: EditText
    private lateinit var txt_servicoName: EditText
    private lateinit var barraP: ProgressBar
    val animacao = Animacao()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.act_anuncio_edit)

        spn_tipo = findViewById(R.id.spn_anuncio_edit_tipo)
        btn_edit = findViewById(R.id.btn_anuncio_edit_edit)
        txt_detalhes = findViewById(R.id.txt_anuncio_edit_p_desc)
        txt_servicoName = findViewById(R.id.txt_anuncio_edit_p_name)
        barraP = findViewById(R.id.progress_anuncio_edit)

        servico = intent.extras?.getParcelable("servico")!!
        supportActionBar!!.title = servico.servicoName

        spn_tipo.adapter = ArrayAdapter(
            this, android.R.layout.simple_spinner_dropdown_item,
            listOf("Instalações elétricas", "Obras de construção", "Eventos")
        )

        btn_edit.setOnClickListener {
            edit()
        }
        setarCampos()
    }

    private fun edit() {
        animacao.tradeView(btn_edit, barraP)
        val nomeServico = txt_servicoName.text.toString()
        val tipo = spn_tipo.selectedItem as String
        val detalhes = txt_detalhes.text.toString()
        val filename = servico.uuid
        val userUuid = servico.userUuid

        if (nomeServico.isEmpty() || nomeServico.trim() == "" || detalhes.isEmpty() ||
            nomeServico.trim() == "" || tipo.isEmpty()
        ) {
            Toast.makeText(this, "Todos campos devem devem ser preenchidos!", Toast.LENGTH_SHORT)
                .show()
            return
        }

        val anuncio = Servico(filename, nomeServico, userUuid, tipo, detalhes)

        FirebaseFirestore.getInstance().collection("anuncios")
            .document(filename!!)
            .set(anuncio)
            .addOnSuccessListener {
                Toast.makeText(this, "Alteração realizada!", Toast.LENGTH_SHORT)
                    .show()
                val intent = Intent(this, MainPAct::class.java)

                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or (Intent.FLAG_ACTIVITY_NEW_TASK)
                animacao.tradeView(barraP, btn_edit)
                startActivity(intent)
            }
            .addOnFailureListener {
                Toast.makeText(this, it.message.toString(), Toast.LENGTH_SHORT).show()
                animacao.tradeView(barraP, btn_edit)
            }
    }

    private fun setarCampos() {
        txt_servicoName.text = Editable.Factory.getInstance().newEditable(servico.servicoName)
        txt_detalhes.text = Editable.Factory.getInstance().newEditable(servico.descricao)
        when (servico.tipo) {
            "Instalações elétricas" -> spn_tipo.setSelection(0)
            "Obras de construção" -> spn_tipo.setSelection(1)
            "Eventos" -> spn_tipo.setSelection(2)
        }
    }
}