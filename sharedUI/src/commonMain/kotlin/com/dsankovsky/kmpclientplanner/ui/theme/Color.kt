package com.dsankovsky.clientmanagement.ui.theme

import androidx.compose.ui.graphics.Color

// =============================================================================
// Kufar Blocknot · Material 3 redesign — LAVENDER / PINK tonal palette
// Drop-in replacement for the existing Color.kt (same val names & package),
// so Theme.kt keeps working with zero changes.
// Extracted verbatim from the HTML mock tokens (:root light / dark).
// =============================================================================

// ---------- LIGHT ----------
val primaryLight = Color(0xFF6A4DE0)
val onPrimaryLight = Color(0xFFFFFFFF)
val primaryContainerLight = Color(0xFFE8DBFF)
val onPrimaryContainerLight = Color(0xFF22005C)
val secondaryLight = Color(0xFF6B5D8F)
val onSecondaryLight = Color(0xFFFFFFFF)
val secondaryContainerLight = Color(0xFFEADCFB)
val onSecondaryContainerLight = Color(0xFF241634)
val tertiaryLight = Color(0xFFD22F76)
val onTertiaryLight = Color(0xFFFFFFFF)
val tertiaryContainerLight = Color(0xFFFFD8E6)
val onTertiaryContainerLight = Color(0xFF3E041F)
val errorLight = Color(0xFFBA1A1A)
val onErrorLight = Color(0xFFFFFFFF)
val errorContainerLight = Color(0xFFFFDAD6)
val onErrorContainerLight = Color(0xFF410002)
val backgroundLight = Color(0xFFF7F1FE)     // == surface
val onBackgroundLight = Color(0xFF1C1526)   // == onSurface
val surfaceLight = Color(0xFFF7F1FE)
val onSurfaceLight = Color(0xFF1C1526)
val surfaceVariantLight = Color(0xFFE0D3F3) // mock has no explicit role → surfaceContainerHighest tone
val onSurfaceVariantLight = Color(0xFF4C4459)
val outlineLight = Color(0xFF7E7592)
val outlineVariantLight = Color(0xFFCFC4E0)
val scrimLight = Color(0xFF000000)
val inverseSurfaceLight = Color(0xFF322A40)
val inverseOnSurfaceLight = Color(0xFFF4EDFB)
val inversePrimaryLight = Color(0xFFD6C0FF) // == dark primary
val surfaceDimLight = Color(0xFFE7DCF7)
val surfaceBrightLight = Color(0xFFFBF6FF)
val surfaceContainerLowestLight = Color(0xFFFFFFFF)
val surfaceContainerLowLight = Color(0xFFF3EBFD)
val surfaceContainerLight = Color(0xFFEEE4FB)
val surfaceContainerHighLight = Color(0xFFE7DBF7)
val surfaceContainerHighestLight = Color(0xFFE0D3F3)

// ---------- DARK ----------
val primaryDark = Color(0xFFD6C0FF)
val onPrimaryDark = Color(0xFF38167E)
val primaryContainerDark = Color(0xFF4F30A8)
val onPrimaryContainerDark = Color(0xFFEADDFF)
val secondaryDark = Color(0xFFD0C1EC)
val onSecondaryDark = Color(0xFF362A4E)
val secondaryContainerDark = Color(0xFF4C3F66)
val onSecondaryContainerDark = Color(0xFFEADCFB)
val tertiaryDark = Color(0xFFFFB0CB)
val onTertiaryDark = Color(0xFF5E0A3A)
val tertiaryContainerDark = Color(0xFF93264F)
val onTertiaryContainerDark = Color(0xFFFFD8E6)
val errorDark = Color(0xFFFFB4AB)
val onErrorDark = Color(0xFF690005)
val errorContainerDark = Color(0xFF93000A)
val onErrorContainerDark = Color(0xFFFFDAD6)
val backgroundDark = Color(0xFF161022)      // == surface
val onBackgroundDark = Color(0xFFE9E0F5)    // == onSurface
val surfaceDark = Color(0xFF161022)
val onSurfaceDark = Color(0xFFE9E0F5)
val surfaceVariantDark = Color(0xFF3A3052)  // surfaceContainerHighest tone
val onSurfaceVariantDark = Color(0xFFCFC4E0)
val outlineDark = Color(0xFF9A8FAE)
val outlineVariantDark = Color(0xFF4C4459)
val scrimDark = Color(0xFF000000)
val inverseSurfaceDark = Color(0xFFE9E0F5)
val inverseOnSurfaceDark = Color(0xFF322A40)
val inversePrimaryDark = Color(0xFF6A4DE0)  // == light primary
val surfaceDimDark = Color(0xFF161022)
val surfaceBrightDark = Color(0xFF3E3550)
val surfaceContainerLowestDark = Color(0xFF100A1C)
val surfaceContainerLowDark = Color(0xFF1F1732)
val surfaceContainerDark = Color(0xFF241B39)
val surfaceContainerHighDark = Color(0xFF2F2646)
val surfaceContainerHighestDark = Color(0xFF3A3052)

// ---------- CUSTOM: SUCCESS ----------
// M3 ColorScheme has no "success" role. The mock uses green "paid" pills.
// Add these as an extension (see README → Design Tokens → Success).
val successLight = Color(0xFF3B7A4B)
val successContainerLight = Color(0xFFB9F0BF)
val onSuccessContainerLight = Color(0xFF00210E)
val successDark = Color(0xFF9ED4A4)
val successContainerDark = Color(0xFF226138)
val onSuccessContainerDark = Color(0xFFB9F0BF)
