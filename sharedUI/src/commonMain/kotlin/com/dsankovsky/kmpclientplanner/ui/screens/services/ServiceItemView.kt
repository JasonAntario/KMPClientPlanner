@file:OptIn(ExperimentalLayoutApi::class)

package com.dsankovsky.kmpclientplanner.ui.screens.services

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Place
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.isSecondaryPressed
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dsankovsky.kmpclientplanner.ui.components.DoneStatusPill
import com.dsankovsky.kmpclientplanner.ui.components.PaidStatusPill

@Composable
fun ServiceItemView(
    serviceItem: ServicesListScreenItem.ServiceItem,
    onAction: (ServicesListScreenAction) -> Unit,
    modifier: Modifier = Modifier
) {
    var showContextMenu by remember { mutableStateOf(false) }

    val timeParts = serviceItem.timeInterval.split("-").map { it.trim() }.filter { it.isNotEmpty() }
    val startTime = timeParts.getOrNull(0).orEmpty()
    val endTime = timeParts.getOrNull(1).orEmpty()

    val priceText = serviceItem.service.price?.let { "$it ${serviceItem.service.currency.code}" }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .pointerInput(serviceItem) {
                awaitPointerEventScope {
                    while (true) {
                        val event = awaitPointerEvent()
                        if (event.type == PointerEventType.Press && event.buttons.isSecondaryPressed) {
                            showContextMenu = true
                        }
                    }
                }
            }
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerLow
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            onClick = { onAction(ServicesListScreenAction.OnServiceClicked(serviceItem)) }
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.Top
            ) {
                if (startTime.isNotEmpty()) {
                    Column(
                        modifier = Modifier.width(52.dp),
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Text(
                            text = startTime,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        if (endTime.isNotEmpty()) {
                            Text(
                                text = endTime,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = serviceItem.client.getFullName(),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    if (serviceItem.title.isNotBlank()) {
                        Text(
                            text = serviceItem.title,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        PaidStatusPill(isPaid = serviceItem.isPaid)
                        DoneStatusPill(isDone = serviceItem.isFinished)
                    }

                    serviceItem.client.address?.let { address ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Place,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = address,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    serviceItem.comment?.let { comment ->
                        Text(
                            text = comment,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                if (priceText != null) {
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = priceText,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        DropdownMenu(
            expanded = showContextMenu,
            onDismissRequest = { showContextMenu = false }
        ) {
            DropdownMenuItem(
                text = { Text(if (serviceItem.isPaid) "Не оплачено" else "Оплачено") },
                onClick = {
                    onAction(ServicesListScreenAction.OnPaidStatusChanged(serviceItem))
                    showContextMenu = false
                }
            )
            DropdownMenuItem(
                text = { Text(if (serviceItem.isFinished) "Запланировано" else "Проведено") },
                onClick = {
                    onAction(ServicesListScreenAction.OnFinishStatusChanged(serviceItem))
                    showContextMenu = false
                }
            )
            HorizontalDivider()
            DropdownMenuItem(
                text = { Text("Удалить", color = MaterialTheme.colorScheme.error) },
                onClick = {
                    onAction(ServicesListScreenAction.OnDeleteService(serviceItem))
                    showContextMenu = false
                }
            )
        }
    }
}
