package com.iservice.iservice.activities.contratante

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ProgressBar
import android.widget.RatingBar
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.iservice.iservice.R
import com.iservice.iservice.classes.Animacao
import com.iservice.iservice.classes.Avaliacao
import com.iservice.iservice.classes.Contact
import com.iservice.iservice.classes.Users

class AvalCMAct : AppCompatActivity() {
    private lateinit var ratingAtendimento: RatingBar
    private lateinit var ratingServico: RatingBar
    private lateinit var ratingOrcamento: RatingBar
    private lateinit var btn_avaliar: Button
    private lateinit var barraP: ProgressBar
    private lateinit var userAval: Contact
    private lateinit var user: Users
    private var subAtend: Double? = null
    private var subServ: Double? = null
    private var subOrc: Double? = null
    private var myName: String? = null
    val animacao = Animacao()
    private var MyAval: Avaliacao? = null
    private var uid = FirebaseAuth.getInstance().uid

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.act_aval_cm)

        userAval = intent.extras?.getParcelable("userAval")!!
        supportActionBar!!.title = "Avaliar ${userAval.username}"

        ratingAtendimento = findViewById(R.id.ratingBar_aval_cm_atendimento)
        ratingServico = findViewById(R.id.ratingBar_aval_cm_servico)
        ratingOrcamento = findViewById(R.id.ratingBar_aval_cm_orcamento)
        btn_avaliar = findViewById(R.id.btn_aval_cm_salvar)
        barraP = findViewById(R.id.progress_aval_cm)

        FirebaseFirestore.getInstance().collection("/usersP")
            .document(FirebaseAuth.getInstance().uid!!)
            .get()
            .addOnSuccessListener { documentSnapshot ->
                val me = documentSnapshot.toObject(Users::class.java)!!
                myName = me.username
            }

        FirebaseFirestore.getInstance().collection("/avaliacoes")
            .document(userAval.uuid!!)
            .collection("contacts")
            .document(uid!!)
            .get()
            .addOnSuccessListener {
                MyAval = it.toObject(Avaliacao::class.java)
                if (MyAval != null) {
                    subAtend = MyAval!!.avalAtend
                    subServ = MyAval!!.avalServ
                    subOrc = MyAval!!.avalOrc
                    setarCampos()
                }
            }

        btn_avaliar.setOnClickListener {
            FirebaseFirestore.getInstance().collection("/usersP")
                .document(userAval.uuid!!)
                .get()
                .addOnSuccessListener { documentSnapshot ->
                    animacao.tradeView(btn_avaliar, barraP)
                    user = documentSnapshot.toObject(Users::class.java)!!
                    Avaliar()
                }
        }
    }

    private fun Avaliar() {
        val avaliando = myName
        val atendimento = ratingAtendimento.rating.toDouble()
        val servico = ratingServico.rating.toDouble()
        val orcamento = ratingOrcamento.rating.toDouble()

        val saveAvaliacao = Avaliacao(avaliando, atendimento, servico, orcamento)
        FirebaseFirestore.getInstance().collection("/avaliacoes")
            .document(user.uuid!!)
            .collection("contacts")
            .document(uid!!)
            .set(saveAvaliacao)
            .addOnSuccessListener {

                var plusAtend = (user.totalAvalAtend + atendimento)
                var plusServ = (user.totalAvalServ + servico)
                var plusOrc = (user.totalAvalOrc + orcamento)
                var plusTotal = (user.numAval + 1)
                if (subAtend != null && subServ != null && subOrc != null) {
                    plusAtend -= subAtend!!
                    plusServ -= subServ!!
                    plusOrc -= subOrc!!
                    plusTotal -= 1
                }

                FirebaseFirestore.getInstance().collection("/usersP")
                    .document(user.uuid!!)
                    .update(
                        "totalAvalAtend", plusAtend,
                        "totalAvalServ", plusServ,
                        "totalAvalOrc", plusOrc, "numAval", plusTotal
                    ).addOnSuccessListener {
                        Toast.makeText(
                            this, "Avaliação salva!",
                            Toast.LENGTH_LONG
                        ).show()
                        val intent = Intent(this, MainCAct::class.java)

                        intent.flags =
                            Intent.FLAG_ACTIVITY_CLEAR_TASK or (Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                        animacao.tradeView(barraP, btn_avaliar)
                    }
            }
    }

    private fun setarCampos() {
        ratingAtendimento.rating = (MyAval!!.avalAtend).toFloat()
        ratingServico.rating = (MyAval!!.avalServ).toFloat()
        ratingOrcamento.rating = (MyAval!!.avalOrc).toFloat()
    }
}