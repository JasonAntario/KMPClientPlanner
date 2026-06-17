import Foundation
import SharedLogic

/// iOS counterpart of `AddEditServiceViewModel`.
@MainActor
final class AddEditServiceViewModel: ObservableObject {

    enum Dialog: Equatable {
        case servicesCrossing([BaseService])
        case confirmDeleting
        static func == (l: Dialog, r: Dialog) -> Bool {
            switch (l, r) {
            case (.confirmDeleting, .confirmDeleting): return true
            case (.servicesCrossing, .servicesCrossing): return true
            default: return false
            }
        }
    }

    enum Event: Equatable { case saved, dismissed, deleted }

    @Published var title = ""
    @Published var address = ""
    @Published var comment = ""
    @Published var price = ""
    @Published var currency: CurrencyItem = CurrencyItem.byn
    @Published var startDateTime: LocalDateTime = DateTimeExtKt.getStartDateTime()
    @Published var endDateTime: LocalDateTime = DateTimeExtKt.getStartDateTime().addHours(hours: Int32(1))
    @Published var isPaid = false
    @Published var isFinished = false

    @Published private(set) var isLoading = true
    @Published private(set) var isEdit = false
    @Published private(set) var serviceType: ServiceType = .base
    @Published private(set) var client: BaseClient?
    @Published private(set) var clientsList: [BaseClient] = []
    @Published private(set) var specificFields: ServiceSpecificFields?
    @Published var dialog: Dialog?
    @Published var event: Event?

    let currencies = CurrencyItem.all
    private var id: Int64 = 0

    private let getServicesUseCase = IosDi.shared.getServicesUseCase
    private let addEditDeleteServiceUseCase = IosDi.shared.addEditDeleteServiceUseCase
    private let getServiceSpecificFieldsUseCase = IosDi.shared.getServiceSpecificFieldsUseCase
    private let getClientSpecificFieldsUseCase = IosDi.shared.getClientSpecificFieldsUseCase
    private let addEditServiceSpecificFieldsUseCase = IosDi.shared.addEditServiceSpecificFieldsUseCase
    private let checkServiceCrossingUseCase = IosDi.shared.checkServiceCrossingUseCase
    private let getClientsUseCase = IosDi.shared.getClientsUseCase
    private let appSettings = IosDi.shared.appSettings

    var isFinishButtonEnabled: Bool { !title.trimmingCharacters(in: .whitespaces).isEmpty && client != nil }

    // MARK: Load

    func load(serviceId: Int64?) {
        Task {
            let startTime = DateTimeExtKt.getStartDateTime()
            let service = await getServicesUseCase.getServiceById(serviceId: serviceId.kotlinLong).firstValue() ?? nil
            let clients = await getClientsUseCase.getAllClients().firstValue() ?? []
            let client = clients.first { $0.id == service?.clientId }
            let savedType = try? await appSettings.getServiceType()
            let type = service?.serviceType ?? savedType ?? ServiceType.base
            let fields = try? await getServiceSpecificFieldsUseCase.getSpecificField(
                serviceId: service?.id.kotlinLong, serviceType: type)

            let fromServices = (try? await getServicesUseCase.getAddressesList()) ?? []
            let fromClients = (try? await getClientsUseCase.getAddressesList()) ?? []

            isLoading = false
            isEdit = service != nil
            id = service?.id ?? 0
            title = service?.title ?? ""
            self.client = client
            clientsList = clients
            startDateTime = service?.startDate ?? startTime
            endDateTime = service?.endDate ?? startTime.addHours(hours: Int32(1))
            isPaid = service?.isPaid ?? false
            isFinished = service?.isFinished ?? false
            price = service?.price?.stringValue ?? client?.price?.stringValue ?? ""
            currency = client?.currency ?? CurrencyItem.byn
            address = service?.address ?? ""
            comment = service?.comment ?? ""
            serviceType = type
            specificFields = fields
            _ = (fromServices + fromClients)  // addresses available for suggestions
        }
    }

    // MARK: Change client

    func changeClient(_ client: BaseClient) {
        Task {
            self.client = client
            price = client.price?.stringValue ?? price
            currency = client.currency
            address = client.address ?? ""
            serviceType = client.serviceType
            specificFields = await specificFields(forNewClient: client)
        }
    }

