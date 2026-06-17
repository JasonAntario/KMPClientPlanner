import SharedLogic

// Skie exports sealed-interface implementors as TOP-LEVEL flattened types
// (e.g. `ClientSpecificFieldsEducationClientSpecificFields`), while data classes nested
// inside an implementor stay NESTED (e.g. `...TattooClientSpecificFields.TattooProject`).
// These typealiases restore the readable dotted form used throughout the view models.

extension ClientSpecificFields {
    typealias EducationClientSpecificFields = ClientSpecificFieldsEducationClientSpecificFields
    typealias SportClientSpecificFields = ClientSpecificFieldsSportClientSpecificFields
    typealias TattooClientSpecificFields = ClientSpecificFieldsTattooClientSpecificFields
    typealias TattooClientSpecificFieldsTattooProject = ClientSpecificFieldsTattooClientSpecificFields.TattooProject
}

extension ServiceSpecificFields {
    typealias EducationServiceSpecificFields = ServiceSpecificFieldsEducationServiceSpecificFields
    typealias BeautyServiceSpecificFields = ServiceSpecificFieldsBeautyServiceSpecificFields
    typealias TattooServiceSpecificFields = ServiceSpecificFieldsTattooServiceSpecificFields
    typealias SportServiceSpecificFields = ServiceSpecificFieldsSportServiceSpecificFields
    typealias SportServiceSpecificFieldsExercise = ServiceSpecificFieldsSportServiceSpecificFields.Exercise
    typealias SportServiceSpecificFieldsExerciseExerciseSet = ServiceSpecificFieldsSportServiceSpecificFields.ExerciseExerciseSet
}

// `IosAutofillOutcome.Crossing` etc. are already exposed as nested types — used directly.

// `data object` singletons are exposed nested under the sealed parent, via `.shared`.
extension CurrencyItem {
    static var byn: CurrencyItem { CurrencyItem.BYN.shared }
}

// Kotlin data classes whose params all have defaults don't expose a no-arg `init()`,
// so provide explicit "empty" instances matching the Kotlin defaults.
extension BaseClient {
    static var empty: BaseClient {
        BaseClient(id: 0, name: "", surname: nil, address: nil, phone: nil, price: nil,
                   currency: CurrencyItem.byn, comment: nil, serviceType: .base, serviceSubtype: nil)
    }
}

extension BaseService {
    static var empty: BaseService {
        BaseService(id: 0, title: "", clientId: 0,
                    startDate: DateTimeExtKt.getCurrentDateTime(), endDate: DateTimeExtKt.getCurrentDateTime(),
                    address: nil, isFinished: false, isPaid: false, price: nil,
                    currency: CurrencyItem.byn, comment: nil, serviceType: .base, serviceSubtype: nil)
    }
}
