package com.example.silang_mobdev.ui.login

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.silang_mobdev.MainActivity
import com.example.silang_mobdev.ViewModelFactory
import com.example.silang_mobdev.data.api.retrofit.ApiConfig
import com.example.silang_mobdev.data.pref.UserModel
import com.example.silang_mobdev.databinding.ActivityLoginBinding
import com.example.silang_mobdev.ui.singup.SingupActivity
import com.google.gson.Gson
import kotlinx.coroutines.launch
import retrofit2.HttpException

class LoginActivity : AppCompatActivity() {

    private val viewModel by viewModels<LoginViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val emailEditText = binding.edLoginEmail
        val passwordEditText = binding.edLoginPassword

        supportActionBar?.hide()

        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                setLoginButtonEnable()
            }

            override fun afterTextChanged(s: Editable) {}
        }

        emailEditText.addTextChangedListener(textWatcher)
        passwordEditText.addTextChangedListener(textWatcher)

        setupAction()
        playAnimation()
    }

    private fun setupAction() {
        binding.loginButton.setOnClickListener {
            val email = binding.edLoginEmail.text.toString().trim()
            val password = binding.edLoginPassword.text.toString().trim()

            if (isValidEmail(email) && isPasswordValid(password)) {
                performLogin(email, password)
            } else {
                showToast("Please enter a valid email and password.")
            }
        }

        binding.tvToSignup.setOnClickListener {
            startActivity(Intent(this, SingupActivity::class.java))
        }

    }

    private fun performLogin(email: String, password: String) {
        lifecycleScope.launch {
            showLoading(true)
            try {
                val apiService = ApiConfig.getApiService()
                val loginResponse = apiService.login(email, password)

                if (!loginResponse.error!!) {
                    val token = loginResponse.loginResult?.token
                    if (token != null) {
                        viewModel.saveSession(UserModel(email, token))
                        navigateToMainActivity()
                    } else {
                        showLoading(false)
                        showToast("Failed to retrieve token. Please try again.")
                    }
                } else {
                    showLoading(false)
                    showToast(loginResponse.message ?: "Login failed. Please try again.")
                }
            } catch (e: HttpException) {
                showLoading(false)
            } catch (e: Exception) {
                showLoading(false)
                handleGenericException(e)
            }
        }
    }


    private fun handleGenericException(exception: Exception) {
        showToast("An error occurred. Please try again.")
        exception.printStackTrace()
    }

    private fun navigateToMainActivity() {
        val intent = Intent(this@LoginActivity, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }

    private fun setLoginButtonEnable() {
        val email = binding.edLoginEmail.text.toString().trim()
        val password = binding.edLoginPassword.text.toString().trim()
        binding.loginButton.isEnabled = isValidEmail(email) && isPasswordValid(password)
    }

    private fun isValidEmail(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun isPasswordValid(password: String): Boolean {
        return password.length >= 8
    }

    private fun playAnimation() {

        val emailTextView =
            ObjectAnimator.ofFloat(binding.emailTextView, View.ALPHA, 1f).setDuration(100)
        val emailEditTextLayout =
            ObjectAnimator.ofFloat(binding.emailEditTextLayout, View.ALPHA, 1f).setDuration(100)
        val passwordTextView =
            ObjectAnimator.ofFloat(binding.passwordTextView, View.ALPHA, 1f).setDuration(100)
        val passwordEditTextLayout =
            ObjectAnimator.ofFloat(binding.passwordEditTextLayout, View.ALPHA, 1f).setDuration(100)
        val login = ObjectAnimator.ofFloat(binding.loginButton, View.ALPHA, 1f).setDuration(100)

        AnimatorSet().apply {
            playSequentially(
                emailTextView,
                emailEditTextLayout,
                passwordTextView,
                passwordEditTextLayout,
                login
            )
            startDelay = 100
        }.start()
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressIndicator.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}