import Foundation
import SharedLogic

/// iOS counterpart of `ServiceDetailsScreenViewModel`.
@MainActor
final class ServiceDetailsViewModel: ObservableObject {

    typealias Sport = ServiceSpecificFields.SportServiceSpecificFields
    typealias Exercise = ServiceSpecificFields.SportServiceSpecificFieldsExercise
    typealias ExerciseSet = ServiceSpecificFields.SportServiceSpecificFieldsExerciseExerciseSet

    enum Event: Equatable { case close, statusUpdated }

    @Published private(set) var isLoading = true
    @Published private(set) var title = ""
    @Published private(set) var dateText = ""
    @Published private(set) var time = ""
    @Published private(set) var clientName = ""
    @Published private(set) var address: String?
    @Published private(set) var price: String?
    @Published private(set) var comment: String?
    @Published private(set) var isPaid = false
    @Published private(set) var isFinished = false
    @Published private(set) var specificFields: ServiceSpecificFields?
    @Published private(set) var exercisesList: [Exercise] = []
    @Published var event: Event?

    private var service = BaseService.empty

    private let getServicesUseCase = IosDi.shared.getServicesUseCase
    private let getClientsUseCase = IosDi.shared.getClientsUseCase
    private let getServiceSpecificFieldsUseCase = IosDi.shared.getServiceSpecificFieldsUseCase
    private let addEditDeleteServiceUseCase = IosDi.shared.addEditDeleteServiceUseCase
    private let addEditServiceSpecificFieldsUseCase = IosDi.shared.addEditServiceSpecificFieldsUseCase

    func load(serviceId: Int64) {
        Task {
            guard let service = await getServicesUseCase.getServiceById(serviceId: serviceId.kotlinLong).firstValue() ?? nil,
                  let client = await getClientsUseCase.getClientById(clientId: service.clientId.kotlinLong).firstValue() ?? nil
            else { return }
            self.service = service
            let fields = try? await getServiceSpecificFieldsUseCase.getSpecificField(
                serviceId: serviceId.kotlinLong, serviceType: service.serviceType)
            let exercises: [Exercise] = service.serviceType == .sport
                ? await loadExercises(clientId: client.id) : []

            title = service.title
            dateText = service.startDate.date.uiString
            time = service.getServiceTime()
            clientName = client.getFullName()
            address = service.address
            isPaid = service.isPaid
            price = service.price?.stringValue
            isFinished = service.isFinished
            comment = service.comment
            specificFields = fields
            exercisesList = exercises
            isLoading = false
        }
    }

    private func loadExercises(clientId: Int64) async -> [Exercise] {
        let services = (try? await getServicesUseCase.getServicesForClient(clientId: clientId)) ?? []
        var result: [Exercise] = []
        var seenTitles = Set<String>()
        for service in services {
            let fields = try? await getServiceSpecificFieldsUseCase.getSpecificField(
                serviceId: service.id.kotlinLong, serviceType: .sport)
            guard let sport = fields as? Sport, !sport.exercises.isEmpty else { continue }
            for exercise in sport.exercises where !seenTitles.contains(exercise.title) {
                seenTitles.insert(exercise.title)
                result.append(exercise)
            }
        }
        return result
    }

    // MARK: Education

    func setHomework(_ homework: String) {
        guard let e = specificFields as? ServiceSpecificFields.EducationServiceSpecificFields else { return }
        specificFields = ServiceSpecificFields.EducationServiceSpecificFields(
            id: e.id, serviceId: e.serviceId, isOnline: e.isOnline, homework: homework)
    }

    // MARK: Beauty / Tattoo images

    func addImages(_ uris: [String]) {
        if let b = specificFields as? ServiceSpecificFields.BeautyServiceSpecificFields {
            specificFields = ServiceSpecificFields.BeautyServiceSpecificFields(
                id: b.id, serviceId: b.serviceId, images: b.images + uris)
        } else if let t = specificFields as? ServiceSpecificFields.TattooServiceSpecificFields {
            specificFields = ServiceSpecificFields.TattooServiceSpecificFields(
                id: t.id, serviceId: t.serviceId, images: t.images + uris)
        }
    }

    func deleteImage(at index: Int) {
        if let b = specificFields as? ServiceSpecificFields.BeautyServiceSpecificFields {
            var images = b.images; guard images.indices.contains(index) else { return }; images.remove(at: index)
            specificFields = ServiceSpecificFields.BeautyServiceSpecificFields(id: b.id, serviceId: b.serviceId, images: images)
        } else if let t = specificFields as? ServiceSpecificFields.TattooServiceSpecificFields {
            var images = t.images; guard images.indices.contains(index) else { return }; images.remove(at: index)
            specificFields = ServiceSpecificFields.TattooServiceSpecificFields(id: t.id, serviceId: t.serviceId, images: images)
        }
    }

