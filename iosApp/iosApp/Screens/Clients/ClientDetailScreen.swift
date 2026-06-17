import SwiftUI
import SharedLogic

/// iOS counterpart of `ui.screens.client_details.ClientDetailScreen`.
struct ClientDetailScreen: View {
    let clientId: Int64
    let onClose: () -> Void
    let onOpenEdit: (Int64) -> Void
    let onOpenServicesHistory: (Int64) -> Void

    @EnvironmentObject private var toast: ToastCenter
    @StateObject private var viewModel = ClientDetailsViewModel()

    var body: some View {
        Group {
            if viewModel.isLoading {
                LoadingScreen()
            } else {
                content
            }
        }
        .navigationTitle(viewModel.clientName)
        .navigationBarTitleDisplayMode(.inline)
        .toolbar {
            ToolbarItem(placement: .topBarTrailing) {
                Button { onOpenEdit(viewModel.client.id) } label: { Image(systemName: "pencil") }
            }
        }
        .onAppear { if viewModel.isLoading { viewModel.load(clientId: clientId) } }
        .onChange(of: viewModel.event) { _, event in
            if event == .autofillCompleted { toast.show(AppStrings.clientDetailsAutofillCompleted) }
        }
        .alert(AppStrings.autofillDescription, isPresented: autofillBinding) {
            Button(AppStrings.cancel, role: .cancel) { viewModel.dismissDialog() }
            Button(AppStrings.confirm) { viewModel.onAutofillConfirm() }
        }
        .alert(AppStrings.serviceCrossing, isPresented: crossingBinding) {
            Button(AppStrings.cancel, role: .cancel) { viewModel.dismissDialog() }
            Button(AppStrings.confirm) { viewModel.onAutofillWithCrossingConfirm() }
        } message: { Text(crossingMessage) }
    }

    private var content: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: 16) {
                HStack(spacing: 16) {
                    ShortNameBoxView(text: viewModel.clientShortName)
                    VStack(alignment: .leading, spacing: 4) {
                        Text(viewModel.clientName).font(.title3.bold())
                        if let price = viewModel.price { Text(price).foregroundColor(.secondary) }
                    }
                }

                infoRow(AppStrings.clientPhone, viewModel.phone)
                infoRow(AppStrings.clientAddress, viewModel.address)
                infoRow(AppStrings.clientComment, viewModel.comment)

                tattooSection

                if viewModel.client.serviceType == .education || viewModel.client.serviceType == .sport {
                    Button {
                        viewModel.fillServices()
                    } label: {
                        Text(viewModel.client.serviceType == .education
                             ? AppStrings.clientDetailsFillLessons : AppStrings.clientDetailsFillTrainings)
                            .frame(maxWidth: .infinity)
                    }
                    .buttonStyle(.bordered)
                }

                if viewModel.showServicesHistory {
                    Button {
                        onOpenServicesHistory(viewModel.client.id)
                    } label: {
                        Text(AppStrings.clientDetailsServices).frame(maxWidth: .infinity)
                    }
                    .buttonStyle(.bordered)
                }
            }
            .padding(16)
        }
    }

    @ViewBuilder private func infoRow(_ label: String, _ value: String?) -> some View {
        if let value, !value.isEmpty {
            VStack(alignment: .leading, spacing: 2) {
                Text(label).font(.caption).foregroundColor(.secondary)
                Text(value)
            }
            .frame(maxWidth: .infinity, alignment: .leading)
        }
    }

    @ViewBuilder private var tattooSection: some View {
        if viewModel.client.serviceType == .tattoo {
            VStack(alignment: .leading, spacing: 12) {
                Text(AppStrings.clientCurrentProject).font(.headline)
                ImageRowView(urls: viewModel.currentProjectImages, onDelete: viewModel.deleteCurrentProjectImage)
                PhotoPickerButton(label: AppStrings.serviceReferenceOrResult) { viewModel.addImagesToCurrentProject($0) }
                if !viewModel.currentProjectImages.isEmpty {
                    Button(AppStrings.clientFinishProject) { viewModel.finishProject() }
                }
                if !viewModel.finishedProjects.isEmpty {
                    Text(AppStrings.clientFinishedProjects).font(.headline)
                    ForEach(Array(viewModel.finishedProjects.enumerated()), id: \.offset) { _, project in
                        ImageRowView(urls: project.imageUrls)
                    }
                }
            }
        }
    }

    // MARK: Dialog bindings

    private var autofillBinding: Binding<Bool> {
        Binding(get: { viewModel.dialog == .confirmAutofill }, set: { if !$0 { viewModel.dismissDialog() } })
    }
    private var crossingBinding: Binding<Bool> {
        Binding(get: { if case .servicesCrossing = viewModel.dialog { return true }; return false },
                set: { if !$0 { viewModel.dismissDialog() } })
    }
    private var crossingMessage: String {
        if case .servicesCrossing(let services) = viewModel.dialog {
            return services.map { "\($0.getServiceDate()) \($0.getServiceTime())" }.joined(separator: "\n")
        }
        return ""
    }
}
