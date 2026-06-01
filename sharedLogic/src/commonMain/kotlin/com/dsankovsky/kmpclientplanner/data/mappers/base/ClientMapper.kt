package com.dsankovsky.kmpclientplanner.data.mappers.base

import com.dsankovsky.kmpclientplanner.data.db.models.base.BaseClientDbModel
import com.dsankovsky.kmpclientplanner.domain.models.additional.CurrencyItem
import com.dsankovsky.kmpclientplanner.domain.models.additional.ServiceType
import com.dsankovsky.kmpclientplanner.domain.models.base.BaseClient


fun BaseClientDbModel.toBaseClient() = BaseClient(
    id = id,
    name = name,
    surname = surname,
    address = address,
    phone = phone,
    price = price,
    currency = CurrencyItem.getCurrenciesList().first { it.code == currency },
    comment = comment,
    serviceType = getServiceTypeByIndex(serviceType)
)

fun List<BaseClientDbModel>.toBaseClientsList() = this.map { it.toBaseClient() }

fun getServiceTypeByIndex(index: Int): ServiceType {
    return ServiceType.entries.first { it.ordinal == index }
}

fun BaseClient.toDbModel() = BaseClientDbModel(
    id = id,
    name = name,
    surname = surname,
    address = address,
    phone = phone,
    price = price,
    currency = currency.code,
    comment = comment,
    serviceType = serviceType.ordinal
)