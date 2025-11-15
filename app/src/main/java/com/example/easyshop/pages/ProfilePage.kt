package com.example.easyshop.pages

import android.content.Context
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.easyshop.GlobalNavigation
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.core.content.edit
import com.example.easyshop.GlobalNavigation.navController

// ---------- Helpers ----------
@Composable
private fun lerpDp(start: Dp, end: Dp, fraction: Float): Dp =
    (start.value + (end.value - start.value) * fraction).dp

// ---------- Main Profile Page (Avatar on RIGHT, floating + shrinking) ----------
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ProfilePage(modifier: Modifier = Modifier) {
    val ctx = LocalContext.current
    val prefs = ctx.getSharedPreferences("user_profile", Context.MODE_PRIVATE)
    val auth = FirebaseAuth.getInstance()
    val firebaseUser = auth.currentUser

    var name by remember { mutableStateOf(prefs.getString("name", "User") ?: "User") }
    var address by remember { mutableStateOf(prefs.getString("address", "Not added") ?: "Not added") }
    var imageUri by remember { mutableStateOf(prefs.getString("imageUri", null)) }
    val email = firebaseUser?.email ?: "No Email Available"

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            imageUri = it.toString()
            prefs.edit { putString("imageUri", imageUri) }
        }
    }

    var showEditDialog by remember { mutableStateOf(false) }
    var showLogoutConfirm by remember { mutableStateOf(false) }

    // dynamic color scheme
    val colorScheme = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        if (isSystemInDarkTheme()) dynamicDarkColorScheme(ctx) else dynamicLightColorScheme(ctx)
    } else if (isSystemInDarkTheme()) darkColorScheme() else lightColorScheme()

    // collapsing settings
    val scrollState = rememberScrollState()
    val maxHeaderHeight = 300f
    val minHeaderHeight = 96f
    val offset = scrollState.value.toFloat().coerceIn(0f, maxHeaderHeight - minHeaderHeight)
    val collapseFraction = (offset / (maxHeaderHeight - minHeaderHeight)).coerceIn(0f, 1f)

    // animated header gradient
    val infinite = rememberInfiniteTransition()
    val c1 by infinite.animateColor(
        initialValue = colorScheme.primary,
        targetValue = colorScheme.secondary,
        animationSpec = infiniteRepeatable(tween(6000), RepeatMode.Reverse)
    )
    val c2 by infinite.animateColor(
        initialValue = colorScheme.secondary,
        targetValue = colorScheme.tertiary,
        animationSpec = infiniteRepeatable(tween(8500), RepeatMode.Reverse)
    )

    val headerHeightDp = (maxHeaderHeight - offset).dp

    // avatar sizing and offsets (avatar will align to the right edge)
    val avatarStart = 120.dp
    val avatarEnd = 44.dp
    val avatarSizeDp = lerpDp(avatarStart, avatarEnd, collapseFraction)
    // vertical shift so avatar moves a bit up while collapsing
    val avatarOffsetY = lerpDp(0.dp, (-18).dp, collapseFraction)
    // horizontal shift — small nudge left when collapsed so it visually sits nicely
    val avatarOffsetX = lerpDp(0.dp, (-8).dp, collapseFraction)

    // title alpha: fade out large title as collapse progresses
    val titleAlpha = (1f - collapseFraction * 1.25f).coerceIn(0f, 1f)
    // collapsed small title alpha (for top bar)
    val collapsedTitleAlpha = collapseFraction.coerceIn(0f, 1f)

    Scaffold(containerColor = colorScheme.background) { padding ->
        Box(modifier = modifier.fillMaxSize().padding(padding)) {
            // Parallax header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(headerHeightDp)
                    .graphicsLayer { translationY = -offset * 0.25f }
                    .background(
                        Brush.linearGradient(
                            colors = listOf(c1.copy(alpha = 0.95f), c2.copy(alpha = 0.9f)),
                            start = Offset.Zero,
                            end = Offset(300f * (1 - collapseFraction), 200f * collapseFraction)
                        )
                    )
            ) {
                // subtle overlay
                Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.06f)))
            }

            // Top overlay bar that appears when collapsed
            TopAppBarOverlay(
                title = name,
                collapsedTitle = name,
                collapseFraction = collapseFraction,
                colorScheme = colorScheme,
                onBack = { /* no-op */ }
            )

            // Scrollable content
            Column(
                modifier = Modifier
                    .verticalScroll(scrollState)
                    .fillMaxSize()
            ) {
                // spacer so content begins after header
                Spacer(modifier = Modifier.height((maxHeaderHeight - avatarSizeDp.value).dp + 12.dp))

                Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                    // Address card
                    Card(
                        shape = RoundedCornerShape(18.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                        colors = CardDefaults.cardColors(containerColor = colorScheme.surface.copy(alpha = 0.98f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Address", fontWeight = FontWeight.Bold, color = colorScheme.primary, fontSize = 14.sp)
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(address, color = colorScheme.onSurfaceVariant)
                            Spacer(modifier = Modifier.height(8.dp))
                            TextButton(onClick = { showEditDialog = true }) {
                                Text("Edit", color = colorScheme.primary)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(22.dp))

                    Text("Account", fontWeight = FontWeight.SemiBold, color = colorScheme.onSurface, fontSize = 18.sp)
                    Spacer(modifier = Modifier.height(12.dp))

                    ProfileOption(
                        icon = Icons.Default.Menu,
                        title = "My Orders",
                        subtitle = "Track & manage",
                        colors = colorScheme
                    ) { GlobalNavigation.navController.navigate("orders") }

                    ProfileOption(
                        icon = Icons.Default.Settings,
                        title = "Settings",
                        subtitle = "Preferences & security",
                        colors = colorScheme
                    ) {GlobalNavigation.navController.navigate("setting") }

                    ProfileOption(
                        icon = Icons.Default.Info,
                        title = "Help & Support",
                        subtitle = "Contact & FAQs",
                        colors = colorScheme
                    ) {GlobalNavigation.navController.navigate("help")}

                    ProfileOption(
                        icon = Icons.Default.ExitToApp,
                        title = "Logout",
                        subtitle = "Sign out safely",
                        colors = colorScheme
                    ) { showLogoutConfirm = true }

                    Spacer(modifier = Modifier.height(36.dp))

                    Text(
                        text = "EasyShop • Version 1.0",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        color = colorScheme.onSurfaceVariant,
                        fontSize = 12.sp
                    )

                    Spacer(modifier = Modifier.height(80.dp))
                }
            }

            // Avatar + Name row placed near the top, avatar on the right.
            // We'll create a full-width Row and align avatar end.
            val pulse by infinite.animateFloat(initialValue = 0.9f, targetValue = 1.06f, animationSpec = infiniteRepeatable(tween(2200), RepeatMode.Reverse))

            // position the row vertically so it floats over header; use offsets calculated earlier
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    // top offset: center vertically relative to header minus half avatar
                    .offset(y = ((headerHeightDp - avatarSizeDp) / 2) + avatarOffsetY)
                    .padding(horizontal = 20.dp)
            ) {
                // Left: Name & Email (fades as collapse advances)
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .graphicsLayer { alpha = titleAlpha }
                ) {
                    Text(
                        text = name,
                        color = colorScheme.onSurface,
                        fontSize = lerpDp(20.dp, 16.dp, collapseFraction).value.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(text = email, color = colorScheme.onSurfaceVariant, fontSize = 13.sp)
                }

                // Right: Avatar with glow + edit overlay
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .offset(x = avatarOffsetX) // small horizontal nudging while collapsing
                ) {
                    // halo glow
                    Box(
                        modifier = Modifier
                            .size(avatarSizeDp * pulse)
                            .graphicsLayer { alpha = 0.22f }
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(listOf(colorScheme.primary.copy(alpha = 0.28f), Color.Transparent))
                            )
                    )

                    AsyncImage(
                        model = ImageRequest.Builder(ctx).data(imageUri ?: "https://cdn-icons-png.flaticon.com/512/3135/3135715.png").crossfade(true).build(),
                        contentDescription = "Profile avatar",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(avatarSizeDp)
                            .clip(CircleShape)
                            .border(2.dp, colorScheme.outline.copy(alpha = 0.16f), CircleShape)
                            .shadow(12.dp, CircleShape)
                            .clickable { launcher.launch("image/*") }
                    )

                    // small circular edit button overlapping the avatar bottom-right
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .offset(x = 6.dp, y = 6.dp)
                            .size((avatarSizeDp * 0.36f).coerceAtLeast(30.dp))
                            .clip(CircleShape)
                            .background(colorScheme.primaryContainer)
                            .border(1.dp, colorScheme.outline.copy(alpha = 0.12f), CircleShape)
                            .clickable { launcher.launch("image/*") },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit", tint = colorScheme.onPrimaryContainer, modifier = Modifier.size(16.dp))
                    }
                }
            }

            // Edit dialog
            if (showEditDialog) {
                EditProfileDialog(
                    name = name,
                    address = address,
                    onDismiss = { showEditDialog = false },
                    onSave = { n: String, a: String ->
                        name = n
                        address = a
                        prefs.edit { putString("name", n); putString("address", a) }
                        showEditDialog = false
                    },
                    colors = colorScheme
                )
            }

            // Logout confirmation
            if (showLogoutConfirm) {
                AlertDialog(
                    onDismissRequest = { showLogoutConfirm = false },
                    confirmButton = {
                        TextButton(onClick = {
                            auth.signOut()
                            GlobalNavigation.navController.navigate("auth") { popUpTo(0) }
                        }) { Text("Logout", color = colorScheme.error) }
                    },
                    dismissButton = {
                        TextButton(onClick = { showLogoutConfirm = false }) { Text("Cancel", color = colorScheme.primary) }
                    },
                    title = { Text("Sign out?", fontWeight = FontWeight.Bold) },
                    text = { Text("Do you really want to sign out?", color = colorScheme.onSurfaceVariant) },
                    containerColor = colorScheme.surface,
                    shape = RoundedCornerShape(14.dp)
                )
            }
        }
    }
}

