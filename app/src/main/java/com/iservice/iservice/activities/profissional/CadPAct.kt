package com.iservice.iservice.activities.profissional

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.iservice.iservice.R
import com.iservice.iservice.classes.Animacao
import com.iservice.iservice.classes.CPFUtil
import com.iservice.iservice.classes.MaskEditUtil
import com.iservice.iservice.classes.Users
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.*

class CadPAct : AppCompatActivity() {
    private lateinit var txt_nome: EditText
    private lateinit var txt_email: EditText
    private lateinit var txt_senha: EditText
    private lateinit var btn_cadastrar: Button
    private lateinit var btn_foto: Button
    private lateinit var selectedUri: Uri
    private lateinit var img_foto: ImageView
    private lateinit var txt_cep: EditText
    private lateinit var txt_cpf: EditText
    private lateinit var txt_dataNasc: EditText
    private lateinit var barraP: ProgressBar
    private var user_cep: String = ""
    private var user_logradouro: String = ""
    private var user_bairro: String = ""
    private var user_cidade: String = ""
    private var user_estado: String = ""
    val animacao = Animacao()
    val edit = MaskEditUtil()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.act_cad_p)

        txt_nome = findViewById(R.id.txt_cadprofissional_nome)
        txt_email = findViewById(R.id.txt_cadprofissional_email)
        txt_senha = findViewById(R.id.txt_cadprofissional_senha)
        txt_cep = findViewById(R.id.txt_cadprofissional_cep)
        txt_cpf = findViewById(R.id.txt_cadprofissional_cpf)
        txt_dataNasc = findViewById(R.id.txt_cadprofissional_nascimento)
        btn_cadastrar = findViewById(R.id.btn_cadprofissional_enter)
        btn_foto = findViewById(R.id.btn_cadprofissional_foto)
        img_foto = findViewById(R.id.img_cadprofissional_foto)
        barraP = findViewById(R.id.progress_p)
        txt_cep.addTextChangedListener(edit.mask(txt_cep, edit.FORMAT_CEP))
        txt_dataNasc.addTextChangedListener(edit.mask(txt_dataNasc, edit.FORMAT_DATE))
        txt_cpf.addTextChangedListener(edit.mask(txt_cpf, edit.FORMAT_CPF))

        btn_foto.setOnClickListener {
            selectPhoto()
        }

        btn_cadastrar.setOnClickListener {
            if (txt_cep.text.toString().length != 9 || txt_dataNasc.text.toString().length != 10) {
                Toast.makeText(
                    this, "Todos campos devem ser preenchidos!",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                if (CPFUtil.myValidateCPF(txt_cpf.text.toString())) {
                    createUser()
                } else {
                    Toast.makeText(
                        this, "CPF Inválido!",
                        Toast.LENGTH_LONG
                    ).show()
                }
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
                animacao.tradeView(barraP, btn_cadastrar)
                Toast.makeText(this@CadPAct, ex.message.toString(), Toast.LENGTH_SHORT)
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

                saveUserInFireBase()

            } catch (ex: Exception) {
                animacao.tradeView(barraP, btn_cadastrar)
                Toast.makeText(this@CadPAct, ex.message.toString(), Toast.LENGTH_SHORT)
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
                val inputStream = contentResolver.openInputStream(selectedUri)
                bitmap = BitmapFactory.decodeStream(inputStream)
                img_foto.setImageBitmap(bitmap)
                btn_foto.alpha = 0F
            } catch (e: Exception) {
            }
        }
    }

    private fun selectPhoto() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, 0)
    }

    private fun createUser() {
        val nome = txt_nome.text.toString()
        val email = txt_email.text.toString()
        val senha = txt_senha.text.toString()


        if (nome.isEmpty() || email.isEmpty() || senha.isEmpty()) {
            Toast.makeText(this, "Todos campos devem ser preenchidos!", Toast.LENGTH_SHORT)
                .show()
            return
        }
        if (selectedUri == null) {
            Toast.makeText(this, "Escolha uma foto!", Toast.LENGTH_SHORT)
                .show()
            return
        }
        animacao.tradeView(btn_cadastrar, barraP)
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, senha)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    getCep(txt_cep.text.toString())
                }
            }
            .addOnFailureListener {
                animacao.tradeView(barraP, btn_cadastrar)
                Toast.makeText(this, it.message.toString(), Toast.LENGTH_SHORT)
                    .show()
            }
    }

    private fun saveUserInFireBase() {
        val filename = UUID.randomUUID().toString()
        val ref: StorageReference = FirebaseStorage.getInstance().getReference("/images/$filename")
        ref.putFile(selectedUri)
            .addOnSuccessListener {
                ref.downloadUrl.addOnSuccessListener { uri ->

                    val uid = FirebaseAuth.getInstance().uid.toString()
                    val nome = txt_nome.text.toString()
                    val nascimento = txt_dataNasc.text.toString()
                    val cpf = txt_cpf.text.toString()
                    val profileUrl = uri.toString()
                    val type = "Profissional"

                    val user_profissional = Users(
                        uid, nome, profileUrl,cpf, nascimento, type, user_cep,
                        user_logradouro, user_bairro, user_cidade, user_estado
                    )

                    FirebaseFirestore.getInstance().collection("usersP")
                        .document(uid)
                        .set(user_profissional)
                        .addOnSuccessListener {
                            animacao.tradeView(barraP, btn_cadastrar)

                            val intent = Intent(this, MainPAct::class.java)

                            intent.flags =
                                Intent.FLAG_ACTIVITY_CLEAR_TASK or (Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                        }
                        .addOnFailureListener {
                            animacao.tradeView(barraP, btn_cadastrar)
                            Toast.makeText(this, it.message.toString(), Toast.LENGTH_SHORT)
                                .show()
                        }
                }
            }
            .addOnFailureListener {
                animacao.tradeView(barraP, btn_cadastrar)
                Toast.makeText(this, it.message.toString(), Toast.LENGTH_SHORT).show()
            }
    }
}
