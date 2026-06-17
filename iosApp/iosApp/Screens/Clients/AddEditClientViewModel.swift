import Foundation
import SharedLogic

/// iOS counterpart of `AddEditClientViewModel`. Logic mirrors the Compose view model:
/// load -> edit base + type-specific fields -> save -> (optionally) ask about autofill.
@MainActor
final class AddEditClientViewModel: ObservableObject {

    enum Dialog: Identifiable, Equatable {
        case confirmAutofill
        case servicesCrossing([BaseService])
        case confirmDeleting
        var id: String {
            switch self {
            case .confirmAutofill: return "autofill"
            case .servicesCrossing: return "crossing"
            case .confirmDeleting: return "deleting"
            }
        }
        static func == (l: Dialog, r: Dialog) -> Bool { l.id == r.id }
    }

    enum Event: Equatable { case saved, dismissed, deleted }

    // Base fields
    @Published var name = ""
    @Published var surname = ""
    @Published var address = ""
    @Published var phone = ""
    @Published var price = ""
    @Published var comment = ""
    @Published var currency: CurrencyItem = CurrencyItem.byn
    @Published var level = ""
    @Published var weight = ""

    @Published private(set) var isLoading = true
    @Published private(set) var isEdit = false
    @Published private(set) var serviceType: ServiceType = .base
    @Published private(set) var addressList: [String] = []
    @Published private(set) var specificFields: ClientSpecificFields?
    @Published var dialog: Dialog?
    @Published var event: Event?

    let currencies = CurrencyItem.all

    private var id: Int64 = 0
    private var initialServiceFields: ClientSpecificFields?

    private let addEditDeleteClientUseCase = IosDi.shared.addEditDeleteClientUseCase
    private let getClientsUseCase = IosDi.shared.getClientsUseCase
    private let getClientSpecificFieldsUseCase = IosDi.shared.getClientSpecificFieldsUseCase
    private let getServicesUseCase = IosDi.shared.getServicesUseCase
    private let addEditClientSpecificFields = IosDi.shared.addEditClientSpecificFieldsUseCase
    private let autofillServiceUseCase = IosDi.shared.autofillServiceUseCase
    private let appSettings = IosDi.shared.appSettings

    // MARK: Load

    func load(clientId: Int64?) {
        Task {
            let client = await getClientsUseCase.getClientById(clientId: clientId.kotlinLong).firstValue() ?? nil
            id = client?.id ?? 0
            isEdit = client != nil
            name = client?.name ?? ""
            surname = client?.surname ?? ""
            address = client?.address ?? ""
            price = client?.price?.stringValue ?? ""
            currency = client?.currency ?? CurrencyItem.byn
            phone = client?.phone ?? ""
            comment = client?.comment ?? ""

            let fromServices = (try? await getServicesUseCase.getAddressesList()) ?? []
            let fromClients = (try? await getClientsUseCase.getAddressesList()) ?? []
            addressList = Array(NSOrderedSet(array: fromServices + fromClients).array as? [String] ?? [])

            let savedType = try? await appSettings.getServiceType()
            let type = client?.serviceType ?? savedType ?? ServiceType.base
            serviceType = type
            let fields = try? await getClientSpecificFieldsUseCase.getSpecificField(
                clientId: client?.id.kotlinLong, serviceType: type
            )
            specificFields = fields
            initialServiceFields = fields
            level = (fields as? ClientSpecificFields.EducationClientSpecificFields)?.level ?? ""
            weight = (fields as? ClientSpecificFields.SportClientSpecificFields)?.weight ?? ""
            isLoading = false
        }
    }

    // MARK: Education / Sport specific-field editing

    private var education: ClientSpecificFields.EducationClientSpecificFields? {
        specificFields as? ClientSpecificFields.EducationClientSpecificFields
    }
    private var sport: ClientSpecificFields.SportClientSpecificFields? {
        specificFields as? ClientSpecificFields.SportClientSpecificFields
    }

    var lessons: [ServiceDateTime] {
        education?.lessonDateTimeList ?? sport?.lessonDateTimeList ?? []
    }
    var isOnline: Bool {
        education?.isOnline ?? sport?.isOnline ?? false
    }

    func setFormat(isOnline: Bool) {
        if let e = education {
            specificFields = ClientSpecificFields.EducationClientSpecificFields(
                id: e.id, clientId: e.clientId, level: e.level, isOnline: isOnline, lessonDateTimeList: e.lessonDateTimeList)
        } else if let s = sport {
            specificFields = ClientSpecificFields.SportClientSpecificFields(
                id: s.id, clientId: s.clientId, weight: s.weight, isOnline: isOnline, lessonDateTimeList: s.lessonDateTimeList)
        }
    }

    func addLesson() {
        updateLessons(lessons + [makeServiceDateTime()])
    }

    func deleteLesson(at index: Int) {
        var list = lessons
        guard list.indices.contains(index) else { return }
        list.remove(at: index)
        updateLessons(list)
    }

