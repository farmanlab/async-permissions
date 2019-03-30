package com.kkagurazaka.asyncpermissions.sample

import android.Manifest
import android.os.Bundle
import android.provider.Contacts
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.kkagurazaka.experimental.asyncpermissions.AsyncPermissions
import com.kkagurazaka.experimental.asyncpermissions.PermissionResult
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var permissions: AsyncPermissions

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        permissions = AsyncPermissions(this)

        findViewById<Button>(R.id.camera_button).setOnClickListener {
            GlobalScope.launch { onCameraButtonClick() }
        }
    }

    private suspend fun onCameraButtonClick() {
        permissions.request(Manifest.permission.CAMERA).let { handlePermissionResult(it) }
    }

    private suspend fun handlePermissionResult(result: PermissionResult) {
        when (result) {
            is PermissionResult.Granted             -> showToast("${result.permission} is granted")
            is PermissionResult.ShouldShowRationale -> {
                showRationale(result)
                // result.proceed() // you can simply call result.proceed() if you don't want to show rationale
            }
            is PermissionResult.Denied              -> showToast("${result.permission} is denied")
            is PermissionResult.NeverAskAgain       -> showToast("${result.permission} is denied with NeverAskAgain")
        }
    }

    private fun showRationale(result: PermissionResult.ShouldShowRationale) {
        AlertDialog.Builder(this)
                .setPositiveButton("Allow") { _, _ ->
                    GlobalScope.launch  { result.proceed().let { handlePermissionResult(it) } }
                }
                .setNegativeButton("Deny") { _, _ ->
                    GlobalScope.launch  { result.cancel().let { handlePermissionResult(it) } }
                }
                .setCancelable(false)
                .setMessage("You should explain why your app requests the permissions.")
                .show()
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}
