package com.kkagurazaka.experimental.asyncpermissions

import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

sealed class PermissionResult {

    val permission: String
        get() = permissions[0]

    abstract val permissions: List<String>

    data class Granted(override val permissions: List<String>) : PermissionResult()

    data class ShouldShowRationale(
            override val permissions: List<String>,
            private val fragment: AsyncPermissionsFragment
    ) : PermissionResult() {

        suspend fun proceed(): PermissionResult = suspendCancellableCoroutine { cont ->
            fragment.requestFromRationale(*permissions.toTypedArray(), cont = cont)
        }

        suspend fun cancel(): PermissionResult = suspendCancellableCoroutine { cont ->
            Denied(permissions).let(cont::resume)
        }
    }

    data class Denied(override val permissions: List<String>) : PermissionResult()

    data class NeverAskAgain(override val permissions: List<String>) : PermissionResult()
}

fun PermissionResult.onResult(
    onGranted: (PermissionResult.Granted) -> Unit,
    onDenied: ((PermissionResult.Denied) -> Unit)? = null,
    onShouldShowRationale: ((PermissionResult.ShouldShowRationale) -> Unit)? = null,
    onNeverAskAgain: ((PermissionResult.NeverAskAgain) -> Unit)? = null
) {
    when(this) {
        is PermissionResult.Granted -> onGranted(this)
        is PermissionResult.Denied -> onDenied?.invoke(this)
        is PermissionResult.ShouldShowRationale -> onShouldShowRationale?.invoke(this)
        is PermissionResult.NeverAskAgain -> onNeverAskAgain?.invoke(this)
    }
}
