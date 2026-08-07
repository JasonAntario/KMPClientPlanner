package com.dsankovsky.kmpclientplanner.ui.screens.client_details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dsankovsky.kmpclientplanner.data.datastore.AppSettings
import com.dsankovsky.kmpclientplanner.domain.models.additional.ServiceType
import com.dsankovsky.kmpclientplanner.domain.models.specific_fields.ClientSpecificFields
import com.dsankovsky.kmpclientplanner.domain.models.base.BaseService
import com.dsankovsky.kmpclientplanner.domain.usecases.client.AddEditClientSpecificFieldsUseCase
import com.dsankovsky.kmpclientplanner.domain.usecases.client.AddEditDeleteClientUseCase
import com.dsankovsky.kmpclientplanner.domain.usecases.client.GetClientSpecificFieldsUseCase
import com.dsankovsky.kmpclientplanner.domain.usecases.client.GetClientsUseCase
import com.dsankovsky.kmpclientplanner.domain.usecases.service.AutofillServiceUseCase
import com.dsankovsky.kmpclientplanner.domain.usecases.service.GetServicesUseCase
import com.dsankovsky.kmpclientplanner.domain.usecases.service.ServicesAutofillResultError
import com.dsankovsky.kmpclientplanner.ui.screens.add_edit_client.ClientScreenDialog
import kmpclientplanner.sharedui.generated.resources.Res
import kmpclientplanner.sharedui.generated.resources.service_prefix_beauty
import kmpclientplanner.sharedui.generated.resources.service_prefix_default
import kmpclientplanner.sharedui.generated.resources.service_prefix_education
import kmpclientplanner.sharedui.generated.resources.service_prefix_sport
import kmpclientplanner.sharedui.generated.resources.service_prefix_tattoo
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString

