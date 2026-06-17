import SwiftUI
import SharedLogic

/// Swift analog of `ServicesListScreenItem.ServiceItem` (that type lives in sharedUI,
/// which has no iOS target, so it is reconstructed here from the shared domain models).
struct ServiceRowItem: Identifiable {
    let service: BaseService
    let client: BaseClient
    var id: Int64 { service.id }
}

/// Swift analog of `ServicesListScreenItem` (date divider + service items).
enum ServiceListEntry: Identifiable {
    case divider(LocalDate)
    case item(ServiceRowItem)

    var id: String {
        switch self {
        case .divider(let date): return "divider-\(date.uiString)"
        case .item(let item): return "item-\(item.id)"
        }
    }
}

/// Groups services by start date into divider + item entries, ordered chronologically.
func buildServiceEntries(services: [BaseService], clientFor: (Int64) -> BaseClient?) -> [ServiceListEntry] {
    let sorted = services.sorted { $0.startDate.toEpochMilliseconds() < $1.startDate.toEpochMilliseconds() }
    var entries: [ServiceListEntry] = []
    var lastKey: String?
    for service in sorted {
        guard let client = clientFor(service.clientId) else { continue }
        let key = service.startDate.date.uiString
        if key != lastKey {
            entries.append(.divider(service.startDate.date))
            lastKey = key
        }
        entries.append(.item(ServiceRowItem(service: service, client: client)))
    }
    return entries
}

/// Row mirroring `ServiceItemView`: title, client, time, paid/finished state.
/// Swipe to toggle paid/finished (leading) and delete (trailing).
struct ServiceItemView: View {
    let item: ServiceRowItem
    let onTap: () -> Void
    let onTogglePaid: () -> Void
    let onToggleFinished: () -> Void
    let onDelete: () -> Void

    var body: some View {
        Button(action: onTap) {
            HStack(spacing: 12) {
                VStack(alignment: .leading, spacing: 2) {
                    Text(item.service.title.isEmpty ? item.client.getFullName() : item.service.title)
                        .font(.body)
                    Text("\(item.client.getFullName()) · \(item.service.getServiceTime())")
                        .font(.caption).foregroundColor(.secondary)
                }
                Spacer()
                VStack(alignment: .trailing, spacing: 4) {
                    statusChip(item.service.isPaid ? AppStrings.servicePaid : AppStrings.serviceNotPaid,
                               color: item.service.isPaid ? .green : .secondary)
                    statusChip(item.service.isFinished ? AppStrings.serviceFinished : AppStrings.serviceNotFinished,
                               color: item.service.isFinished ? .blue : .secondary)
                }
            }
            .contentShape(Rectangle())
        }
        .buttonStyle(.plain)
        .swipeActions(edge: .leading) {
            Button(action: onTogglePaid) { Text(AppStrings.servicePaid) }.tint(.green)
            Button(action: onToggleFinished) { Text(AppStrings.serviceFinished) }.tint(.blue)
        }
        .swipeActions(edge: .trailing) {
            Button(role: .destructive, action: onDelete) { Image(systemName: "trash") }
        }
    }

    private func statusChip(_ text: String, color: Color) -> some View {
        Text(text)
            .font(.caption2)
            .padding(.horizontal, 8).padding(.vertical, 2)
            .background(color.opacity(0.15))
            .foregroundColor(color)
            .clipShape(Capsule())
    }
}

/// Horizontal filter chips (mirrors the filter row used on Home/Statistics).
struct FilterChipsView: View {
    let filters: [ServicesFilter]
    let current: ServicesFilter
    let onSelect: (ServicesFilter) -> Void

    var body: some View {
        ScrollView(.horizontal, showsIndicators: false) {
            HStack(spacing: 8) {
                ForEach(Array(filters.enumerated()), id: \.offset) { _, filter in
                    let selected = filter == current
                    Button { onSelect(filter) } label: {
                        Text(filter.uiName)
                            .font(.subheadline)
                            .padding(.horizontal, 12).padding(.vertical, 6)
                            .background(selected ? Color.accentColor : Color(.secondarySystemBackground))
                            .foregroundColor(selected ? .white : .primary)
                            .clipShape(Capsule())
                    }
                }
            }
            .padding(.horizontal, 16)
        }
    }
}
