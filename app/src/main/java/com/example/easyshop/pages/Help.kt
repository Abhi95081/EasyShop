package com.example.easyshop.pages

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun Help(modifier: Modifier = Modifier) {

    Column(
        modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text(
            text = "Help & Support",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 20.dp)
        )

        // ---------------- FAQ Section ----------------
        Text(
            text = "FAQs",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(vertical = 10.dp)
        )

        HelpItem(
            title = "How to use the app?",
            onClick = { /* TODO: Show details */ }
        )

        HelpItem(
            title = "How to place an order?",
            onClick = { /* TODO */ }
        )

        HelpItem(
            title = "How to track my order?",
            onClick = { /* TODO */ }
        )

        Divider(Modifier.padding(vertical = 12.dp))

        // ---------------- Contact Section ----------------
        Text(
            text = "Contact Us",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(vertical = 10.dp)
        )

        ContactRow(
            icon = Icons.Default.Email,
            text = "Email Support",
            onClick = { /* TODO: open email */ }
        )

        ContactRow(
            icon = Icons.Default.Phone,
            text = "Call Support",
            onClick = { /* TODO: dial number */ }
        )

        ContactRow(
            icon = Icons.Default.Face,
            text = "Live Chat",
            onClick = { /* TODO */ }
        )
    }
}

@Composable
fun HelpItem(title: String, onClick: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(title)
        Icon(Icons.Default.ArrowForward, contentDescription = null)
    }
}

@Composable
fun ContactRow(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String, onClick: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 14.dp)
    ) {
        Icon(icon, contentDescription = null)
        Spacer(Modifier.width(12.dp))
        Text(text)
    }
}
