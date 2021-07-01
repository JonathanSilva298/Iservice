package com.iservice.iservice.activities.contratante

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.RatingBar
import android.widget.TextView
import com.iservice.iservice.R
import com.iservice.iservice.classes.Idade
import com.iservice.iservice.classes.Users
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import java.text.SimpleDateFormat
import java.util.*

class PerfilPfromCAct : AppCompatActivity() {
    private lateinit var img_foto: CircleImageView
    private lateinit var txt_nome: TextView
    private lateinit var txt_cep: TextView
    private lateinit var txt_idade: TextView
    private lateinit var txt_cidade_e_estado: TextView
    private lateinit var txt_bairro: TextView
    private lateinit var btn_portfolio: Button
    private lateinit var txt_logradouro: TextView
    private lateinit var txt_numAvalAtend: TextView
    private lateinit var txt_numAvalServ: TextView
    private lateinit var txt_numAvalOrc: TextView
    private lateinit var ratingAtendimento: RatingBar
    private lateinit var ratingServico: RatingBar
    private lateinit var ratingOrcamento: RatingBar
    private lateinit var user: Users
    val idadeFormat = Idade()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.act_perfil_pfrom_c)

        txt_nome = findViewById(R.id.txt_perfil_pfrom_c_username)
        txt_cep = findViewById(R.id.txt_perfil_pfrom_c_cep)
        txt_cidade_e_estado = findViewById(R.id.txt_perfil_pfrom_c_cidade)
        txt_bairro = findViewById(R.id.txt_perfil_pfrom_c_bairro)
        txt_logradouro = findViewById(R.id.txt_perfil_pfrom_c_logradouro)
        txt_idade = findViewById(R.id.txt_perfil_pfrom_c_idade)
        txt_numAvalAtend = findViewById(R.id.txt_perfil_pfrom_c_atendimento_cont)
        txt_numAvalServ = findViewById(R.id.txt_perfil_pfrom_c_servico_cont)
        txt_numAvalOrc = findViewById(R.id.txt_perfil_pfrom_c_orcamento_cont)
        ratingAtendimento = findViewById(R.id.ratingBar_perfil_pfrom_c_atendimento)
        ratingServico = findViewById(R.id.ratingBar_perfil_pfrom_c_servico)
        ratingOrcamento = findViewById(R.id.ratingBar_perfil_pfrom_c_orcamento)
        img_foto = findViewById(R.id.img_perfil_pfrom_c)
        btn_portfolio = findViewById(R.id.btn_perfil_pfromc_portfolio)

        user = intent.extras?.getParcelable("user")!!
        supportActionBar!!.title = user.username

        btn_portfolio.setOnClickListener {
            val intent = Intent(this, PortPfromCAct::class.java)

            intent.putExtra("user", user)
            startActivity(intent)
        }

        setarCampos()
    }

    private fun setarCampos() {
        val dataf = SimpleDateFormat("dd/MM/yyyy")
        val dataNascimento: Date = dataf.parse(user.dataNasc)
        val idade: Int = idadeFormat.calculaIdade(dataNascimento)
        txt_nome.text = user.username
        txt_cep.text = user.cep
        "${user.cidade}-${user.estado}".also { txt_cidade_e_estado.text = it }
        txt_bairro.text = user.bairro
        txt_logradouro.text = user.logradouro
        "$idade anos".also { txt_idade.text = it }
        ratingAtendimento.rating = (user.totalAvalAtend / user.numAval).toFloat()
        ratingServico.rating = (user.totalAvalServ / user.numAval).toFloat()
        ratingOrcamento.rating = (user.totalAvalOrc / user.numAval).toFloat()
        "(${user.numAval})".also { txt_numAvalAtend.text = it }
        "(${user.numAval})".also { txt_numAvalServ.text = it }
        "(${user.numAval})".also { txt_numAvalOrc.text = it }
        Picasso.get()
            .load(user.profileUrl)
            .into(img_foto)
    }
}