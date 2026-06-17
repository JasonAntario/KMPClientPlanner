import SharedLogic

/// Helpers for the shared sealed `CurrencyItem`.
extension CurrencyItem {
    /// All selectable currencies (mirrors `CurrencyItem.getCurrenciesList()`).
    static var all: [CurrencyItem] {
        CurrencyItem.companion.getCurrenciesList()
    }

    /// Stable identity for SwiftUI pickers (the singletons compare by reference,
    /// so the currency code is used as the identifier).
    var id: String { code }
}
