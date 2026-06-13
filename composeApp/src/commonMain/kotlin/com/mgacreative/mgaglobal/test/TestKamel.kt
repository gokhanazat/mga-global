package com.mgacreative.mgaglobal.test

import androidx.compose.runtime.Composable
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource

@Composable
fun TestKamel(bytes: ByteArray) {
    KamelImage(
        resource = asyncPainterResource(data = bytes),
        contentDescription = "Test"
    )
}

