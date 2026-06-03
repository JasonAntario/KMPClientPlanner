package com.dsankovsky.kmpclientplanner.ui.screens.add_edit_service

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dsankovsky.kmpclientplanner.data.BaseConstants.UNDEFINED_ID
import com.dsankovsky.kmpclientplanner.data.datastore.AppSettings
import com.dsankovsky.kmpclientplanner.domain.models.additional.CurrencyItem
import com.dsankovsky.kmpclientplanner.domain.models.additional.ServiceType
import com.dsankovsky.kmpclientplanner.domain.models.base.BaseClient
import com.dsankovsky.kmpclientplanner.domain.models.base.BaseService
import com.dsankovsky.kmpclientplanner.domain.models.specific_fields.ClientSpecificFields
import com.dsankovsky.kmpclientplanner.domain.models.specific_fields.ServiceSpecificFields
import com.dsankovsky.kmpclientplanner.domain.usecases.client.GetClientSpecificFieldsUseCase
import com.dsankovsky.kmpclientplanner.domain.usecases.client.GetClientsUseCase
import com.dsankovsky.kmpclientplanner.domain.usecases.service.AddEditDeleteServiceUseCase
import com.dsankovsky.kmpclientplanner.domain.usecases.service.AddEditServiceSpecificFieldsUseCase
import com.dsankovsky.kmpclientplanner.domain.usecases.service.CheckServiceCrossingUseCase
import com.dsankovsky.kmpclientplanner.domain.usecases.service.GetServiceSpecificFieldsUseCase
import com.dsankovsky.kmpclientplanner.domain.usecases.service.GetServicesUseCase
import com.dsankovsky.kmpclientplanner.ui.extensions.addHours
import com.dsankovsky.kmpclientplanner.ui.extensions.getStartDateTime
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDateTime

