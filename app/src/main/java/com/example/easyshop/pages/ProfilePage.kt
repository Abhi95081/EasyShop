package com.example.easyshop.pages

import android.content.Context
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import androidx.core.view.WindowCompat
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.easyshop.GlobalNavigation
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class, ExperimentalFoundationApi::class)
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
    var showLogoutConfirm by remember { mutableStateOf(false) }

    // Dynamic Material You colors
    val dynamicColors = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        if (isSystemInDarkTheme()) dynamicDarkColorScheme(context)
        else dynamicLightColorScheme(context)
    } else if (isSystemInDarkTheme()) darkColorScheme() else lightColorScheme()

    val scrollState = rememberScrollState()
    val maxHeader = 260f
    val minHeader = 140f
    val offset = scrollState.value.toFloat().coerceIn(0f, maxHeader - minHeader)
    val headerHeight = (maxHeader - offset).coerceAtLeast(minHeader)
    val avatarScale = ((headerHeight - minHeader) / (maxHeader - minHeader)).coerceIn(0f, 1f)
    val avatarSize = 108.dp * (0.85f + 0.15f * avatarScale)

    val infinite = rememberInfiniteTransition(label = "")
    val shift by infinite.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(8000, easing = LinearEasing), RepeatMode.Reverse), label = ""
    )

    val headerBrush = Brush.linearGradient(
        colors = listOf(
            dynamicColors.primary.copy(alpha = 0.35f),
            dynamicColors.surface
        ),
        start = Offset.Zero,
        end = Offset(300f * shift, 400f * (1 - shift))
    )

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = dynamicColors.background
    ) { padding ->

        Box(modifier = Modifier.fillMaxSize()) {

            // Animated header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(headerHeight.dp)
                    .graphicsLayer { translationY = -offset * 0.25f }
                    .background(headerBrush)
            )

            Column(
                modifier = Modifier
                    .verticalScroll(scrollState)
                    .padding(padding)
            ) {
                Spacer(Modifier.height((maxHeader - 70).dp))

                Column(Modifier.padding(horizontal = 20.dp)) {
                    // Address card
                    ElevatedCard(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.elevatedCardColors(
                            containerColor = dynamicColors.surfaceColorAtElevation(2.dp)
                        ),
                        elevation = CardDefaults.cardElevation(4.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(Modifier.padding(18.dp)) {
                            Text("Address", fontWeight = FontWeight.Bold, color = dynamicColors.primary)
                            Spacer(Modifier.height(6.dp))
                            Text(address, color = dynamicColors.onSurfaceVariant, fontSize = 14.sp)
                            Spacer(Modifier.height(8.dp))
                            TextButton(
                                onClick = { showEditDialog = true },
                                colors = ButtonDefaults.textButtonColors(contentColor = dynamicColors.primary)
                            ) {
                                Text("Edit", fontWeight = FontWeight.Medium)
                            }
                        }
                    }

                    Spacer(Modifier.height(28.dp))
                    Text(
                        "Account",
                        color = dynamicColors.onSurface,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(Modifier.height(12.dp))

                    ProfileOption(Icons.Default.Menu, "My Orders", "Track & manage", dynamicColors) {
                        GlobalNavigation.navController.navigate("orders")
                    }
                    ProfileOption(Icons.Default.Settings, "Settings", "Preferences & security", dynamicColors) { }
                    ProfileOption(Icons.Default.AccountCircle, "Help & Support", "FAQs & contact", dynamicColors) { }
                    ProfileOption(Icons.Default.ExitToApp, "Logout", "Sign out of account", dynamicColors) {
                        showLogoutConfirm = true
                    }

                    Spacer(Modifier.height(40.dp))
                    Text(
                        "EasyShop • Version 1.0",
                        color = dynamicColors.onSurfaceVariant.copy(alpha = 0.7f),
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(60.dp))
                }
            }

            // Avatar section
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = (headerHeight.dp - (avatarSize / 2)))
            ) {
                Box(contentAlignment = Alignment.Center) {
                    val halo by infinite.animateFloat(
                        initialValue = 0.9f, targetValue = 1.1f,
                        animationSpec = infiniteRepeatable(
                            tween(2400, easing = FastOutSlowInEasing),
                            RepeatMode.Reverse
                        ), label = ""
                    )
                    Box(
                        modifier = Modifier
                            .size(avatarSize * halo)
                            .graphicsLayer { alpha = 0.3f }
                            .background(
                                Brush.radialGradient(
                                    listOf(dynamicColors.primary.copy(alpha = 0.2f), Color.Transparent)
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
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(avatarSize)
                            .clip(CircleShape)
                            .border(2.dp, dynamicColors.outline.copy(alpha = 0.2f), CircleShape)
                            .shadow(12.dp, CircleShape)
                    )

                    IconButton(
                        onClick = { imagePicker.launch("image/*") },
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .offset((-6).dp, (-6).dp)
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(dynamicColors.primaryContainer)
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit", tint = dynamicColors.onPrimaryContainer)
                    }
                }

                Spacer(Modifier.height(8.dp))
                Text(name, color = dynamicColors.onSurface, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Text(email, color = dynamicColors.onSurfaceVariant, fontSize = 13.sp)
            }

            // Edit dialog
            if (showEditDialog) {
                AnimatedVisibility(
                    visible = showEditDialog,
                    enter = fadeIn() + scaleIn(initialScale = 0.95f),
                    exit = fadeOut() + scaleOut(targetScale = 0.95f)
                ) {
                    EditProfileDialog(
                        name = name,
                        address = address,
                        onDismiss = { showEditDialog = false },
                        onSave = { n, a ->
                            name = n; address = a
                            sharedPref.edit().putString("name", n).putString("address", a).apply()
                            showEditDialog = false
                        },
                        colors = dynamicColors
                    )
                }
            }

            // Logout confirmation
            if (showLogoutConfirm) {
                AlertDialog(
                    onDismissRequest = { showLogoutConfirm = false },
                    confirmButton = {
                        TextButton(onClick = {
                            auth.signOut()
                            GlobalNavigation.navController.navigate("auth") { popUpTo(0) }
                        }) {
                            Text("Logout", color = dynamicColors.error, fontWeight = FontWeight.SemiBold)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showLogoutConfirm = false }) {
                            Text("Cancel", color = dynamicColors.primary)
                        }
                    },
                    title = { Text("Confirm Logout", color = dynamicColors.onSurface, fontWeight = FontWeight.Bold) },
                    text = { Text("Do you really want to sign out?", color = dynamicColors.onSurfaceVariant) },
                    containerColor = dynamicColors.surface,
                    shape = RoundedCornerShape(16.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ProfileOption(
    icon: ImageVector,
    title: String,
    subtitle: String,
    colors: ColorScheme,
    onClick: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val pressed = remember { mutableStateOf(false) }
    val scale = animateFloatAsState(if (pressed.value) 0.97f else 1f, spring(stiffness = Spring.StiffnessMedium))

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .graphicsLayer { scaleX = scale.value; scaleY = scale.value }
            .clip(RoundedCornerShape(14.dp))
            .background(colors.surfaceColorAtElevation(1.dp))
            .border(1.dp, colors.outlineVariant.copy(alpha = 0.2f), RoundedCornerShape(14.dp))
            .combinedClickable(
                onClick = {
                    scope.launch {
                        pressed.value = true
                        onClick()
                        kotlinx.coroutines.delay(120)
                        pressed.value = false
                    }
                },
                onLongClick = {}
            )
            .padding(horizontal = 14.dp, vertical = 14.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(colors.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = title, tint = colors.onPrimaryContainer)
            }
            Spacer(Modifier.width(14.dp))
            Column {
                Text(title, color = colors.onSurface, fontWeight = FontWeight.Medium)
                Spacer(Modifier.height(2.dp))
                Text(subtitle, color = colors.onSurfaceVariant, fontSize = 12.sp)
            }
        }
    }
}

@Composable
fun EditProfileDialog(
    name: String,
    address: String,
    onDismiss: () -> Unit,
    onSave: (String, String) -> Unit,
    colors: ColorScheme
) {
    var newName by remember { mutableStateOf(name) }
    var newAddress by remember { mutableStateOf(address) }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = { onSave(newName.trim(), newAddress.trim()) }) {
                Text("Save", fontWeight = FontWeight.SemiBold, color = colors.primary)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = colors.onSurfaceVariant)
            }
        },
        title = { Text("Edit Profile", color = colors.onSurface, fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = newName,
                    onValueChange = { newName = it },
                    label = { Text("Name") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = colors.onSurface,
                        unfocusedTextColor = colors.onSurface,
                        focusedBorderColor = colors.primary,
                        unfocusedBorderColor = colors.outline,
                        focusedLabelColor = colors.primary,
                        unfocusedLabelColor = colors.onSurfaceVariant
                    ),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = newAddress,
                    onValueChange = { newAddress = it },
                    label = { Text("Address") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = colors.onSurface,
                        unfocusedTextColor = colors.onSurface,
                        focusedBorderColor = colors.primary,
                        unfocusedBorderColor = colors.outline,
                        focusedLabelColor = colors.primary,
                        unfocusedLabelColor = colors.onSurfaceVariant
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        containerColor = colors.surface,
        shape = RoundedCornerShape(18.dp)
    )
}
