import SwiftUI
import SharedLogic

/// iOS counterpart of `ui.screens.service_details.ServiceDetailsScreen`.
struct ServiceDetailsScreen: View {
    let serviceId: Int64
    let onClose: () -> Void
    let onOpenEdit: (Int64) -> Void

    @EnvironmentObject private var toast: ToastCenter
    @StateObject private var viewModel = ServiceDetailsViewModel()

    var body: some View {
        Group {
            if viewModel.isLoading { LoadingScreen() } else { content }
        }
        .navigationTitle(viewModel.title.isEmpty ? AppStrings.serviceDetails : viewModel.title)
        .navigationBarTitleDisplayMode(.inline)
        .toolbar {
            ToolbarItem(placement: .topBarTrailing) {
                Button { onOpenEdit(serviceId) } label: { Image(systemName: "pencil") }
            }
        }
        .onAppear { if viewModel.isLoading { viewModel.load(serviceId: serviceId) } }
        .onChange(of: viewModel.event) { _, event in
            switch event {
            case .close: onClose()
            case .statusUpdated: toast.show(AppStrings.clientDetailsStatusUpdated)
            case .none: break
            }
        }
    }

    private var content: some View {
        Form {
            Section {
                row(AppStrings.serviceDate, viewModel.dateText)
                row(AppStrings.serviceTime, viewModel.time)
                row(AppStrings.statisticsClient, viewModel.clientName)
                if let address = viewModel.address, !address.isEmpty { row(AppStrings.clientAddress, address) }
                if let price = viewModel.price { row(AppStrings.servicePrice, price) }
                if let comment = viewModel.comment, !comment.isEmpty { row(AppStrings.serviceComment, comment) }
            }

            Section {
                Toggle(AppStrings.servicePaid, isOn: Binding(get: { viewModel.isPaid }, set: { _ in viewModel.togglePaid() }))
                Toggle(AppStrings.serviceFinished, isOn: Binding(get: { viewModel.isFinished }, set: { _ in viewModel.toggleFinished() }))
            }

            specificSection
        }
    }

    @ViewBuilder private var specificSection: some View {
        switch viewModel.serviceType {
        case .education:
            Section(AppStrings.serviceHomework) {
                TextField(AppStrings.serviceHomework, text: Binding(
                    get: { viewModel.homework }, set: { viewModel.setHomework($0) }), axis: .vertical)
                updateButton
            }
        case .beauty, .tattoo:
            Section(AppStrings.serviceReferenceOrResult) {
                ImageRowView(urls: viewModel.images, onDelete: { viewModel.deleteImage(at: $0) })
                PhotoPickerButton(label: AppStrings.serviceReferenceOrResult) { viewModel.addImages($0) }
                updateButton
            }
        case .sport:
            sportSection
        case .base:
            EmptyView()
        }
    }

    @ViewBuilder private var sportSection: some View {
        Section {
            BooleanSelectorView(value: viewModel.isOnline, falseLabel: AppStrings.clientOffline,
                                trueLabel: AppStrings.clientOnline, onChange: { viewModel.setFormat(isOnline: $0) })
        }
        ForEach(Array(viewModel.exercises.enumerated()), id: \.offset) { exerciseIndex, exercise in
            Section {
                HStack {
                    TextField("Упражнение", text: Binding(
                        get: { exercise.title },
                        set: { viewModel.setExerciseTitle(at: exerciseIndex, title: $0, selectable: false) }))
                    Menu {
                        ForEach(Array(viewModel.exercisesList.enumerated()), id: \.offset) { _, option in
                            Button(option.title) {
                                viewModel.setExerciseTitle(at: exerciseIndex, title: option.title, selectable: true)
                            }
                        }
                    } label: { Image(systemName: "list.bullet") }
                    Button(role: .destructive) { viewModel.deleteExercise(at: exerciseIndex) } label: {
                        Image(systemName: "trash")
                    }
                }
                ForEach(Array(exercise.sets.enumerated()), id: \.offset) { setIndex, set in
                    HStack {
                        TextField(AppStrings.serviceRepeats, text: Binding(
                            get: { set.repeats }, set: { viewModel.setRepeats(exerciseIndex: exerciseIndex, setIndex: setIndex, $0) }))
                            .keyboardType(.numberPad)
                        TextField(AppStrings.serviceWeight, text: Binding(
                            get: { set.weight }, set: { viewModel.setWeight(exerciseIndex: exerciseIndex, setIndex: setIndex, $0) }))
                            .keyboardType(.decimalPad)
                        Button(role: .destructive) { viewModel.deleteSet(exerciseIndex: exerciseIndex, setIndex: setIndex) } label: {
                            Image(systemName: "minus.circle")
                        }
                    }
                }
                Button(AppStrings.serviceTrainingAddSet) { viewModel.addSet(exerciseIndex: exerciseIndex) }
            }
        }
        Section {
            Button(AppStrings.serviceTrainingAddExercise) { viewModel.addExercise() }
            updateButton
        }
    }

    private var updateButton: some View {
        Button(AppStrings.serviceUpdateData) { viewModel.updateData() }
    }

    private func row(_ label: String, _ value: String) -> some View {
        HStack { Text(label).foregroundColor(.secondary); Spacer(); Text(value) }
    }
}
