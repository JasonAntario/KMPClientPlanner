package com.dsankovsky.kmpclientplanner.ui.screens.service_details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dsankovsky.kmpclientplanner.domain.models.additional.ServiceType
import com.dsankovsky.kmpclientplanner.domain.models.base.BaseService
import com.dsankovsky.kmpclientplanner.domain.models.specific_fields.ClientSpecificFields
import com.dsankovsky.kmpclientplanner.domain.models.specific_fields.ServiceSpecificFields
import com.dsankovsky.kmpclientplanner.domain.models.specific_fields.ServiceSpecificFields.SportServiceSpecificFields.Exercise
import com.dsankovsky.kmpclientplanner.domain.usecases.client.GetClientSpecificFieldsUseCase
import com.dsankovsky.kmpclientplanner.domain.usecases.client.GetClientsUseCase
import com.dsankovsky.kmpclientplanner.domain.usecases.service.AddEditDeleteServiceUseCase
import com.dsankovsky.kmpclientplanner.domain.usecases.service.AddEditServiceSpecificFieldsUseCase
import com.dsankovsky.kmpclientplanner.domain.usecases.service.GetServiceSpecificFieldsUseCase
import com.dsankovsky.kmpclientplanner.domain.usecases.service.GetServicesUseCase
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ServiceDetailsScreenViewModel(
    private val getServicesUseCase: GetServicesUseCase,
    private val getClientsUseCase: GetClientsUseCase,
    private val getClientSpecificFieldsUseCase: GetClientSpecificFieldsUseCase,
    private val getServiceSpecificFieldsUseCase: GetServiceSpecificFieldsUseCase,
    private val addEditDeleteServiceUseCase: AddEditDeleteServiceUseCase,
    private val addEditServiceSpecificFieldsUseCase: AddEditServiceSpecificFieldsUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(ServiceDetailsScreenState())
    val state = _state.asStateFlow()

    val event = MutableSharedFlow<ServiceDetailsScreenEvent>()

    /** Одна панель переиспользуется под разные услуги — прошлую подписку надо снять. */
    private var serviceJob: Job? = null

    /**
     * Очередь сохранения категорийных полей. Кнопки «Обновить данные» в новом дизайне нет:
     * домашнее задание и упражнения пишутся сами, но не на каждое нажатие клавиши.
     */
    private val pendingSave = MutableStateFlow<ServiceSpecificFields?>(null)

    init {
        @OptIn(FlowPreview::class)
        viewModelScope.launch {
            pendingSave
                .filterNotNull()
                .debounce(SaveDebounceMillis)
                .collect { addEditServiceSpecificFieldsUseCase.updateSpecificField(it) }
        }
    }

    fun handleActions(action: ServiceDetailsScreenAction) {
        when (action) {
            is ServiceDetailsScreenAction.LoadData -> loadData(action.serviceId)

            ServiceDetailsScreenAction.OnEditServiceClicked -> {
                viewModelScope.launch {
                    event.emit(ServiceDetailsScreenEvent.OpenEditServiceScreen)
                }
            }

            ServiceDetailsScreenAction.OnDeleteServiceClicked -> {
                _state.update { it.copy(dialog = ServiceDetailsDialog.ConfirmDeleting) }
            }

            ServiceDetailsScreenAction.OnDeleteServiceConfirmed -> {
                viewModelScope.launch {
                    _state.update { it.copy(dialog = null) }
                    addEditDeleteServiceUseCase.deleteService(state.value.service.id)
                    event.emit(ServiceDetailsScreenEvent.ServiceDeleted)
                }
            }

            ServiceDetailsScreenAction.CloseDialog -> {
                _state.update { it.copy(dialog = null) }
            }

            ServiceDetailsScreenAction.OnFinishStatusChanged -> updateStatus(finished = true)
            ServiceDetailsScreenAction.OnPaidStatusChanged -> updateStatus(finished = false)

            is ServiceDetailsScreenAction.EducationServiceAction.OnHomeworkChanged -> {
                val fields = state.value.educationFields ?: return
                updateSpecificFields(fields.copy(homework = action.homework))
            }

            is ServiceDetailsScreenAction.BeautyServiceAction.OnImagesAdded -> {
                val fields = state.value.beautyFields ?: return
                updateSpecificFields(fields.copy(images = fields.images + action.imageUris))
            }

            is ServiceDetailsScreenAction.BeautyServiceAction.OnImageDeleteClicked -> {
                val fields = state.value.beautyFields ?: return
                updateSpecificFields(
                    fields.copy(images = fields.images.withoutAt(action.imageListIndex)),
                )
            }

            is ServiceDetailsScreenAction.TattooServiceAction.OnImagesAdded -> {
                val fields = state.value.tattooFields ?: return
                updateSpecificFields(fields.copy(images = fields.images + action.imageUris))
            }

            is ServiceDetailsScreenAction.TattooServiceAction.OnImageDeleteClicked -> {
                val fields = state.value.tattooFields ?: return
                updateSpecificFields(
                    fields.copy(images = fields.images.withoutAt(action.imageListIndex)),
                )
            }

            ServiceDetailsScreenAction.SportServiceAction.OnNewExerciseClicked -> {
                _state.update { it.copy(dialog = ServiceDetailsDialog.NewExercise()) }
            }

            ServiceDetailsScreenAction.SportServiceAction.OnPickExerciseClicked -> {
                _state.update { it.copy(dialog = ServiceDetailsDialog.PickExercise) }
            }

            is ServiceDetailsScreenAction.SportServiceAction.OnKnownExercisePicked -> {
                // Значения прошлого выполнения подставляются в форму, а не сразу в таблицу:
                // подходы и вес обычно правят.
                _state.update {
                    it.copy(dialog = ServiceDetailsDialog.NewExercise(prefill = action.exercise))
                }
            }

            is ServiceDetailsScreenAction.SportServiceAction.OnExerciseAdded -> {
                val fields = state.value.sportFields ?: return
                val exercise = Exercise(
                    title = action.title,
                    sets = List(action.setsCount.coerceAtLeast(1)) {
                        Exercise.ExerciseSet(repeats = action.repeats, weight = action.weight)
                    },
                )
                _state.update { it.copy(dialog = null) }
                updateSpecificFields(fields.copy(exercises = fields.exercises + exercise))
            }

            is ServiceDetailsScreenAction.SportServiceAction.OnDeleteExerciseClicked -> {
                val fields = state.value.sportFields ?: return
                updateSpecificFields(
                    fields.copy(exercises = fields.exercises.withoutAt(action.exerciseIndex)),
                )
            }
        }
    }

    /**
     * Правки категорийных полей: состояние обновляется сразу, база — с задержкой.
     * Таблица упражнений пересчитывается тут же, чтобы «Прошлый раз» не отставало.
     */
    private fun updateSpecificFields(fields: ServiceSpecificFields) {
        _state.update { current ->
            current.copy(
                serviceSpecificFields = fields,
                exerciseRows = (fields as? ServiceSpecificFields.SportServiceSpecificFields)
                    ?.exercises
                    ?.toRowsKeepingPrevious(current.exerciseRows)
                    ?: current.exerciseRows,
            )
        }
        pendingSave.value = fields
    }

    private fun updateStatus(finished: Boolean) {
        viewModelScope.launch {
            val service = state.value.service
            val updated = if (finished) {
                service.copy(isFinished = !service.isFinished)
            } else {
                service.copy(isPaid = !service.isPaid)
            }
            addEditDeleteServiceUseCase.update(updated)
            event.emit(ServiceDetailsScreenEvent.StatusUpdated)
        }
    }

    private fun loadData(serviceId: Long) {
        serviceJob?.cancel()
        serviceJob = viewModelScope.launch {
            // Услуга — потоком: статусы переключаются и из списка слева, и здесь.
            getServicesUseCase.getServiceById(serviceId).filterNotNull().collect { service ->
                val client = getClientsUseCase.getClientById(service.clientId).firstOrNull()
                val fields = getServiceSpecificFieldsUseCase
                    .getSpecificField(serviceId, service.serviceType)

                val previousExercises = if (service.serviceType == ServiceType.SPORT) {
                    previousExercises(service)
                } else {
                    emptyList()
                }

                val references = if (service.serviceType == ServiceType.TATTOO) {
                    val clientFields = getClientSpecificFieldsUseCase
                        .getSpecificField(service.clientId, ServiceType.TATTOO)
                    (clientFields as? ClientSpecificFields.TattooClientSpecificFields)
                        ?.currentProject
                        ?.imageUrls
                        .orEmpty()
                } else {
                    emptyList()
                }

                val exercises = (fields as? ServiceSpecificFields.SportServiceSpecificFields)
                    ?.exercises
                    .orEmpty()

                _state.update {
                    it.copy(
                        isLoading = false,
                        title = service.title,
                        date = service.startDate.date,
                        startDateTime = service.startDate,
                        endDateTime = service.endDate,
                        time = service.getServiceTime(),
                        clientName = client?.getFullName().orEmpty(),
                        address = service.address,
                        isOnline = fields.isOnline(),
                        isPaid = service.isPaid,
                        isFinished = service.isFinished,
                        comment = service.comment,
                        service = service,
                        // Локальные правки полей не перетираем: они уже в очереди сохранения.
                        serviceSpecificFields = it.serviceSpecificFields
                            ?.takeIf { current -> current.sameKind(fields) && it.service.id == serviceId }
                            ?: fields,
                        exerciseRows = exercises.toRows(previousExercises),
                        knownExercises = previousExercises,
                        referenceImages = references,
                    )
                }
            }
        }
    }

    /**
     * Последнее выполнение каждого упражнения клиента до текущего занятия.
     *
     * Именно это в макете 06 стоит в колонке «Прошлый раз» и даёт дельту веса; заодно это
     * список для «Выбрать из созданных».
     */
    private suspend fun previousExercises(current: BaseService): List<KnownExercise> {
        return getServicesUseCase.getServicesForClient(current.clientId)
            .filter { it.id != current.id && it.startDate < current.startDate }
            .sortedByDescending { it.startDate }
            .flatMap { service ->
                val fields = getServiceSpecificFieldsUseCase
                    .getSpecificField(service.id, ServiceType.SPORT)
                (fields as? ServiceSpecificFields.SportServiceSpecificFields)?.exercises.orEmpty()
            }
            .mapNotNull { exercise ->
                val set = exercise.sets.firstOrNull() ?: return@mapNotNull null
                KnownExercise(
                    title = exercise.title,
                    setsCount = exercise.sets.size,
                    repeats = set.repeats,
                    weight = set.weight,
                )
            }
            // Занятия отсортированы от новых к старым, поэтому первое вхождение — последнее выполнение.
            .distinctBy { it.title }
    }
}

