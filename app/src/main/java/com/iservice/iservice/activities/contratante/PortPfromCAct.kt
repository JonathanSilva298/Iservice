package com.iservice.iservice.activities.contratante

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.iservice.iservice.R
import com.iservice.iservice.classes.Portfolio
import com.iservice.iservice.classes.Users
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import de.hdodenhof.circleimageview.CircleImageView

class PortPfromCAct : AppCompatActivity() {
    private lateinit var img_foto: CircleImageView
    private lateinit var txt_nome: TextView
    private lateinit var rv: RecyclerView
    private lateinit var adapter: GroupAdapter<ViewHolder>
    private lateinit var user: Users

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.act_port_pfrom_c)

        img_foto = findViewById(R.id.img_port_pfromc)
        txt_nome = findViewById(R.id.txt_port_pfromc_username)
        rv = findViewById(R.id.recycler_port_pfromc)
        rv.layoutManager = LinearLayoutManager(this)
        rv.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))

        adapter = GroupAdapter()
        rv.adapter = adapter

        user = intent.extras?.getParcelable("user")!!
        supportActionBar!!.title = "Potfólio de ${user.username}"

        adapter.setOnItemClickListener { item, _ ->
            val intent = Intent(this, PortPDescFromCAct::class.java)

            val portfolioItem: PortfolioItem = item as PortfolioItem
            intent.putExtra("item", portfolioItem.portfolio)
            startActivity(intent)
        }

        fetchPortfolio()
        setarCampos()
    }

    private fun setarCampos() {
        txt_nome.text = user.username
        Picasso.get()
            .load(user.profileUrl)
            .into(img_foto)
    }

    private fun fetchPortfolio() {
        FirebaseFirestore.getInstance().collection("/portfolio")
            .document(user.uuid!!)
            .collection("services")
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
                            val portfolio: Portfolio = doc.document.toObject(Portfolio::class.java)
                            adapter.add(PortfolioItem(portfolio))
                        }
                    }
                }
            }
    }

    class PortfolioItem(val portfolio: Portfolio) : Item<ViewHolder>() {
        override fun bind(viewHolder: ViewHolder, position: Int) {
            val servicodesc: TextView = viewHolder.itemView.findViewById(R.id.txt_item_port_desc)
            val imgservico: ImageView = viewHolder.itemView.findViewById(R.id.img_item_port_servico)

            servicodesc.text = portfolio.descricao
            Picasso.get()
                .load(portfolio.photoUrl)
                .into(imgservico)
        }

        override fun getLayout(): Int {
            return R.layout.item_portfolio_p
        }
    }
}