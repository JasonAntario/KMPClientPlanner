import SwiftUI
import SharedLogic

/// iOS counterpart of `PayServicesScreenViewModel`.
@MainActor
final class PayServicesViewModel: ObservableObject {
    enum Event: Equatable { case success }

    @Published private(set) var clientsList: [BaseClient] = []
    @Published private(set) var isPaymentReady = false
    @Published private(set) var client: BaseClient?
    @Published private(set) var availableServices = 0
    @Published var servicesAmount = "0"
    @Published var event: Event?

    private let getClientsUseCase = IosDi.shared.getClientsUseCase
    private let getServicesUseCase = IosDi.shared.getServicesUseCase
    private let addEditDeleteServiceUseCase = IosDi.shared.addEditDeleteServiceUseCase

    func load() {
        Task { clientsList = await getClientsUseCase.getAllClients().firstValue() ?? [] }
    }

    func changeClient(_ client: BaseClient) {
        Task {
            let unpaid = await getServicesUseCase.getAllUnpaidServices(clientId: client.id).firstValue() ?? []
            self.client = client
            availableServices = unpaid.count
            let amount = Int(servicesAmount) ?? 0
            isPaymentReady = amount > 0 && amount <= unpaid.count
        }
    }

    func amountChanged(_ amount: String) {
        servicesAmount = amount
        let value = Int(amount) ?? 0
        isPaymentReady = client != nil && value > 0 && value <= availableServices
    }

    func pay() {
        Task {
            guard let client else { return }
            let amount = servicesAmount.isEmpty ? 0 : (Int(servicesAmount) ?? 0)
            let unpaid = await getServicesUseCase.getAllUnpaidServices(clientId: client.id).firstValue() ?? []
            for service in unpaid.prefix(amount) {
                let paid = BaseService(
                    id: service.id, title: service.title, clientId: service.clientId,
                    startDate: service.startDate, endDate: service.endDate, address: service.address,
                    isFinished: service.isFinished, isPaid: true, price: service.price,
                    currency: service.currency, comment: service.comment,
                    serviceType: service.serviceType, serviceSubtype: service.serviceSubtype)
                try? await addEditDeleteServiceUseCase.update(service: paid)
            }
            changeClient(client)
            event = .success
        }
    }
}

/// iOS counterpart of `ui.screens.pay_services.PayServicesScreen`.
struct PayServicesScreen: View {
    let onDismiss: () -> Void
    let onSuccess: () -> Void

    @StateObject private var viewModel = PayServicesViewModel()

    var body: some View {
        Form {
            Section(AppStrings.paymentTitle) {
                Picker(AppStrings.serviceChooseClient, selection: Binding(
                    get: { viewModel.client?.id ?? -1 },
                    set: { id in if let c = viewModel.clientsList.first(where: { $0.id == id }) { viewModel.changeClient(c) } }
                )) {
                    Text(AppStrings.serviceChooseClient).tag(Int64(-1))
                    ForEach(viewModel.clientsList, id: \.id) { Text($0.getFullName()).tag($0.id) }
                }
                if viewModel.client != nil {
                    Text(AppStrings.paymentAvailableServices(viewModel.availableServices))
                        .font(.caption).foregroundColor(.secondary)
                }
                TextField(AppStrings.serviceAddService, text: Binding(
                    get: { viewModel.servicesAmount }, set: { viewModel.amountChanged($0) }))
                    .keyboardType(.numberPad)
            }
            Section {
                Button(action: { viewModel.pay() }) {
                    Text(AppStrings.payServicesTitle).frame(maxWidth: .infinity)
                }
                .disabled(!viewModel.isPaymentReady)
            }
        }
        .navigationTitle(AppStrings.payServicesTitle)
        .navigationBarTitleDisplayMode(.inline)
        .onAppear { viewModel.load() }
        .onChange(of: viewModel.event) { _, event in
            if event == .success { onSuccess() }
        }
    }
}
