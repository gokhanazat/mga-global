package com.mgacreative.mgaglobal.ui.news

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.Article
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import io.ktor.http.Url
import io.ktor.client.request.header
import com.mgacreative.mgaglobal.openUrl

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewsDetailScreen(
    newsItem: NewsItem,
    onBack: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Bloomberg HT",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, "Geri", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF0F172A),
                    titleContentColor = Color.White
                ),
                actions = {
                    if (newsItem.link.isNotEmpty()) {
                        IconButton(onClick = { openUrl(newsItem.link) }) {
                            Icon(Icons.Default.OpenInBrowser, "Tarayıcıda Aç", tint = Color.White)
                        }
                    }
                }
            )
        }
    ) { padding ->
        if (newsItem.link.isNotEmpty()) {
            // Show full article in WebView
            // Use the cross-platform WebView
            NewsWebView(
                url = newsItem.link,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            )
        } else {
            // Fallback: show text content
            val scroll = rememberScrollState()
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(scroll)
                    .padding(24.dp)
            ) {
                if (newsItem.pubDate.isNotEmpty()) {
                    Text(newsItem.pubDate, fontSize = 11.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.height(8.dp))
                }
                Text(
                    newsItem.title,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    newsItem.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.DarkGray,
                    lineHeight = 22.sp
                )
            }
        }
    }
}

