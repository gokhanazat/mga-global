package com.mgacreative.globaltrade.ui.news

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Article
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mgacreative.globaltrade.openUrl
import com.mgacreative.globaltrade.getPlatform
import com.mgacreative.globaltrade.Platform
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import io.ktor.http.Url
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.launch

data class NewsItem(
    val title: String,
    val link: String,
    val description: String,
    val imageUrl: String?,
    val pubDate: String
)

fun parseRss(xml: String): List<NewsItem> {
    val items = mutableListOf<NewsItem>()
    val itemRegex = Regex("(?si)<item[^>]*>(.*?)</item>")

    val titleRegex = Regex("(?si)<title[^>]*>(.*?)</title>")
    val linkRegex = Regex("(?si)<link[^>]*>(.*?)</link>")
    val descRegex = Regex("(?si)<description[^>]*>(.*?)</description>")
    val pubDateRegex = Regex("(?si)<pubDate[^>]*>(.*?)</pubDate>")
    val enclosureRegex = Regex("(?si)<enclosure[^>]+url=\"(.*?)\"")
    val mediaRegex = Regex("(?si)<media:content[^>]+url=\"(.*?)\"")
    val itemImageRegex = Regex("(?si)<image[^>]*>(.*?)</image>")
    val imgTagRegex = Regex("(?si)<img[^>]+src=\"(.*?)\"")

    fun String.cleanCData(): String {
        return this.trim()
            .replace(Regex("^<!\\[CDATA\\["), "")
            .replace(Regex("\\]\\]>$"), "")
            .trim()
    }

    for (match in itemRegex.findAll(xml)) {
        val block = match.groupValues[1]

        val title = titleRegex.find(block)?.groupValues?.get(1)?.cleanCData()
            ?.replace("&amp;", "&")?.replace("&lt;", "<")?.replace("&gt;", ">")
            ?.replace("&quot;", "\"")?.replace("&apos;", "'")?.replace("&#39;", "'")
            ?.replace("&rsquo;", "'")?.replace("&lsquo;", "'")?.replace("&rdquo;", "\"")?.replace("&ldquo;", "\"")
            ?: continue

        val link = linkRegex.find(block)?.groupValues?.get(1)?.cleanCData() ?: ""

        val rawDesc = descRegex.find(block)?.groupValues?.get(1)?.cleanCData() ?: ""
        
        var imageUrl = (enclosureRegex.find(block)?.groupValues?.get(1)
            ?: mediaRegex.find(block)?.groupValues?.get(1)
            ?: itemImageRegex.find(block)?.groupValues?.get(1)
            ?: imgTagRegex.find(rawDesc)?.groupValues?.get(1)
            ?: imgTagRegex.find(block)?.groupValues?.get(1))
            ?.cleanCData()
            ?.trim()?.replace("\n", "")?.replace("\r", "")

        // Göreceli URL'leri tam URL'ye dönüştürelim ve protokolü kontrol edelim
        if (imageUrl != null) {
            imageUrl = imageUrl.trim()
            if (imageUrl.startsWith("//")) {
                imageUrl = "https:$imageUrl"
            } else if (imageUrl.startsWith("/")) {
                imageUrl = "https://www.bloomberght.com$imageUrl"
            }
        }

        if (imageUrl != null) {
            println("EconomicNews extracted image: $imageUrl")
        }

        val desc = rawDesc.replace(Regex("<[^>]+>"), "").trim()
            .replace("&amp;", "&").replace("&lt;", "<").replace("&gt;", ">")
            .replace("&quot;", "\"").replace("&apos;", "'")

        val pubDate = pubDateRegex.find(block)?.groupValues?.get(1)?.cleanCData() ?: ""

        if (title.isNotEmpty()) {
            items.add(NewsItem(
                title = title,
                link = link,
                description = desc,
                imageUrl = imageUrl,
                pubDate = pubDate
            ))
        }
    }
    return items
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EconomicNewsScreen(
    paddingValues: PaddingValues = PaddingValues(0.dp),
    onBack: () -> Unit = {}
) {
    val scope = rememberCoroutineScope()
    var newsList by remember { mutableStateOf<List<NewsItem>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var retryCount by remember { mutableStateOf(0) }

    LaunchedEffect(retryCount) {
        try {
            isLoading = true
            error = null
            
            val rssFeedUrl = "https://www.bloomberght.com/rss"
            val isWeb = getPlatform().name.lowercase().contains("web")
            val finalUrl = if (isWeb) {
                "https://api.codetabs.com/v1/proxy?quest=$rssFeedUrl"
            } else {
                rssFeedUrl
            }
            
            val client = HttpClient()
            try {
                val response: HttpResponse = client.get(finalUrl) {
                    headers {
                        append(HttpHeaders.UserAgent, "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
                        append("Referer", "https://www.bloomberght.com/")
                    }
                }
                val xml = response.bodyAsText()
                newsList = parseRss(xml)
            } finally {
                client.close()
            }
        } catch (e: Exception) {
            error = "Haberler yüklenemedi: ${e.message}"
        } finally {
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Ekonomi Haberleri",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, "Geri")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFF0F172A),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFF1F3F5)),
            contentAlignment = Alignment.TopCenter
        ) {
            BoxWithConstraints(
                modifier = Modifier
                    .widthIn(max = 1200.dp)
                    .fillMaxHeight()
            ) {
                val screenWidth = maxWidth
                when {
                    isLoading -> {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                CircularProgressIndicator(
                                    color = Color(0xFF0F172A),
                                    strokeWidth = 3.dp
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    "Bloomberg HT haberleri yükleniyor...",
                                    color = Color.Gray,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                    error != null -> {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(32.dp)
                            ) {
                                Icon(
                                    Icons.Default.Article,
                                    contentDescription = null,
                                    modifier = Modifier.size(64.dp),
                                    tint = Color.Gray
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    error ?: "",
                                    color = Color.Gray,
                                    style = MaterialTheme.typography.bodyMedium,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(24.dp))
                                Button(
                                    onClick = { retryCount++ },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F172A)),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("Tekrar Dene")
                                }
                            }
                        }
                    }
                    newsList.isEmpty() -> {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("Haber bulunamadı.", color = Color.Gray)
                        }
                    }
                    else -> {
                        val columns = when {
                            screenWidth < 600.dp -> 1
                            screenWidth < 1100.dp -> 2
                            else -> 3
                        }
                        
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(columns),
                            contentPadding = PaddingValues(16.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(newsList) { news ->
                                NewsListCard(
                                    newsItem = news,
                                    onClick = {
                                        if (news.link.isNotEmpty()) {
                                            openUrl(news.link)
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NewsListCard(
    newsItem: NewsItem,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = Color(0xFF1E293B).copy(alpha = 0.05f)
                ) {
                    Text(
                        "BLOOMBERG",
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF475569)
                    )
                }
                
                Text(
                    text = newsItem.pubDate,
                    fontSize = 10.sp,
                    color = Color.Gray,
                    fontWeight = FontWeight.Medium
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = newsItem.title,
                fontSize = 17.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF0F172A),
                lineHeight = 24.sp
            )
            
            if (newsItem.description.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = newsItem.description,
                    fontSize = 14.sp,
                    color = Color(0xFF64748B),
                    maxLines = 4,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 20.sp
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Haberin Devamı →",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF3B82F6)
            )
        }
    }
}
