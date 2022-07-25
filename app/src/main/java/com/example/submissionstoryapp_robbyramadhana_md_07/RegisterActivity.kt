package com.example.submissionstoryapp_robbyramadhana_md_07

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.submissionstoryapp_robbyramadhana_md_07.databinding.ActivityRegisterBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        binding.passwordEditTextReq.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                binding.signupButton.isEnabled = binding.passwordEditTextReq.text?.length!! >= 6
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                binding.signupButton.isEnabled = binding.passwordEditTextReq.text?.length!! >= 6
            }

            override fun afterTextChanged(p0: Editable?) {
                binding.signupButton.isEnabled = binding.passwordEditTextReq.text?.length!! >= 6
            }

        })

        binding.signupButton.setOnClickListener { setupAction() }
        playAnimation()
    }

    private fun setupAction() {
        val email = binding.emailEditTextReq.text.toString().trim()
        val name = binding.nameEditText.text.toString().trim()
        val password = binding.passwordEditTextReq.text.toString().trim()

        binding.pbSignup.visibility = View.VISIBLE
        ApiConfig().getApiService().registerUser(Register(name, email, password))
            .enqueue(object : Callback<SaveDataResponse> {
                override fun onFailure(call: Call<SaveDataResponse>, t: Throwable) {
                    binding.pbSignup.visibility = View.INVISIBLE
                    Log.d("failure: ", t.message.toString())
                }

                override fun onResponse(
                    call: Call<SaveDataResponse>,
                    response: Response<SaveDataResponse>
                ) {
                    if (response.code() == 201) {
                        binding.pbSignup.visibility = View.INVISIBLE
                        Toast.makeText(
                            applicationContext,
                            getString(R.string.user_created),
                            Toast.LENGTH_SHORT
                        ).show()
                        finish()
                    } else {
                        binding.pbSignup.visibility = View.INVISIBLE
                        Toast.makeText(
                            applicationContext,
                            getString(R.string.invalid_input),
                            Toast.LENGTH_SHORT
                        ).show()
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
        val nameTextView =
            ObjectAnimator.ofFloat(binding.nameTextView, View.ALPHA, 1f).setDuration(500)
        val nameEditTextLayout =
            ObjectAnimator.ofFloat(binding.nameEditTextLayout, View.ALPHA, 1f).setDuration(500)
        val emailTextView =
            ObjectAnimator.ofFloat(binding.emailTextView, View.ALPHA, 1f).setDuration(500)
        val emailEditTextLayout =
            ObjectAnimator.ofFloat(binding.emailEditTextLayout, View.ALPHA, 1f).setDuration(500)
        val passwordTextView =
            ObjectAnimator.ofFloat(binding.passwordTextView, View.ALPHA, 1f).setDuration(500)
        val passwordEditTextLayout =
            ObjectAnimator.ofFloat(binding.passwordEditTextLayout, View.ALPHA, 1f).setDuration(500)
        val signup = ObjectAnimator.ofFloat(binding.signupButton, View.ALPHA, 1f).setDuration(500)


        AnimatorSet().apply {
            playSequentially(
                title,
                nameTextView,
                nameEditTextLayout,
                emailTextView,
                emailEditTextLayout,
                passwordTextView,
                passwordEditTextLayout,
                signup
            )
            startDelay = 500
        }.start()
    }
}