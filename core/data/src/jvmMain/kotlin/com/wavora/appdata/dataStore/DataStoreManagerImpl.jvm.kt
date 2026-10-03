package com.wavora.appdata.dataStore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.wavora.common.SETTINGS_FILENAME
import com.wavora.appdata.io.getHomeFolderPath
import createDataStore
import java.io.File
import java.util.Locale

// Un único DataStore por proceso para ~/.wavora/settings.preferences_pb. DesktopApp
// reintenta startKoin{} ante fallos de arranque y cada Koin nuevo volvería a llamar
// createDataStoreInstance(); dos DataStore sobre el mismo archivo es exactamente lo
// que DataStore reporta como "multiple instances of DataStore" (rename del .tmp falla).
private val settingsDataStore: DataStore<Preferences> by lazy {
    RetryingDataStore(
        createDataStore(
            producePath = {
                val file = File(getHomeFolderPath(listOf(".wavora")), "$SETTINGS_FILENAME.preferences_pb")
                file.absolutePath
            }
        )
    )
}

actual fun createDataStoreInstance(): DataStore<Preferences> = settingsDataStore

// AUDIT NOTE (carteles en inglés antes de elegir idioma en Desktop):
// DesktopApp.kt llama a changeLanguageNative() con este valor en CADA
// arranque, incluso antes de que el usuario elija nada en el onboarding
// - antes, el default (en DataStoreManagerImpl.kt) era simplemente
// SUPPORTED_LANGUAGE.codes.first() ("en-US"), que sobreescribía el
// locale real de Windows (confirmado: la propia JVM arranca detectando
// es-AR en la máquina de Sebastián) con inglés a la fuerza. Usamos el
// locale por defecto de la JVM ANTES de que nada lo pise, que en
// Desktop refleja el idioma real del sistema operativo.
actual fun getSystemDefaultLanguageCode(): String = Locale.getDefault().toLanguageTag()