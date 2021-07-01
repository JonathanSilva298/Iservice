package com.iservice.iservice.classes

import android.app.Activity
import android.app.Application
import android.app.Application.ActivityLifecycleCallbacks
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ChatApplication : Application(), ActivityLifecycleCallbacks {
     fun setOnline(enabled: Boolean) {
        val uid = FirebaseAuth.getInstance().uid
        if (uid != null) {
            FirebaseFirestore.getInstance().collection("usersP").document(uid)
                .update("online", enabled)
        }
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}
    override fun onActivityStarted(activity: Activity) {}
    override fun onActivityResumed(activity: Activity) {
        setOnline(true)
    }

    override fun onActivityPaused(activity: Activity) {
        setOnline(false)
    }

    override fun onActivityStopped(activity: Activity) {}
    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
    override fun onActivityDestroyed(activity: Activity) {}
}