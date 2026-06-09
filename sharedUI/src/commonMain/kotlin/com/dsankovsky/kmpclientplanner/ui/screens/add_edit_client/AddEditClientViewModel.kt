package com.dsankovsky.kmpclientplanner.ui.screens.add_edit_client

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dsankovsky.kmpclientplanner.data.BaseConstants.UNDEFINED_ID
import com.dsankovsky.kmpclientplanner.data.datastore.AppSettings
import com.dsankovsky.kmpclientplanner.data.helpers.update
import com.dsankovsky.kmpclientplanner.domain.models.additional.CurrencyItem
import com.dsankovsky.kmpclientplanner.domain.models.additional.ServiceType
import com.dsankovsky.kmpclientplanner.domain.models.base.BaseClient
import com.dsankovsky.kmpclientplanner.domain.models.specific_fields.ClientSpecificFields
import com.dsankovsky.kmpclientplanner.domain.models.specific_fields.ServiceDateTime
import com.dsankovsky.kmpclientplanner.domain.usecases.client.AddEditClientSpecificFieldsUseCase
import com.dsankovsky.kmpclientplanner.domain.usecases.client.AddEditDeleteClientUseCase
import com.dsankovsky.kmpclientplanner.domain.usecases.client.GetClientSpecificFieldsUseCase
import com.dsankovsky.kmpclientplanner.domain.usecases.client.GetClientsUseCase
import com.dsankovsky.kmpclientplanner.domain.usecases.service.AutofillServiceUseCase
import com.dsankovsky.kmpclientplanner.domain.usecases.service.GetServicesUseCase
import com.dsankovsky.kmpclientplanner.domain.usecases.service.ServicesAutofillResult
import com.dsankovsky.kmpclientplanner.domain.usecases.service.ServicesAutofillResultError
import kmpclientplanner.sharedui.generated.resources.Res
import kmpclientplanner.sharedui.generated.resources.service_prefix_beauty
import kmpclientplanner.sharedui.generated.resources.service_prefix_default
import kmpclientplanner.sharedui.generated.resources.service_prefix_education
import kmpclientplanner.sharedui.generated.resources.service_prefix_sport
import kmpclientplanner.sharedui.generated.resources.service_prefix_tattoo
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString

