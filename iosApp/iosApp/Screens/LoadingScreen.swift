import SwiftUI

/// iOS counterpart of `ui.screens.loading.LoadingScreen`.
/// A centered, enlarged progress indicator on the background color.
struct LoadingScreen: View {
    var body: some View {
        ZStack {
            Color(.systemBackground)
                .ignoresSafeArea()
            ProgressView()
                .progressViewStyle(.circular)
                .scaleEffect(2.0)
        }
    }
}

#Preview {
    LoadingScreen()
}
