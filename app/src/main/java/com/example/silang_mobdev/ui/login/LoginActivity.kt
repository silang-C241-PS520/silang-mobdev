package com.example.silang_mobdev.ui.login

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.silang_mobdev.MainActivity
import com.example.silang_mobdev.ViewModelFactory
import com.example.silang_mobdev.data.api.retrofit.ApiConfig
import com.example.silang_mobdev.data.pref.UserModel
import com.example.silang_mobdev.databinding.ActivityLoginBinding
import com.example.silang_mobdev.ui.singup.SingupActivity
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

        val emailEditText = binding.edLoginUsername
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
            val username = binding.edLoginUsername.text.toString().trim()
            val password = binding.edLoginPassword.text.toString().trim()

            if (isValidUsername(username) && isPasswordValid(password)) {
                performLogin(username, password)
            } else {
                showToast("Please enter a valid email and password.")
            }
        }

        binding.tvToSignup.setOnClickListener {
            startActivity(Intent(this, SingupActivity::class.java))
        }

    }

    private fun showAlert(title: String, message: String) {
        AlertDialog.Builder(this).apply {
            setTitle(title)
            setMessage(message)
            setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
            create()
            show()
        }
    }

    private fun performLogin(username: String, password: String) {
        lifecycleScope.launch {
            showLoading(true)
            try {
                val apiService = ApiConfig.getApiService()
                val loginResponse = apiService.login1(username, password)
                val token = loginResponse.access_token
                if (token != null) {
                    // Save the session with the token
                    val userModel = UserModel(username, token, true)
                    viewModel.saveSession(userModel)
                    showToast("Login successful!")
                    navigateToMainActivity()
                } else {
                    showLoading(false)
                }
            } catch (e: HttpException) {
                showLoading(false)
                if (e.code() == 401) {
                    showAlert("Login Failed","Incorrect username or password.")
                } else {
                    showAlert("Login Failed","Something went wrong. Please try again.")
                }
            } catch (e: Exception) {
                showLoading(false)
                showAlert("Error","An error occurred: ${e.message}")
            }
        }
    }

    private fun navigateToMainActivity() {
        val intent = Intent(this@LoginActivity, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or
                    Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
        finish()
    }

    private fun setLoginButtonEnable() {
        val email = binding.edLoginUsername.text.toString().trim()
        val password = binding.edLoginPassword.text.toString().trim()
        binding.loginButton.isEnabled = isValidUsername(email) && isPasswordValid(password)
    }

    private fun isValidUsername(username: String): Boolean {
        return username.length >= 8
    }

    private fun isPasswordValid(password: String): Boolean {
        return password.length >= 8
    }

    private fun playAnimation() {

        val emailTextView =
            ObjectAnimator.ofFloat(binding.usernameTextView, View.ALPHA, 1f).setDuration(100)
        val emailEditTextLayout =
            ObjectAnimator.ofFloat(binding.usernameEditTextLayout, View.ALPHA, 1f).setDuration(100)
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