    private func specificFields(forNewClient client: BaseClient) async -> ServiceSpecificFields? {
        switch client.serviceType {
        case .education:
            guard let e = specificFields as? ServiceSpecificFields.EducationServiceSpecificFields,
                  let c = (try? await getClientSpecificFieldsUseCase.getSpecificField(clientId: client.id.kotlinLong, serviceType: .education)) as? ClientSpecificFields.EducationClientSpecificFields
            else { return specificFields }
            return ServiceSpecificFields.EducationServiceSpecificFields(id: e.id, serviceId: e.serviceId, isOnline: c.isOnline, homework: e.homework)
        case .sport:
            guard let s = specificFields as? ServiceSpecificFields.SportServiceSpecificFields,
                  let c = (try? await getClientSpecificFieldsUseCase.getSpecificField(clientId: client.id.kotlinLong, serviceType: .sport)) as? ClientSpecificFields.SportClientSpecificFields
            else { return specificFields }
            return ServiceSpecificFields.SportServiceSpecificFields(id: s.id, serviceId: s.serviceId, isOnline: c.isOnline, exercises: s.exercises)
        case .tattoo:
            guard let t = specificFields as? ServiceSpecificFields.TattooServiceSpecificFields,
                  let c = (try? await getClientSpecificFieldsUseCase.getSpecificField(clientId: client.id.kotlinLong, serviceType: .tattoo)) as? ClientSpecificFields.TattooClientSpecificFields
            else { return specificFields }
            let images = c.currentProject.imageUrls
            return images.isEmpty ? t : ServiceSpecificFields.TattooServiceSpecificFields(id: t.id, serviceId: t.serviceId, images: images)
        default:
            return specificFields
        }
    }

    func setFormat(isOnline: Bool) {
        if let e = specificFields as? ServiceSpecificFields.EducationServiceSpecificFields {
            specificFields = ServiceSpecificFields.EducationServiceSpecificFields(id: e.id, serviceId: e.serviceId, isOnline: isOnline, homework: e.homework)
        } else if let s = specificFields as? ServiceSpecificFields.SportServiceSpecificFields {
            specificFields = ServiceSpecificFields.SportServiceSpecificFields(id: s.id, serviceId: s.serviceId, isOnline: isOnline, exercises: s.exercises)
        }
    }

    var isOnline: Bool {
        (specificFields as? ServiceSpecificFields.EducationServiceSpecificFields)?.isOnline
            ?? (specificFields as? ServiceSpecificFields.SportServiceSpecificFields)?.isOnline ?? false
    }

    // MARK: Save

    func save() {
        Task {
            let crossing = (try? await checkServiceCrossingUseCase.checkCrossing(date: KotlinPair(first: startDateTime, second: endDateTime))) ?? []
            if !crossing.isEmpty {
                dialog = .servicesCrossing(crossing)
            } else {
                await saveService()
            }
        }
    }

    func confirmSave() { dialog = nil; Task { await saveService() } }

    private func saveService() async {
        guard let client else { return }
        let priceValue = Float(price)
        let service = BaseService(
            id: id, title: title.trimmingCharacters(in: .whitespaces), clientId: client.id,
            startDate: startDateTime, endDate: endDateTime, address: address.trimmedOrNil,
            isFinished: isFinished, isPaid: isPaid,
            price: (priceValue == nil || priceValue == 0) ? nil : KotlinFloat(value: priceValue!),
            currency: currency, comment: comment.trimmedOrNil, serviceType: serviceType, serviceSubtype: nil)

        let serviceId: Int64
        if isEdit {
            try? await addEditDeleteServiceUseCase.update(service: service)
            serviceId = service.id
        } else {
            serviceId = (try? await addEditDeleteServiceUseCase.addService(service: service))?.int64Value ?? 0
        }

        if let fields = buildSpecificFields(serviceId: serviceId) {
            if isEdit {
                try? await addEditServiceSpecificFieldsUseCase.updateSpecificField(serviceSpecificFields: fields)
            } else {
                _ = try? await addEditServiceSpecificFieldsUseCase.addSpecificField(serviceSpecificFields: fields)
            }
        }
        event = .saved
    }

    private func buildSpecificFields(serviceId: Int64) -> ServiceSpecificFields? {
        switch specificFields {
        case let f as ServiceSpecificFields.EducationServiceSpecificFields:
            return ServiceSpecificFields.EducationServiceSpecificFields(
                id: f.id, serviceId: serviceId, isOnline: f.isOnline, homework: f.homework?.isEmpty == true ? nil : f.homework)
        case let f as ServiceSpecificFields.BeautyServiceSpecificFields:
            return ServiceSpecificFields.BeautyServiceSpecificFields(id: f.id, serviceId: serviceId, images: f.images)
        case let f as ServiceSpecificFields.TattooServiceSpecificFields:
            return ServiceSpecificFields.TattooServiceSpecificFields(id: f.id, serviceId: serviceId, images: f.images)
        case let f as ServiceSpecificFields.SportServiceSpecificFields:
            return ServiceSpecificFields.SportServiceSpecificFields(id: f.id, serviceId: serviceId, isOnline: f.isOnline, exercises: f.exercises)
        default:
            return nil
        }
    }

    // MARK: Delete / dialogs

    func requestDelete() { dialog = .confirmDeleting }
    func confirmDelete() {
        dialog = nil
        Task {
            try? await addEditDeleteServiceUseCase.deleteService(serviceId: id)
            event = .deleted
        }
    }
    func dismissDialog() { dialog = nil }
    func close() { event = .dismissed }
}
