package com.iservice.iservice.activities.contratante

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuItemCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.installations.FirebaseInstallations
import com.iservice.iservice.R
import com.iservice.iservice.activities.MainAct
import com.iservice.iservice.classes.ChatApplication
import com.iservice.iservice.classes.Servico
import com.iservice.iservice.classes.Users
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import de.hdodenhof.circleimageview.CircleImageView

class MainCAct : AppCompatActivity() {

    private lateinit var rv: RecyclerView
    private lateinit var adapter: GroupAdapter<ViewHolder>
    private lateinit var item_search: MenuItem
    val status = ChatApplication()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.act_main_c)

        val application = application as ChatApplication
        application.registerActivityLifecycleCallbacks(application)

        rv = findViewById(R.id.recycler_anuncios_c)
        rv.layoutManager = LinearLayoutManager(this)

        adapter = GroupAdapter()
        rv.adapter = adapter

        adapter.setOnItemClickListener { item, _ ->
            if (FirebaseAuth.getInstance().uid == null) {
                Toast.makeText(this, "Login Necessário!", Toast.LENGTH_SHORT).show()
            } else {
                val intent = Intent(this, AnuncioDescCAct::class.java)

                val servicoItem: ServicoItem = item as ServicoItem
                intent.putExtra("servico", servicoItem.servico)
                startActivity(intent)
            }
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

    private fun search(s: String?) {
        if (s?.isEmpty() == true || s == null || s.trim() == "") {
            fetchAnuncios()
            return
        }
        FirebaseFirestore.getInstance().collection("anuncios").orderBy("servicoName")
            .startAt(s).endAt("$s\uf8ff").get().addOnCompleteListener {
                if (it.isSuccessful) {
                    adapter.clear()
                    val servico: MutableList<Servico> = it.result!!.toObjects(Servico::class.java)
                    for (serv: Servico in servico) {
                        adapter.add(ServicoItem(serv))
                    }
                } else {
                    Toast.makeText(this, it.exception!!.message.toString(), Toast.LENGTH_SHORT)
                        .show()
                }
            }
    }

    private fun fetchAnuncios() {
        FirebaseFirestore.getInstance().collection("anuncios")
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
                            adapter.add(ServicoItem(servico))
                        }
                    }
                }
            }
    }

    private fun verifyAuthentication() {
        if (FirebaseAuth.getInstance().uid == null) {
            val intent = Intent(this@MainCAct, MainAct::class.java)

            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or (Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        if (FirebaseAuth.getInstance().uid == null) {
            menuInflater.inflate(R.menu.menu_c_semlogin, menu)
        } else {
            menuInflater.inflate(R.menu.menu_c, menu)
        }

        item_search = menu.findItem(R.id.search_c)
        val searchView: SearchView = MenuItemCompat.getActionView(item_search) as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                search(query)
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                search(newText)
                return false
            }

        })
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.perfil_c -> {
                val intent = Intent(this@MainCAct, PerfilCAct::class.java)
                startActivity(intent)
                return true
            }
            R.id.messages_c -> {
                val intent = Intent(this@MainCAct, MessagesCAct::class.java)
                startActivity(intent)
                return true
            }
            R.id.logout_c -> {
                status.setOnline(false)
                FirebaseAuth.getInstance().signOut()
                verifyAuthentication()
                return true
            }
            R.id.login_c_semlogin -> {
                val intent = Intent(this, LoginCAct::class.java)

                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or (Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    class ServicoItem(val servico: Servico) : Item<ViewHolder>() {
        override fun bind(viewHolder: ViewHolder, position: Int) {

            val serviconame: TextView =
                viewHolder.itemView.findViewById(R.id.txt_item_anunciosc_username)
            val prestadorname: TextView =
                viewHolder.itemView.findViewById(R.id.txt_item_anunciosc_servico)
            val estadoUF: TextView =
                viewHolder.itemView.findViewById(R.id.txt_item_anunciosc_uf)
            val iconservico: ImageView =
                viewHolder.itemView.findViewById(R.id.img_item_c_iconServico)
            val prestadorfoto: CircleImageView =
                viewHolder.itemView.findViewById(R.id.img_item_anunciosc_foto)
            var prof: Users?

            serviconame.text = servico.servicoName

            FirebaseFirestore.getInstance().collection("/usersP")
                .document(servico.userUuid!!)
                .get()
                .addOnSuccessListener {
                    prof = it.toObject(Users::class.java)!!
                    estadoUF.text = prof!!.estado
                    prestadorname.text = prof!!.username
                    Picasso.get()
                        .load(prof!!.profileUrl)
                        .into(prestadorfoto)
                }

            when (servico.tipo) {
                "Instalações elétricas" -> iconservico.setImageResource(R.drawable.icon_eletricista)
                "Eventos" -> iconservico.setImageResource(R.drawable.icon_events)
                "Obras de construção" -> iconservico.setImageResource(R.drawable.icon_pedreiro)
            }
        }

        override fun getLayout(): Int {
            return R.layout.item_anuncios_c
        }
    }
}