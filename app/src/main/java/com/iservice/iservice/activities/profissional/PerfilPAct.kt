package com.iservice.iservice.activities.profissional

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
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

class PerfilPAct : AppCompatActivity() {
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
        setContentView(R.layout.act_perfil_p)

        txt_nome = findViewById(R.id.txt_perfil_p_username)
        txt_cep = findViewById(R.id.txt_perfil_p_cep)
        txt_cpf = findViewById(R.id.txt_perfil_p_cpf)
        txt_cidade_e_estado = findViewById(R.id.txt_perfil_p_cidade)
        txt_bairro = findViewById(R.id.txt_perfil_p_bairro)
        txt_logradouro = findViewById(R.id.txt_perfil_p_logradouro)
        txt_idade = findViewById(R.id.txt_perfil_p_idade)
        img_foto = findViewById(R.id.img_perfil_p)
        btn_edit = findViewById(R.id.btn_perfil_p_edit)

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
                    val intent = Intent(this, AttPAct::class.java)

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