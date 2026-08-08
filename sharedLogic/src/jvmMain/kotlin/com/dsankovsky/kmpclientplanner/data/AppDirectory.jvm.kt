package com.dsankovsky.kmpclientplanner.data

import java.io.File
import java.util.Locale

/**
 * Единый каталог для всего, что приложение создаёт на диске: база и настройки.
 *
 * Приоритет — «портативный» режим: файлы данных лежат рядом с исполняемым файлом,
 * то есть в той же папке, куда установщик распаковал приложение. Так вся установка
 * — один самодостаточный каталог: снёс папку, и от приложения не осталось следов.
 *
 * Оговорка, из-за которой тут не просто `File(".")`: каталог установки бывает
 * недоступен на запись. `C:\Program Files\...` защищён UAC, `/opt` из .deb — правами
 * root, а внутрь macOS-бандла писать нельзя из-за подписи. Раньше путь к настройкам
 * был относительным (`prefs.preferences_pb`) и резолвился от рабочей директории
 * процесса — под Gradle это `desktopApp/`, а в установленном msi — `Program Files`,
 * куда запись падала с AccessDenied, и приложение молча застревало на первом экране.
 * Поэтому запись проверяется пробным файлом, а не `canWrite()` (на Windows он врёт
 * для каталогов), и при неудаче данные уезжают в пользовательский каталог ОС.
 */
object AppDirectory {

    /** Куда класть файлы данных. Вычисляется один раз за запуск. */
    val dataDir: File by lazy { resolveDataDir() }

    fun file(name: String): File = File(dataDir, name)

    private fun resolveDataDir(): File {
        val portable = portableDir()
        if (portable != null && portable.isWritableDir()) return portable
        return userDataDir().also { it.mkdirs() }
    }

    /**
     * Каталог установки. `jpackage.app-path` — полный путь к лаунчеру, его выставляет
     * сам jpackage; в dev-запуске (Gradle/IDE) свойства нет, и остаётся рабочая
     * директория — прежнее поведение, чтобы отладочные данные не утекали в профиль.
     */
    private fun portableDir(): File? {
        val appPath = System.getProperty("jpackage.app-path")
            ?: return File(System.getProperty("user.dir"))

        val launcher = File(appPath)
        // macOS: лаунчер лежит внутри Foo.app/Contents/MacOS. Бандл подписан,
        // запись в него ломает подпись и Gatekeeper — портативный режим тут неприменим.
        if (launcher.absolutePath.contains(".app${File.separator}Contents${File.separator}")) return null
        return launcher.parentFile
    }

    private fun userDataDir(): File {
        val os = System.getProperty("os.name").orEmpty().lowercase(Locale.ROOT)
        val home = System.getProperty("user.home")
        return when {
            os.contains("win") ->
                File(System.getenv("LOCALAPPDATA") ?: File(home, "AppData/Local").path, APP_FOLDER)

            os.contains("mac") ->
                File(home, "Library/Application Support/$APP_FOLDER")

            else ->
                File(System.getenv("XDG_DATA_HOME") ?: File(home, ".local/share").path, APP_FOLDER)
        }
    }

    private fun File.isWritableDir(): Boolean = try {
        if (!isDirectory && !mkdirs()) {
            false
        } else {
            val probe = File(this, ".write-probe")
            probe.delete()
            probe.createNewFile().also { probe.delete() }
        }
    } catch (_: Exception) {
        false
    }

    private const val APP_FOLDER = "ClientPlanner"
}
