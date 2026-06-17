import Foundation
import SharedLogic

/// Swift analogs of the sharedUI-only `StatisticsClientItem` types.
struct StatPaymentItem: Identifiable {
    let money: Float
    let currency: CurrencyItem
    var id: String { currency.code }
}

struct StatClientItem: Identifiable {
    let client: BaseClient
    let income: [StatPaymentItem]
    let mustBePaid: [StatPaymentItem]
    var id: Int64 { client.id }
}

/// iOS counterpart of `StatisticsScreenViewModel`.
@MainActor
final class StatisticsViewModel: ObservableObject {

    @Published private(set) var isLoading = true
    @Published private(set) var receivedTotalByCurrency: [StatPaymentItem] = []
    @Published private(set) var expectedTotalByCurrency: [StatPaymentItem] = []
    @Published private(set) var receivedTotal: Float = 0
    @Published private(set) var expectedTotal: Float = 0
    @Published private(set) var itemsByClients: [StatClientItem] = []
    @Published private(set) var currentFilter: ServicesFilter = .today
    @Published private(set) var dateInterval: (LocalDate, LocalDate)?
    @Published var showDatePicker = false

    let filters = ServicesFilter.statisticsFilters
    private var customStart: LocalDate?
    private var customEnd: LocalDate?

    private let getClientsUseCase = IosDi.shared.getClientsUseCase
    private let getServicesUseCase = IosDi.shared.getServicesUseCase

    func start() { loadData() }

    func selectFilter(_ filter: ServicesFilter) {
        if filter == .customInterval {
            showDatePicker = true
        } else {
            currentFilter = filter
            showDatePicker = false
            loadData()
        }
    }

    func setCustomInterval(start: LocalDate, end: LocalDate) {
        showDatePicker = false
        currentFilter = .customInterval
        customStart = start
        customEnd = end
        loadData()
    }

    private func loadData() {
        Task {
            let allServices = await getServicesUseCase.getAllServices().firstValue() ?? []
            let clients = await getClientsUseCase.getAllClients().firstValue() ?? []

            let services: [BaseService]
            if currentFilter == .customInterval, let s = customStart, let e = customEnd {
                services = allServices.filter { (s.ordinalKey...e.ordinalKey).contains($0.startDate.date.ordinalKey) }
            } else if let interval = currentFilter.dateInterval {
                services = allServices.filter { (interval.0.ordinalKey...interval.1.ordinalKey).contains($0.startDate.date.ordinalKey) }
            } else {
                services = allServices
            }

            let clientItems: [StatClientItem] = clients.compactMap { client in
                let forClient = services.filter { $0.clientId == client.id }
                if forClient.isEmpty { return nil }
                let byCurrency = Dictionary(grouping: forClient) { $0.currency.code }
                let income = byCurrency.map { _, group -> StatPaymentItem in
                    let money = group.filter { $0.isPaid }.reduce(Float(0)) { $0 + ($1.price?.floatValue ?? 0) }
                    return StatPaymentItem(money: money, currency: group[0].currency)
                }
                let mustBePaid = byCurrency.map { _, group -> StatPaymentItem in
                    let money = group.filter { !$0.isPaid && $0.isFinished }.reduce(Float(0)) { $0 + ($1.price?.floatValue ?? 0) }
                    return StatPaymentItem(money: money, currency: group[0].currency)
                }
                return StatClientItem(client: client, income: income, mustBePaid: mustBePaid)
            }

            receivedTotalByCurrency = totalsByCurrency(services.filter { $0.isPaid })
            expectedTotalByCurrency = totalsByCurrency(services.filter { $0.isFinished && !$0.isPaid })
            receivedTotal = clientItems.flatMap { $0.income }.reduce(Float(0)) { $0 + $1.money }
            expectedTotal = clientItems.flatMap { $0.mustBePaid }.reduce(Float(0)) { $0 + $1.money }
            itemsByClients = clientItems
            dateInterval = currentFilter == .customInterval
                ? (customStart != nil && customEnd != nil ? (customStart!, customEnd!) : nil)
                : currentFilter.dateInterval
            isLoading = false
        }
    }

    private func totalsByCurrency(_ services: [BaseService]) -> [StatPaymentItem] {
        Dictionary(grouping: services) { $0.currency.code }.map { _, group in
            StatPaymentItem(money: group.reduce(Float(0)) { $0 + ($1.price?.floatValue ?? 0) }, currency: group[0].currency)
        }
    }
}
