package com.iservice.iservice.activities.profissional

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.iservice.iservice.R
import com.iservice.iservice.classes.Animacao
import com.iservice.iservice.classes.Portfolio
import com.iservice.iservice.classes.Users
import java.util.*

class ADDPortAct : AppCompatActivity() {
    private lateinit var btn_cadastrar: Button
    private lateinit var me: Users
    private lateinit var txt_detalhes: EditText
    private lateinit var img_foto: ImageView
    private lateinit var selectedUri: Uri
    private lateinit var barraP:ProgressBar
    var animacao = Animacao()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.act_addport)

        btn_cadastrar = findViewById(R.id.btn_addport_cadastrar)
        txt_detalhes = findViewById(R.id.txt_addpot_detalhes)
        img_foto = findViewById(R.id.img_addport_foto)
        barraP = findViewById(R.id.progress_addport)

        btn_cadastrar.setOnClickListener {
            FirebaseFirestore.getInstance().collection("/usersP")
                .document(FirebaseAuth.getInstance().uid!!)
                .get()
                .addOnSuccessListener {
                    me = it.toObject(Users::class.java)!!
                    addServico()
                }

        }

        img_foto.setOnClickListener {
            selectPhoto()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 0) {
            selectedUri = data!!.data!!

            val bitmap: Bitmap
            try {
                val inputStream = contentResolver.openInputStream(selectedUri)
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

    private fun addServico() {
        val detalhes = txt_detalhes.text.toString()
        val uid = me.uuid
        val filename = UUID.randomUUID().toString()

        if (selectedUri == null) {
            Toast.makeText(this, "Escolha uma foto!", Toast.LENGTH_SHORT)
                .show()
            return
        } else if(detalhes.isEmpty()){
            Toast.makeText(this, "Preencha a descrição!", Toast.LENGTH_SHORT)
                .show()
            return
        }
        animacao.tradeView(btn_cadastrar, barraP)
        val ref: StorageReference = FirebaseStorage.getInstance()
            .getReference("/images/services/$filename")
        ref.putFile(selectedUri)
            .addOnSuccessListener {
                ref.downloadUrl.addOnSuccessListener { uri ->
                    val photoUrl = uri.toString()
                    val portfolio = Portfolio(filename, uid, photoUrl, detalhes)

                    FirebaseFirestore.getInstance().collection("/portfolio")
                        .document(uid!!)
                        .collection("services")
                        .document(filename)
                        .set(portfolio)
                        .addOnSuccessListener {
                            animacao.tradeView(barraP, btn_cadastrar)
                            val intent = Intent(this, PortPAct::class.java)
                            startActivity(intent)
                            finish()
                        }
                        .addOnFailureListener {
                            animacao.tradeView(barraP, btn_cadastrar)
                            Toast.makeText(this, it.message.toString(), Toast.LENGTH_SHORT)
                                .show()
                        }
                }
            }
            .addOnFailureListener{
                animacao.tradeView(barraP, btn_cadastrar)
                Toast.makeText(this, it.message.toString(), Toast.LENGTH_SHORT).show()
            }
    }

}