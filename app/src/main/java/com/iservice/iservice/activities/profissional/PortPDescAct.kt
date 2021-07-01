package com.iservice.iservice.activities.profissional

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.widget.*
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.iservice.iservice.R
import com.iservice.iservice.classes.Animacao
import com.iservice.iservice.classes.Portfolio
import com.squareup.picasso.Picasso

class PortPDescAct : AppCompatActivity() {
    private lateinit var item: Portfolio
    private lateinit var txt_desc: EditText
    private lateinit var img_foto: ImageView
    private lateinit var barraP: ProgressBar
    private lateinit var btn_edit: Button
    private var selectedUri: Uri? = null
    private lateinit var btn_delete: FloatingActionButton
    val animacao = Animacao()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.act_port_pdesc)
        txt_desc = findViewById(R.id.txt_port_pdesc_desc)
        img_foto = findViewById(R.id.img_port_pdesc_foto)
        btn_delete = findViewById(R.id.btn_port_pdesc_delete)
        btn_edit = findViewById(R.id.btn_port_pdesc_edit)
        barraP = findViewById(R.id.progress_port_pdesc)

        item = intent.extras?.getParcelable("item")!!

        img_foto.setOnClickListener {
            selectPhoto()
        }
        btn_edit.setOnClickListener {
            edit()
        }
        btn_delete.setOnClickListener {
            deletar()
        }

        setarCampos()
    }

    private fun deletar() {
        FirebaseFirestore.getInstance().collection("/portfolio")
            .document(item.userUuid!!)
            .collection("services")
            .document(item.uuid!!)
            .delete()
            .addOnSuccessListener {
                FirebaseStorage.getInstance().getReference(
                    "/images/services/" +
                            "${item.uuid}"
                )
                    .delete()

                Toast.makeText(this, "Excluído com sucesso!", Toast.LENGTH_SHORT)
                    .show()
                val intent = Intent(this, PortPAct::class.java)
                startActivity(intent)
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(
                    this,
                    it.message.toString(),
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    private fun edit() {
        animacao.tradeView(btn_edit, barraP)
        val desc = txt_desc.text.toString()

        if (selectedUri == null) {
            val portfolioItem = Portfolio(
                item.uuid, item.userUuid, item.photoUrl, desc
            )

            FirebaseFirestore.getInstance().collection("/portfolio")
                .document(item.userUuid!!)
                .collection("services")
                .document(item.uuid!!)
                .set(portfolioItem)
                .addOnSuccessListener {
                    Toast.makeText(this, "Alteração realizada!", Toast.LENGTH_SHORT)
                        .show()
                    val intent = Intent(this, PortPAct::class.java)
                    animacao.tradeView(barraP, btn_edit)
                    startActivity(intent)
                    finish()
                }
                .addOnFailureListener {
                    Toast.makeText(
                        this,
                        it.message.toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                    animacao.tradeView(barraP, btn_edit)
                }
        } else {
            val ref: StorageReference = FirebaseStorage.getInstance()
                .getReference("/images/services/${item.uuid}")
            ref.putFile(selectedUri!!)
                .addOnSuccessListener {
                    ref.downloadUrl.addOnSuccessListener { uri ->

                        val profileUrl = uri.toString()

                        val portfolioItem = Portfolio(
                            item.uuid, item.userUuid, profileUrl, desc
                        )

                        FirebaseFirestore.getInstance().collection("/portfolio")
                            .document(item.userUuid!!)
                            .collection("services")
                            .document(item.uuid!!)
                            .set(portfolioItem)
                            .addOnSuccessListener {
                                Toast.makeText(this, "Alteração realizada!", Toast.LENGTH_SHORT)
                                    .show()
                                val intent = Intent(this, PortPAct::class.java)
                                startActivity(intent)
                                finish()
                            }
                            .addOnFailureListener {
                                Toast.makeText(
                                    this,
                                    it.message.toString(),
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                            }
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(
                        this,
                        it.message.toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                }
        }
    }

    private fun setarCampos() {
        txt_desc.text = Editable.Factory.getInstance().newEditable(item.descricao)
        Picasso.get()
            .load(item.photoUrl)
            .into(img_foto)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 0) {
            selectedUri = data!!.data!!

            val bitmap: Bitmap
            try {
                val inputStream = contentResolver.openInputStream(selectedUri!!)
                bitmap = BitmapFactory.decodeStream(inputStream)
                img_foto.setImageBitmap(bitmap)
            } catch (e: Exception) {
            }
        }
    }

    private fun selectPhoto() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, 0)
    }
}