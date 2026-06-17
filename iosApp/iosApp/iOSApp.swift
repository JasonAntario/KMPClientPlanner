import SwiftUI
import SharedLogic

@main
struct iOSApp: App {
    init() {
        // Start the shared Koin graph once, before any ViewModel resolves dependencies.
        KoinIosKt.doInitKoinIos()
    }

    var body: some Scene {
        WindowGroup {
            RootView()
        }
    }
}
