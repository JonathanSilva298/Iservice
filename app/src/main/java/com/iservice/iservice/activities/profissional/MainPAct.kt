package com.iservice.iservice.activities.profissional

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.installations.FirebaseInstallations
import com.iservice.iservice.R
import com.iservice.iservice.activities.MainAct
import com.iservice.iservice.classes.ChatApplication
import com.iservice.iservice.classes.Servico
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder

class MainPAct : AppCompatActivity() {

    private lateinit var rv: RecyclerView
    private lateinit var adapter: GroupAdapter<ViewHolder>
    private lateinit var btn_add: FloatingActionButton
    val status = ChatApplication()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.act_main_p)

        val application = application as ChatApplication
        application.registerActivityLifecycleCallbacks(application)

        btn_add = findViewById(R.id.btn_anunciar_add)
        rv = findViewById(R.id.recycler_main_p)
        rv.layoutManager = LinearLayoutManager(this)
        rv.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))

        adapter = GroupAdapter()
        rv.adapter = adapter

        adapter.setOnItemClickListener { item, _ ->
            val intent = Intent(this, AnuncioDescPAct::class.java)

            val servicoItem: ServicoItem = item as ServicoItem
            intent.putExtra("servico", servicoItem.servico)
            startActivity(intent)
        }

        btn_add.setOnClickListener {
            val intent = Intent(this, AnunciarAct::class.java)
            startActivity(intent)
        }

        updateToken()
        fetchAnuncios()
    }

    private fun updateToken() {
        FirebaseInstallations.getInstance().getToken(true).addOnCompleteListener {
            val uid = FirebaseAuth.getInstance().uid

            if (uid != null) {
                FirebaseFirestore.getInstance().collection("usersP")
                    .document(uid)
                    .update("token", it.result!!.token)
            }
        }
    }

    private fun fetchAnuncios() {
        val uid: String = FirebaseAuth.getInstance().uid!!

        FirebaseFirestore.getInstance().collection("/anuncios")
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Toast.makeText(this, e.message.toString(), Toast.LENGTH_SHORT)
                        .show()
                    return@addSnapshotListener
                }
                val documentChanges: List<DocumentChange>? = snapshot?.documentChanges
                adapter.clear()

                if (documentChanges != null) {
                    for (doc: DocumentChange in documentChanges) {
                        if (doc.type == DocumentChange.Type.ADDED) {
                            val servico: Servico = doc.document.toObject(Servico::class.java)
                            if (servico.userUuid == uid) {
                                adapter.add(ServicoItem(servico))
                            }
                        }
                    }
                }
            }
    }

    private fun verifyAuthentication() {
        if (FirebaseAuth.getInstance().uid == null) {
            val intent = Intent(this@MainPAct, MainAct::class.java)

            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or (Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_p, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.perfil_p -> {
                val intent = Intent(this@MainPAct, PerfilPAct::class.java)
                startActivity(intent)
                return true
            }
            R.id.portfolio_p -> {
                val intent = Intent(this@MainPAct, PortPAct::class.java)
                startActivity(intent)
                return true
            }
            R.id.messages_p -> {
                val intent = Intent(this@MainPAct, MessagesPAct::class.java)
                startActivity(intent)
                return true
            }
            R.id.logout_p -> {
                status.setOnline(false)
                FirebaseAuth.getInstance().signOut()
                verifyAuthentication()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    class ServicoItem(val servico: Servico) : Item<ViewHolder>() {
        override fun bind(viewHolder: ViewHolder, position: Int) {
            val serviconame: TextView = viewHolder.itemView.findViewById(R.id.txt_item_servicoName)
            val servicotipo: TextView = viewHolder.itemView.findViewById(R.id.txt_item_servicoTipo)
            val iconservico: ImageView = viewHolder.itemView.findViewById(R.id.img_item_iconServico)

            serviconame.text = servico.servicoName
            servicotipo.text = servico.tipo

            when (servico.tipo) {
                "Instalações elétricas" -> iconservico.setImageResource(R.drawable.icon_eletricista)
                "Eventos" -> iconservico.setImageResource(R.drawable.icon_events)
                "Obras de construção" -> iconservico.setImageResource(R.drawable.icon_pedreiro)
            }
        }


        override fun getLayout(): Int {
            return R.layout.item_anuncios_p
        }
    }
}