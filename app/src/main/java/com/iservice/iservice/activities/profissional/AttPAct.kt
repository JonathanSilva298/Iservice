package com.iservice.iservice.activities.profissional

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.widget.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.iservice.iservice.R
import com.iservice.iservice.classes.Animacao
import com.iservice.iservice.classes.MaskEditUtil
import com.iservice.iservice.classes.Users
import com.squareup.picasso.Picasso
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

class AttPAct : AppCompatActivity() {
    private lateinit var txt_cep: EditText
    private lateinit var txt_nome: EditText
    private lateinit var btn_atualizar: Button
    private var selectedUri: Uri? = null
    private lateinit var img_foto: ImageView
    private lateinit var barraP: ProgressBar
    private lateinit var user: Users
    private var user_cep: String = ""
    private var user_logradouro: String = ""
    private var user_bairro: String = ""
    private var user_cidade: String = ""
    private var user_estado: String = ""
    val animacao = Animacao()
    val edit = MaskEditUtil()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.act_att_p)

        txt_nome = findViewById(R.id.txt_att_p_name)
        txt_cep = findViewById(R.id.txt_att_p_cep)
        btn_atualizar = findViewById(R.id.btn_att_p_edit)
        img_foto = findViewById(R.id.img_att_p_foto)
        barraP = findViewById(R.id.progress_att_p)
        txt_cep.addTextChangedListener(edit.mask(txt_cep, edit.FORMAT_CEP))

        user = intent.extras?.getParcelable("user")!!

        img_foto.setOnClickListener {
            selectPhoto()
        }

        btn_atualizar.setOnClickListener {
            if (txt_cep.text.toString().length != 9 || txt_nome.text.isEmpty()) {
                Toast.makeText(
                    this, "Todos campos devem ser preenchidos!",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                animacao.tradeView(btn_atualizar, barraP)
                getCep(txt_cep.text.toString())
            }
        }

        setarCampos()
    }

    private fun atualizar() {
        val nome = txt_nome.text.toString()

        if (selectedUri == null) {
            val user_profissional = Users(
                user.uuid, nome, user.profileUrl, user.cpf, user.dataNasc,
                user.accountType, user_cep, user_logradouro, user_bairro, user_cidade,
                user_estado
            )

            FirebaseFirestore.getInstance().collection("usersP")
                .document(user.uuid!!)
                .set(user_profissional)
                .addOnSuccessListener {
                    animacao.tradeView(barraP, btn_atualizar)
                    Toast.makeText(this, "Alteração realizada!", Toast.LENGTH_SHORT)
                        .show()
                    val intent = Intent(this, MainPAct::class.java)

                    intent.flags =
                        Intent.FLAG_ACTIVITY_CLEAR_TASK or (Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                }
                .addOnFailureListener {
                    animacao.tradeView(barraP, btn_atualizar)
                    Toast.makeText(this, it.message.toString(), Toast.LENGTH_SHORT).show()
                }
        } else {
            val ref: StorageReference = FirebaseStorage.getInstance()
                .getReference("/images/${user.uuid}")
            ref.putFile(selectedUri!!)
                .addOnSuccessListener {
                    ref.downloadUrl.addOnSuccessListener { uri ->

                        val profileUrl = uri.toString()

                        val user_profissional = Users(
                            user.uuid, nome, profileUrl, user.dataNasc,
                            user.accountType, user_cep, user_logradouro, user_bairro, user_cidade,
                            user_estado
                        )

                        FirebaseFirestore.getInstance().collection("usersP")
                            .document(user.uuid!!)
                            .set(user_profissional)
                            .addOnSuccessListener {
                                animacao.tradeView(barraP, btn_atualizar)
                                Toast.makeText(this, "Alteração realizada!", Toast.LENGTH_SHORT)
                                    .show()
                                val intent = Intent(this, MainPAct::class.java)

                                intent.flags =
                                    Intent.FLAG_ACTIVITY_CLEAR_TASK or (Intent.FLAG_ACTIVITY_NEW_TASK)
                                startActivity(intent)
                            }
                            .addOnFailureListener {
                                animacao.tradeView(barraP, btn_atualizar)
                                Toast.makeText(this, it.message.toString(), Toast.LENGTH_SHORT)
                                    .show()
                            }
                    }
                }
                .addOnFailureListener {
                    animacao.tradeView(barraP, btn_atualizar)
                    Toast.makeText(this, it.message.toString(), Toast.LENGTH_SHORT).show()
                }
        }

    }

    fun getCep(cep: String) {
        val url = "https://viacep.com.br/ws/$cep/json/"
        MyAsyncTask().execute(url)
    }

    inner class MyAsyncTask : AsyncTask<String, String, String>() {
        override fun onPreExecute() {
        }

        override fun doInBackground(vararg params: String?): String {
            try {
                val url = URL(params[0])
                val urlConnection = url.openConnection() as HttpURLConnection
                urlConnection.connectTimeout = 7000
                val instring = ConvertStreamString(urlConnection.inputStream)
                publishProgress(instring)
            } catch (ex: Exception) {
                animacao.tradeView(barraP, btn_atualizar)
                Toast.makeText(this@AttPAct, ex.message.toString(), Toast.LENGTH_SHORT)
                    .show()
            }
            return ""
        }

        override fun onProgressUpdate(vararg params: String?) {
            try {
                val json = JSONObject(params[0])
                user_cep = json.getString("cep")
                user_logradouro = json.getString("logradouro")
                user_bairro = json.getString("bairro")
                user_cidade = json.getString("localidade")
                user_estado = json.getString("uf")

                atualizar()

            } catch (ex: Exception) {
                animacao.tradeView(barraP, btn_atualizar)
                Toast.makeText(this@AttPAct, ex.message.toString(), Toast.LENGTH_SHORT)
                    .show()
            }
        }

        fun ConvertStreamString(inputStream: InputStream): String {
            val reader = BufferedReader(inputStream.reader())
            val content = StringBuilder()
            var line = reader.readLine()
            reader.use {
                while (line != null) {
                    content.append(line)
                    line = it.readLine()
                }
            }
            return content.toString()
        }
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

    private fun setarCampos() {
        txt_nome.text = Editable.Factory.getInstance().newEditable(user.username)
        txt_cep.text = Editable.Factory.getInstance().newEditable(user.cep)
        Picasso.get()
            .load(user.profileUrl)
            .into(img_foto)
    }
}