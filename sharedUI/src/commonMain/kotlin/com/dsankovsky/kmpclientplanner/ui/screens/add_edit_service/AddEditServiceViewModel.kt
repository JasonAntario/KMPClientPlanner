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
import com.dsankovsky.kmpclientplanner.domain.usecases.service.ShiftFutureServicesUseCase
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
    private val shiftFutureServicesUseCase: ShiftFutureServicesUseCase,
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

            // Правки есть — сначала М9, иначе закрываем сразу.
            AddEditServiceAction.OnCloseRequested -> {
                if (state.value.isDirty) {
                    _state.update {
                        it.copy(
                            showDialog = AddEditServiceScreenState.ServiceScreenDialog.ConfirmDiscard,
                        )
                    }
                } else {
                    handleActions(AddEditServiceAction.OnCloseScreenClicked)
                }
            }

            AddEditServiceAction.OnCloseScreenClicked -> {
                viewModelScope.launch {
                    closeDialog()
                    event.emit(AddEditServiceEvent.OnDismissClicked)
                }
            }

            AddEditServiceAction.OnSaveServiceClicked -> checkServiceBeforeSaving()

            // М6: время занято, но пользователь настоял — остаётся вопрос про серию занятий.
            AddEditServiceAction.OnSaveServiceConfirmed -> {
                closeDialog()
                askAboutFutureServices()
            }

            AddEditServiceAction.OnShiftFutureServicesConfirmed -> {
                closeDialog()
                saveService(shiftSlot = true)
            }

            AddEditServiceAction.OnShiftFutureServicesDeclined -> {
                closeDialog()
                saveService()
            }

            is AddEditServiceAction.OnTitleChanged -> {
                _state.update { it.copy(title = action.title) }
            }

            is AddEditServiceAction.OnTimeChanged -> _state.update { state ->
                // Длительность держим прежней: время начала сдвигает и конец.
                val minutes = state.durationMinutes
                val start = LocalDateTime(state.startDateTime.date, action.time)
                state.copy(startDateTime = start, endDateTime = start.addMinutes(minutes))
            }

            is AddEditServiceAction.OnDateChanged -> _state.update { state ->
                val minutes = state.durationMinutes
                val start = LocalDateTime(action.date, state.startDateTime.time)
                state.copy(startDateTime = start, endDateTime = start.addMinutes(minutes))
            }

            is AddEditServiceAction.OnDurationChanged -> _state.update { state ->
                val minutes = action.minutes.trim().toIntOrNull()?.takeIf { it > 0 }
                    ?: return@update state.copy(durationText = action.minutes)
                state.copy(
                    durationText = action.minutes,
                    endDateTime = state.startDateTime.addMinutes(minutes),
                )
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

            is AddEditServiceAction.EducationServiceAction.OnHomeworkChanged -> {
                val specificFields = getEducationSpecificFields() ?: return
                _state.update {
                    it.copy(serviceSpecificFields = specificFields.copy(homework = action.homework))
                }
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
        // Как и у формы клиента: ViewModel переживает закрытие модалки, поэтому поля
        // прошлой услуги надо сбросить, а не показывать до конца загрузки.
        _state.value = AddEditServiceScreenState()
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
                val loaded = AddEditServiceScreenState(
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
                loaded.copy(
                    durationText = loaded.durationMinutes.toString(),
                    initialSnapshot = loaded.snapshot,
                )
            }
        }
    }

    /** Минуты — единица длительности в форме; в модели живут две даты. */
    private fun LocalDateTime.addMinutes(minutes: Int): LocalDateTime =
        addHours(minutes / 60f)

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
                // При правке занятие уже лежит в базе в этом же интервале и находит само себя.
                .filterNot { it.id == currentState.id }

            if (crossingServices.isNotEmpty()) {
                _state.update {
                    it.copy(
                        showDialog = AddEditServiceScreenState.ServiceScreenDialog.ServicesCrossing(
                            crossingServices
                        )
                    )
                }
            } else {
                askAboutFutureServices()
            }
        }
    }

    /**
     * Занятия ставятся расписанием серией, поэтому переезд одного из них — обычно переезд
     * всей серии и самой строки расписания в карточке клиента. Спрашиваем только когда
     * переносить правда есть что: вопрос без последствий лишний.
     */
    private fun askAboutFutureServices() {
        viewModelScope.launch {
            val services = findServicesToShift()
            val updatesSchedule = hasClientScheduleSlot()
            if (services.isEmpty() && !updatesSchedule) {
                saveService()
            } else {
                _state.update {
                    it.copy(
                        showDialog = AddEditServiceScreenState.ServiceScreenDialog
                            .ConfirmShiftFutureServices(
                                services = services,
                                updatesClientSchedule = updatesSchedule,
                            )
                    )
                }
            }
        }
    }

    private suspend fun findServicesToShift(): List<BaseService> {
        val state = state.value
        val client = state.client ?: return emptyList()
        val initialStart = shiftableSlotStart() ?: return emptyList()

        return shiftFutureServicesUseCase.findServicesToShift(
            clientId = client.id,
            serviceId = state.id,
            oldStart = initialStart,
        )
    }

    private suspend fun hasClientScheduleSlot(): Boolean {
        val client = state.value.client ?: return false
        val initialStart = shiftableSlotStart() ?: return false

        return shiftFutureServicesUseCase.hasScheduleSlot(
            clientId = client.id,
            serviceType = client.serviceType,
            oldStart = initialStart,
        )
    }

    /** Начало занятия до правки — если правка вообще сдвинула его в другой слот недели. */
    private fun shiftableSlotStart(): LocalDateTime? {
        val state = state.value
        if (!state.isEdit || !state.movedToAnotherWeekSlot) return null
        // Занятие переписали на другого клиента: серия и расписание остались у прошлого.
        if (state.clientChanged) return null
        return state.initialStartDateTime
    }

    /**
     * @param shiftSlot переносить ли вслед за занятием весь его слот недели: остальные
     *   занятия серии и строку расписания в карточке клиента
     */
    private fun saveService(shiftSlot: Boolean = false) {
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

            val initialStart = state.initialStartDateTime
            if (!shiftSlot || initialStart == null) {
                event.emit(AddEditServiceEvent.OnServiceSaved)
                return@launch
            }

            // Само занятие уже сохранено: перенос слота — отдельный итог со своим сообщением.
            // Серию ищем заново: между вопросом и ответом занятия могли измениться.
            shiftFutureServicesUseCase.shift(
                services = findServicesToShift(),
                oldStart = initialStart,
                newStart = state.startDateTime,
                clientId = state.client.id,
                serviceType = state.client.serviceType,
            ).fold(
                onSuccess = { count ->
                    event.emit(AddEditServiceEvent.OnFutureServicesShifted(count))
                },
                onFailure = {
                    event.emit(AddEditServiceEvent.OnFutureServicesShiftFailed)
                },
            )
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