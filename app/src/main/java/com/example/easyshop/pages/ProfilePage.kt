package com.example.easyshop.pages

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.easyshop.GlobalNavigation
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfilePage(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val sharedPref = context.getSharedPreferences("user_profile", Context.MODE_PRIVATE)
    val auth = FirebaseAuth.getInstance()
    val user = auth.currentUser

    var name by remember { mutableStateOf(sharedPref.getString("name", "User") ?: "User") }
    var address by remember { mutableStateOf(sharedPref.getString("address", "Not added") ?: "Not added") }
    var imageUri by remember { mutableStateOf(sharedPref.getString("imageUri", null)) }
    val email = user?.email ?: "No Email Available"

    val imagePicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            imageUri = it.toString()
            sharedPref.edit().putString("imageUri", it.toString()).apply()
        }
    }

    var showEditDialog by remember { mutableStateOf(false) }
    if (showEditDialog) {
        EditProfileDialog(
            name, address,
            onDismiss = { showEditDialog = false },
            onSave = { newName, newAddress ->
                name = newName
                address = newAddress
                sharedPref.edit()
                    .putString("name", newName)
                    .putString("address", newAddress)
                    .apply()
                showEditDialog = false
            }
        )
    }

    val scrollState = rememberScrollState()

    // collapsing header values
    val maxHeaderHeight = 280f
    val minHeaderHeight = 140f
    val scrollOffset = scrollState.value.toFloat().coerceIn(0f, maxHeaderHeight - minHeaderHeight)
    val headerHeight = (maxHeaderHeight - scrollOffset).coerceAtLeast(minHeaderHeight)
    val imageScale = ((headerHeight - minHeaderHeight) / (maxHeaderHeight - minHeaderHeight)).coerceIn(0f, 1f)
    val imageSize = 110.dp * (0.6f + 0.4f * imageScale)
    val textColor = if (imageScale > 0.3f) Color.White else MaterialTheme.colorScheme.onSurface
    val nameAlpha = 0.7f + (0.3f * imageScale)

    // animated gradient colors
    val infiniteTransition = rememberInfiniteTransition(label = "")
    val color1 by infiniteTransition.animateColor(
        initialValue = Color(0xFF6A11CB),
        targetValue = Color(0xFF2575FC),
        animationSpec = infiniteRepeatable(tween(6000, easing = LinearEasing), RepeatMode.Reverse),
        label = ""
    )
    val color2 by infiniteTransition.animateColor(
        initialValue = Color(0xFF00C6FF),
        targetValue = Color(0xFF0072FF),
        animationSpec = infiniteRepeatable(tween(8000, easing = LinearEasing), RepeatMode.Reverse),
        label = ""
    )

    // pulsing glow halo
    val glowTransition = rememberInfiniteTransition(label = "")
    val glowScale by glowTransition.animateFloat(
        0.9f, 1.2f,
        animationSpec = infiniteRepeatable(tween(2500, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = ""
    )
    val glowAlpha by glowTransition.animateFloat(
        0.3f, 0.6f,
        animationSpec = infiniteRepeatable(tween(2500, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = ""
    )

    Scaffold(modifier = modifier.fillMaxSize()) { padding ->
        Box(modifier = Modifier.fillMaxSize()) {

            // animated gradient header + subtle motion blur overlay
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(headerHeight.dp)
                    .graphicsLayer { translationY = scrollOffset * 0.3f } // parallax
                    .background(
                        Brush.linearGradient(listOf(color1, color2))
                    )
            ) {
                Box(
                    Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.25f))
                        .blur(50.dp) // soft blur for premium depth
                )
            }

            // scrollable content
            Column(
                modifier = Modifier
                    .verticalScroll(scrollState)
                    .padding(padding)
            ) {
                Spacer(Modifier.height(maxHeaderHeight.dp - 60.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                ) {
                    ElevatedCard(
                        shape = RoundedCornerShape(20.dp),
                        elevation = CardDefaults.cardElevation(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Text(
                                text = "Address",
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = address,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 14.sp
                            )
                            Spacer(Modifier.height(8.dp))
                            TextButton(onClick = { showEditDialog = true }) {
                                Text("Edit Address")
                            }
                        }
                    }

                    Spacer(Modifier.height(30.dp))
                    Text(
                        text = "Account Settings",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(Modifier.height(12.dp))

                    ProfileOption(Icons.Default.Menu, "My Orders") {
                        GlobalNavigation.navController.navigate("orders")
                    }
                    ProfileOption(Icons.Default.Settings, "Settings") { }
                    ProfileOption(Icons.Default.Info, "Help & Support") { }
                    ProfileOption(Icons.Default.ExitToApp, "Logout") {
                        auth.signOut()
                        GlobalNavigation.navController.navigate("auth") { popUpTo(0) }
                    }

                    Spacer(Modifier.height(50.dp))
                    Text(
                        text = "EasyShop • Version 1.0",
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(50.dp))
                }
            }

            // floating profile + glow
            Column(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = (headerHeight.dp - 150.dp).coerceAtLeast(40.dp)),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Box(
                        modifier = Modifier
                            .size(imageSize * glowScale)
                            .graphicsLayer { alpha = glowAlpha }
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(color1.copy(alpha = 0.5f), Color.Transparent)
                                ),
                                shape = CircleShape
                            )
                    )

                    AsyncImage(
                        model = ImageRequest.Builder(context)
                            .data(imageUri ?: "https://cdn-icons-png.flaticon.com/512/3135/3135715.png")
                            .crossfade(true)
                            .build(),
                        contentDescription = "Profile Photo",
                        modifier = Modifier
                            .size(imageSize)
                            .clip(CircleShape)
                            .shadow(8.dp, CircleShape),
                        contentScale = ContentScale.Crop
                    )

                    IconButton(
                        onClick = { imagePicker.launch("image/*") },
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .offset((-8).dp, (-8).dp)
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit Photo",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(Modifier.height(10.dp))
                Text(
                    text = name,
                    color = textColor.copy(alpha = nameAlpha),
                    fontWeight = FontWeight.Bold,
                    fontSize = (20.sp * (0.9f + 0.1f * imageScale)),
                )
                Text(
                    text = email,
                    color = textColor.copy(alpha = nameAlpha * 0.9f),
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
fun ProfileOption(icon: ImageVector, title: String, onClick: () -> Unit) {
    val scope = rememberCoroutineScope()
    val anim = remember { Animatable(1f) }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .graphicsLayer {
                scaleX = anim.value
                scaleY = anim.value
            }
            .clickable {
                scope.launch {
                    anim.animateTo(0.95f, tween(80))
                    anim.animateTo(1f, spring(stiffness = Spring.StiffnessLow))
                }
                onClick()
            }
            .shadow(4.dp, RoundedCornerShape(16.dp))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = title, tint = MaterialTheme.colorScheme.primary)
            }
            Spacer(Modifier.width(16.dp))
            Text(title, fontWeight = FontWeight.Medium, fontSize = 16.sp)
        }
    }
}

@Composable
fun EditProfileDialog(
    name: String,
    address: String,
    onDismiss: () -> Unit,
    onSave: (String, String) -> Unit
) {
    var newName by remember { mutableStateOf(name) }
    var newAddress by remember { mutableStateOf(address) }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = { onSave(newName.trim(), newAddress.trim()) }) {
                Text("Save", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } },
        title = { Text("Edit Profile", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(value = newName, onValueChange = { newName = it }, label = { Text("Name") })
                OutlinedTextField(value = newAddress, onValueChange = { newAddress = it }, label = { Text("Address") })
            }
        },
        shape = RoundedCornerShape(20.dp),
        containerColor = MaterialTheme.colorScheme.surface
    )
}
