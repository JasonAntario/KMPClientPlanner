import SwiftUI
import SharedLogic

/// Main app shell: a tab bar (Home, Clients, Statistics, Settings), each tab a
/// NavigationStack. Mirrors the Compose `MainScreen` bottom-navigation + back stack.
struct MainTabView: View {
    let onAllDataCleared: () -> Void

    @State private var homePath: [Route] = []
    @State private var clientsPath: [Route] = []
    @State private var statsPath: [Route] = []
    @StateObject private var toast = ToastCenter()

    var body: some View {
        TabView {
            // Home (services)
            NavigationStack(path: $homePath) {
                ServicesListScreen(
                    onOpenService: { homePath.append(.serviceDetails($0)) },
                    onAddService: { homePath.append(.addEditService(nil)) }
                )
                .navigationDestination(for: Route.self) { destination($0, path: $homePath) }
            }
            .tabItem { Label(AppStrings.navBarMain, systemImage: "house") }

            // Clients
            NavigationStack(path: $clientsPath) {
                ClientsListScreen(
                    onOpenClient: { clientsPath.append(.clientDetails($0)) },
                    onAddClient: { clientsPath.append(.addEditClient(nil)) }
                )
                .navigationDestination(for: Route.self) { destination($0, path: $clientsPath) }
            }
            .tabItem { Label(AppStrings.navBarClients, systemImage: "person.2") }

            // Statistics
            NavigationStack(path: $statsPath) {
                StatisticsScreen(onOpenPayServices: { statsPath.append(.payServices) })
                    .navigationDestination(for: Route.self) { destination($0, path: $statsPath) }
            }
            .tabItem { Label(AppStrings.navBarStatistics, systemImage: "chart.bar") }

            // Settings
            NavigationStack {
                SettingsScreen(onEvent: { event in
                    if event == .allDataCleared { onAllDataCleared() }
                })
            }
            .tabItem { Label(AppStrings.navBarSettings, systemImage: "gearshape") }
        }
        .environmentObject(toast)
        .toastHost(toast)
    }

    // MARK: - Route -> screen

    @ViewBuilder
    private func destination(_ route: Route, path: Binding<[Route]>) -> some View {
        switch route {
        case .clientDetails(let clientId):
            ClientDetailScreen(
                clientId: clientId,
                onClose: { pop(path) },
                onOpenEdit: { path.wrappedValue.append(.addEditClient($0)) },
                onOpenServicesHistory: { path.wrappedValue.append(.servicesHistory($0)) }
            )

        case .addEditClient(let clientId):
            AddEditClientScreen(
                clientId: clientId,
                onSaved: {
                    toast.show(clientId != nil ? AppStrings.addEditClientUpdated : AppStrings.addEditClientCreated)
                    popToClientList(path, openedFromDetails: containsClientDetails(path))
                },
                onDismiss: { pop(path) },
                onDeleted: {
                    toast.show(AppStrings.addEditClientDeleted)
                    path.wrappedValue.removeAll()
                }
            )

        case .serviceDetails(let serviceId):
            ServiceDetailsScreen(
                serviceId: serviceId,
                onClose: { pop(path) },
                onOpenEdit: { path.wrappedValue.append(.addEditService($0)) }
            )

        case .addEditService(let serviceId):
            AddEditServiceScreen(
                serviceId: serviceId,
                onSaved: {
                    toast.show(serviceId != nil ? AppStrings.addEditServiceUpdated : AppStrings.addEditServiceCreated)
                    if containsServiceDetails(path) { pop(path) } else { path.wrappedValue.removeAll() }
                },
                onDismiss: { pop(path) },
                onDeleted: {
                    toast.show(AppStrings.addEditServiceDeleted)
                    path.wrappedValue.removeAll()
                }
            )

        case .servicesHistory(let clientId):
            ServicesHistoryScreen(
                clientId: clientId,
                onClose: { pop(path) },
                onOpenService: { path.wrappedValue.append(.serviceDetails($0)) }
            )

        case .payServices:
            PayServicesScreen(
                onDismiss: { pop(path) },
                onSuccess: { toast.show(AppStrings.servicesPaid) }
            )
        }
    }

    private func pop(_ path: Binding<[Route]>) {
        if !path.wrappedValue.isEmpty { path.wrappedValue.removeLast() }
    }

    private func popToClientList(_ path: Binding<[Route]>, openedFromDetails: Bool) {
        if openedFromDetails { pop(path) } else { path.wrappedValue.removeAll() }
    }

    private func containsClientDetails(_ path: Binding<[Route]>) -> Bool {
        path.wrappedValue.contains { if case .clientDetails = $0 { return true }; return false }
    }

    private func containsServiceDetails(_ path: Binding<[Route]>) -> Bool {
        path.wrappedValue.contains { if case .serviceDetails = $0 { return true }; return false }
    }
}
