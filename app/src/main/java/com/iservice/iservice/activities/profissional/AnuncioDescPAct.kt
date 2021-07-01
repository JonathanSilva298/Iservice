package com.iservice.iservice.activities.profissional

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.FirebaseFirestore
import com.iservice.iservice.R
import com.iservice.iservice.classes.Servico
import com.iservice.iservice.classes.Users
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class AnuncioDescPAct : AppCompatActivity() {
    private lateinit var userName: TextView
    private lateinit var servicoTipo: TextView
    private lateinit var servicoDesc: TextView
    private lateinit var img_icon: ImageView
    private lateinit var img_foto: CircleImageView
    private lateinit var servico: Servico
    private lateinit var btn_delete: FloatingActionButton
    private lateinit var btn_edit: FloatingActionButton
    private lateinit var user: Users

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.act_anuncio_desc_p)

        userName = findViewById(R.id.txt_anuncio_desc_p_username)
        servicoTipo = findViewById(R.id.txt_anuncio_desc_p_tipo)
        servicoDesc = findViewById(R.id.txt_anuncio_desc_p_desc)
        img_icon = findViewById(R.id.img_anuncio_desc_p_iconServico)
        img_foto = findViewById(R.id.img_anuncio_desc_p_userfoto)
        btn_delete = findViewById(R.id.btn_anuncio_desc_p_delete)
        btn_edit = findViewById(R.id.btn_anuncio_desc_p_edit)

        servico = intent.extras?.getParcelable("servico")!!
        supportActionBar!!.title = servico.servicoName

        btn_delete.setOnClickListener {
            deletar()
        }
        btn_edit.setOnClickListener {
            val intent = Intent(this, AnuncioEditAct::class.java)

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
    }

    private fun setarCampos() {
        userName.text = user.username
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

    private fun deletar() {
        FirebaseFirestore.getInstance().collection("anuncios")
            .document(servico.uuid!!)
            .delete()
            .addOnSuccessListener {

                Toast.makeText(this, "Excluído com sucesso!", Toast.LENGTH_SHORT)
                    .show()
                val intent = Intent(this, MainPAct::class.java)

                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or (Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)

            }
            .addOnFailureListener {
                Toast.makeText(this, it.message.toString(), Toast.LENGTH_SHORT).show()
            }

    }
}