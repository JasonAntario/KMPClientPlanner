package com.dsankovsky.kmpclientplanner.ui.extensions

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import com.dsankovsky.kmpclientplanner.domain.models.additional.ServiceType
import kmpclientplanner.sharedui.generated.resources.Res
import kmpclientplanner.sharedui.generated.resources.service_type_base
import kmpclientplanner.sharedui.generated.resources.service_type_beauty
import kmpclientplanner.sharedui.generated.resources.service_type_education
import kmpclientplanner.sharedui.generated.resources.service_type_sport
import kmpclientplanner.sharedui.generated.resources.service_type_tattoo
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
    LaunchedEffect(Unit) {
        lifecycle.repeatOnLifecycle(state = state) {
            flow.collect {
                collect(it)
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

fun Modifier.edgeToEdgeBottomPadding(additionalBottomPadding: Dp = 16.dp) =
    this
        .navigationBarsPadding()
        .padding(bottom = additionalBottomPadding)


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