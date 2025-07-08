package io.github.jan.supabase.compose.auth.ui

import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.Density
import io.github.jan.supabase.annotations.SupabaseInternal
import org.jetbrains.compose.resources.decodeToSvgPainter

@SupabaseInternal
actual fun svgPainter(bytes: ByteArray, density: Density): Painter = bytes.inputStream().readAllBytes().decodeToSvgPainter(density)