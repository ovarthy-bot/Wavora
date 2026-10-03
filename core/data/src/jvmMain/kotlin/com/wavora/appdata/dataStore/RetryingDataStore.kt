package com.wavora.appdata.dataStore

import androidx.datastore.core.DataStore
import com.wavora.logger.Logger
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.delay
import java.io.IOException

private const val TAG = "RetryingDataStore"
private const val MAX_WRITE_ATTEMPTS = 5
private const val WRITE_RETRY_BASE_DELAY_MS = 100L

// En Windows el rename atómico de DataStore (settings.preferences_pb.tmp ->
// settings.preferences_pb) falla con IOException si otro handle tiene el archivo
// abierto justo en ese instante (un lector concurrente, antivirus o indexador).
// Es un fallo transitorio: el siguiente intento escribe bien y el DataStore sigue
// usable. Sin este wrapper, esa IOException llega al hilo que llamó al write
// (p. ej. VLC-Player-Thread) y termina en CrashDialog.
internal class RetryingDataStore<T>(
    private val delegate: DataStore<T>,
) : DataStore<T> {
    override val data: Flow<T> get() = delegate.data

    // edit {} de Preferences DataStore se resuelve en updateData, así que cubre
    // todos los writes de DataStoreManagerImpl.
    override suspend fun updateData(transform: suspend (t: T) -> T): T {
        var attempt = 1
        while (true) {
            try {
                return delegate.updateData(transform)
            } catch (e: IOException) {
                if (attempt >= MAX_WRITE_ATTEMPTS) throw e
                Logger.w(TAG, "Write de DataStore falló (intento $attempt/$MAX_WRITE_ATTEMPTS), reintentando: ${e.message}")
                delay(WRITE_RETRY_BASE_DELAY_MS * attempt)
                attempt++
            }
        }
    }
}
