package com.example.submissionstoryapp_robbyramadhana_md_07

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.example.submissionstoryapp_robbyramadhana_md_07.databinding.ActivityLoginBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class LoginActivity : AppCompatActivity() {
    private lateinit var loginViewModel: LoginViewModel
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()
        setupViewModel()

        binding.passwordEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                binding.loginButton.isEnabled = binding.passwordEditText.text?.length!! >= 6
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                binding.loginButton.isEnabled = binding.passwordEditText.text?.length!! >= 6
            }

            override fun afterTextChanged(p0: Editable?) {
                binding.loginButton.isEnabled = binding.passwordEditText.text?.length!! >= 6
            }

        })
        binding.loginButton.setOnClickListener { setupAction() }
        playAnimation()
    }

    private fun setupViewModel() {
        val pref = UserPreference.getInstance(dataStore)
        loginViewModel = ViewModelProvider(
            this@LoginActivity,
            ViewModelFactory(pref)
        )[LoginViewModel::class.java]
    }

    private fun setupAction() {
        val email = binding.emailEditText.text.toString()
        val password = binding.passwordEditText.text.toString().trim()

        binding.pbLogin.visibility = View.VISIBLE
        ApiConfig().getApiService().loginUser(Login(email, password))
            .enqueue(object : Callback<LoginResponse> {

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    Log.d("failure: ", t.message.toString())
                }

                override fun onResponse(
                    call: Call<LoginResponse>,
                    response: Response<LoginResponse>
                ) {
                    if (response.code() == 200) {
                        val body = response.body()?.loginResult as UserModel

                        loginViewModel.saveUser(UserModel(body.userId, body.name, body.token, true))
                        binding.pbLogin.visibility = View.INVISIBLE

                        Toast.makeText(
                            applicationContext,
                            getString(R.string.success_login),
                            Toast.LENGTH_SHORT
                        ).show()
                        val intent = Intent(this@LoginActivity, MainActivity::class.java)
                        intent.flags =
                            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)

                    } else {
                        binding.pbLogin.visibility = View.INVISIBLE
                        Toast.makeText(
                            applicationContext,
                            getString(R.string.fail_login),
                            Toast.LENGTH_SHORT
                        ).show()

                        Log.d(
                            LoginActivity::class.java.simpleName,
                            response.body()?.message.toString()
                        )
                    }
                }

            })
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.imageView, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val title = ObjectAnimator.ofFloat(binding.titleTextView, View.ALPHA, 1f).setDuration(500)
        val message =
            ObjectAnimator.ofFloat(binding.messageTextView, View.ALPHA, 1f).setDuration(500)
        val emailTextView =
            ObjectAnimator.ofFloat(binding.emailTextView, View.ALPHA, 1f).setDuration(500)
        val emailEditTextLayout =
            ObjectAnimator.ofFloat(binding.emailEditTextLayout, View.ALPHA, 1f).setDuration(500)
        val passwordTextView =
            ObjectAnimator.ofFloat(binding.passwordTextView, View.ALPHA, 1f).setDuration(500)
        val passwordEditTextLayout =
            ObjectAnimator.ofFloat(binding.passwordEditTextLayout, View.ALPHA, 1f).setDuration(500)
        val login = ObjectAnimator.ofFloat(binding.loginButton, View.ALPHA, 1f).setDuration(500)

        AnimatorSet().apply {
            playSequentially(
                title,
                message,
                emailTextView,
                emailEditTextLayout,
                passwordTextView,
                passwordEditTextLayout,
                login
            )
            startDelay = 500
        }.start()
    }

}



