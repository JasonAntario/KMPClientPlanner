package com.dsankovsky.kmpclientplanner.ui.extensions

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import com.dsankovsky.kmpclientplanner.domain.models.additional.ServiceType
import com.dsankovsky.kmpclientplanner.ui.design.icons.OrganicIcons
import kmpclientplanner.sharedui.generated.resources.Res
import kmpclientplanner.sharedui.generated.resources.service_type_base
import kmpclientplanner.sharedui.generated.resources.service_type_base_hint
import kmpclientplanner.sharedui.generated.resources.service_type_beauty
import kmpclientplanner.sharedui.generated.resources.service_type_beauty_hint
import kmpclientplanner.sharedui.generated.resources.service_type_education
import kmpclientplanner.sharedui.generated.resources.service_type_education_hint
import kmpclientplanner.sharedui.generated.resources.service_type_sport
import kmpclientplanner.sharedui.generated.resources.service_type_sport_hint
import kmpclientplanner.sharedui.generated.resources.service_type_tattoo
import kmpclientplanner.sharedui.generated.resources.service_type_tattoo_hint
import kotlinx.coroutines.flow.SharedFlow
import org.jetbrains.compose.resources.stringResource

@Suppress("ComposableNaming")
@Composable
fun <T> SharedFlow<T>.collectWithLifecycle(
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
    state: Lifecycle.State = Lifecycle.State.STARTED,
    collect: (T) -> Unit
) {
    val flow = this
    val lifecycle = lifecycleOwner.lifecycle
    // Подписка живёт дольше одной композиции, а обработчик — нет: он замыкает параметры
    // экрана (например id выбранного клиента). Без rememberUpdatedState коллектор навсегда
    // остался бы с лямбдой первой композиции и работал бы со устаревшими данными.
    val currentCollect by rememberUpdatedState(collect)
    LaunchedEffect(Unit) {
        lifecycle.repeatOnLifecycle(state = state) {
            flow.collect {
                currentCollect(it)
            }
        }
    }
}

@Composable
fun PaddingValues.withNavBarPadding(): PaddingValues {
    val layoutDirection = LocalLayoutDirection.current
    val bottomPadding = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    return PaddingValues(
        top = this.calculateTopPadding(),
        start = this.calculateStartPadding(layoutDirection),
        end = this.calculateEndPadding(layoutDirection),
        bottom = bottomPadding + this.calculateBottomPadding()
    )
}

@Composable
fun ServiceType.toUIName(): String {
    val res = when (this) {
        ServiceType.BASE -> Res.string.service_type_base
        ServiceType.EDUCATION -> Res.string.service_type_education
        ServiceType.BEAUTY -> Res.string.service_type_beauty
        ServiceType.TATTOO -> Res.string.service_type_tattoo
        ServiceType.SPORT -> Res.string.service_type_sport
    }

    return stringResource(res)
}

/** Подпись категории на экране выбора (01): чем именно она отличается от базовой. */
@Composable
fun ServiceType.toUIHint(): String {
    val res = when (this) {
        ServiceType.BASE -> Res.string.service_type_base_hint
        ServiceType.EDUCATION -> Res.string.service_type_education_hint
        ServiceType.BEAUTY -> Res.string.service_type_beauty_hint
        ServiceType.TATTOO -> Res.string.service_type_tattoo_hint
        ServiceType.SPORT -> Res.string.service_type_sport_hint
    }

    return stringResource(res)
}

/** Иконка категории — те же глифы, что в макете на экране 01. */
val ServiceType.organicIcon: ImageVector
    get() = when (this) {
        ServiceType.BASE -> OrganicIcons.Plus
        ServiceType.EDUCATION -> OrganicIcons.Monitor
        ServiceType.BEAUTY -> OrganicIcons.Award
        ServiceType.TATTOO -> OrganicIcons.PenTool
        ServiceType.SPORT -> OrganicIcons.Dumbbell
    }

/** Порядок карточек на экране 01. */
val ServiceTypeOrder: List<ServiceType> = listOf(
    ServiceType.EDUCATION,
    ServiceType.SPORT,
    ServiceType.TATTOO,
    ServiceType.BEAUTY,
    ServiceType.BASE,
)