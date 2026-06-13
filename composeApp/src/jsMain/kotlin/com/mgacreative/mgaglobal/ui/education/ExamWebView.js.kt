package com.mgacreative.mgaglobal.ui.education

import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Text

@Composable
actual fun ExamWebView(
    url: String,
    onCertificateRequested: (String) -> Unit,
    onBack: () -> Unit
) {
    Box {
        Text("Exam WebView not supported on JS yet")
    }
}

