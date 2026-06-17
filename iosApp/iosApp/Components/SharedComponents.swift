import SwiftUI

// MARK: - CardView (mirrors components/CardView.kt)

struct CardView<Content: View>: View {
    var onTap: (() -> Void)? = nil
    @ViewBuilder var content: () -> Content

    var body: some View {
        let card = VStack(alignment: .leading, spacing: 4) {
            content()
        }
        .frame(maxWidth: .infinity, alignment: .leading)
        .padding(16)
        .background(Color(.secondarySystemBackground))
        .clipShape(RoundedRectangle(cornerRadius: 12, style: .continuous))

        if let onTap {
            Button(action: onTap) { card }
                .buttonStyle(.plain)
        } else {
            card
        }
    }
}

// MARK: - HeaderView (mirrors components/HeaderView.kt)

struct HeaderView: View {
    let text: String
    init(_ text: String) { self.text = text }

    var body: some View {
        Text(text)
            .font(.largeTitle.bold())
            .foregroundColor(.accentColor)
            .frame(maxWidth: .infinity, alignment: .leading)
            .padding(.bottom, 8)
    }
}

// MARK: - BooleanSelectorView (mirrors components/SelectorView.kt)

struct BooleanSelectorView: View {
    let value: Bool
    let falseLabel: String
    let trueLabel: String
    let onChange: (Bool) -> Void

    var body: some View {
        Picker("", selection: Binding(get: { value }, set: { onChange($0) })) {
            Text(falseLabel).tag(false)
            Text(trueLabel).tag(true)
        }
        .pickerStyle(.segmented)
    }
}

// MARK: - ShortNameBoxView (mirrors components/ShortNameBoxView.kt)

struct ShortNameBoxView: View {
    let text: String
    var size: CGFloat = 64

    var body: some View {
        Text(text)
            .font(.title)
            .foregroundColor(.accentColor)
            .frame(width: size, height: size)
            .background(Color.accentColor.opacity(0.15))
            .clipShape(Circle())
    }
}

// MARK: - LabeledTextField

struct LabeledTextField: View {
    let label: String
    @Binding var text: String
    var keyboard: UIKeyboardType = .default
    var axis: Axis = .horizontal

    var body: some View {
        TextField(label, text: $text, axis: axis)
            .keyboardType(keyboard)
            .textFieldStyle(.roundedBorder)
    }
}

// MARK: - Generic dropdown (mirrors components/DropDownMenuView.kt)

struct DropDownMenuView<Item>: View {
    let label: String
    let items: [Item]
    let title: (Item) -> String
    let isSelected: (Item) -> Bool
    let onSelect: (Item) -> Void
    let currentText: String

    var body: some View {
        Menu {
            ForEach(Array(items.enumerated()), id: \.offset) { _, item in
                Button {
                    onSelect(item)
                } label: {
                    if isSelected(item) {
                        Label(title(item), systemImage: "checkmark")
                    } else {
                        Text(title(item))
                    }
                }
            }
        } label: {
            HStack {
                VStack(alignment: .leading, spacing: 2) {
                    if !label.isEmpty {
                        Text(label).font(.caption).foregroundColor(.secondary)
                    }
                    Text(currentText.isEmpty ? " " : currentText)
                        .foregroundColor(.primary)
                }
                Spacer()
                Image(systemName: "chevron.down").foregroundColor(.secondary)
            }
            .padding(12)
            .overlay(
                RoundedRectangle(cornerRadius: 8).stroke(Color.secondary.opacity(0.4))
            )
        }
    }
}

// MARK: - Toast overlay (replaces Compose Snackbar)

@MainActor
final class ToastCenter: ObservableObject {
    @Published var message: String?

    func show(_ text: String) {
        message = text
        Task {
            try? await Task.sleep(nanoseconds: 2_000_000_000)
            if message == text { message = nil }
        }
    }
}

struct ToastView: View {
    let message: String
    var body: some View {
        Text(message)
            .font(.subheadline)
            .padding(.horizontal, 16).padding(.vertical, 10)
            .background(.ultraThinMaterial, in: Capsule())
            .shadow(radius: 4)
            .padding(.bottom, 24)
    }
}

extension View {
    func toastHost(_ center: ToastCenter) -> some View {
        overlay(alignment: .bottom) {
            if let message = center.message {
                ToastView(message: message)
                    .transition(.move(edge: .bottom).combined(with: .opacity))
            }
        }
        .animation(.easeInOut, value: center.message)
    }
}
