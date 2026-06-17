import SwiftUI
import SharedLogic

/// iOS counterpart of `ui.screens.add_edit_client.AddEditClientScreen`.
struct AddEditClientScreen: View {
    let clientId: Int64?
    let onSaved: () -> Void
    let onDismiss: () -> Void
    let onDeleted: () -> Void

    @StateObject private var viewModel = AddEditClientViewModel()

    var body: some View {
        Group {
            if viewModel.isLoading {
                LoadingScreen()
            } else {
                form
            }
        }
        .navigationTitle(viewModel.isEdit ? AppStrings.clientEditClient : AppStrings.clientNewClient)
        .navigationBarTitleDisplayMode(.inline)
        .toolbar {
            if viewModel.isEdit {
                ToolbarItem(placement: .topBarTrailing) {
                    Button(role: .destructive) { viewModel.requestDelete() } label: {
                        Image(systemName: "trash")
                    }
                }
            }
        }
        .onAppear { if viewModel.isLoading { viewModel.load(clientId: clientId) } }
        .onChange(of: viewModel.event) { _, event in
            switch event {
            case .saved: onSaved()
            case .dismissed: onDismiss()
            case .deleted: onDeleted()
            case .none: break
            }
        }
        .alert(AppStrings.clientConfirmDeleting, isPresented: deletingBinding) {
            Button(AppStrings.cancel, role: .cancel) { viewModel.dismissDialog() }
            Button(AppStrings.confirm, role: .destructive) { viewModel.confirmDelete() }
        }
        .alert(AppStrings.autofillDescription, isPresented: autofillBinding) {
            Button(AppStrings.cancel, role: .cancel) { viewModel.onAutofillDismiss() }
            Button(AppStrings.confirm) { viewModel.onAutofillConfirm() }
        }
        .alert(AppStrings.serviceCrossing, isPresented: crossingBinding) {
            Button(AppStrings.cancel, role: .cancel) { viewModel.dismissDialog() }
            Button(AppStrings.confirm) { viewModel.onAutofillWithCrossingConfirm() }
        } message: {
            Text(crossingMessage)
        }
    }

    private var form: some View {
        Form {
            Section {
                LabeledTextField(label: AppStrings.clientName, text: $viewModel.name)
                LabeledTextField(label: AppStrings.clientSurname, text: $viewModel.surname)
                LabeledTextField(label: AppStrings.clientPhone, text: $viewModel.phone, keyboard: .phonePad)
                LabeledTextField(label: AppStrings.clientAddress, text: $viewModel.address)
            }

            Section(footer: Text(AppStrings.clientPriceWillFillAutomatically).font(.footnote)) {
                LabeledTextField(label: AppStrings.clientPrice, text: $viewModel.price, keyboard: .decimalPad)
                Picker(AppStrings.clientCurrency, selection: $viewModel.currency) {
                    ForEach(viewModel.currencies, id: \.id) { Text($0.code).tag($0) }
                }
            }

            if viewModel.serviceType == .education {
                educationSection
            } else if viewModel.serviceType == .sport {
                sportSection
            }

            Section {
                LabeledTextField(label: AppStrings.clientComment, text: $viewModel.comment, axis: .vertical)
            }

            Section {
                Button(action: { viewModel.save() }) {
                    Text(AppStrings.clientAddClient).frame(maxWidth: .infinity)
                }
                .disabled(viewModel.name.trimmingCharacters(in: .whitespaces).isEmpty)
            }
        }
    }

    @ViewBuilder private var educationSection: some View {
        Section(AppStrings.clientLevel) {
            LabeledTextField(label: AppStrings.clientLevel, text: $viewModel.level)
            BooleanSelectorView(value: viewModel.isOnline, falseLabel: AppStrings.clientOffline,
                                trueLabel: AppStrings.clientOnline, onChange: viewModel.setFormat)
        }
        lessonsSection(title: AppStrings.clientDetailsLessons, addLabel: AppStrings.clientAddLesson)
    }

    @ViewBuilder private var sportSection: some View {
        Section(AppStrings.clientWeight) {
            LabeledTextField(label: AppStrings.clientWeight, text: $viewModel.weight, keyboard: .decimalPad)
            BooleanSelectorView(value: viewModel.isOnline, falseLabel: AppStrings.clientOffline,
                                trueLabel: AppStrings.clientOnline, onChange: viewModel.setFormat)
        }
        lessonsSection(title: AppStrings.clientDetailsTraining, addLabel: AppStrings.clientAddTraining)
    }

    private func lessonsSection(title: String, addLabel: String) -> some View {
        Section(title) {
            ForEach(Array(viewModel.lessons.enumerated()), id: \.offset) { index, lesson in
                LessonEditorRow(
                    lesson: lesson,
                    onDay: { viewModel.setDayOfWeek(at: index, $0) },
                    onTime: { viewModel.setTime(at: index, $0) },
                    onDuration: { viewModel.setDuration(at: index, $0) },
                    onDelete: { viewModel.deleteLesson(at: index) }
                )
            }
            Button(addLabel) { viewModel.addLesson() }
        }
    }

    // MARK: Dialog bindings

    private var deletingBinding: Binding<Bool> {
        Binding(get: { viewModel.dialog == .confirmDeleting }, set: { if !$0 { viewModel.dismissDialog() } })
    }
    private var autofillBinding: Binding<Bool> {
        Binding(get: { viewModel.dialog == .confirmAutofill }, set: { if !$0 { viewModel.dismissDialog() } })
    }
    private var crossingBinding: Binding<Bool> {
        Binding(get: { if case .servicesCrossing = viewModel.dialog { return true }; return false },
                set: { if !$0 { viewModel.dismissDialog() } })
    }
    private var crossingMessage: String {
        if case .servicesCrossing(let services) = viewModel.dialog {
            let lines = services.map { "\($0.getServiceDate()) \($0.getServiceTime())" }.joined(separator: "\n")
            return lines + "\n\n" + AppStrings.clientShouldContinueAutofill
        }
        return ""
    }
}

/// One editable lesson/training row: day of week, time, duration, delete.
struct LessonEditorRow: View {
    let lesson: ServiceDateTime
    let onDay: (Kotlinx_datetimeDayOfWeek) -> Void
    let onTime: (LocalTime) -> Void
    let onDuration: (String) -> Void
    let onDelete: () -> Void

    var body: some View {
        VStack(alignment: .leading, spacing: 8) {
            HStack {
                Menu {
                    ForEach(Kotlinx_datetimeDayOfWeek.allDays, id: \.ordinal) { day in
                        Button(day.uiName) { onDay(day) }
                    }
                } label: {
                    Text(lesson.dayOfWeek.uiName)
                    Image(systemName: "chevron.down").font(.caption)
                }
                Spacer()
                Button(role: .destructive, action: onDelete) { Image(systemName: "trash") }
            }
            HStack {
                DatePicker("", selection: Binding(
                    get: { lesson.time.asDate },
                    set: { onTime(LocalTime.from($0)) }
                ), displayedComponents: .hourAndMinute)
                .labelsHidden()
                Spacer()
                TextField(AppStrings.datetimeDuration, text: Binding(
                    get: { lesson.duration }, set: { onDuration($0) }
                ))
                .keyboardType(.decimalPad)
                .frame(width: 80)
                .textFieldStyle(.roundedBorder)
            }
        }
        .padding(.vertical, 4)
    }
}
