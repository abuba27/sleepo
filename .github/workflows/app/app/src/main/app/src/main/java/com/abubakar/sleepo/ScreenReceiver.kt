package com.abubakar.sleepo

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ScreenReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val type = when (intent.action) {
            Intent.ACTION_SCREEN_ON -> "ON"
            Intent.ACTION_SCREEN_OFF -> "OFF"
            Intent.ACTION_USER_PRESENT -> "ON"
            Intent.ACTION_BOOT_COMPLETED -> return
            else -> return
        }

        val dao = SleepoDatabase.get(context).screenEventDao()
        CoroutineScope(Dispatchers.IO).launch {
            dao.insert(ScreenEvent(timestamp = System.currentTimeMillis(), type = type))
        }
    }
}
