package com.example.submissionstoryapp_robbyramadhana_md_07

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.submissionstoryapp_robbyramadhana_md_07.databinding.ActivityPostStoryBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class PostStoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPostStoryBinding
    private lateinit var viewModel: MainViewModel

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSION) {
            if (!allPermissionsGranted()) {
                Toast.makeText(this, getString(R.string.permission_no_granted), Toast.LENGTH_SHORT)
                    .show()
                finish()
            }
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSION.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSION,
                REQUEST_CODE_PERMISSION
            )
        }

        setupViewModel()

        binding.btCamera.setOnClickListener { startCameraX() }
        binding.btGallery.setOnClickListener { startGallery() }
        binding.btUpload.setOnClickListener { lifecycleScope.launch(Dispatchers.Main) { uploadImage() } }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun setupViewModel() {
        val pref = UserPreference.getInstance(dataStore)
        viewModel = ViewModelProvider(this, ViewModelFactory(pref))[MainViewModel::class.java]
    }

    private fun startGallery() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, "Choose a Picture")
        launcherIntentGallery.launch(chooser)
    }

    private var getFile: File? = null
    private val launcherIntentGallery =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val selectedImg: Uri = result.data?.data as Uri
                val myFile = uriToFile(selectedImg, this@PostStoryActivity)

                getFile = myFile

                binding.previewImageView.setImageURI(selectedImg)
            }
        }

    private fun startCameraX() {
        val intent = Intent(this, CameraActivity::class.java)
        launcherIntentCameraX.launch(intent)
    }

    private val launcherIntentCameraX =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == CAMERA_X_RESULT) {
                val myFile = it.data?.getSerializableExtra("picture") as File
                it.data?.getBooleanExtra("isBackCamera", true) as Boolean

                getFile = myFile
                val result = BitmapFactory.decodeFile(getFile?.path)

                binding.previewImageView.setImageBitmap(result)
            }
        }

    private fun uploadImage() {
        if (getFile != null) {
            val file = reduceFileImage(getFile as File)

            val description =
                binding.edtDescription.text.toString().toRequestBody("text/plain".toMediaType())
            val requestImageFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                "photo",
                file.name,
                requestImageFile
            )

            binding.btUpload.visibility = View.GONE
            binding.pbPost.visibility = View.VISIBLE

            viewModel.getUser().observe(this) { user ->
                val service = ApiConfig().getApiService()
                    .uploadImage(imageMultipart, description, "Bearer ${user.token}")
                service.enqueue(object : Callback<SaveDataResponse> {
                    override fun onResponse(
                        call: Call<SaveDataResponse>,
                        response: Response<SaveDataResponse>
                    ) {
                        if (response.isSuccessful) {
                            val responseBody = response.body()
                            if (responseBody != null && !responseBody.error) {
                                Toast.makeText(
                                    this@PostStoryActivity,
                                    getString(R.string.success_story),
                                    Toast.LENGTH_SHORT
                                ).show()
                                finish()
                            }
                        } else {
                            Toast.makeText(
                                this@PostStoryActivity,
                                response.message(),
                                Toast.LENGTH_SHORT
                            ).show()
                            binding.btUpload.visibility = View.VISIBLE
                        }
                    }

                    override fun onFailure(call: Call<SaveDataResponse>, t: Throwable) {
                        Toast.makeText(
                            this@PostStoryActivity,
                            getString(R.string.failed_retrofit_instance),
                            Toast.LENGTH_SHORT
                        ).show()
                        binding.btUpload.visibility = View.VISIBLE
                    }
                })
            }

        } else {
            Toast.makeText(
                this@PostStoryActivity,
                getString(R.string.add_image_warning),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    companion object {
        const val CAMERA_X_RESULT = 200

        private val REQUIRED_PERMISSION = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSION = 10
    }
}