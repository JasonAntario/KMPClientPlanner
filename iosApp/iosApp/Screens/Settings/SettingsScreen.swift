import SwiftUI
import SharedLogic

/// iOS counterpart of `ui.screens.settings.SettingsScreen`.
/// Service-type picker, app version, feedback link and a destructive
/// "delete all data" action. Emits `.allDataCleared` upward via `onEvent`.
struct SettingsScreen: View {
    let onEvent: (SettingsScreenEvent) -> Void

    @StateObject private var viewModel = SettingsViewModel()
    @Environment(\.openURL) private var openURL

    var body: some View {
        let state = viewModel.state

        List {
            // Service type selector (mirrors DropDownMenuView)
            Section {
                Picker(
                    AppStrings.settingsServiceType,
                    selection: Binding(
                        get: { state.serviceType },
                        set: { viewModel.handle(.onServiceTypeSelected($0)) }
                    )
                ) {
                    ForEach(state.serviceTypeList, id: \.self) { serviceType in
                        Text(serviceType.uiName).tag(serviceType)
                    }
                }
            }

            // App version
            Section {
                HStack {
                    Text(AppStrings.settingsVersion)
                    Spacer()
                    Text(state.version)
                        .foregroundColor(.secondary)
                }
            }

            // Feedback
            Section {
                Button {
                    if let url = URL(string: AppStrings.feedbackUrl) {
                        openURL(url)
                    }
                } label: {
                    Label(AppStrings.settingsFeedback, systemImage: "square.and.pencil")
                }
            }

            // Delete all data (destructive)
            Section {
                Button(role: .destructive) {
                    viewModel.handle(.deleteAllData)
                } label: {
                    Label(AppStrings.settingsClearData, systemImage: "trash")
                        .foregroundColor(.red)
                }
            }
        }
        .navigationTitle(AppStrings.settingsTitle)
        .onAppear {
            viewModel.handle(.loadData)
        }
        .onChange(of: viewModel.event) { _, event in
            if event == .allDataCleared {
                viewModel.event = nil
                onEvent(.allDataCleared)
            }
        }
    }
}

#Preview {
    NavigationStack {
        SettingsScreen(onEvent: { _ in })
    }
}