    var images: [String] {
        (specificFields as? ServiceSpecificFields.BeautyServiceSpecificFields)?.images
            ?? (specificFields as? ServiceSpecificFields.TattooServiceSpecificFields)?.images ?? []
    }

    // MARK: Sport exercises

    private var sport: Sport? { specificFields as? Sport }
    var exercises: [Exercise] { sport?.exercises ?? [] }

    private func setExercises(_ exercises: [Exercise]) {
        guard let s = sport else { return }
        specificFields = Sport(id: s.id, serviceId: s.serviceId, isOnline: s.isOnline, exercises: exercises)
    }

    func addExercise() { setExercises(exercises + [Exercise(title: "", sets: [ExerciseSet(repeats: "", weight: "")])]) }
    func deleteExercise(at index: Int) {
        var list = exercises; guard list.indices.contains(index) else { return }; list.remove(at: index); setExercises(list)
    }

    func setExerciseTitle(at index: Int, title: String, selectable: Bool) {
        var list = exercises; guard list.indices.contains(index) else { return }
        if selectable, let chosen = exercisesList.first(where: { $0.title == title }) {
            list[index] = chosen
        } else {
            list[index] = Exercise(title: title, sets: list[index].sets)
        }
        setExercises(list)
    }

    func addSet(exerciseIndex: Int) {
        var list = exercises; guard list.indices.contains(exerciseIndex) else { return }
        let e = list[exerciseIndex]
        list[exerciseIndex] = Exercise(title: e.title, sets: e.sets + [ExerciseSet(repeats: "", weight: "")])
        setExercises(list)
    }
    func deleteSet(exerciseIndex: Int, setIndex: Int) {
        var list = exercises; guard list.indices.contains(exerciseIndex) else { return }
        var sets = list[exerciseIndex].sets; guard sets.indices.contains(setIndex) else { return }
        sets.remove(at: setIndex)
        list[exerciseIndex] = Exercise(title: list[exerciseIndex].title, sets: sets)
        setExercises(list)
    }
    func setRepeats(exerciseIndex: Int, setIndex: Int, _ repeats: String) {
        mutateSet(exerciseIndex, setIndex) { ExerciseSet(repeats: repeats, weight: $0.weight) }
    }
    func setWeight(exerciseIndex: Int, setIndex: Int, _ weight: String) {
        mutateSet(exerciseIndex, setIndex) { ExerciseSet(repeats: $0.repeats, weight: weight) }
    }
    private func mutateSet(_ exerciseIndex: Int, _ setIndex: Int, _ transform: (ExerciseSet) -> ExerciseSet) {
        var list = exercises; guard list.indices.contains(exerciseIndex) else { return }
        var sets = list[exerciseIndex].sets; guard sets.indices.contains(setIndex) else { return }
        sets[setIndex] = transform(sets[setIndex])
        list[exerciseIndex] = Exercise(title: list[exerciseIndex].title, sets: sets)
        setExercises(list)
    }

    func setFormat(isOnline: Bool) {
        guard let s = sport else { return }
        specificFields = Sport(id: s.id, serviceId: s.serviceId, isOnline: isOnline, exercises: s.exercises)
    }

    // MARK: Save / status

    func updateData() {
        Task {
            guard let fields = specificFields else { return }
            try? await addEditServiceSpecificFieldsUseCase.updateSpecificField(serviceSpecificFields: fields)
            event = .close
        }
    }

    func toggleFinished() {
        Task {
            let new = reconstruct(finished: !isFinished)
            try? await addEditDeleteServiceUseCase.update(service: new)
            service = new; isFinished = new.isFinished
            event = .statusUpdated
        }
    }
    func togglePaid() {
        Task {
            let new = reconstruct(paid: !isPaid)
            try? await addEditDeleteServiceUseCase.update(service: new)
            service = new; isPaid = new.isPaid
            event = .statusUpdated
        }
    }

    private func reconstruct(paid: Bool? = nil, finished: Bool? = nil) -> BaseService {
        BaseService(
            id: service.id, title: service.title, clientId: service.clientId,
            startDate: service.startDate, endDate: service.endDate, address: service.address,
            isFinished: finished ?? service.isFinished, isPaid: paid ?? service.isPaid, price: service.price,
            currency: service.currency, comment: service.comment,
            serviceType: service.serviceType, serviceSubtype: service.serviceSubtype)
    }

    var serviceType: ServiceType { service.serviceType }
    var homework: String { (specificFields as? ServiceSpecificFields.EducationServiceSpecificFields)?.homework ?? "" }
    var isOnline: Bool { sport?.isOnline ?? false }
}
