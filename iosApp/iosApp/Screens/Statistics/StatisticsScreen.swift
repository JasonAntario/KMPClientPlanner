import SwiftUI
import SharedLogic

/// iOS counterpart of `ui.screens.statistics.StatisticsScreen`.
struct StatisticsScreen: View {
    let onOpenPayServices: () -> Void

    @StateObject private var viewModel = StatisticsViewModel()
    @State private var customStart = Date()
    @State private var customEnd = Date()

    var body: some View {
        Group {
            if viewModel.isLoading {
                LoadingScreen()
            } else {
                content
            }
        }
        .navigationTitle(AppStrings.statisticsTitle)
        .toolbar {
            ToolbarItem(placement: .topBarTrailing) {
                Button(action: onOpenPayServices) { Image(systemName: "creditcard") }
            }
        }
        .onAppear { viewModel.start() }
        .sheet(isPresented: $viewModel.showDatePicker) { datePickerSheet }
    }

    private var content: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: 16) {
                FilterChipsView(filters: viewModel.filters, current: viewModel.currentFilter,
                                onSelect: viewModel.selectFilter)

                totalsCard(title: AppStrings.statisticsIncomeInPeriod, items: viewModel.receivedTotalByCurrency)
                totalsCard(title: AppStrings.statisticsExpectedInPeriod, items: viewModel.expectedTotalByCurrency)

                if !viewModel.itemsByClients.isEmpty {
                    Text(AppStrings.statisticsByClient).font(.headline).padding(.horizontal, 16)
                    ForEach(viewModel.itemsByClients) { item in
                        clientCard(item)
                    }
                }
            }
            .padding(.vertical, 16)
        }
    }

    private func totalsCard(title: String, items: [StatPaymentItem]) -> some View {
        CardView {
            Text(title).font(.subheadline).foregroundColor(.secondary)
            if items.isEmpty {
                Text("0").font(.title3.bold())
            } else {
                ForEach(items) { item in
                    Text(format(item)).font(.title3.bold())
                }
            }
        }
        .padding(.horizontal, 16)
    }

    private func clientCard(_ item: StatClientItem) -> some View {
        CardView {
            Text(item.client.getFullName()).font(.body.bold())
            HStack(alignment: .top, spacing: 24) {
                VStack(alignment: .leading, spacing: 2) {
                    Text(AppStrings.statisticsPaid).font(.caption).foregroundColor(.secondary)
                    ForEach(item.income) { Text(format($0)).foregroundColor(.green) }
                }
                VStack(alignment: .leading, spacing: 2) {
                    Text(AppStrings.statisticsExpected).font(.caption).foregroundColor(.secondary)
                    ForEach(item.mustBePaid) { Text(format($0)) }
                }
            }
        }
        .padding(.horizontal, 16)
    }

    private func format(_ item: StatPaymentItem) -> String {
        let money = item.money == item.money.rounded() ? String(Int(item.money)) : String(format: "%.2f", item.money)
        return "\(money) \(item.currency.code)"
    }

    private var datePickerSheet: some View {
        NavigationStack {
            Form {
                DatePicker(AppStrings.serviceStartTime, selection: $customStart, displayedComponents: .date)
                DatePicker(AppStrings.serviceEndTime, selection: $customEnd, displayedComponents: .date)
            }
            .navigationTitle(AppStrings.tabsCustomInterval)
            .toolbar {
                ToolbarItem(placement: .cancellationAction) {
                    Button(AppStrings.cancel) { viewModel.showDatePicker = false }
                }
                ToolbarItem(placement: .confirmationAction) {
                    Button(AppStrings.ok) {
                        viewModel.setCustomInterval(
                            start: KotlinDateTime.fromDate(customStart).date,
                            end: KotlinDateTime.fromDate(customEnd).date)
                    }
                }
            }
        }
        .presentationDetents([.medium])
    }
}
