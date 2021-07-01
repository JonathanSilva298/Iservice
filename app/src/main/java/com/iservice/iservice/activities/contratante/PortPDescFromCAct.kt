package com.iservice.iservice.activities.contratante

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import com.iservice.iservice.R
import com.iservice.iservice.classes.Portfolio
import com.squareup.picasso.Picasso

class PortPDescFromCAct : AppCompatActivity() {
    private lateinit var item: Portfolio
    private lateinit var txt_desc: TextView
    private lateinit var img_foto: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.act_port_pdesc_from_c)
        txt_desc = findViewById(R.id.txt_port_pfromc_desc)
        img_foto = findViewById(R.id.img_port_pfromc_desc)

        item = intent.extras?.getParcelable("item")!!

        setarCampos()
    }

    private fun setarCampos() {
        txt_desc.text = item.descricao
        Picasso.get()
            .load(item.photoUrl)
            .into(img_foto)
    }
}