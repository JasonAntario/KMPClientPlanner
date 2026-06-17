import SwiftUI

/// iOS counterpart of `ui.screens.main.empty.NoClientsScreen`.
/// Shown when a service type is configured but there are no clients yet.
struct NoClientsScreen: View {
    let onAddClient: () -> Void
    let onChangeServiceType: () -> Void

    @State private var showAddClient = false

    var body: some View {
        VStack(alignment: .leading, spacing: 16) {
            Text(AppStrings.navBarClients)
                .frame(maxWidth: .infinity, alignment: .leading)
                .padding(.bottom, 8)
            Text(AppStrings.clientsListNoClientsDescription)
            Spacer()
            Button(action: { showAddClient = true }) {
                Text(AppStrings.clientAddClient).frame(maxWidth: .infinity)
            }
            Button(action: onChangeServiceType) {
                Text(AppStrings.serviceTypeChange).frame(maxWidth: .infinity)
            }
        }
        .padding(.horizontal, 16)
        .padding(.top, 24)
        .padding(.bottom, 16)
        .frame(maxWidth: .infinity, maxHeight: .infinity, alignment: .top)
        .background(Color(.systemBackground))
        .fullScreenCover(isPresented: $showAddClient) {
            NavigationStack {
                AddEditClientScreen(
                    clientId: nil,
                    onSaved: { showAddClient = false; onAddClient() },
                    onDismiss: { showAddClient = false },
                    onDeleted: { showAddClient = false }
                )
            }
        }
    }
}
