package com.iservice.iservice.activities.contratante

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.FirebaseFirestore
import com.iservice.iservice.R
import com.iservice.iservice.classes.Servico
import com.iservice.iservice.classes.Users
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class AnuncioDescCAct : AppCompatActivity() {
    private lateinit var userName: TextView
    private lateinit var userCep: TextView
    private lateinit var userEndereco: TextView
    private lateinit var servicoTipo: TextView
    private lateinit var servicoDesc: TextView
    private lateinit var img_icon: ImageView
    private lateinit var img_foto: CircleImageView
    private lateinit var servico: Servico
    private lateinit var btn_message: FloatingActionButton
    private lateinit var btn_perfil: FloatingActionButton
    private lateinit var user: Users

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.act_anuncio_desc_c)
        userName = findViewById(R.id.txt_anuncio_desc_c_username)
        userCep = findViewById(R.id.txt_anuncio_desc_c_cep)
        userEndereco = findViewById(R.id.txt_anuncio_desc_c_endereco)
        servicoTipo = findViewById(R.id.txt_anuncio_desc_c_tipo)
        servicoDesc = findViewById(R.id.txt_anuncio_desc_c_desc)
        img_icon = findViewById(R.id.img_anuncio_desc_c_iconServico)
        img_foto = findViewById(R.id.img_anuncio_desc_c_userfoto)
        btn_message = findViewById(R.id.btn_anuncio_desc_c_message)
        btn_perfil = findViewById(R.id.btn_anuncio_desc_c_perfil)

        servico = intent.extras?.getParcelable("servico")!!
        supportActionBar!!.title = servico.servicoName

        btn_message.setOnClickListener {
            val intent = Intent(this, ChatCAct::class.java)

            intent.putExtra("servico", servico)
            startActivity(intent)
        }
        FirebaseFirestore.getInstance().collection("/usersP")
            .document(servico.userUuid!!)
            .get()
            .addOnSuccessListener {
                user = it.toObject(Users::class.java)!!
                setarCampos()
            }

        btn_perfil.setOnClickListener {
            val intent = Intent(this, PerfilPfromCAct::class.java)

            FirebaseFirestore.getInstance().collection("/usersP")
                .document(servico.userUuid!!)
                .get()
                .addOnSuccessListener {
                    user = it.toObject(Users::class.java)!!
                    intent.putExtra("user", user)
                    startActivity(intent)
                }

        }
    }

    private fun setarCampos() {
        userName.text = user.username
        userCep.text = user.cep
        "${user.logradouro}, ${user.bairro}. ${user.cidade}-${user.estado}".also {
            userEndereco.text = it
        }
        servicoTipo.text = servico.tipo
        servicoDesc.text = servico.descricao
        when (servico.tipo) {
            "Instalações elétricas" -> img_icon.setImageResource(R.drawable.icon_eletricista)
            "Eventos" -> img_icon.setImageResource(R.drawable.icon_events)
            "Obras de construção" -> img_icon.setImageResource(R.drawable.icon_pedreiro)
        }
        Picasso.get()
            .load(user.profileUrl)
            .into(img_foto)
    }
}