class ClientDetailsViewModel(
    private val getClientsUseCase: GetClientsUseCase,
    private val getClientSpecificFieldsUseCase: GetClientSpecificFieldsUseCase,
    private val addEditClientSpecificFields: AddEditClientSpecificFieldsUseCase,
    private val addEditDeleteClientUseCase: AddEditDeleteClientUseCase,
    private val getServicesUseCase: GetServicesUseCase,
    private val autofillServiceUseCase: AutofillServiceUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(ClientDetailsScreenState())
    val state = _state.asStateFlow()

    val event = MutableSharedFlow<ClientDetailsEvents>()

    /**
     * Подписки на текущего клиента и его занятия: одна панель деталей переиспользуется
     * под разных клиентов, поэтому при смене id прошлые подписки надо снять.
     */
    private var clientJob: Job? = null
    private var servicesJob: Job? = null

    fun handleActions(action: ClientDetailsActions) {
        when (action) {
            is ClientDetailsActions.LoadData -> loadData(action.clientId)
            ClientDetailsActions.OnCloseScreenClicked -> {
                viewModelScope.launch {
                    event.emit(ClientDetailsEvents.OnCloseScreen)
                }
            }

            ClientDetailsActions.OnEditClientClicked -> {
                viewModelScope.launch {
                    event.emit(ClientDetailsEvents.OpenEditClientScreen)
                }
            }

            is ClientDetailsActions.TattooClientAction.OnImagesAdded -> {
                val specificFields = getTattooSpecificFields() ?: return
                val currentProject = specificFields.currentProject
                val imageList = currentProject.imageUrls.toMutableList()
                imageList.addAll(action.imageUris)
                val newField = specificFields.copy(
                    currentProject = currentProject.copy(
                        imageUrls = imageList
                    )
                )
                _state.update {
                    it.copy(
                        clientSpecificFields = newField
                    )
                }
            }

            is ClientDetailsActions.TattooClientAction.OnImageDeleteClicked -> {
                val specificFields = getTattooSpecificFields() ?: return
                val currentProject = specificFields.currentProject
                val imageList = currentProject.imageUrls.toMutableList()
                imageList.removeAt(action.imageListIndex)
                val newField = specificFields.copy(
                    currentProject = currentProject.copy(
                        imageUrls = imageList
                    )
                )
                _state.update {
                    it.copy(
                        clientSpecificFields = newField
                    )
                }
            }

            is ClientDetailsActions.TattooClientAction.OnImagesAddedInFinishedProject -> {
                val specificFields = getTattooSpecificFields() ?: return
                val projectList = specificFields.finishedProjects.toMutableList()
                val project = projectList[action.projectIndex]
                val imageList = project.imageUrls.toMutableList()
                imageList.addAll(action.imageUris)
                projectList.removeAt(action.projectIndex)
                projectList.add(action.projectIndex, project.copy(imageUrls = imageList))
                val newField = specificFields.copy(
                    finishedProjects = projectList
                )
                _state.update {
                    it.copy(
                        clientSpecificFields = newField
                    )
                }
            }

            is ClientDetailsActions.TattooClientAction.OnImageDeleteClickedInFinishedProject -> {
                val specificFields = getTattooSpecificFields() ?: return
                val projectList = specificFields.finishedProjects.toMutableList()
                val project = projectList[action.projectIndex]
                val imageList = project.imageUrls.toMutableList()
                imageList.removeAt(action.imageListIndex)
                projectList.removeAt(action.projectIndex)
                projectList.add(action.projectIndex, project.copy(imageUrls = imageList))
                val newField = specificFields.copy(finishedProjects = projectList)
                _state.update {
                    it.copy(
                        clientSpecificFields = newField
                    )
                }
            }

            ClientDetailsActions.TattooClientAction.OnFinishProjectClicked -> {
                val specificFields = getTattooSpecificFields() ?: return
                val currentProject = specificFields.currentProject
                val finishedProjects = specificFields.finishedProjects.toMutableList()
                finishedProjects.add(currentProject)
                val newField = specificFields.copy(
                    finishedProjects = finishedProjects,
                    currentProject = ClientSpecificFields.TattooClientSpecificFields.TattooProject()
                )
                _state.update {
                    it.copy(
                        clientSpecificFields = newField
                    )
                }
            }

            ClientDetailsActions.FillServicesClicked -> {
                viewModelScope.launch {
                    val currentState = state.value
                    val fields = currentState.clientSpecificFields

                    val askAboutAutofill = autofillServiceUseCase.askAboutAutofill(
                        specificFields = fields,
                        serviceType = currentState.client.serviceType
                    )

                    if (askAboutAutofill) {
                        _state.update {
                            it.copy(showDialog = ClientScreenDialog.ConfirmAutofillServices)
                        }
                    }
                }
            }

            ClientDetailsActions.CloseClientDialog -> {
                closeDialog()
            }

            ClientDetailsActions.OnAutofillConfirmClicked -> {
                closeDialog()
                autofillServices()
            }

            ClientDetailsActions.OnAutofillDismissClicked -> {
                closeDialog()
            }

            ClientDetailsActions.OnAutofillWithCrossingConfirmClicked -> {
                closeDialog()
                autofillServices(true)
            }

            ClientDetailsActions.ShowServicesHistory -> {
                viewModelScope.launch {
                    event.emit(ClientDetailsEvents.OpenServicesHistory)
                }
            }

            ClientDetailsActions.OnDeleteClientClicked -> {
                _state.update { it.copy(showDialog = ClientScreenDialog.ConfirmClientDeleting) }
            }

            ClientDetailsActions.OnDeleteClientConfirmed -> {
                closeDialog()
                deleteClient()
            }
        }
    }

    private fun deleteClient() {
        viewModelScope.launch {
            addEditDeleteClientUseCase.deleteClient(state.value.client.id)
            event.emit(ClientDetailsEvents.ClientDeleted)
        }
    }

    private fun closeDialog() {
        _state.update { it.copy(showDialog = null) }
    }

    private fun autofillServices(ignoreCrossing: Boolean = false) {
        viewModelScope.launch {
            val state = state.value
            val lastDate = getServicesUseCase.getLastServiceForClient(state.client.id)

            val titlePrefixRes = when (state.client.serviceType) {
                ServiceType.EDUCATION -> Res.string.service_prefix_education
                ServiceType.SPORT -> Res.string.service_prefix_sport
                ServiceType.BASE -> Res.string.service_prefix_default
                ServiceType.BEAUTY -> Res.string.service_prefix_beauty
                ServiceType.TATTOO -> Res.string.service_prefix_tattoo
            }

            val titlePrefix = getString(titlePrefixRes)

            autofillServiceUseCase.autofillServices(
                clientId = state.client.id,
                serviceType = state.client.serviceType,
                ignoreCrossing = ignoreCrossing,
                startDateTime = lastDate.endDate,
                titlePrefix = titlePrefix
            )
                .fold(
                    onSuccess = {
                        event.emit(ClientDetailsEvents.AutofillCompleted)
                    },
                    onFailure = {
                        when (val error = it as? ServicesAutofillResultError) {
                            is ServicesAutofillResultError.ServicesCrossing -> {
                                _state.update {
                                    it.copy(
                                        showDialog = ClientScreenDialog.ServicesCrossing(
                                            error.services
                                        )
                                    )
                                }
                            }

                            else -> {}
                        }
                    }
                )
        }
    }

    /**
     * Панель деталей теперь всегда на экране, а редактирование идёт в модалке поверх неё,
     * поэтому клиента слушаем потоком: после сохранения формы карточка обновляется сама.
     */
    private fun loadData(clientId: Long) {
        clientJob?.cancel()
        clientJob = viewModelScope.launch {
            getClientsUseCase.getClientById(clientId).collect { client ->
                if (client == null) return@collect
                val specificFields =
                    getClientSpecificFieldsUseCase.getSpecificField(client.id, client.serviceType)

                _state.update {
                    it.copy(
                        isLoading = false,
                        clientName = client.getFullName(),
                        clientShortName = client.getShortName(),
                        phone = client.phone?.let { phone -> AppSettings.phonePrefix + phone },
                        address = client.address,
                        comment = client.comment,
                        client = client,
                        clientSpecificFields = specificFields,
                        initialClientSpecificFields = specificFields,
                    )
                }
            }
        }
        observeServices(clientId)
    }

    /** Метрики карточки: долг, предоплаты, «клиент с …» и кнопка истории занятий. */
    private fun observeServices(clientId: Long) {
        servicesJob?.cancel()
        servicesJob = viewModelScope.launch {
            getServicesUseCase.getServicesForClientFlow(clientId).collect { services ->
                _state.update {
                    it.copy(
                        unpaidTotals = services.filterNot { service -> service.isPaid }.sumByCurrency(),
                        prepaidCount = services.count { service -> service.isPaid && !service.isFinished },
                        servicesCount = services.size,
                        firstServiceDate = services.minOfOrNull { service -> service.startDate.date },
                        showServicesHistory = services.isNotEmpty(),
                    )
                }
            }
        }
    }

    private fun getTattooSpecificFields(): ClientSpecificFields.TattooClientSpecificFields? {
        return state.value.clientSpecificFields as? ClientSpecificFields.TattooClientSpecificFields
    }
}

/** Занятия одного клиента могут быть в разных валютах — складываем каждую отдельно. */
private fun List<BaseService>.sumByCurrency(): List<ClientAmount> =
    filter { (it.price ?: 0f) > 0f }
        .groupBy { it.currency }
        .map { (currency, services) ->
            ClientAmount(services.sumOf { (it.price ?: 0f).toDouble() }.toFloat(), currency)
        }
        .sortedByDescending { it.money }