class AddEditServiceViewModel(
    private val getServicesUseCase: GetServicesUseCase,
    private val addEditDeleteServiceUseCase: AddEditDeleteServiceUseCase,
    private val getServiceSpecificFieldsUseCase: GetServiceSpecificFieldsUseCase,
    private val getClientSpecificFieldsUseCase: GetClientSpecificFieldsUseCase,
    private val addEditServiceSpecificFieldsUseCase: AddEditServiceSpecificFieldsUseCase,
    private val checkServiceCrossingUseCase: CheckServiceCrossingUseCase,
    private val getClientsUseCase: GetClientsUseCase,
    private val appSettings: AppSettings
) : ViewModel() {

    private val _state = MutableStateFlow(AddEditServiceScreenState())
    val state = _state.asStateFlow()

    val event = MutableSharedFlow<AddEditServiceEvent>()

    fun handleActions(action: AddEditServiceAction) {
        when (action) {
            is AddEditServiceAction.LoadServiceData -> loadData(action.serviceId)
            is AddEditServiceAction.OnClientChanged -> {
                changeClient(action.client)
            }

            is AddEditServiceAction.OnAddressChanged -> {
                _state.update { it.copy(address = action.address) }
            }

            is AddEditServiceAction.OnCommentChanged -> {
                _state.update { it.copy(comment = action.comment) }
            }

            AddEditServiceAction.OnCloseScreenClicked -> {
                viewModelScope.launch {
                    event.emit(AddEditServiceEvent.OnDismissClicked)
                }
            }

            AddEditServiceAction.OnSaveServiceClicked -> {
                checkServiceBeforeSaving()
            }

            AddEditServiceAction.OnSaveServiceConfirmed -> {
                closeDialog()
                saveService()
            }

            is AddEditServiceAction.OnTitleChanged -> {
                _state.update { it.copy(title = action.title) }
            }

            is AddEditServiceAction.OnTimeChanged -> {
                _state.update {
                    when (action.source) {
                        TimeSource.BASE_START_TIME -> {
                            val dateTime = LocalDateTime(it.startDateTime.date, action.time)
                            it.copy(
                                startDateTime = dateTime
                            )
                        }

                        TimeSource.BASE_END_TIME -> {
                            val dateTime = LocalDateTime(it.endDateTime.date, action.time)
                            it.copy(
                                endDateTime = dateTime
                            )
                        }
                    }
                }
            }

            is AddEditServiceAction.OnDateChanged -> {
                _state.update {
                    when (action.source) {
                        DateSource.BASE_START_DATE -> {
                            val dateTime = LocalDateTime(action.date, it.startDateTime.time)
                            it.copy(
                                startDateTime = dateTime
                            )
                        }

                        DateSource.BASE_END_DATE -> {
                            val dateTime = LocalDateTime(action.date, it.endDateTime.time)
                            it.copy(
                                endDateTime = dateTime
                            )
                        }
                    }
                }
            }

            is AddEditServiceAction.OnCurrencyChanged -> {
                _state.update {
                    it.copy(currency = action.currency)
                }
            }

            is AddEditServiceAction.OnPriceChanged -> {
                _state.update {
                    it.copy(price = action.price)
                }
            }

            AddEditServiceAction.OnDeleteService -> {
                _state.update {
                    it.copy(
                        showDialog = AddEditServiceScreenState.ServiceScreenDialog.ConfirmServiceDeleting
                    )
                }
            }

            AddEditServiceAction.OnDeleteServiceConfirmed -> {
                closeDialog()
                viewModelScope.launch {
                    addEditDeleteServiceUseCase.deleteService(state.value.id)
                    event.emit(AddEditServiceEvent.OnServiceDeleted)
                }
            }

            AddEditServiceAction.OnDialogDismissed -> {
                closeDialog()
            }

            is AddEditServiceAction.EducationServiceAction.OnFormatChanged -> {
                val specificFields = getEducationSpecificFields() ?: return
                val newSpecificField = specificFields.copy(isOnline = action.isOnline)
                _state.update {
                    it.copy(serviceSpecificFields = newSpecificField)
                }
            }

            is AddEditServiceAction.SportServiceAction.OnFormatChanged -> {
                val specificFields = getSportSpecificFields() ?: return
                val newSpecificField = specificFields.copy(isOnline = action.isOnline)
                _state.update {
                    it.copy(serviceSpecificFields = newSpecificField)
                }
            }

            is AddEditServiceAction.OnFinishedStatusChanged -> {
                _state.update {
                    it.copy(isFinished = action.isFinished)
                }
            }

            is AddEditServiceAction.OnPaidStatusChanged -> {
                _state.update {
                    it.copy(isPaid = action.isPaid)
                }
            }
        }
    }

    private fun closeDialog() {
        _state.update { it.copy(showDialog = null) }
    }

    private fun getEducationSpecificFields(): ServiceSpecificFields.EducationServiceSpecificFields? {
        return state.value.serviceSpecificFields as? ServiceSpecificFields.EducationServiceSpecificFields
    }

    private fun getTattooSpecificFields(): ServiceSpecificFields.TattooServiceSpecificFields? {
        return state.value.serviceSpecificFields as? ServiceSpecificFields.TattooServiceSpecificFields
    }

    private fun getSportSpecificFields(): ServiceSpecificFields.SportServiceSpecificFields? {
        return state.value.serviceSpecificFields as? ServiceSpecificFields.SportServiceSpecificFields
    }

    private fun loadData(serviceId: Long?) {
        viewModelScope.launch(Dispatchers.IO) {
            val startTime = getStartDateTime()
            val service = getServicesUseCase.getServiceById(serviceId).first()
            val clients = getClientsUseCase.getAllClients().first()
            val client = clients.firstOrNull { it.id == service?.clientId }
            val serviceType =
                service?.serviceType ?: appSettings.getServiceType() ?: ServiceType.BASE
            val specificFields =
                getServiceSpecificFieldsUseCase.getSpecificField(service?.id, serviceType)

            val addressesList = buildList {
                val fromServices = getServicesUseCase.getAddressesList()
                addAll(fromServices)
                val fromClients = getClientsUseCase.getAddressesList()
                addAll(fromClients)
            }.distinct()

            _state.update {
                AddEditServiceScreenState(
                    isLoading = false,
                    isEdit = service != null,
                    id = service?.id ?: UNDEFINED_ID,
                    title = service?.title ?: "",
                    client = client,
                    clientsList = clients,
                    startDateTime = service?.startDate ?: startTime,
                    endDateTime = service?.endDate ?: startTime.addHours(1),
                    isPaid = service?.isPaid == true,
                    isFinished = service?.isFinished == true,
                    price = service?.price?.toString() ?: client?.price?.toString() ?: "",
                    currency = client?.currency ?: CurrencyItem.BYN,
                    address = service?.address ?: "",
                    addressList = addressesList,
                    comment = service?.comment ?: "",
                    serviceType = serviceType,
                    serviceSpecificFields = specificFields
                )
            }
        }
    }

    private fun changeClient(client: BaseClient) {
        viewModelScope.launch {
            val currentState = state.value
            val price = client.price?.toString() ?: currentState.price
            val currency = client.currency
            val address = client.address ?: ""
            val serviceType = client.serviceType
            val specificFields: ServiceSpecificFields? = when (serviceType) {
                ServiceType.EDUCATION -> {
                    val educationSpecificFields = getEducationSpecificFields()
                    updateEducationServiceSpecificFieldsByClient(
                        educationServiceSpecificFields = educationSpecificFields,
                        client = client
                    )
                }

                ServiceType.SPORT -> {
                    val sportServiceSpecificFields = getSportSpecificFields()
                    updateSportServiceSpecificFieldsByClient(
                        sportServiceSpecificFields = sportServiceSpecificFields,
                        client = client
                    )
                }

                ServiceType.TATTOO -> {
                    val tattooSpecificFields = getTattooSpecificFields()
                    updateTattooServiceSpecificFieldsByClient(
                        tattooServiceSpecificFields = tattooSpecificFields,
                        client = client
                    )
                }

                else -> {
                    currentState.serviceSpecificFields
                }
            }

            _state.update {
                it.copy(
                    client = client,
                    price = price,
                    address = address,
                    currency = currency,
                    serviceSpecificFields = specificFields
                )
            }
        }
    }

    private suspend fun updateTattooServiceSpecificFieldsByClient(
        tattooServiceSpecificFields: ServiceSpecificFields.TattooServiceSpecificFields?,
        client: BaseClient
    ): ServiceSpecificFields? {
        val clientSpecificFields =
            getClientSpecificFieldsUseCase.getSpecificField(client.id, ServiceType.TATTOO)
                ?: return tattooServiceSpecificFields
        val currentProject =
            (clientSpecificFields as? ClientSpecificFields.TattooClientSpecificFields)?.currentProject
                ?: return tattooServiceSpecificFields
        return when {
            currentProject.imageUrls.isEmpty() -> tattooServiceSpecificFields
            else -> tattooServiceSpecificFields?.copy(images = currentProject.imageUrls)
        }
    }

    private suspend fun updateEducationServiceSpecificFieldsByClient(
        educationServiceSpecificFields: ServiceSpecificFields.EducationServiceSpecificFields?,
        client: BaseClient
    ): ServiceSpecificFields? {
        val clientSpecificFields = getClientSpecificFieldsUseCase.getSpecificField(
            client.id,
            ServiceType.EDUCATION
        ) as? ClientSpecificFields.EducationClientSpecificFields
            ?: return educationServiceSpecificFields

        return educationServiceSpecificFields?.copy(isOnline = clientSpecificFields.isOnline)
    }

    private suspend fun updateSportServiceSpecificFieldsByClient(
        sportServiceSpecificFields: ServiceSpecificFields.SportServiceSpecificFields?,
        client: BaseClient
    ): ServiceSpecificFields? {
        val clientSpecificFields = getClientSpecificFieldsUseCase.getSpecificField(
            client.id,
            ServiceType.SPORT
        ) as? ClientSpecificFields.SportClientSpecificFields
            ?: return sportServiceSpecificFields

        return sportServiceSpecificFields?.copy(isOnline = clientSpecificFields.isOnline)
    }

    private fun checkServiceBeforeSaving() {
        viewModelScope.launch {
            val currentState = state.value
            val crossingServices = checkServiceCrossingUseCase.checkCrossing(
                Pair(
                    currentState.startDateTime,
                    currentState.endDateTime
                )
            )

            if (crossingServices.isNotEmpty()) {
                _state.update {
                    it.copy(
                        showDialog = AddEditServiceScreenState.ServiceScreenDialog.ServicesCrossing(
                            crossingServices
                        )
                    )
                }
            } else {
                saveService()
            }
        }
    }

    private fun saveService() {
        viewModelScope.launch {
            val state = state.value
            if (state.client == null) throw RuntimeException("Client must not be null")
            val price = state.price.toFloatOrNull()
            val serviceType = state.serviceType

            val service = BaseService(
                id = state.id,
                title = state.title.trim(),
                clientId = state.client.id,
                startDate = state.startDateTime,
                endDate = state.endDateTime,
                isFinished = state.isFinished,
                isPaid = state.isPaid,
                price = if (price == null || price == 0f) null else price,
                currency = state.currency,
                address = state.address.trim().ifBlank { null },
                comment = state.comment.trim().ifBlank { null },
                serviceType = serviceType
            )
            val serviceId = if (state.isEdit) {
                addEditDeleteServiceUseCase.update(service)
                service.id
            } else {
                addEditDeleteServiceUseCase.addService(service)
            }

            val specificFields = checkSpecificFieldsBeforeSaving(serviceId)
            specificFields?.let {
                if (state.isEdit) {
                    addEditServiceSpecificFieldsUseCase.updateSpecificField(it)
                } else {
                    addEditServiceSpecificFieldsUseCase.addSpecificField(it)
                }
            }

            event.emit(AddEditServiceEvent.OnServiceSaved)
        }
    }

    private fun checkSpecificFieldsBeforeSaving(serviceId: Long): ServiceSpecificFields? {
        return when (val fields = state.value.serviceSpecificFields) {
            is ServiceSpecificFields.EducationServiceSpecificFields -> {
                ServiceSpecificFields.EducationServiceSpecificFields(
                    id = fields.id,
                    serviceId = serviceId,
                    homework = fields.homework?.ifBlank { null }
                )
            }

            is ServiceSpecificFields.BeautyServiceSpecificFields -> fields.copy(serviceId = serviceId)
            is ServiceSpecificFields.TattooServiceSpecificFields -> fields.copy(serviceId = serviceId)
            is ServiceSpecificFields.SportServiceSpecificFields -> fields.copy(serviceId = serviceId)
            null -> null
        }
    }
}