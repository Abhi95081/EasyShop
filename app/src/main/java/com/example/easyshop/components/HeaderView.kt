package com.example.easyshop.components

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.easyshop.GlobalNavigation
import com.google.firebase.auth.FirebaseAuth

@Composable
fun HeaderView(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val sharedPref = context.getSharedPreferences("user_profile", Context.MODE_PRIVATE)

    val user = FirebaseAuth.getInstance().currentUser
    val email = user?.email ?: "User"

    var name by remember { mutableStateOf(sharedPref.getString("name", "User") ?: "User") }

    LaunchedEffect(Unit) {
        name = sharedPref.getString("name", "User") ?: "User"
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(
                text = "Welcome back 👋",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
            Text(
                text = name.split(" ")[0],
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        IconButton(onClick = {
            val navController = GlobalNavigation.navController
            navController.navigate("search")
        }) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}