// ---------- Top appbar overlay (appears when collapsed) ----------
@Composable
private fun TopAppBarOverlay(
    title: String,
    collapsedTitle: String,
    collapseFraction: Float,
    colorScheme: ColorScheme,
    onBack: () -> Unit
) {
    // show small top bar when collapsed enough
    val visible = collapseFraction > 0.45f
    val animatedAlpha by animateFloatAsState(if (visible) 1f else 0f, tween(320))
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(Color.Transparent)
            .padding(horizontal = 8.dp, vertical = 8.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxSize()) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = colorScheme.onSurface.copy(alpha = animatedAlpha))
            }
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = collapsedTitle,
                color = colorScheme.onSurface.copy(alpha = animatedAlpha),
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.alpha(animatedAlpha)
            )
        }
    }
}

// ---------- Profile option row ----------
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ProfileOption(
    icon: ImageVector,
    title: String,
    subtitle: String,
    colors: ColorScheme,
    onClick: () -> Unit
) {
    val scope = rememberCoroutineScope()
    var pressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(targetValue = if (pressed) 0.98f else 1f, spring(stiffness = Spring.StiffnessMedium))
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer { scaleX = scale; scaleY = scale }
            .clip(RoundedCornerShape(14.dp))
            .background(colors.surfaceColorAtElevation(1.dp))
            .border(1.dp, colors.outline.copy(alpha = 0.08f), RoundedCornerShape(14.dp))
            .combinedClickable(
                onClick = {
                    scope.launch {
                        pressed = true
                        onClick()
                        kotlinx.coroutines.delay(120)
                        pressed = false
                    }
                },
                onLongClick = {}
            )
            .padding(14.dp)
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
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(title, color = colors.onSurface, fontWeight = FontWeight.Medium)
                Text(subtitle, color = colors.onSurfaceVariant, fontSize = 12.sp)
            }
        }
    }
}

// ---------- Edit dialog ----------
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
                Text("Save", color = colors.primary, fontWeight = FontWeight.SemiBold)
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
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = newAddress,
                    onValueChange = { newAddress = it },
                    label = { Text("Address") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        containerColor = colors.surface,
        shape = RoundedCornerShape(14.dp)
    )
}
