package com.iservice.iservice.classes

import android.os.Handler
import android.view.View
import android.view.animation.AlphaAnimation

class Animacao {
    var now: Boolean = false
    private fun fadeIn(view: View){
        val animation = AlphaAnimation(0f,1f)
        animation.duration = 500L
        animation.repeatCount = 0
        view.startAnimation(animation)
    }
    private fun fadeOut(view: View){
        val animation = AlphaAnimation(1f,0f)
        animation.duration = 500L
        animation.repeatCount = 0
        view.startAnimation(animation)
    }
    fun tradeView(view1: View, view2: View){
        fadeOut(view1)
        Handler().postDelayed({
            view1.visibility = View.INVISIBLE
            view2.visibility = View.VISIBLE
            fadeIn(view2)
            now = false
        }, 500L)
    }

    fun startView(view: View){
        Handler().postDelayed({
            view.visibility = View.VISIBLE
            fadeIn(view)
            now = false
        }, 500L)
    }

    fun stopView(view: View){
        fadeOut(view)
        Handler().postDelayed({
            view.visibility = View.INVISIBLE
            now = false
        }, 500L)
    }
}