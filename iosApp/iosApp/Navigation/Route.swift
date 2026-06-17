import Foundation

/// Pushable destinations inside a tab's NavigationStack.
/// Mirrors the navigable `Screen` subtypes from `navigation/Screen.kt`.
enum Route: Hashable {
    case clientDetails(Int64)
    case addEditClient(Int64?)
    case serviceDetails(Int64)
    case addEditService(Int64?)
    case servicesHistory(Int64)
    case payServices
}
