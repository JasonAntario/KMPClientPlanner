import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.koin.compiler)
}

dependencies {
    implementation(projects.sharedUI)

    implementation(compose.desktop.currentOs)
    implementation(libs.kotlinx.coroutinesSwing)

    implementation(libs.compose.uiToolingPreview)

    implementation(libs.koin.core)
    implementation(libs.koin.compose)

    // Нужен только чтобы перенастроить дисковый кэш Coil: по умолчанию он уезжает
    // в системный temp, мимо каталога приложения.
    implementation(libs.coil3.compose)
}

compose.desktop {
    application {
        mainClass = "com.dsankovsky.kmpclientplanner.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "Client Planner"
            packageVersion = "1.0.0"

            // jlink собирает runtime только из перечисленных модулей, и всё, чего тут нет,
            // падает с NoClassDefFoundError — но уже у пользователя, не на сборке.
            // jdk.unsupported и java.instrument называет сам плагин (suggestRuntimeModules):
            // без первого protobuf внутри DataStore не находит sun.misc.Unsafe, и любая
            // запись настроек молча срывается. jdk.crypto.ec нужен для ECDHE — без него
            // OkHttp под Coil не установит https-соединение; jdeps его не видит, потому
            // что провайдер подхватывается рефлексией.
            modules("java.instrument", "jdk.unsupported", "jdk.crypto.ec")

            windows {
                menuGroup = "Client Planner"
                shortcut = true
                // Установка в профиль пользователя (%LOCALAPPDATA%\Client Planner), а не
                // в Program Files: приложение держит базу и настройки рядом с собой,
                // а Program Files защищён UAC и на запись недоступен.
                perUserInstall = true
                dirChooser = true
                // Фиксированный UUID: без него каждый msi ставится как новый продукт
                // и обновления копятся рядом вместо замены предыдущей версии.
                upgradeUuid = "a510a39d-a85c-4874-a92c-0481aaae6d9a"
            }
        }
    }
}