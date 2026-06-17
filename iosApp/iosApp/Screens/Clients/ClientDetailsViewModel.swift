import Foundation
import SharedLogic

/// iOS counterpart of `ClientDetailsViewModel`.
@MainActor
final class ClientDetailsViewModel: ObservableObject {

    enum Dialog: Equatable {
        case confirmAutofill
        case servicesCrossing([BaseService])
        static func == (l: Dialog, r: Dialog) -> Bool {
            switch (l, r) {
            case (.confirmAutofill, .confirmAutofill): return true
            case (.servicesCrossing, .servicesCrossing): return true
            default: return false
            }
        }
    }

    enum Event: Equatable { case autofillCompleted }

    @Published private(set) var isLoading = true
    @Published private(set) var clientName = ""
    @Published private(set) var clientShortName = ""
    @Published private(set) var phone: String?
    @Published private(set) var address: String?
    @Published private(set) var price: String?
    @Published private(set) var comment: String?
    @Published private(set) var specificFields: ClientSpecificFields?
    @Published private(set) var showServicesHistory = false
    @Published var dialog: Dialog?
    @Published var event: Event?

    private(set) var client = BaseClient.empty

    private let getClientsUseCase = IosDi.shared.getClientsUseCase
    private let getClientSpecificFieldsUseCase = IosDi.shared.getClientSpecificFieldsUseCase
    private let getServicesUseCase = IosDi.shared.getServicesUseCase
    private let autofillServiceUseCase = IosDi.shared.autofillServiceUseCase

    func load(clientId: Int64) {
        Task {
            guard let client = await getClientsUseCase.getClientById(clientId: clientId.kotlinLong).firstValue() ?? nil
            else { return }
            self.client = client
            let fields = try? await getClientSpecificFieldsUseCase.getSpecificField(
                clientId: client.id.kotlinLong, serviceType: client.serviceType)
            let history = ((try? await getServicesUseCase.getServicesForClient(clientId: clientId)) ?? []).isEmpty == false

            clientName = client.getFullName()
            clientShortName = client.getShortName()
            phone = client.phone.map { AppSettings.companion.phonePrefix + $0 }
            price = client.getFormattedPrice()
            address = client.address
            comment = client.comment
            specificFields = fields
            showServicesHistory = history
            isLoading = false
        }
    }

    // MARK: Tattoo project image management

    private var tattoo: ClientSpecificFields.TattooClientSpecificFields? {
        specificFields as? ClientSpecificFields.TattooClientSpecificFields
    }

    var currentProjectImages: [String] { tattoo?.currentProject.imageUrls ?? [] }
    var finishedProjects: [ClientSpecificFields.TattooClientSpecificFieldsTattooProject] {
        tattoo?.finishedProjects ?? []
    }

    func addImagesToCurrentProject(_ uris: [String]) {
        guard let t = tattoo else { return }
        let project = ClientSpecificFields.TattooClientSpecificFieldsTattooProject(
            imageUrls: t.currentProject.imageUrls + uris)
        setTattoo(currentProject: project, finished: t.finishedProjects)
    }

    func deleteCurrentProjectImage(at index: Int) {
        guard let t = tattoo else { return }
        var images = t.currentProject.imageUrls
        guard images.indices.contains(index) else { return }
        images.remove(at: index)
        setTattoo(currentProject: ClientSpecificFields.TattooClientSpecificFieldsTattooProject(imageUrls: images),
                  finished: t.finishedProjects)
    }

    func finishProject() {
        guard let t = tattoo else { return }
        setTattoo(currentProject: ClientSpecificFields.TattooClientSpecificFieldsTattooProject(imageUrls: []),
                  finished: t.finishedProjects + [t.currentProject])
    }

    private func setTattoo(currentProject: ClientSpecificFields.TattooClientSpecificFieldsTattooProject,
                           finished: [ClientSpecificFields.TattooClientSpecificFieldsTattooProject]) {
        guard let t = tattoo else { return }
        specificFields = ClientSpecificFields.TattooClientSpecificFields(
            id: t.id, clientId: t.clientId, currentProject: currentProject, finishedProjects: finished)
    }

    // MARK: Autofill

    func fillServices() {
        Task {
            let ask = autofillServiceUseCase.askAboutAutofill(
                specificFields: specificFields, serviceType: client.serviceType)
            if ask { dialog = .confirmAutofill }
        }
    }

    func onAutofillConfirm() { dialog = nil; autofill(ignoreCrossing: false) }
    func onAutofillWithCrossingConfirm() { dialog = nil; autofill(ignoreCrossing: true) }
    func dismissDialog() { dialog = nil }

    private func autofill(ignoreCrossing: Bool) {
        Task {
            guard let last = try? await getServicesUseCase.getLastServiceForClient(clientId: client.id) else { return }
            guard let outcome = try? await autofillServiceUseCase.autofillServicesIos(
                clientId: client.id, serviceType: client.serviceType, ignoreCrossing: ignoreCrossing,
                titlePrefix: client.serviceType.titlePrefix, startDateTime: last.endDate)
            else { return }
            switch outcome {
            case let crossing as IosAutofillOutcome.Crossing:
                dialog = .servicesCrossing(crossing.services)
            case is IosAutofillOutcome.Completed, is IosAutofillOutcome.NoAutofillNeeded:
                event = .autofillCompleted
            default:
                break // NoClient / NoClientSpecificFields -> do nothing (matches Compose)
            }
        }
    }
}
