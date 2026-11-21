package com.zenix.launcher

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL

val ZenixYellow = Color(0xFFFFD700)
val ZenixBlack = Color(0xFF0B0B0B)
val ZenixCard = Color(0xE61A1A1A)

data class McVersion(val id: String, val type: String)

suspend fun loadVersions(): List<McVersion> = withContext(Dispatchers.IO) {
    val text = URL("https://piston-meta.mojang.com/mc/game/version_manifest_v2.json").readText()
    val arr = JSONObject(text).getJSONArray("versions")
    List(arr.length()) {
        val o = arr.getJSONObject(it)
        McVersion(o.getString("id"), o.getString("type"))
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { ZenixApp() }
    }
}

@Composable
fun ZenixApp() {
    var tab by remember { mutableIntStateOf(0) }
    val items = listOf(
        Pair("Home", Icons.Filled.Home),
        Pair("Accounts", Icons.Filled.Person),
        Pair("Mods", Icons.Filled.Extension),
        Pair("Settings", Icons.Filled.Settings)
    )
    Scaffold(
        containerColor = ZenixBlack,
        bottomBar = {
            NavigationBar(containerColor = Color(0xFF121212)) {
                items.forEachIndexed { i, item ->
                    NavigationBarItem(
                        selected = tab == i,
                        onClick = { tab = i },
                        icon = { Icon(item.second, contentDescription = null) },
                        label = { Text(item.first, fontSize = 12.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.Black,
                            selectedTextColor = ZenixYellow,
                            indicatorColor = ZenixYellow,
                            unselectedIconColor = Color.Gray,
                            unselectedTextColor = Color.Gray
                        )
                    )
                }
            }
        }
    ) { pad ->
        Box(Modifier.padding(pad)) {
            if (tab == 0) HomeScreen() else Placeholder(items[tab].first)
        }
    }
}

@Composable
fun Placeholder(name: String) {
    Box(
        Modifier.fillMaxSize().background(ZenixBlack),
        contentAlignment = Alignment.Center
    ) {
        Text("$name tez orada", color = Color.Gray, fontSize = 18.sp)
    }
}

@Composable
fun HomeScreen() {
    var versions by remember { mutableStateOf(emptyList<McVersion>()) }
    var selected by remember { mutableStateOf("1.21.11") }
    var showSnapshots by remember { mutableStateOf(false) }
    var menuOpen by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        try {
            versions = loadVersions()
        } catch (e: Exception) {
            error = true
        }
    }
    val shown = versions.filter { showSnapshots || it.type == "release" }

    Box(Modifier.fillMaxSize().background(ZenixBlack)) {

        // Fon rasm (tiniq)
        Image(
            painter = painterResource(R.drawable.zenix_bg),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        // Faqat pastki qismi qorong'i, tepasi tiniq
        Box(
            Modifier.fillMaxSize().background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0x00000000),
                        Color(0x00000000),
                        Color(0xCC000000)
                    )
                )
            )
        )

        Column(Modifier.fillMaxSize().padding(20.dp)) {

            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Surface(shape = RoundedCornerShape(50), color = ZenixCard) {
                    Row(
                        Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            Modifier.size(28.dp).clip(CircleShape).background(ZenixYellow),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Z", color = Color.Black, fontWeight = FontWeight.Black)
                        }
                        Spacer(Modifier.width(8.dp))
                        Text("Player", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
                Surface(shape = RoundedCornerShape(50), color = ZenixCard) {
                    Row(
                        Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(Modifier.size(8.dp).clip(CircleShape).background(Color(0xFF2ECC71)))
                        Spacer(Modifier.width(6.dp))
                        Text("Renderer Auto", color = Color.LightGray, fontSize = 12.sp)
                    }
                }
            }

            Spacer(Modifier.weight(1f))

            Text(
                "ZENIX",
                style = TextStyle(
                    color = ZenixYellow,
                    fontSize = 64.sp,
                    fontWeight = FontWeight.Black,
                    shadow = Shadow(color = Color.Black, blurRadius = 24f)
                )
            )
            Text("Zenix Launcher", color = Color.LightGray, fontSize = 14.sp)

            Spacer(Modifier.height(24.dp))

            Surface(
                shape = RoundedCornerShape(24.dp),
                color = ZenixCard,
                border = BorderStroke(1.dp, ZenixYellow.copy(alpha = 0.35f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(Modifier.padding(20.dp)) {
                    Text(
                        "$selected Fabric",
                        color = Color.White,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        if (error) "Internet yo'q, versiyalar yuklanmadi"
                        else "Fabric  |  Java 21  |  ${shown.size} ta versiya",
                        color = ZenixYellow,
                        fontSize = 13.sp
                    )
                    Spacer(Modifier.height(16.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box {
                            OutlinedButton(
                                onClick = { menuOpen = true },
                                border = BorderStroke(1.dp, ZenixYellow)
                            ) {
                                Text(selected, color = ZenixYellow)
                                Icon(
                                    Icons.Filled.KeyboardArrowDown,
                                    contentDescription = null,
                                    tint = ZenixYellow
                                )
                            }
                            DropdownMenu(
                                expanded = menuOpen,
                                onDismissRequest = { menuOpen = false },
                                modifier = Modifier.heightIn(max = 400.dp)
                            ) {
                                shown.forEach { v ->
                                    DropdownMenuItem(
                                        text = { Text(v.id) },
                                        onClick = {
                                            selected = v.id
                                            menuOpen = false
                                        }
                                    )
                                }
                            }
                        }
                        Spacer(Modifier.width(12.dp))
                        FilterChip(
                            selected = showSnapshots,
                            onClick = { showSnapshots = !showSnapshots },
                            label = { Text("Snapshot") }
                        )
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Button(
                    onClick = { },
                    colors = ButtonDefaults.buttonColors(containerColor = ZenixYellow),
                    shape = RoundedCornerShape(32.dp),
                    modifier = Modifier.weight(1f).height(60.dp)
                ) {
                    Icon(Icons.Filled.PlayArrow, contentDescription = null, tint = Color.Black)
                    Spacer(Modifier.width(8.dp))
                    Text("PLAY", color = Color.Black, fontWeight = FontWeight.Black, fontSize = 22.sp)
                }
                Spacer(Modifier.width(12.dp))
                IconButton(
                    onClick = { },
                    modifier = Modifier.size(60.dp).clip(CircleShape).background(ZenixCard)
                ) {
                    Icon(Icons.Filled.Folder, contentDescription = null, tint = Color.White)
                }
                Spacer(Modifier.width(8.dp))
                IconButton(
                    onClick = { },
                    modifier = Modifier.size(60.dp).clip(CircleShape).background(ZenixCard)
                ) {
                    Icon(Icons.Filled.Tune, contentDescription = null, tint = Color.White)
                }
            }
        }
    }
}
