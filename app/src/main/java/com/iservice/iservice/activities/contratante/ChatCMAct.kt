package com.iservice.iservice.activities.contratante

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.iservice.iservice.R
import com.iservice.iservice.classes.Contact
import com.iservice.iservice.classes.Message
import com.iservice.iservice.classes.Users
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder

class ChatCMAct : AppCompatActivity() {
    private lateinit var rv: RecyclerView
    private lateinit var adapter: GroupAdapter<ViewHolder>
    private lateinit var userc: Contact
    private lateinit var me: Users
    private lateinit var btn_chat: FloatingActionButton
    private lateinit var edit_chat: EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.act_chat_c_m)

        userc = intent.extras?.getParcelable("userc")!!
        supportActionBar!!.title = userc.username

        rv = findViewById(R.id.recycler_chat_c_m)
        btn_chat = findViewById(R.id.btn_chat_c_m)
        edit_chat = findViewById(R.id.edit_chat_c_m)

        btn_chat.setOnClickListener {
            sendMessage()
        }

        adapter = GroupAdapter()
        rv.layoutManager = LinearLayoutManager(this)
        rv.adapter = adapter

        FirebaseFirestore.getInstance().collection("/usersP")
            .document(FirebaseAuth.getInstance().uid!!)
            .get()
            .addOnSuccessListener {
                me = it.toObject(Users::class.java)!!
                fetchMessages()
            }
    }

    private fun fetchMessages() {
        val fromId: String = me.uuid!!
        val toId: String = userc.uuid!!

        FirebaseFirestore.getInstance().collection("/conversations")
            .document(fromId)
            .collection(toId)
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, _ ->
                val documentChanges: List<DocumentChange>? = snapshot?.documentChanges

                if (documentChanges != null) {
                    for (doc: DocumentChange in documentChanges) {
                        if (doc.type == DocumentChange.Type.ADDED) {
                            val message: Message = doc.document.toObject(Message::class.java)
                            adapter.add(MessageItem(message))
                        }
                    }
                }
            }
    }

    private fun sendMessage() {
        val text = edit_chat.text.toString()

        edit_chat.text = null

        val fromId: String = FirebaseAuth.getInstance().uid!!
        val toId: String = userc.uuid!!
        val timestamp: Long = System.currentTimeMillis()
        val userFoto: String? = me.profileUrl

        val message = Message()
        message.fromId = fromId
        message.toId = toId
        message.timestamp = timestamp
        message.text = text
        message.userPhoto = userFoto

        if (message.text?.isEmpty() == false) {
            FirebaseFirestore.getInstance().collection("/conversations")
                .document(fromId)
                .collection(toId)
                .add(message)
                .addOnSuccessListener {

                    val contact = Contact()
                    contact.uuid = toId
                    contact.username = userc.username
                    contact.photoUrl = userc.photoUrl
                    contact.timestamp = message.timestamp
                    contact.lastMessage = message.text

                    FirebaseFirestore.getInstance().collection("/last-messages")
                        .document(fromId)
                        .collection("contacts")
                        .document(toId)
                        .set(contact)

                }
                .addOnFailureListener {
                    Toast.makeText(this, it.message.toString(), Toast.LENGTH_SHORT)
                        .show()
                }

            FirebaseFirestore.getInstance().collection("/conversations")
                .document(toId)
                .collection(fromId)
                .add(message)
                .addOnSuccessListener {

                    val contact = Contact()
                    contact.uuid = fromId
                    contact.username = me.username
                    contact.photoUrl = me.profileUrl
                    contact.timestamp = message.timestamp
                    contact.lastMessage = message.text

                    FirebaseFirestore.getInstance().collection("/last-messages")
                        .document(toId)
                        .collection("contacts")
                        .document(fromId)
                        .set(contact)
                }
                .addOnFailureListener {
                    Toast.makeText(this, it.message.toString(), Toast.LENGTH_SHORT)
                        .show()
                }
        }
    }

    private class MessageItem(val message: Message) : Item<ViewHolder>() {
        override fun bind(viewHolder: ViewHolder, position: Int) {
            val txtMsg: TextView = viewHolder.itemView.findViewById(R.id.txt_chat_message)
            val imgMessage: ImageView = viewHolder.itemView.findViewById(R.id.img_chat_user)

            txtMsg.text = message.text
            Picasso.get()
                .load(message.userPhoto)
                .into(imgMessage)
        }

        override fun getLayout(): Int {
            return if (message.fromId.equals(FirebaseAuth.getInstance().uid))
                R.layout.item_to_message
            else
                R.layout.item_from_message
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_chat_c, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.avaliar_c -> {
                val intent = Intent(this@ChatCMAct, AvalCMAct::class.java)
                intent.putExtra("userAval", userc)
                startActivity(intent)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}