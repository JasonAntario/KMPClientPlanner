import SwiftUI

/// iOS counterpart of `ui.screens.welcome.WelcomeScreen`.
/// Title + description, with a full-width "Start" button pinned to the bottom.
struct WelcomeScreen: View {
    let onStartClick: () -> Void

    var body: some View {
        VStack(alignment: .leading, spacing: 24) {
            Text(AppStrings.welcomeMessage)
                .font(.largeTitle.bold())
                .foregroundColor(.accentColor)
                .frame(maxWidth: .infinity, alignment: .leading)
                .padding(.top, 24)

            Text(AppStrings.welcomeDescription)
                .font(.body)
                .foregroundColor(.accentColor)

            Spacer()

            Button(action: onStartClick) {
                Text(AppStrings.start)
                    .frame(maxWidth: .infinity)
            }
            .buttonStyle(.borderedProminent)
            .controlSize(.large)
        }
        .padding(16)
        .frame(maxWidth: .infinity, maxHeight: .infinity, alignment: .top)
        .background(Color(.systemBackground))
    }
}

#Preview {
    WelcomeScreen(onStartClick: {})
}
