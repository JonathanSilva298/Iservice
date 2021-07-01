package com.iservice.iservice.activities.profissional

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.iservice.iservice.R
import com.iservice.iservice.classes.Contact
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder

class MessagesPAct : AppCompatActivity() {
    private lateinit var rv: RecyclerView
    private lateinit var adapter: GroupAdapter<ViewHolder>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.act_messages_p)

        rv = findViewById(R.id.recycler_messages_p)
        rv.layoutManager = LinearLayoutManager(this)
        rv.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))

        adapter = GroupAdapter()
        rv.adapter = adapter

        adapter.setOnItemClickListener { item, _ ->
            val intent = Intent(this, ChatPAct::class.java)

            val userItem: ContactItem = item as ContactItem
            intent.putExtra("userc", userItem.contact)
            startActivity(intent)
        }

        fetchLastMessage()
    }

    private fun fetchLastMessage() {
        val uid: String = FirebaseAuth.getInstance().uid!!

        FirebaseFirestore.getInstance().collection("/last-messages")
            .document(uid)
            .collection("/contacts")
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Toast.makeText(this, e.message.toString(), Toast.LENGTH_SHORT)
                        .show()
                    return@addSnapshotListener
                }
                val documentChanges: List<DocumentChange>? = snapshot?.documentChanges

                if (documentChanges != null) {
                    for (doc: DocumentChange in documentChanges) {
                        if (doc.type == DocumentChange.Type.ADDED) {
                            val contact: Contact = doc.document.toObject(Contact::class.java)

                            adapter.add(ContactItem(contact))
                        }
                    }
                }
            }
    }

    class ContactItem(val contact: Contact) : Item<ViewHolder>() {
        override fun bind(viewHolder: ViewHolder, position: Int) {
            val username: TextView = viewHolder.itemView.findViewById(R.id.txt_item_message)
            val message: TextView = viewHolder.itemView.findViewById(R.id.txt_item_message_2)
            val imgPhoto: ImageView = viewHolder.itemView.findViewById(R.id.img_item_message)

            username.text = contact.username
            message.text = contact.lastMessage
            Picasso.get()
                .load(contact.photoUrl)
                .into(imgPhoto)
        }

        override fun getLayout(): Int {
            return R.layout.item_user_message_p
        }

    }
}