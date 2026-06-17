import SwiftUI
import SharedLogic

/// iOS counterpart of `ui.screens.service_type_selection.ServiceTypeSelectionScreen`.
/// Header + description, then the service types listed in descending order
/// (matches `ServiceType.entries.sortedDescending()`).
struct ServiceTypeSelectionScreen: View {
    let onServiceTypeClicked: (ServiceType) -> Void

    /// `ServiceType.allCases` is in declaration (ordinal) order; reversing it
    /// reproduces Kotlin's `sortedDescending()`.
    private var serviceTypes: [ServiceType] {
        ServiceType.allCases.reversed()
    }

    var body: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: 8) {
                Text(AppStrings.serviceTypeSelectionTitle)
                    .font(.largeTitle.bold())
                    .foregroundColor(.accentColor)
                    .frame(maxWidth: .infinity, alignment: .leading)

                Text(AppStrings.serviceTypeSelectionDescription)
                    .font(.title3)
                    .foregroundColor(.accentColor)
                    .padding(.top, 12)
                    .padding(.bottom, 24)

                ForEach(serviceTypes, id: \.self) { serviceType in
                    Button {
                        onServiceTypeClicked(serviceType)
                    } label: {
                        Text(serviceType.uiName)
                            .font(.body)
                            .frame(maxWidth: .infinity)
                            .padding(.vertical, 8)
                    }
                    .buttonStyle(.borderless)
                }
            }
            .padding(.horizontal, 16)
            .padding(.top, 24)
            .padding(.bottom, 16)
        }
        .frame(maxWidth: .infinity, maxHeight: .infinity)
        .background(Color(.systemBackground))
    }
}

#Preview {
    ServiceTypeSelectionScreen(onServiceTypeClicked: { _ in })
}