    func setDayOfWeek(at index: Int, _ day: Kotlinx_datetimeDayOfWeek) {
        mutateLesson(at: index) { ServiceDateTime(dayOfWeek: day, time: $0.time, uiTime: $0.uiTime, duration: $0.duration) }
    }
    func setTime(at index: Int, _ time: LocalTime) {
        mutateLesson(at: index) { ServiceDateTime(dayOfWeek: $0.dayOfWeek, time: time, uiTime: $0.uiTime, duration: $0.duration) }
    }
    func setDuration(at index: Int, _ duration: String) {
        mutateLesson(at: index) { ServiceDateTime(dayOfWeek: $0.dayOfWeek, time: $0.time, uiTime: $0.uiTime, duration: duration) }
    }

    private func mutateLesson(at index: Int, _ transform: (ServiceDateTime) -> ServiceDateTime) {
        var list = lessons
        guard list.indices.contains(index) else { return }
        list[index] = transform(list[index])
        updateLessons(list)
    }

    private func updateLessons(_ list: [ServiceDateTime]) {
        if let e = education {
            specificFields = ClientSpecificFields.EducationClientSpecificFields(
                id: e.id, clientId: e.clientId, level: e.level, isOnline: e.isOnline, lessonDateTimeList: list)
        } else if let s = sport {
            specificFields = ClientSpecificFields.SportClientSpecificFields(
                id: s.id, clientId: s.clientId, weight: s.weight, isOnline: s.isOnline, lessonDateTimeList: list)
        }
    }

    private func makeServiceDateTime() -> ServiceDateTime {
        ServiceDateTime(
            dayOfWeek: DateTimeExtKt.getCurrentDayOfWeek(),
            time: DateTimeExtKt.getStartDateTime().time,
            uiTime: "",
            duration: "1")
    }

    // MARK: Save / delete / autofill

    func save() {
        Task {
            let priceValue = Float(price)
            let client = BaseClient(
                id: id,
                name: name.trimmingCharacters(in: .whitespaces),
                surname: surname.trimmedOrNil,
                address: address.trimmedOrNil,
                phone: phone.trimmedOrNil,
                price: (priceValue == nil || priceValue == 0) ? nil : KotlinFloat(value: priceValue!),
                currency: currency,
                comment: comment.trimmedOrNil,
                serviceType: serviceType,
                serviceSubtype: nil)

            let clientId: Int64
            if isEdit {
                try? await addEditDeleteClientUseCase.update(client: client)
                clientId = client.id
            } else {
                clientId = (try? await addEditDeleteClientUseCase.addClient(client: client))?.int64Value ?? 0
            }

            let fields = buildSpecificFields(clientId: clientId)
            if let fields {
                if isEdit {
                    try? await addEditClientSpecificFields.updateSpecificField(clientSpecificFields: fields)
                } else {
                    _ = try? await addEditClientSpecificFields.addSpecificField(clientSpecificFields: fields)
                }
            }
            id = clientId

            let ask = autofillServiceUseCase.askAboutAutofill(
                specificFields: fields, initialSpecificFields: initialServiceFields, serviceType: serviceType)
            if ask {
                dialog = .confirmAutofill
            } else {
                event = .saved
            }
        }
    }

    private func buildSpecificFields(clientId: Int64) -> ClientSpecificFields? {
        switch specificFields {
        case let f as ClientSpecificFields.EducationClientSpecificFields:
            return ClientSpecificFields.EducationClientSpecificFields(
                id: f.id, clientId: clientId, level: level.isEmpty ? nil : level,
                isOnline: f.isOnline, lessonDateTimeList: f.lessonDateTimeList)
        case let f as ClientSpecificFields.TattooClientSpecificFields:
            return ClientSpecificFields.TattooClientSpecificFields(
                id: f.id, clientId: clientId, currentProject: f.currentProject, finishedProjects: f.finishedProjects)
        case let f as ClientSpecificFields.SportClientSpecificFields:
            return ClientSpecificFields.SportClientSpecificFields(
                id: f.id, clientId: clientId, weight: weight.isEmpty ? nil : weight,
                isOnline: f.isOnline, lessonDateTimeList: f.lessonDateTimeList)
        default:
            return nil
        }
    }

    func requestDelete() { dialog = .confirmDeleting }

    func confirmDelete() {
        dialog = nil
        Task {
            try? await addEditDeleteClientUseCase.deleteClient(clientId: id)
            event = .deleted
        }
    }

    func dismissDialog() { dialog = nil }

    func onAutofillConfirm() { dialog = nil; autofill(ignoreCrossing: false) }
    func onAutofillWithCrossingConfirm() { dialog = nil; autofill(ignoreCrossing: true) }
    func onAutofillDismiss() { dialog = nil; event = .saved }

    private func autofill(ignoreCrossing: Bool) {
        Task {
            guard let outcome = try? await autofillServiceUseCase.autofillServicesIos(
                clientId: id, serviceType: serviceType, ignoreCrossing: ignoreCrossing,
                titlePrefix: serviceType.titlePrefix, startDateTime: KotlinDateTime.now())
            else { event = .saved; return }
            switch outcome {
            case let crossing as IosAutofillOutcome.Crossing:
                dialog = .servicesCrossing(crossing.services)
            default:
                event = .saved
            }
        }
    }

    func close() { event = .dismissed }
}

extension String {
    /// Trimmed, or nil if blank — mirrors Kotlin `trim().ifBlank { null }`.
    var trimmedOrNil: String? {
        let t = trimmingCharacters(in: .whitespacesAndNewlines)
        return t.isEmpty ? nil : t
    }
}
