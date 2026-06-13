package com.mgacreative.mgaglobal.ui.education

import androidx.compose.runtime.Composable

@Composable
expect fun ExamWebView(
    url: String,
    onCertificateRequested: (String) -> Unit,
    onBack: () -> Unit
)

