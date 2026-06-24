package com.dsankovsky.kmpclientplanner.uinew.desktop.theme

import androidx.compose.ui.graphics.Color

/**
 * Color tokens for the "Lessons App — Desktop (MVP)" design.
 * Warm terracotta / cream palette taken directly from the Claude Design mockup.
 */
object LessonsColors {
    // Surfaces
    val PageBackground = Color(0xFFE9E1D6)
    val RailBackground = Color(0xFFF4EEE7)
    val PanelBackground = Color(0xFFFBF8F3)
    val CardBackground = Color(0xFFFFFFFF)

    // Borders / dividers
    val Border = Color(0xFFE2D7CB)
    val BorderSoft = Color(0xFFEFE7DE)
    val BorderCard = Color(0xFFF0E8DF)
    val BorderNeutral = Color(0xFFECE3D9)
    val Divider = Color(0xFFEEE4D9)

    // Brand
    val Primary = Color(0xFFC15F3C)
    val PrimaryTint = Color(0xFFF8E9E1)
    val PrimaryTintBorder = Color(0xFFF0D2C4)

    // Text
    val TextPrimary = Color(0xFF2C2620)
    val TextSecondary = Color(0xFF7A6E63)
    val TextSecondaryAlt = Color(0xFF6B5F54)
    val TextMuted = Color(0xFF9C8E80)
    val TextBody = Color(0xFF5A5048)

    // Status
    val Success = Color(0xFF2F8A5B)
    val SuccessTint = Color(0xFFE4F2E9)
    val SuccessTintBorder = Color(0xFFBFE0CD)
    val Warning = Color(0xFFE0A04A)
    val WarningStrong = Color(0xFFC9883B)
    val WarningTint = Color(0xFFFBF1E4)
    val Danger = Color(0xFFC75D5D)
    val DangerTint = Color(0xFFFBEDED)
    val DangerTintBorder = Color(0xFFF0CFCF)

    // Progress track / segmented control background
    val Track = Color(0xFFEFE6DB)

    // Toggle (off state)
    val ToggleOff = Color(0xFFD8CBBD)

    // Avatar palette (rotating)
    val AvatarColors = listOf(
        Color(0xFFB07A4E),
        Color(0xFF9E7B49),
        Color(0xFFE0A04A),
        Color(0xFFC15F3C),
        Color(0xFF2F8A5B),
        Color(0xFF9C8E80),
    )
}