private const val SaveDebounceMillis = 600L

private val ServiceDetailsScreenState.educationFields
    get() = serviceSpecificFields as? ServiceSpecificFields.EducationServiceSpecificFields

private val ServiceDetailsScreenState.beautyFields
    get() = serviceSpecificFields as? ServiceSpecificFields.BeautyServiceSpecificFields

private val ServiceDetailsScreenState.tattooFields
    get() = serviceSpecificFields as? ServiceSpecificFields.TattooServiceSpecificFields

private val ServiceDetailsScreenState.sportFields
    get() = serviceSpecificFields as? ServiceSpecificFields.SportServiceSpecificFields

private fun ServiceSpecificFields?.isOnline(): Boolean = when (this) {
    is ServiceSpecificFields.EducationServiceSpecificFields -> isOnline
    is ServiceSpecificFields.SportServiceSpecificFields -> isOnline
    else -> false
}

/** Поля одной категории — чтобы не подменить локальную правку данными из базы. */
private fun ServiceSpecificFields.sameKind(other: ServiceSpecificFields?): Boolean =
    other != null && this::class == other::class

private fun <T> List<T>.withoutAt(index: Int): List<T> =
    if (index in indices) toMutableList().apply { removeAt(index) } else this

/** Строки таблицы: текущие значения плюс прошлое выполнение того же упражнения. */
private fun List<Exercise>.toRows(previous: List<KnownExercise>): List<ExerciseRow> =
    map { exercise ->
        val set = exercise.sets.firstOrNull()
        val last = previous.firstOrNull { it.title == exercise.title }
        ExerciseRow(
            title = exercise.title,
            setsCount = exercise.sets.size,
            repeats = set?.repeats.orEmpty(),
            weight = set?.weight.orEmpty(),
            previousSetsCount = last?.setsCount,
            previousRepeats = last?.repeats,
            previousWeight = last?.weight,
            weightDelta = weightDelta(set?.weight, last?.weight),
        )
    }

/** Строки таблицы после локальной правки — прошлые значения берём из уже посчитанных. */
private fun List<Exercise>.toRowsKeepingPrevious(existingRows: List<ExerciseRow>): List<ExerciseRow> =
    toRows(
        existingRows.mapNotNull { row ->
            val setsCount = row.previousSetsCount ?: return@mapNotNull null
            KnownExercise(
                title = row.title,
                setsCount = setsCount,
                repeats = row.previousRepeats.orEmpty(),
                weight = row.previousWeight.orEmpty(),
            )
        },
    )

/** Вес приходит свободным текстом («60», «60 кг», «32,5») — берём первое число. */
private fun weightDelta(current: String?, previous: String?): Float? {
    val now = current.toWeightOrNull() ?: return null
    val before = previous.toWeightOrNull() ?: return null
    val delta = now - before
    return delta.takeIf { it != 0f }
}

private fun String?.toWeightOrNull(): Float? = this
    ?.replace(',', '.')
    ?.filter { it.isDigit() || it == '.' }
    ?.toFloatOrNull()
