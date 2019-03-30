package com.kkagurazaka.experimental.asyncpermissions

import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.suspendCancellableCoroutine

class AsyncPermissions(activity: AppCompatActivity) {

    private val asyncPermissionsFragment: AsyncPermissionsFragment =
            AsyncPermissionsFragment.attach(activity)

    suspend fun request(permission: String, vararg others: String): PermissionResult =
            suspendCancellableCoroutine { cont ->
                asyncPermissionsFragment.request(permission, *others, cont = cont)
            }
}
