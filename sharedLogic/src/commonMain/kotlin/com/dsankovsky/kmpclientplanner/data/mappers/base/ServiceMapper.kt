package com.dsankovsky.kmpclientplanner.data.mappers.base

import com.dsankovsky.kmpclientplanner.data.db.models.base.BaseServiceDbModel
import com.dsankovsky.kmpclientplanner.domain.models.additional.CurrencyItem
import com.dsankovsky.kmpclientplanner.domain.models.base.BaseService
import com.dsankovsky.kmpclientplanner.extensions.toEpochMilliseconds
import com.dsankovsky.kmpclientplanner.extensions.toLocalDateTime


fun BaseServiceDbModel.toBaseService() = BaseService(
    id = id,
    title = title,
    clientId = clientId,
    address = address,
    startDate = startTimestamp.toLocalDateTime(),
    endDate = endTimestamp.toLocalDateTime(),
    isPaid = isPaid,
    isFinished = isFinished,
    currency = CurrencyItem.getCurrenciesList().first { it.code == currency },
    price = price,
    comment = comment,
    serviceType = getServiceTypeByIndex(serviceType),
)

fun List<BaseServiceDbModel>.toBaseServicesList() = this.map { it.toBaseService() }

fun BaseService.toDbModel() = BaseServiceDbModel(
    id = id,
    title = title,
    clientId = clientId,
    startTimestamp = startDate.toEpochMilliseconds(),
    endTimestamp = endDate.toEpochMilliseconds(),
    address = address,
    isPaid = isPaid,
    isFinished = isFinished,
    price = price,
    currency = currency.code,
    comment = comment,
    serviceType = serviceType.ordinal
)