package com.legaltech.supervakalat

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class CaseModel(
    val id: String,
    val caseNumber: String,
    val parties: String,
    val serialNumber: Int,
    val stage: String
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    SuperVakalatScreen()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SuperVakalatScreen() {
    val context = LocalContext.current
    var currentItem by remember { mutableIntStateOf(16) }

    val cases = remember {
        mutableStateListOf(
            CaseModel("1", "WPA 4120/2026", "A. Roy vs State of W.B.", 18, "Motion / Admission"),
            CaseModel("2", "MAT 102/2026", "P. Sharma vs Union of India", 23, "Hearing"),
            CaseModel("3", "FMA 88/2025", "K. Sen vs CESC Ltd.", 31, "Urgent Notice")
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("SuperVakalat", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                        Text(
                            "Calcutta High Court • Live Board",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Court No. 08", fontWeight = FontWeight.Bold)
                            Box(
                                modifier = Modifier
                                    .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(8.dp))
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text("Running: #$currentItem", color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedButton(onClick = { if (currentItem > 1) currentItem-- }) {
                                Text("Previous")
                            }
                            Button(onClick = { currentItem++ }) {
                                Text("Next Item (Court Proceeding)")
                            }
                        }
                    }
                }
            }

            item {
                Text(
                    text = "Tracked Matters Today",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            items(cases) { matter ->
                val itemsAhead = matter.serialNumber - currentItem
                val isUrgent = itemsAhead in 1..4

                ElevatedCard(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.elevatedCardColors(
                        containerColor = if (isUrgent) Color(0xFFFFF3E0) else MaterialTheme.colorScheme.surface
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(matter.caseNumber, fontWeight = FontWeight.Bold)
                            Badge(containerColor = if (isUrgent) Color(0xFFE65100) else MaterialTheme.colorScheme.secondary) {
                                Text("Item #${matter.serialNumber}", color = Color.White)
                            }
                        }
                        Text(matter.parties, style = MaterialTheme.typography.bodyMedium)
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                if (itemsAhead > 0) "$itemsAhead items remaining"
                                else if (itemsAhead == 0) "NOW RUNNING!"
                                else "Matter Passed",
                                color = if (isUrgent) Color(0xFFE65100) else Color.Gray,
                                fontWeight = FontWeight.SemiBold
                            )

                            Button(
                                onClick = {
                                    val msg = "Update: Your case ${matter.caseNumber} is scheduled at Item #${matter.serialNumber}. Current court serial is #$currentItem."
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://api.whatsapp.com/send?text=${Uri.encode(msg)}"))
                                    context.startActivity(intent)
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366))
                            ) {
                                Text("WhatsApp Client", color = Color.White)
                            }
                        }
                    }
                }
            }
        }
    }
}
