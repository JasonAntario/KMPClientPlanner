package com.dsankovsky.kmpclientplanner.ui.screens.service_details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dsankovsky.kmpclientplanner.data.helpers.update
import com.dsankovsky.kmpclientplanner.domain.models.additional.ServiceType
import com.dsankovsky.kmpclientplanner.domain.models.specific_fields.ServiceSpecificFields
import com.dsankovsky.kmpclientplanner.domain.usecases.client.GetClientsUseCase
import com.dsankovsky.kmpclientplanner.domain.usecases.service.AddEditDeleteServiceUseCase
import com.dsankovsky.kmpclientplanner.domain.usecases.service.AddEditServiceSpecificFieldsUseCase
import com.dsankovsky.kmpclientplanner.domain.usecases.service.GetServiceSpecificFieldsUseCase
import com.dsankovsky.kmpclientplanner.domain.usecases.service.GetServicesUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ServiceDetailsScreenViewModel(
    private val getServicesUseCase: GetServicesUseCase,
    private val getClientsUseCase: GetClientsUseCase,
    private val getServiceSpecificFieldsUseCase: GetServiceSpecificFieldsUseCase,
    private val addEditDeleteServiceUseCase: AddEditDeleteServiceUseCase,
    private val addEditServiceSpecificFieldsUseCase: AddEditServiceSpecificFieldsUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(ServiceDetailsScreenState())
    val state = _state.asStateFlow()

    val event = MutableSharedFlow<ServiceDetailsScreenEvent>()

    fun handleActions(action: ServiceDetailsScreenAction) {
        when (action) {
            is ServiceDetailsScreenAction.LoadData -> loadData(action.serviceId)
            ServiceDetailsScreenAction.OnCloseScreenClicked -> {
                viewModelScope.launch {
                    event.emit(ServiceDetailsScreenEvent.OnCloseScreen)
                }
            }

            ServiceDetailsScreenAction.OnEditServiceClicked -> {
                viewModelScope.launch {
                    event.emit(ServiceDetailsScreenEvent.OpenEditServiceScreen)
                }
            }

            is ServiceDetailsScreenAction.EducationServiceAction.OnHomeworkChanged -> {
                val specificFields = getEducationSpecificFields() ?: return
                _state.update {
                    it.copy(serviceSpecificFields = specificFields.copy(homework = action.homework))
                }
            }

            is ServiceDetailsScreenAction.BeautyServiceAction.OnImagesAdded -> {
                val specificFields = getBeautySpecificFields() ?: return
                val imageList = specificFields.images.toMutableList()
                imageList.addAll(action.imageUris)
                _state.update {
                    it.copy(serviceSpecificFields = specificFields.copy(images = imageList))
                }
            }


            is ServiceDetailsScreenAction.TattooServiceAction.OnImagesAdded -> {
                val specificFields = getTattooSpecificFields() ?: return
                val imageList = specificFields.images.toMutableList()
                imageList.addAll(action.imageUris)
                _state.update {
                    it.copy(serviceSpecificFields = specificFields.copy(images = imageList))
                }
            }

            is ServiceDetailsScreenAction.BeautyServiceAction.OnImageDeleteClicked -> {
                val specificFields = getBeautySpecificFields() ?: return
                val imageList = specificFields.images.toMutableList()
                imageList.removeAt(action.imageListIndex)
                _state.update {
                    it.copy(serviceSpecificFields = specificFields.copy(images = imageList))
                }
            }

            is ServiceDetailsScreenAction.TattooServiceAction.OnImageDeleteClicked -> {
                val specificFields = getTattooSpecificFields() ?: return
                val imageList = specificFields.images.toMutableList()
                imageList.removeAt(action.imageListIndex)
                _state.update {
                    it.copy(serviceSpecificFields = specificFields.copy(images = imageList))
                }
            }

            ServiceDetailsScreenAction.SportServiceAction.OnAddExerciseClicked -> {
                val specificFields = getSportSpecificFields() ?: return
                val exercises = specificFields.exercises.toMutableList()
                exercises.add(ServiceSpecificFields.SportServiceSpecificFields.Exercise())
                _state.update {
                    it.copy(serviceSpecificFields = specificFields.copy(exercises = exercises))
                }
            }

            is ServiceDetailsScreenAction.SportServiceAction.OnDeleteExerciseClicked -> {
                val specificFields = getSportSpecificFields() ?: return
                val exercises = specificFields.exercises.toMutableList()
                exercises.removeAt(action.exerciseIndex)
                _state.update {
                    it.copy(serviceSpecificFields = specificFields.copy(exercises = exercises))
                }
            }

            is ServiceDetailsScreenAction.SportServiceAction.OnExerciseTitleChanged -> {
                val specificFields = getSportSpecificFields() ?: return
                val exercises = specificFields.exercises.toMutableList()
                val exercise = exercises[action.exerciseIndex]
                val newExercise = if (action.isSelectable) {
                    state.value.exercisesList.first { it.title == action.title }
                } else {
                    exercise.copy(title = action.title)
                }
                val newList = exercises.update(action.exerciseIndex, newExercise)
                _state.update {
                    it.copy(serviceSpecificFields = specificFields.copy(exercises = newList))
                }
            }

            is ServiceDetailsScreenAction.SportServiceAction.OnAddSetClicked -> {
                val specificFields = getSportSpecificFields() ?: return
                val exercises = specificFields.exercises.toMutableList()
                val exercise = exercises[action.exerciseIndex]
                val sets = exercise.sets.toMutableList()
                sets.add(ServiceSpecificFields.SportServiceSpecificFields.Exercise.ExerciseSet())
                val newExercises =
                    exercises.update(action.exerciseIndex, exercise.copy(sets = sets))
                _state.update {
                    it.copy(serviceSpecificFields = specificFields.copy(exercises = newExercises))
                }
            }

            is ServiceDetailsScreenAction.SportServiceAction.OnDeleteSetClicked -> {
                val specificFields = getSportSpecificFields() ?: return
                val exercises = specificFields.exercises.toMutableList()
                val exercise = exercises[action.exerciseIndex]
                val sets = exercise.sets.toMutableList()
                sets.removeAt(action.setIndex)
                val newExercises =
                    exercises.update(action.exerciseIndex, exercise.copy(sets = sets))
                _state.update {
                    it.copy(serviceSpecificFields = specificFields.copy(exercises = newExercises))
                }
            }

            is ServiceDetailsScreenAction.SportServiceAction.OnSetRepeatsChanged -> {
                val specificFields = getSportSpecificFields() ?: return
                val exercises = specificFields.exercises.toMutableList()
                val exercise = exercises[action.exerciseIndex]
                val sets = exercise.sets.toMutableList()
                val set = sets[action.setIndex]
                val newSets = sets.update(action.setIndex, set.copy(repeats = action.repeats))
                val newExercises =
                    exercises.update(action.exerciseIndex, exercise.copy(sets = newSets))
                _state.update {
                    it.copy(serviceSpecificFields = specificFields.copy(exercises = newExercises))
                }
            }

            is ServiceDetailsScreenAction.SportServiceAction.OnSetWeightChanged -> {
                val specificFields = getSportSpecificFields() ?: return
                val exercises = specificFields.exercises.toMutableList()
                val exercise = exercises[action.exerciseIndex]
                val sets = exercise.sets.toMutableList()
                val set = sets[action.setIndex]
                val newSets = sets.update(action.setIndex, set.copy(weight = action.weight))
                val newExercises =
                    exercises.update(action.exerciseIndex, exercise.copy(sets = newSets))
                _state.update {
                    it.copy(serviceSpecificFields = specificFields.copy(exercises = newExercises))
                }
            }

            ServiceDetailsScreenAction.OnUpdateDataClicked -> {
                viewModelScope.launch {
                    val fields = state.value.serviceSpecificFields ?: return@launch
                    addEditServiceSpecificFieldsUseCase.updateSpecificField(serviceSpecificFields = fields)
                    event.emit(ServiceDetailsScreenEvent.OnCloseScreen)
                }
            }

            ServiceDetailsScreenAction.OnFinishStatusChanged -> {
                updateFinishServiceStatus()
            }

            ServiceDetailsScreenAction.OnPaidStatusChanged -> {
                updatePayServiceStatus()
            }
        }
    }

    private fun updateFinishServiceStatus() {
        viewModelScope.launch {
            val currentState = state.value
            val isFinishedOld = currentState.isFinished
            addEditDeleteServiceUseCase.update(currentState.service.copy(isFinished = !isFinishedOld))
            _state.update {
                it.copy(isFinished = !isFinishedOld)
            }
            event.emit(ServiceDetailsScreenEvent.StatusUpdated)
        }
    }

    private fun updatePayServiceStatus() {
        viewModelScope.launch {
            val currentState = state.value
            val isPaidOld = currentState.isPaid
            addEditDeleteServiceUseCase.update(currentState.service.copy(isPaid = !isPaidOld))
            _state.update {
                it.copy(isPaid = !isPaidOld)
            }
            event.emit(ServiceDetailsScreenEvent.StatusUpdated)
        }
    }

    private fun loadData(serviceId: Long) {
        viewModelScope.launch {
            val service =
                getServicesUseCase.getServiceById(serviceId).firstOrNull() ?: return@launch
            val client =
                getClientsUseCase.getClientById(service.clientId).firstOrNull() ?: return@launch
            val time = service.getServiceTime()

            val specificFields =
                getServiceSpecificFieldsUseCase.getSpecificField(serviceId, service.serviceType)

            val exercises = if (service.serviceType == ServiceType.SPORT) {
                getExercisesList(client.id)
            } else {
                emptyList()
            }

            _state.update {
                it.copy(
                    isLoading = false,
                    title = service.title,
                    date = service.startDate.date,
                    time = time,
                    clientName = client.getFullName(),
                    address = service.address,
                    isPaid = service.isPaid,
                    price = service.price?.toString(),
                    isFinished = service.isFinished,
                    comment = service.comment,
                    service = service,
                    serviceSpecificFields = specificFields,
                    initialServiceSpecificFields = specificFields,
                    exercisesList = exercises
                )
            }
        }
    }

    private suspend fun getExercisesList(clientId: Long): List<ServiceSpecificFields.SportServiceSpecificFields.Exercise> {
        return getServicesUseCase.getServicesForClient(clientId)
            .mapNotNull { service ->
                getServiceSpecificFieldsUseCase.getSpecificField(service.id, ServiceType.SPORT)
            }
            .mapNotNull { it as? ServiceSpecificFields.SportServiceSpecificFields }
            .filter { it.exercises.isNotEmpty() }
            .flatMap { it.exercises }
            .distinctBy { it.title }
    }

    private fun getEducationSpecificFields(): ServiceSpecificFields.EducationServiceSpecificFields? {
        return state.value.serviceSpecificFields as? ServiceSpecificFields.EducationServiceSpecificFields
    }

    private fun getBeautySpecificFields(): ServiceSpecificFields.BeautyServiceSpecificFields? {
        return state.value.serviceSpecificFields as? ServiceSpecificFields.BeautyServiceSpecificFields
    }

    private fun getTattooSpecificFields(): ServiceSpecificFields.TattooServiceSpecificFields? {
        return state.value.serviceSpecificFields as? ServiceSpecificFields.TattooServiceSpecificFields
    }

    private fun getSportSpecificFields(): ServiceSpecificFields.SportServiceSpecificFields? {
        return state.value.serviceSpecificFields as? ServiceSpecificFields.SportServiceSpecificFields
    }
}