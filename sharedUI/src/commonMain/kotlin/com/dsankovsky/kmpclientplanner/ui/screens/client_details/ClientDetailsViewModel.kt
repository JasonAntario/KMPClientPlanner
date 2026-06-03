package com.dsankovsky.kmpclientplanner.ui.screens.client_details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dsankovsky.kmpclientplanner.data.datastore.AppSettings
import com.dsankovsky.kmpclientplanner.domain.models.specific_fields.ClientSpecificFields
import com.dsankovsky.kmpclientplanner.domain.usecases.client.AddEditClientSpecificFieldsUseCase
import com.dsankovsky.kmpclientplanner.domain.usecases.client.GetClientSpecificFieldsUseCase
import com.dsankovsky.kmpclientplanner.domain.usecases.client.GetClientsUseCase
import com.dsankovsky.kmpclientplanner.domain.usecases.service.AutofillServiceUseCase
import com.dsankovsky.kmpclientplanner.domain.usecases.service.GetServicesUseCase
import com.dsankovsky.kmpclientplanner.domain.usecases.service.ServicesAutofillResultError
import com.dsankovsky.kmpclientplanner.ui.screens.add_edit_client.ClientScreenDialog
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ClientDetailsViewModel(
    private val getClientsUseCase: GetClientsUseCase,
    private val getClientSpecificFieldsUseCase: GetClientSpecificFieldsUseCase,
    private val addEditClientSpecificFields: AddEditClientSpecificFieldsUseCase,
    private val getServicesUseCase: GetServicesUseCase,
    private val autofillServiceUseCase: AutofillServiceUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(ClientDetailsScreenState())
    val state = _state.asStateFlow()

    val event = MutableSharedFlow<ClientDetailsEvents>()

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

            ClientDetailsActions.OnUpdateDataClicked -> {
                viewModelScope.launch {
                    val fields = state.value.clientSpecificFields ?: return@launch
                    addEditClientSpecificFields.updateSpecificField(fields)
                    event.emit(ClientDetailsEvents.ClientsDataUpdated)
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
        }
    }

    private fun closeDialog() {
        _state.update { it.copy(showDialog = null) }
    }

    private fun autofillServices(ignoreCrossing: Boolean = false) {
        viewModelScope.launch {
            val state = state.value
            val lastDate = getServicesUseCase.getLastServiceForClient(state.client.id)
            autofillServiceUseCase.autofillServices(
                clientId = state.client.id,
                serviceType = state.client.serviceType,
                ignoreCrossing = ignoreCrossing,
                startDateTime = lastDate.endDate
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

    private fun loadData(clientId: Long) {
        viewModelScope.launch {
            val client = getClientsUseCase.getClientById(clientId).firstOrNull()
            if (client == null) {
                return@launch
            }

            val serviceType = client.serviceType
            val specificFields =
                getClientSpecificFieldsUseCase.getSpecificField(client.id, serviceType)

            val showHistory = getServicesUseCase.getServicesForClient(clientId).isNotEmpty()
            _state.update {
                it.copy(
                    isLoading = false,
                    clientName = client.getFullName(),
                    clientShortName = client.getShortName(),
                    phone = client.phone?.let { AppSettings.phonePrefix + it },
                    price = client.getFormattedPrice(),
                    address = client.address,
                    comment = client.comment,
                    client = client,
                    clientSpecificFields = specificFields,
                    initialClientSpecificFields = specificFields,
                    showServicesHistory = showHistory
                )
            }
        }
    }

    private fun getTattooSpecificFields(): ClientSpecificFields.TattooClientSpecificFields? {
        return state.value.clientSpecificFields as? ClientSpecificFields.TattooClientSpecificFields
    }
}