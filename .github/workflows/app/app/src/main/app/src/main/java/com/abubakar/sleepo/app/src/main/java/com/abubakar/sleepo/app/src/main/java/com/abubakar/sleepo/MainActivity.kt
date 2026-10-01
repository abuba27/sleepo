package com.abubakar.sleepo

import android.Manifest
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private lateinit var dao: ScreenEventDao
    private val receiver = ScreenReceiver()

    private val notifPermLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dao = SleepoDatabase.get(this).screenEventDao()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            notifPermLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }

        setContent {
            MaterialTheme(colorScheme = darkColorScheme()) {
                SleepoScreen(dao)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_SCREEN_ON)
            addAction(Intent.ACTION_SCREEN_OFF)
            addAction(Intent.ACTION_USER_PRESENT)
        }
        registerReceiver(receiver, filter)
    }

    override fun onPause() {
        super.onPause()
        try { unregisterReceiver(receiver) } catch (_: Exception) {}
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SleepoScreen(dao: ScreenEventDao) {
    val events by dao.getAll().collectAsState(initial = emptyList())
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Sleepo", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = {
                        scope.launch { dao.clearAll() }
                    }) {
                        Icon(Icons.Default.Delete, contentDescription = "Clear all")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF1A1A2E),
                    titleContentColor = Color.White,
                    actionIconContentColor = Color.White
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(Modifier.height(8.dp))

            if (events.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "No events yet.\nTurn your screen off and on to start tracking.",
                        color = Color.Gray,
                        fontSize = 16.sp
                    )
                }
            } else {
                val lastOff = events.firstOrNull { it.type == "OFF" }
                val lastOn = events.firstOrNull { it.type == "ON" }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF16213E))
                ) {
                    Column(Modifier.padding(16.dp)) {
                        SummaryRow("Last screen OFF", lastOff?.timestamp)
                        Spacer(Modifier.height(8.dp))
                        SummaryRow("Last screen ON", lastOn?.timestamp)
                    }
                }

                Spacer(Modifier.height(16.dp))
                Text("Recent events", color = Color.White, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(8.dp))

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(events) { event ->
                        EventRow(event)
                    }
                }
            }
        }
    }
}

@Composable
fun SummaryRow(label: String, ts: Long?) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = Color.LightGray)
        Text(
            if (ts == null) "—" else "${EventFormatter.formatTime(ts)}  •  ${EventFormatter.formatDate(ts)}",
            color = Color.White,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun EventRow(event: ScreenEvent) {
    val isOn = event.type == "ON"
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isOn) Color(0xFF1F4068) else Color(0xFF2C2C54)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (isOn) Icons.Default.LightMode else Icons.Default.Bedtime,
                contentDescription = null,
                tint = if (isOn) Color(0xFFFFD166) else Color(0xFF8ECAE6)
            )
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    if (isOn) "Screen ON" else "Screen OFF",
                    color = Color.White,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    EventFormatter.formatDate(event.timestamp),
                    color = Color.Gray,
                    fontSize = 12.sp
                )
            }
            Text(
                EventFormatter.formatTime(event.timestamp),
                color = Color.White
            )
        }
    }
}
