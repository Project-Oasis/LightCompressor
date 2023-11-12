package com.abedelazizshe.lightcompressorlibrary

import android.content.Context
import android.net.Uri
import com.abedelazizshe.lightcompressorlibrary.config.AppSpecificStorageConfiguration
import com.abedelazizshe.lightcompressorlibrary.config.Configuration
import com.abedelazizshe.lightcompressorlibrary.config.SharedStorageConfiguration
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

suspend fun compressVideosInCoroutine(
    context: Context,
    uris: List<Uri>,
    isStreamable: Boolean = false,
    sharedStorageConfiguration: SharedStorageConfiguration? = null,
    appSpecificStorageConfiguration: AppSpecificStorageConfiguration? = null,
    configureWith: Configuration
): CompressionSuccessResult = suspendCancellableCoroutine { continuation ->
    VideoCompressor.start(
        context = context,
        uris,
        isStreamable = isStreamable,
        sharedStorageConfiguration = sharedStorageConfiguration,
        appSpecificStorageConfiguration = appSpecificStorageConfiguration,
        configureWith = configureWith,
        listener = object : CompressionListener {
            override fun onProgress(index: Int, percent: Float) {
                // Update UI progress
            }

            override fun onStart(index: Int) {
                // Handle start, update UI
            }

            override fun onSuccess(index: Int, size: Long, path: String?) {
                // Handle success, update UI
                continuation.resume(CompressionSuccessResult(index, size, path))
            }

            override fun onFailure(index: Int, failureMessage: String) {
                // Handle failure, update UI
                continuation.resumeWithException(Exception(failureMessage))
            }

            override fun onCancelled(index: Int) {
                // Handle cancellation, update UI
                continuation.cancel()
            }
        }
    )
    continuation.invokeOnCancellation {
        // Clean up or cancel the operation if possible
        VideoCompressor.cancel()
    }
}

data class CompressionSuccessResult(
    val index: Int,
    val size: Long,
    val path: String?
)