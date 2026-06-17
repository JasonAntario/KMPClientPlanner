import SwiftUI
import SharedLogic

/// Top-level host. Switches between onboarding phases and the main tab UI,
/// reproducing the Compose `MainScreen` start-destination flow.
struct RootView: View {
    @StateObject private var viewModel = RootViewModel()

    var body: some View {
        Group {
            switch viewModel.phase {
            case .loading:
                LoadingScreen()
            case .welcome:
                WelcomeScreen(onStartClick: viewModel.onStartClicked)
            case .serviceTypeSelection:
                ServiceTypeSelectionScreen(onServiceTypeClicked: viewModel.onServiceTypeSelected)
            case .noClients:
                NoClientsScreen(
                    onAddClient: { viewModel.reevaluate() },
                    onChangeServiceType: viewModel.onChangeServiceTypeClicked
                )
            case .main:
                MainTabView(onAllDataCleared: viewModel.onAllDataCleared)
            }
        }
        .onAppear {
            if case .loading = viewModel.phase {
                viewModel.resolveStartDestination()
            }
        }
    }
}

#Preview {
    RootView()
}