class AddEditClientViewModel(
    private val addEditDeleteClientUseCase: AddEditDeleteClientUseCase,
    private val getClientsUseCase: GetClientsUseCase,
    private val getClientSpecificFieldsUseCase: GetClientSpecificFieldsUseCase,
    private val getServicesUseCase: GetServicesUseCase,
    private val addEditClientSpecificFields: AddEditClientSpecificFieldsUseCase,
    private val autofillServiceUseCase: AutofillServiceUseCase,
    private val appSettings: AppSettings
) : ViewModel() {

    private val _state = MutableStateFlow(AddEditClientScreenState())
    val state = _state.asStateFlow()

    val event = MutableSharedFlow<AddEditClientEvent>()

    fun handleActions(action: AddEditClientAction) {
        when (action) {
            is AddEditClientAction.LoadClientData -> loadData(action.clientId)

            is AddEditClientAction.OnAddressChanged -> {
                _state.update {
                    it.copy(address = action.address)
                }
            }

            is AddEditClientAction.OnCommentChanged -> {
                _state.update {
                    it.copy(comment = action.comment)
                }
            }

            is AddEditClientAction.OnCurrencyChanged -> {
                _state.update {
                    it.copy(currency = action.currency)
                }
            }

            is AddEditClientAction.OnCurrencyMenuExpandedChange -> {
                _state.update {
                    it.copy(isCurrencyMenuExpanded = action.isExpanded)
                }
            }

            is AddEditClientAction.OnNameChanged -> {
                _state.update {
                    it.copy(name = action.name)
                }
            }

            is AddEditClientAction.OnPhoneChanged -> {
                _state.update {
                    it.copy(phone = action.phone)
                }
            }

            is AddEditClientAction.OnPriceChanged -> {
                _state.update {
                    it.copy(price = action.price)
                }
            }

            is AddEditClientAction.OnSurnameChanged -> {
                _state.update {
                    it.copy(surname = action.surname)
                }
            }

            is AddEditClientAction.EducationClientAction.OnLevelChanged -> {
                val specificFields = getEducationSpecificFields() ?: return
                _state.update {
                    it.copy(clientSpecificFields = specificFields.copy(level = action.level))
                }
            }

            is AddEditClientAction.SportClientAction.OnWeightChanged -> {
                val specificFields = getSportSpecificFields() ?: return
                _state.update {
                    it.copy(clientSpecificFields = specificFields.copy(weight = action.weight))
                }
            }

            AddEditClientAction.OnCloseScreenClicked -> {
                viewModelScope.launch {
                    closeDialog()
                    event.emit(AddEditClientEvent.OnDismissClicked)
                }
            }

            AddEditClientAction.EducationClientAction.OnAddNewServiceTime -> {
                val specificFields = getEducationSpecificFields() ?: return
                val serviceTimeList = specificFields.lessonDateTimeList.toMutableList()
                serviceTimeList.add(ServiceDateTime())
                _state.update {
                    it.copy(clientSpecificFields = specificFields.copy(lessonDateTimeList = serviceTimeList))
                }
            }

            AddEditClientAction.SportClientAction.OnAddNewServiceTime -> {
                val specificFields = getSportSpecificFields() ?: return
                val serviceTimeList = specificFields.lessonDateTimeList.toMutableList()
                serviceTimeList.add(ServiceDateTime())
                _state.update {
                    it.copy(clientSpecificFields = specificFields.copy(lessonDateTimeList = serviceTimeList))
                }
            }

            is AddEditClientAction.EducationClientAction.OnDayOfWeekChanged -> {
                val specificFields = getEducationSpecificFields() ?: return
                val serviceTimeList = specificFields.lessonDateTimeList.toMutableList()
                val serviceTime = serviceTimeList[action.itemIndex]
                val newServiceTime = serviceTime.copy(dayOfWeek = action.dayOfWeek)
                val newServiceTimeList = serviceTimeList.update(action.itemIndex, newServiceTime)
                _state.update {
                    it.copy(clientSpecificFields = specificFields.copy(lessonDateTimeList = newServiceTimeList))
                }
            }

            is AddEditClientAction.SportClientAction.OnDayOfWeekChanged -> {
                val specificFields = getSportSpecificFields() ?: return
                val serviceTimeList = specificFields.lessonDateTimeList.toMutableList()
                val serviceTime = serviceTimeList[action.itemIndex]
                val newServiceTime = serviceTime.copy(dayOfWeek = action.dayOfWeek)
                val newServiceTimeList = serviceTimeList.update(action.itemIndex, newServiceTime)
                _state.update {
                    it.copy(clientSpecificFields = specificFields.copy(lessonDateTimeList = newServiceTimeList))
                }
            }

            is AddEditClientAction.EducationClientAction.OnDurationChanged -> {
                val specificFields = getEducationSpecificFields() ?: return
                val serviceTimeList = specificFields.lessonDateTimeList.toMutableList()
                val serviceTime = serviceTimeList[action.itemIndex]
                val newServiceTime = serviceTime.copy(duration = action.duration)
                val newServiceTimeList = serviceTimeList.update(action.itemIndex, newServiceTime)
                _state.update {
                    it.copy(clientSpecificFields = specificFields.copy(lessonDateTimeList = newServiceTimeList))
                }
            }

            is AddEditClientAction.SportClientAction.OnDurationChanged -> {
                val specificFields = getSportSpecificFields() ?: return
                val serviceTimeList = specificFields.lessonDateTimeList.toMutableList()
                val serviceTime = serviceTimeList[action.itemIndex]
                val newServiceTime = serviceTime.copy(duration = action.duration)
                val newServiceTimeList = serviceTimeList.update(action.itemIndex, newServiceTime)
                _state.update {
                    it.copy(clientSpecificFields = specificFields.copy(lessonDateTimeList = newServiceTimeList))
                }
            }

            is AddEditClientAction.EducationClientAction.OnTimeChanged -> {
                val specificFields = getEducationSpecificFields() ?: return
                val serviceTimeList = specificFields.lessonDateTimeList.toMutableList()
                val serviceTime = serviceTimeList[action.itemIndex]
                val newServiceTime = serviceTime.copy(time = action.time)
                val newServiceTimeList = serviceTimeList.update(action.itemIndex, newServiceTime)
                _state.update {
                    it.copy(clientSpecificFields = specificFields.copy(lessonDateTimeList = newServiceTimeList))
                }
            }

            is AddEditClientAction.SportClientAction.OnTimeChanged -> {
                val specificFields = getSportSpecificFields() ?: return
                val serviceTimeList = specificFields.lessonDateTimeList.toMutableList()
                val serviceTime = serviceTimeList[action.itemIndex]
                val newServiceTime = serviceTime.copy(time = action.time)
                val newServiceTimeList = serviceTimeList.update(action.itemIndex, newServiceTime)
                _state.update {
                    it.copy(clientSpecificFields = specificFields.copy(lessonDateTimeList = newServiceTimeList))
                }
            }

            is AddEditClientAction.EducationClientAction.OnFormatChanged -> {
                val specificFields = getEducationSpecificFields() ?: return
                _state.update {
                    it.copy(clientSpecificFields = specificFields.copy(isOnline = action.isOnline))
                }
            }

            is AddEditClientAction.SportClientAction.OnFormatChanged -> {
                val specificFields = getSportSpecificFields() ?: return
                _state.update {
                    it.copy(clientSpecificFields = specificFields.copy(isOnline = action.isOnline))
                }
            }

            is AddEditClientAction.OnClientSaveClicked -> saveClient(
                name = action.name,
                surname = action.surname,
                comment = action.comment,
                address = action.address,
                phone = action.phone,
                price = action.price,
                level = action.level,
                weight = action.weight
            )

            AddEditClientAction.OnDeleteClient -> {
                _state.update {
                    it.copy(showDialog = ClientScreenDialog.ConfirmClientDeleting)
                }
            }

            AddEditClientAction.OnDeleteClientConfirmed -> {
                viewModelScope.launch {
                    closeDialog()
                    addEditDeleteClientUseCase.deleteClient(state.value.id)
                    val clients = getClientsUseCase.getAllClients().firstOrNull()
                    event.emit(AddEditClientEvent.OnClientDeleted(clients.isNullOrEmpty()))
                }
            }

            AddEditClientAction.OnAutofillConfirmClicked -> {
                closeDialog()
                autofillServices()
            }

            AddEditClientAction.OnAutofillDismissClicked -> {
                closeDialog()
                viewModelScope.launch {
                    event.emit(AddEditClientEvent.OnClientSaved)
                }
            }

            AddEditClientAction.CloseClientDialog -> {
                closeDialog()
            }

            AddEditClientAction.OnAutofillWithCrossingConfirmClicked -> {
                closeDialog()
                autofillServices(true)
            }

            is AddEditClientAction.EducationClientAction.OnDeleteLessonClicked -> {
                val specificFields = getEducationSpecificFields() ?: return
                val lessonsList = specificFields.lessonDateTimeList.toMutableList()
                lessonsList.removeAt(action.lessonIndex)
                _state.update {
                    it.copy(
                        clientSpecificFields = specificFields.copy(
                            lessonDateTimeList = lessonsList,
                        )
                    )
                }
            }

            is AddEditClientAction.SportClientAction.OnDeleteTrainingClicked -> {
                val specificFields = getSportSpecificFields() ?: return
                val trainingsList = specificFields.lessonDateTimeList.toMutableList()
                trainingsList.removeAt(action.trainingIndex)
                _state.update {
                    it.copy(
                        clientSpecificFields = specificFields.copy(
                            lessonDateTimeList = trainingsList,
                        )
                    )
                }
            }
        }
    }

    private fun closeDialog() {
        _state.update { it.copy(showDialog = null) }
    }

    private fun getEducationSpecificFields(): ClientSpecificFields.EducationClientSpecificFields? {
        return state.value.clientSpecificFields as? ClientSpecificFields.EducationClientSpecificFields
    }

    private fun getSportSpecificFields(): ClientSpecificFields.SportClientSpecificFields? {
        return state.value.clientSpecificFields as? ClientSpecificFields.SportClientSpecificFields
    }

    private fun loadData(clientId: Long?) {
        viewModelScope.launch {
            val client = getClientsUseCase.getClientById(clientId).firstOrNull()
            val id = client?.id ?: UNDEFINED_ID
            val isEdit = client != null
            val name = client?.name ?: ""
            val surname = client?.surname ?: ""
            val address = client?.address ?: ""
            val price = client?.price?.toString() ?: ""
            val currency = client?.currency ?: CurrencyItem.BYN
            val phone = client?.phone ?: ""
            val comment = client?.comment ?: ""
            val addressesList = buildList {
                val fromServices = getServicesUseCase.getAddressesList()
                addAll(fromServices)
                val fromClients = getClientsUseCase.getAddressesList()
                addAll(fromClients)
            }.distinct()

            val serviceType =
                client?.serviceType ?: appSettings.getServiceType() ?: ServiceType.BASE
            val specificFields =
                getClientSpecificFieldsUseCase.getSpecificField(client?.id, serviceType)

            _state.update {
                it.copy(
                    id = id,
                    isEdit = isEdit,
                    isLoading = false,
                    name = name,
                    surname = surname,
                    address = address,
                    addressList = addressesList,
                    price = price,
                    currency = currency,
                    phone = phone,
                    comment = comment,
                    serviceType = serviceType,
                    initialServiceFields = specificFields,
                    clientSpecificFields = specificFields
                )
            }
        }
    }

    private fun saveClient(
        name: String,
        surname: String = "",
        comment: String = "",
        address: String = "",
        phone: String = "",
        price: String = "",
        level: String = "",
        weight: String = ""
    ) {
        viewModelScope.launch {
            val currentState = state.value
            val price = price.toFloatOrNull()
            val serviceType = currentState.serviceType

            val client = BaseClient(
                id = currentState.id,
                name = name.trim(),
                surname = surname.trim().ifBlank { null },
                address = address.trim().ifBlank { null },
                phone = phone.trim().ifBlank { null },
                price = if (price == null || price == 0f) null else price,
                currency = currentState.currency,
                comment = comment.trim().ifBlank { null },
                serviceType = serviceType
            )

            val clientId = if (currentState.isEdit) {
                addEditDeleteClientUseCase.update(client)
                client.id
            } else {
                addEditDeleteClientUseCase.addClient(client)
            }

            val specificFields = checkSpecificFieldsBeforeSaving(clientId, level, weight)
            specificFields?.let {
                if (currentState.isEdit) {
                    addEditClientSpecificFields.updateSpecificField(specificFields)
                } else {
                    addEditClientSpecificFields.addSpecificField(specificFields)
                }
            }

            _state.update {
                it.copy(id = clientId)
            }

            val askAboutAutofill = autofillServiceUseCase.askAboutAutofill(
                specificFields,
                currentState.initialServiceFields,
                currentState.serviceType
            )

            if (askAboutAutofill) {
                _state.update {
                    it.copy(showDialog = ClientScreenDialog.ConfirmAutofillServices)
                }
            } else {
                event.emit(AddEditClientEvent.OnClientSaved)
            }
        }
    }

    private fun checkSpecificFieldsBeforeSaving(
        clientId: Long,
        level: String = "",
        weight: String = ""
    ): ClientSpecificFields? {
        return when (val fields = state.value.clientSpecificFields) {
            is ClientSpecificFields.EducationClientSpecificFields -> {
                ClientSpecificFields.EducationClientSpecificFields(
                    id = fields.id,
                    clientId = clientId,
                    level = level.ifBlank { null },
                    isOnline = fields.isOnline,
                    lessonDateTimeList = fields.lessonDateTimeList
                )
            }

            is ClientSpecificFields.TattooClientSpecificFields -> {
                ClientSpecificFields.TattooClientSpecificFields(
                    id = fields.id,
                    clientId = clientId,
                    currentProject = fields.currentProject,
                    finishedProjects = fields.finishedProjects
                )
            }

            is ClientSpecificFields.SportClientSpecificFields -> {
                ClientSpecificFields.SportClientSpecificFields(
                    id = fields.id,
                    clientId = clientId,
                    weight = weight.ifBlank { null },
                    isOnline = fields.isOnline,
                    lessonDateTimeList = fields.lessonDateTimeList
                )
            }

            null -> null
        }
    }

    private fun autofillServices(ignoreCrossing: Boolean = false) {
        viewModelScope.launch {
            val state = state.value
            val titlePrefixRes = when (state.serviceType) {
                ServiceType.EDUCATION -> Res.string.service_prefix_education
                ServiceType.SPORT -> Res.string.service_prefix_sport
                ServiceType.BASE -> Res.string.service_prefix_default
                ServiceType.BEAUTY -> Res.string.service_prefix_beauty
                ServiceType.TATTOO -> Res.string.service_prefix_tattoo
            }

            val titlePrefix = getString(titlePrefixRes)
            autofillServiceUseCase.autofillServices(
                clientId = state.id,
                serviceType = state.serviceType,
                ignoreCrossing = ignoreCrossing,
                titlePrefix = titlePrefix
            )
                .fold(
                    onSuccess = {
                        when (it) {
                            ServicesAutofillResult.AutofillCompleted -> {
                                event.emit(AddEditClientEvent.OnClientSaved)
                            }

                            ServicesAutofillResult.NoAutofillNeeded -> {
                                event.emit(AddEditClientEvent.OnClientSaved)
                            }
                        }
                    },
                    onFailure = {
                        when (val error = it as? ServicesAutofillResultError) {
                            is ServicesAutofillResultError.NoClient -> {
                                event.emit(AddEditClientEvent.OnClientSaved)
                            }

                            is ServicesAutofillResultError.NoClientSpecificFields -> {
                                event.emit(AddEditClientEvent.OnClientSaved)
                            }

                            is ServicesAutofillResultError.ServicesCrossing -> {
                                _state.update {
                                    it.copy(
                                        showDialog = ClientScreenDialog.ServicesCrossing(
                                            error.services
                                        )
                                    )
                                }
                            }

                            null -> {}
                        }
                    }
                )
        }
    }
}