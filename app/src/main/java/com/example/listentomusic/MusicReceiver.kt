package com.example.listentomusic

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class MusicReceiver:BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.e(this.javaClass.simpleName,intent?.getIntExtra("action",0).toString())
        var intentService = Intent(context,MusicService::class.java)
        intentService.putExtra("action",intent?.getIntExtra("action",0))
        context?.startService(intentService)

    }
}