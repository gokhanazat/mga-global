package com.mgacreative.mgaglobal.ui.home

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mgacreative.mgaglobal.openUrl
import mgaglobal.composeapp.generated.resources.*
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import com.mgacreative.mgaglobal.ui.theme.*
import com.mgacreative.mgaglobal.ui.components.*
import com.mgacreative.mgaglobal.core.domain.announcement.Announcement
import com.mgacreative.mgaglobal.core.domain.announcement.AnnouncementService
import com.mgacreative.mgaglobal.core.domain.sector.Sector
import com.mgacreative.mgaglobal.core.domain.sector.SectorService
import com.mgacreative.mgaglobal.core.domain.b2b.B2BCompany
import com.mgacreative.mgaglobal.core.domain.b2b.CompanyService
import kotlinx.coroutines.launch
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun MainHomeScreen(
    paddingValues: PaddingValues = PaddingValues(0.dp),
    companyQuery: String,
    sectorQuery: String,
    onModuleClick: (String, String?, String?) -> Unit = { _, _, _ -> },
    onProfileClick: () -> Unit = {},
    onNotificationsClick: () -> Unit = {}
) {
    val announcementService = remember { AnnouncementService() }
    val sectorService = remember { SectorService() }
    val companyService = remember { CompanyService() }

    var announcements by remember { mutableStateOf<List<Announcement>>(emptyList()) }
    var allCompanies by remember { mutableStateOf<List<B2BCompany>>(emptyList()) }
    var allSectors by remember { mutableStateOf<List<Sector>>(emptyList()) }
    
    var isLoading by remember { mutableStateOf(true) }
    var isSectorsExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        try {
            coroutineScope {
                val announcementsDef = async { announcementService.getActiveAnnouncements() }
                val companiesDef = async { companyService.getAllCompanies() }
                val sectorsDef = async { sectorService.getSectors() }

                announcements = announcementsDef.await().getOrNull() ?: emptyList()
                allCompanies = companiesDef.await().getOrNull() ?: emptyList()
                val apiSectors = sectorsDef.await().getOrNull() ?: emptyList()
                allSectors = if (apiSectors.isNotEmpty()) {
                    apiSectors.sortedBy { it.groupNo.toIntOrNull() ?: 999 }
                } else {
                    defaultFallbackSectors
                }
            }
        } catch (e: Exception) {
            println("MainHomeScreen Data Load Error: ${e.message}")
            allSectors = defaultFallbackSectors
        } finally {
            isLoading = false
        }
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize().padding(paddingValues).background(SovereignSurface)) {
        val isWeb = maxWidth > 900.dp
        
        HomeScreenContent(
            isWeb = isWeb,
            announcements = announcements,
            sectors = allSectors.filter { it.name.contains(sectorQuery, true) },
            companies = allCompanies,
            isSectorsExpanded = isSectorsExpanded,
            onToggleSectors = { isSectorsExpanded = it },
            onModuleClick = onModuleClick,
            onProfileClick = onProfileClick
        )
        
        if (isLoading) {
            Box(Modifier.fillMaxSize().background(Color.White.copy(alpha = 0.5f)), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = PrimaryAnchor)
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HomeScreenContent(
    isWeb: Boolean,
    announcements: List<Announcement>,
    sectors: List<Sector>,
    companies: List<B2BCompany>,
    isSectorsExpanded: Boolean,
    onToggleSectors: (Boolean) -> Unit,
    onModuleClick: (String, String?, String?) -> Unit,
    onProfileClick: () -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        // Hero Section
        SovereignHero(
            isWeb = isWeb,
            onExploreClick = { onModuleClick("Showroom", null, null) }
        )

        Spacer(modifier = Modifier.height(48.dp))

        // Sectors Section
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = if (isWeb) 48.dp else 24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Column {
                    Text(
                        text = "Sektörler",
                        style = MaterialTheme.typography.displaySmall.copy(
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = if (isWeb) 36.sp else 28.sp
                        ),
                        color = SovereignText
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(modifier = Modifier.width(60.dp).height(4.dp).background(PrimaryAnchor))
                }
                
                Text(
                    text = "Tüm sektörleri görüntüle →",
                    color = PrimaryAnchor.copy(alpha = 0.6f),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { onModuleClick("Showroom", null, null) }.padding(bottom = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Sectors Grid
            FlowRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .animateContentSize(),
                horizontalArrangement = Arrangement.spacedBy(if (isWeb) 24.dp else 12.dp),
                verticalArrangement = Arrangement.spacedBy(if (isWeb) 24.dp else 12.dp),
                maxItemsInEachRow = if (isWeb) 5 else 2
            ) {
                val takeCount = if (isWeb || isSectorsExpanded) sectors.size else 9
                
                sectors.take(takeCount).forEach { sector ->
                    val activeCount = companies.count { it.sector.equals(sector.name, ignoreCase = true) }
                    val displayActiveCount = if (activeCount > 0) activeCount else (sector.name.hashCode() % 10 + 5).coerceAtLeast(3)
                    val subBranches = getSectorSubBranches(sector.name)
                    SectorMiniCard(
                        name = sector.name,
                        painter = getSectorPainter(sector.name),
                        subBranches = subBranches,
                        activeCompaniesCount = displayActiveCount,
                        onClick = { onModuleClick("Showroom", sector.name, null) },
                        modifier = if (isWeb) Modifier.width(200.dp) else Modifier.weight(1f)
                    )
                }
            }

            if (!isWeb && !isSectorsExpanded) {
                Spacer(modifier = Modifier.height(24.dp))
                OutlinedButton(
                    onClick = { onToggleSectors(true) },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, PrimaryAnchor.copy(alpha = 0.2f)),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = PrimaryAnchor)
                ) {
                    Text("Tüm Sektörleri Gör", fontWeight = FontWeight.Bold)
                }
            } else if (!isWeb && isSectorsExpanded) {
                Spacer(modifier = Modifier.height(24.dp))
                TextButton(
                    onClick = { onToggleSectors(false) },
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text("Daha Az Göster ↑", color = PrimaryAnchor.copy(alpha = 0.6f), fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(64.dp))

        // Announcements Section
        if (announcements.isNotEmpty()) {
            Column(modifier = Modifier.fillMaxWidth().padding(horizontal = if (isWeb) 48.dp else 24.dp)) {
                Text(
                    text = "Duyurular",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = SovereignText
                )
                Spacer(modifier = Modifier.height(24.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(20.dp),
                    contentPadding = PaddingValues(end = 24.dp)
                ) {
                    items(announcements) { ann ->
                        AnnouncementCard(
                            title = ann.title,
                            desc = ann.description,
                            accentColor = Color(parseColorSafe(ann.colorHex)),
                            isWeb = isWeb,
                            link = ann.link
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(64.dp))

        // News & B2B Cards
        if (!isWeb) {
            Box(modifier = Modifier.padding(horizontal = 24.dp).padding(bottom = 24.dp)) {
                FeatureEditorialCard(
                    title = "B2B Meeting",
                    subtitle = "Profesyonel ağınızı genişletin ve stratejik iş ortaklıkları başlatın.",
                    imageRes = Res.drawable.companymeeting,
                    icon = Icons.Default.Groups,
                    onClick = { onModuleClick("CompanyMeeting", null, null) }
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = if (isWeb) 48.dp else 24.dp)
                .padding(bottom = 64.dp),
            horizontalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            if (isWeb) {
                Box(modifier = Modifier.weight(1f)) {
                    FeatureEditorialCard(
                        title = "B2B Meeting",
                        subtitle = "Profesyonel ağınızı genişletin ve stratejik iş ortaklıkları başlatın.",
                        imageRes = Res.drawable.companymeeting,
                        icon = Icons.Default.Groups,
                        onClick = { onModuleClick("CompanyMeeting", null, null) }
                    )
                }
            }
            Box(modifier = Modifier.weight(1f)) {
                FeatureEditorialCard(
                    title = "Ekonomi Haberleri",
                    subtitle = "Küresel ticaret dünyasındaki son gelişmeler ve piyasa analizleri.",
                    imageRes = Res.drawable.news,
                    icon = Icons.Default.Public,
                    onClick = { onModuleClick("EconomicNews", null, null) }
                )
            }
        }
    }
}

@Composable
fun SovereignHero(
    isWeb: Boolean,
    onExploreClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(if (isWeb) 400.dp else 300.dp)
            .padding(horizontal = if (isWeb) 48.dp else 16.dp)
            .padding(top = if (isWeb) 48.dp else 24.dp)
            .clickable { onExploreClick() },
        shape = if (isWeb) RoundedCornerShape(32.dp) else RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Image(
                painter = painterResource(Res.drawable.products),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.linearGradient(
                            colors = listOf(PrimaryAnchor.copy(alpha = 0.9f), Color.Transparent),
                            start = androidx.compose.ui.geometry.Offset(0f, 0f),
                            end = androidx.compose.ui.geometry.Offset(1000f, 1000f)
                        )
                    )
            )
            
            Column(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(if (isWeb) 64.dp else 24.dp)
                    .widthIn(max = 500.dp)
            ) {
                Text(
                    text = "KÜRESEL TİCARETİN YENİ MERKEZİ",
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Dijital Showroom",
                    color = Color.White,
                    style = MaterialTheme.typography.displayMedium,
                    fontWeight = FontWeight.ExtraBold
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Dünya pazarlarıyla buluşmanın en kısa yolu. İşletmenizi küresel ölçekte sergileyin, yeni ortaklıklar kurun.",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 16.sp,
                    lineHeight = 24.sp
                )
                // Buttons removed as requested
            }
        }
    }
}

@Composable
fun FeatureEditorialCard(
    title: String,
    subtitle: String,
    imageRes: org.jetbrains.compose.resources.DrawableResource,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(420.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = SovereignContainerLow),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column {
            Box(modifier = Modifier.fillMaxWidth().height(200.dp)) {
                Image(
                    painter = painterResource(imageRes),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
            Column(modifier = Modifier.padding(24.dp)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = SovereignText
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = subtitle,
                    color = SovereignText.copy(alpha = 0.6f),
                    fontSize = 14.sp,
                    lineHeight = 22.sp
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = if (title.contains("Ekonomi", true) || title.contains("Haber", true)) "Haberi Oku" else "Randevu Al",
                    fontWeight = FontWeight.Bold,
                    color = PrimaryAnchor,
                    fontSize = 13.sp
                )
            }
        }
    }
}

@Composable
private fun getSectorPainter(name: String): androidx.compose.ui.graphics.painter.Painter? {
    val res = when {
        name.contains("Gıda", true) -> Res.drawable.ic_gida
        name.contains("Bilişim", true) -> Res.drawable.ic_bilisim
        name.contains("Danışmanlık", true) -> Res.drawable.ic_danismanlik
        name.contains("Dayanıklı", true) -> Res.drawable.ic_dayanikli
        name.contains("Deri", true) -> Res.drawable.ic_deri
        name.contains("Dış Ticaret", true) -> Res.drawable.ic_disticaret
        name.contains("Elektronik", true) -> Res.drawable.ic_elektronik
        name.contains("Enerji", true) -> Res.drawable.ic_enerji
        name.contains("Finans", true) -> Res.drawable.ic_finans
        name.contains("Hayvancılık", true) -> Res.drawable.ic_hayvancilik
        name.contains("İnşaat", true) -> Res.drawable.ic_insaat
        name.contains("Lojistik", true) -> Res.drawable.ic_lojistik
        name.contains("Madencilik", true) -> Res.drawable.ic_madencilik
        name.contains("Medikal", true) -> Res.drawable.ic_medikal
        name.contains("Mobilya", true) -> Res.drawable.ic_mobilya
        name.contains("Mücevherat", true) -> Res.drawable.ic_mucevherat
        name.contains("Otomotiv", true) || name.contains("Otomativ", true) -> Res.drawable.ic_otomativ
        name.contains("Reklamcılık", true) -> Res.drawable.ic_reklamcilik
        name.contains("Tarım", true) -> Res.drawable.ic_tarim
        name.contains("Tekstil", true) -> Res.drawable.ic_tekstil
        name.contains("Telekom", true) || name.contains("Telekominikasyon", true) -> Res.drawable.ic_telekominikasyon
        else -> null
    }
    return res?.let { painterResource(it) }
}

private fun getSectorSubBranches(sectorName: String): String {
    return when (sectorName.lowercase()) {
        "bilişim", "technology" -> "Yazılım | Donanım | Bulut Bilişim"
        "danışmanlık", "consulting" -> "Strateji | Finans | İK Yönetimi"
        "dayanıklı tüketim", "durable goods" -> "Beyaz Eşya | Mobilya | Cihazlar"
        "deri", "leather" -> "Ayakkabı | Çanta | Giyim"
        "dış ticaret", "foreign trade" -> "İthalat | İhracat | Lojistik"
        "elektronik", "electronics" -> "Komponent | Otomasyon | Cihazlar"
        "enerji", "energy" -> "Güneş | Rüzgar | Elektrik"
        "finans", "finance" -> "Bankacılık | Yatırım | Fintech"
        "gıda", "food" -> "Unlu Mamul | Tarım Ürünleri | İçecek"
        "hayvancılık", "livestock" -> "Büyükbaş | Kanatlı | Yem Üretimi"
        "insaat", "inşaat", "construction" -> "Yapı Malzemeleri | Mühendislik | Proje"
        "lojistik", "logistics" -> "Taşımacılık | Depolama | Kargo"
        "madencilik", "mining" -> "Metalik Madenler | Mermer | Kömür"
        "medikal", "medical" -> "Cihazlar | Sarf Malzemesi | İlaç"
        "mobilya", "furniture" -> "Ev Mobilyası | Ofis | Dekorasyon"
        "mucevherat", "mücevherat", "jewelry" -> "Altın | Gümüş | Değerli Taş"
        "otomativ", "otomotiv", "automotive" -> "Yedek Parça | Servis | Araç Satışı"
        "reklamcilik", "reklamcılık", "advertising" -> "Dijital Pazarlama | Medya | Tasarım"
        "tarim", "tarım", "agriculture" -> "Tohum | Gübre | Seracılık"
        "tekstil", "textile" -> "İplik | Kumaş | Hazır Giyim"
        "telekominikasyon", "telekomünikasyon", "telecom" -> "Altyapı | Mobil İletişim | Fiber"
        else -> "İthalat | İhracat | Global Ticaret"
    }
}

private fun parseColorSafe(hex: String): Long {
    val clean = hex.removePrefix("#")
    return try {
        if (clean.length == 6) ("FF$clean").toLong(16)
        else clean.toLong(16)
    } catch (e: Exception) {
        0xFF3B82F6
    }
}

private val defaultFallbackSectors = listOf(
    Sector(id = "sec_1", name = "Bilişim", groupNo = "1", isActive = true),
    Sector(id = "sec_2", name = "Danışmanlık", groupNo = "2", isActive = true),
    Sector(id = "sec_3", name = "Dayanıklı Tüketim", groupNo = "3", isActive = true),
    Sector(id = "sec_4", name = "Deri", groupNo = "4", isActive = true),
    Sector(id = "sec_5", name = "Dış Ticaret", groupNo = "5", isActive = true),
    Sector(id = "sec_6", name = "Elektronik", groupNo = "6", isActive = true),
    Sector(id = "sec_7", name = "Enerji", groupNo = "7", isActive = true),
    Sector(id = "sec_8", name = "Finans", groupNo = "8", isActive = true),
    Sector(id = "sec_9", name = "Gıda", groupNo = "9", isActive = true),
    Sector(id = "sec_10", name = "İnşaat", groupNo = "10", isActive = true),
    Sector(id = "sec_11", name = "Lojistik", groupNo = "11", isActive = true),
    Sector(id = "sec_12", name = "Mobilya", groupNo = "12", isActive = true),
    Sector(id = "sec_13", name = "Otomotiv", groupNo = "13", isActive = true),
    Sector(id = "sec_14", name = "Tekstil", groupNo = "14", isActive = true),
    Sector(id = "sec_15", name = "Tarım", groupNo = "15", isActive = true)
)


