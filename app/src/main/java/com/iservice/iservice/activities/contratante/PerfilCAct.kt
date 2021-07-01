package com.iservice.iservice.activities.contratante

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.iservice.iservice.R
import com.iservice.iservice.classes.Idade
import com.iservice.iservice.classes.Users
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import java.text.SimpleDateFormat
import java.util.*

class PerfilCAct : AppCompatActivity() {
    private lateinit var img_foto: CircleImageView
    private lateinit var txt_nome: TextView
    private lateinit var txt_cep: TextView
    private lateinit var txt_cpf: TextView
    private lateinit var txt_idade: TextView
    private lateinit var txt_cidade_e_estado: TextView
    private lateinit var txt_bairro: TextView
    private lateinit var txt_logradouro: TextView
    private lateinit var btn_edit: FloatingActionButton
    private lateinit var me: Users
    val idadeFormat = Idade()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.act_perfil_c)

        img_foto = findViewById(R.id.img_perfil_c)
        txt_cep = findViewById(R.id.txt_perfil_c_cep)
        txt_cpf = findViewById(R.id.txt_perfil_c_cpf)
        txt_cidade_e_estado = findViewById(R.id.txt_perfil_c_cidade)
        txt_bairro = findViewById(R.id.txt_perfil_c_bairro)
        txt_logradouro = findViewById(R.id.txt_perfil_c_logradouro)
        txt_idade = findViewById(R.id.txt_perfil_c_idade)
        txt_nome = findViewById(R.id.txt_perfil_c_username)
        btn_edit = findViewById(R.id.btn_perfil_c_edit)

        FirebaseFirestore.getInstance().collection("/usersP")
            .document(FirebaseAuth.getInstance().uid!!)
            .get()
            .addOnSuccessListener {
                me = it.toObject(Users::class.java)!!
                setarCampos()
            }

        btn_edit.setOnClickListener {
            FirebaseFirestore.getInstance().collection("/usersP")
                .document(FirebaseAuth.getInstance().uid!!)
                .get()
                .addOnSuccessListener {
                    me = it.toObject(Users::class.java)!!
                    val intent = Intent(this, AttCAct::class.java)

                    intent.putExtra("user", me)
                    startActivity(intent)
                }
        }
    }

    private fun setarCampos() {
        val dataf = SimpleDateFormat("dd/MM/yyyy")
        val dataNascimento: Date = dataf.parse(me.dataNasc)
        val idade: Int = idadeFormat.calculaIdade(dataNascimento)
        txt_nome.text = me.username
        txt_cep.text = me.cep
        txt_cpf.text = me.cpf
        "${me.cidade}-${me.estado}".also { txt_cidade_e_estado.text = it }
        txt_bairro.text = me.bairro
        txt_logradouro.text = me.logradouro
        "$idade anos".also { txt_idade.text = it }
        Picasso.get()
            .load(me.profileUrl)
            .into(img_foto)
    }
}