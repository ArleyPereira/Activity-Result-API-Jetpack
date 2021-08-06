package com.example.activityresultapi

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var dialog: AlertDialog

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                // Após a permissão garantida, podemos chamar o método para iniciar a câmera
                startCamera()
            }
        }

    private val requestMultiplePermissionsLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            if (!permissions.values.contains(false)) {
                // Após a permissão garantida, podemos chamar o método para iniciar os contatos e
                // calendário
                startContactsAndCalendar()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnCamera.setOnClickListener {
            checkPermission()
        }

        btnContactsAndCalendar.setOnClickListener {
            checkMultiplePermissions()
        }

    }

    // Solicita permissão para 1 permissão
    private fun checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            when {
                // Verifica se a permissão foi concedida, nesse caso podemos iniciar a câmera
                ContextCompat.checkSelfPermission(this, CAMERA_PERMISSION)
                        == PackageManager.PERMISSION_GRANTED -> startCamera()

                /* Esse método retorna true quando o usuário negou a pemissão.
                 * A recomendação, nesse caso, é exibir uma UI explicando
                 * o motivo da solicitação
                 */
                shouldShowRequestPermissionRationale(CAMERA_PERMISSION) -> showMessagePermissionCameraDenied()

                else -> requestPermissionLauncher.launch(CAMERA_PERMISSION)
            }
        }
    }

    // Solicita permissão para várias permissões
    private fun checkMultiplePermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            val hasContactsPermission = checkPermissionGranted(READ_CONTACTS_PERMISSION)
            val hasCalendarPermission = checkPermissionGranted(READ_CALENDAR_PERMISSION)

            when {
                /* Verifica se ambas as permissões foram concedidas, nesse caso podemos
                 * chamar o método para exibir os dados
                 */
                hasContactsPermission and hasCalendarPermission -> startContactsAndCalendar()

                /* Esse método retorna true quando o usuário negou a pemissão.
                 * A recomendação, nesse caso, é exibir uma UI explicando
                 * o motivo da solicitação
                 */
                shouldShowRequestPermissionRationale(READ_CONTACTS_PERMISSION) ||
                        shouldShowRequestPermissionRationale(READ_CALENDAR_PERMISSION)
                -> showMessagePermissionContactsAndCalendarDenied()

                /* Chamamos o método launch da instância de ActivityResultLauncher
                 * que criamos anteriormente para exibir o Dialog de permissões do sistema
                 */

                else -> requestMultiplePermissionsLauncher.launch(
                    arrayOf(READ_CONTACTS_PERMISSION, READ_CALENDAR_PERMISSION)
                )

            }
        }
    }

    private fun checkPermissionGranted(permission: String) =
        ContextCompat.checkSelfPermission(this, permission) == PERMISSION_GRANTED

    // Implementar lógica de acesso a câmera
    private fun startCamera() {
        Toast.makeText(this, "Permissão garantidas", Toast.LENGTH_SHORT).show()
    }

    // Implementar lógica de acesso a Contatos e Calendário
    private fun startContactsAndCalendar() {
        Toast.makeText(this, "Contatos e Permissão.", Toast.LENGTH_SHORT).show()
    }

    private fun showMessagePermissionCameraDenied() {
        val buider = AlertDialog.Builder(this)
            .setTitle("Atenção")
            .setMessage("Para acessar a câmera é preciso aceitar a permissão, deseja fazer isso agora?")
            .setNegativeButton("Não") { _, _ ->
                dialog.dismiss()
            }
            .setPositiveButton("Sim") { _, _ ->
                val intent = Intent(
                    Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                    Uri.fromParts("package", packageName, null)
                )
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                dialog.dismiss()
            }

        dialog = buider.create()
        dialog.show()
    }

    private fun showMessagePermissionContactsAndCalendarDenied() {
        val buider = AlertDialog.Builder(this)
            .setTitle("Atenção")
            .setMessage("Para acessar os contatos e/ou o canlendário é preciso aceitar a permissão, deseja fazer isso agora?")
            .setNegativeButton("Não") { _, _ ->
                dialog.dismiss()
            }
            .setPositiveButton("Sim") { _, _ ->
                val intent = Intent(
                    Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                    Uri.fromParts("package", packageName, null)
                )
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                dialog.dismiss()
            }

        dialog = buider.create()
        dialog.show()
    }

    companion object {
        private const val CAMERA_PERMISSION = Manifest.permission.CAMERA
        private const val READ_CONTACTS_PERMISSION = Manifest.permission.READ_CONTACTS
        private const val READ_CALENDAR_PERMISSION = Manifest.permission.READ_CALENDAR
    }

}