import SwiftUI
import SharedLogic

/// iOS counterpart of `ui.screens.add_edit_service.AddEditServiceScreen`.
struct AddEditServiceScreen: View {
    let serviceId: Int64?
    let onSaved: () -> Void
    let onDismiss: () -> Void
    let onDeleted: () -> Void

    @StateObject private var viewModel = AddEditServiceViewModel()

    var body: some View {
        Group {
            if viewModel.isLoading { LoadingScreen() } else { form }
        }
        .navigationTitle(viewModel.isEdit ? AppStrings.serviceEditService : AppStrings.serviceNewService)
        .navigationBarTitleDisplayMode(.inline)
        .toolbar {
            if viewModel.isEdit {
                ToolbarItem(placement: .topBarTrailing) {
                    Button(role: .destructive) { viewModel.requestDelete() } label: { Image(systemName: "trash") }
                }
            }
        }
        .onAppear { if viewModel.isLoading { viewModel.load(serviceId: serviceId) } }
        .onChange(of: viewModel.event) { _, event in
            switch event {
            case .saved: onSaved()
            case .dismissed: onDismiss()
            case .deleted: onDeleted()
            case .none: break
            }
        }
        .alert(AppStrings.serviceConfirmDeleting, isPresented: deletingBinding) {
            Button(AppStrings.cancel, role: .cancel) { viewModel.dismissDialog() }
            Button(AppStrings.confirm, role: .destructive) { viewModel.confirmDelete() }
        }
        .alert(AppStrings.serviceCrossing, isPresented: crossingBinding) {
            Button(AppStrings.cancel, role: .cancel) { viewModel.dismissDialog() }
            Button(AppStrings.confirm) { viewModel.confirmSave() }
        } message: { Text(crossingMessage) }
    }

    private var form: some View {
        Form {
            Section {
                Picker(AppStrings.serviceChooseClient, selection: Binding(
                    get: { viewModel.client?.id ?? -1 },
                    set: { id in if let c = viewModel.clientsList.first(where: { $0.id == id }) { viewModel.changeClient(c) } }
                )) {
                    Text(AppStrings.serviceChooseClient).tag(Int64(-1))
                    ForEach(viewModel.clientsList, id: \.id) { Text($0.getFullName()).tag($0.id) }
                }
                LabeledTextField(label: AppStrings.serviceTitle, text: $viewModel.title)
            }

            Section {
                DatePicker(AppStrings.serviceStartTime, selection: Binding(
                    get: { KotlinDateTime.toDate(viewModel.startDateTime) },
                    set: { viewModel.startDateTime = KotlinDateTime.fromDate($0) }))
                DatePicker(AppStrings.serviceEndTime, selection: Binding(
                    get: { KotlinDateTime.toDate(viewModel.endDateTime) },
                    set: { viewModel.endDateTime = KotlinDateTime.fromDate($0) }))
            }

            Section {
                LabeledTextField(label: AppStrings.servicePrice, text: $viewModel.price, keyboard: .decimalPad)
                Picker(AppStrings.serviceCurrency, selection: $viewModel.currency) {
                    ForEach(viewModel.currencies, id: \.id) { Text($0.code).tag($0) }
                }
            }

            Section {
                LabeledTextField(label: AppStrings.clientAddress, text: $viewModel.address)
                LabeledTextField(label: AppStrings.serviceComment, text: $viewModel.comment, axis: .vertical)
            }

            Section {
                Toggle(AppStrings.servicePaid, isOn: $viewModel.isPaid)
                Toggle(AppStrings.serviceFinished, isOn: $viewModel.isFinished)
            }

            if viewModel.serviceType == .education || viewModel.serviceType == .sport {
                Section(viewModel.serviceType == .education ? AppStrings.serviceChooseFormatLesson : AppStrings.serviceChooseFormatTraining) {
                    BooleanSelectorView(value: viewModel.isOnline, falseLabel: AppStrings.clientOffline,
                                        trueLabel: AppStrings.clientOnline, onChange: { viewModel.setFormat(isOnline: $0) })
                }
            }

            Section {
                Button(action: { viewModel.save() }) {
                    Text(AppStrings.serviceAddService).frame(maxWidth: .infinity)
                }
                .disabled(!viewModel.isFinishButtonEnabled)
            }
        }
    }

    private var deletingBinding: Binding<Bool> {
        Binding(get: { viewModel.dialog == .confirmDeleting }, set: { if !$0 { viewModel.dismissDialog() } })
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
