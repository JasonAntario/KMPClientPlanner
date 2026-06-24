package com.dsankovsky.kmpclientplanner.uinew.desktop.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import kmpclientplanner.sharedui.generated.resources.Res
import kmpclientplanner.sharedui.generated.resources.nunito_black
import kmpclientplanner.sharedui.generated.resources.nunito_bold
import kmpclientplanner.sharedui.generated.resources.nunito_extrabold
import kmpclientplanner.sharedui.generated.resources.nunito_medium
import kmpclientplanner.sharedui.generated.resources.nunito_regular
import kmpclientplanner.sharedui.generated.resources.nunito_semibold
import org.jetbrains.compose.resources.Font

/** Nunito family covering the weights used by the desktop design (400–900). */
@Composable
fun nunitoFamily(): FontFamily = FontFamily(
    Font(Res.font.nunito_regular, FontWeight.Normal),
    Font(Res.font.nunito_medium, FontWeight.Medium),
    Font(Res.font.nunito_semibold, FontWeight.SemiBold),
    Font(Res.font.nunito_bold, FontWeight.Bold),
    Font(Res.font.nunito_extrabold, FontWeight.ExtraBold),
    Font(Res.font.nunito_black, FontWeight.Black),
)

@Composable
fun lessonsTypography(): Typography {
    val nunito = nunitoFamily()
    fun base(style: TextStyle) = style.copy(fontFamily = nunito)
    val default = Typography()
    return Typography(
        displayLarge = base(default.displayLarge),
        displayMedium = base(default.displayMedium),
        displaySmall = base(default.displaySmall),
        headlineLarge = base(default.headlineLarge),
        headlineMedium = base(default.headlineMedium),
        headlineSmall = base(default.headlineSmall),
        titleLarge = base(default.titleLarge),
        titleMedium = base(default.titleMedium),
        titleSmall = base(default.titleSmall),
        bodyLarge = base(default.bodyLarge),
        bodyMedium = base(default.bodyMedium),
        bodySmall = base(default.bodySmall),
        labelLarge = base(default.labelLarge),
        labelMedium = base(default.labelMedium),
        labelSmall = base(default.labelSmall),
    )
}

/**
 * Convenience text styles matching the mockup's `font:<weight> <size>` declarations.
 * Use together with [nunitoFamily] for one-off labels that don't map to Material roles.
 */
object LessonsText {
    @Composable
    fun style(weight: FontWeight, size: Int, letterSpacingEm: Float = 0f): TextStyle =
        TextStyle(
            fontFamily = nunitoFamily(),
            fontWeight = weight,
            fontSize = size.sp,
            letterSpacing = (size * letterSpacingEm).sp,
        )
}
