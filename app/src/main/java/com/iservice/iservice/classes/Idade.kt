package com.iservice.iservice.classes

import java.util.*

class Idade {

    fun calculaIdade(dataNasc: Date): Int {

        val dataNascimento: Calendar = Calendar.getInstance()
        dataNascimento.time = dataNasc
        val hoje = Calendar.getInstance()

        var idade: Int = hoje.get(Calendar.YEAR) - dataNascimento.get(Calendar.YEAR)

        if (hoje.get(Calendar.MONTH) < dataNascimento.get(Calendar.MONTH)) {
            idade--
        } else {
            if(hoje.get(Calendar.MONTH) == dataNascimento.get(Calendar.MONTH)
                && hoje.get(Calendar.DAY_OF_MONTH) < dataNascimento.get(Calendar.DAY_OF_MONTH)){
                idade--
            }
        }

        return idade
    }
}