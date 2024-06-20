package com.example.silang_mobdev.ui.singup

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.silang_mobdev.R
import com.example.silang_mobdev.data.api.request.RegisterRequest
import com.example.silang_mobdev.data.api.retrofit.ApiConfig
import com.example.silang_mobdev.databinding.ActivitySingupBinding
import com.example.silang_mobdev.ui.login.LoginActivity
import kotlinx.coroutines.launch
import retrofit2.HttpException

class SingupActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySingupBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySingupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        setupTextWatchers()
        setupAction()
        playAnimation()
    }

    private fun setupTextWatchers() {
        binding.edRegisterName.addTextChangedListener(textWatcher)
        binding.edRegisterPassword.addTextChangedListener(textWatcher)
        binding.edRegisterConfirmPassword.addTextChangedListener(textWatcher)
    }

    private val textWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            setSignupButtonEnable()
        }

        override fun afterTextChanged(s: Editable) {}
    }

    private fun setSignupButtonEnable() {
        val username = binding.edRegisterName.text.toString().trim()
        val password = binding.edRegisterPassword.text.toString().trim()
        val confirmPassword = binding.edRegisterConfirmPassword.text.toString().trim()
        binding.signupButton.isEnabled =
            isValidUsername(username) && isPasswordValid(password) && isConfirmPasswordValid(password, confirmPassword)
    }

    private fun isValidUsername(name: String): Boolean {
        return name.length >= 8
    }


    private fun isPasswordValid(password: String): Boolean {
        return password.length >= 8
    }

    private fun isConfirmPasswordValid(password: String, confirmPassword: String): Boolean {
        return password == confirmPassword
    }

    private fun setupAction() {
        binding.signupButton.setOnClickListener {
            val name = binding.edRegisterName.text.toString().trim()
            val password = binding.edRegisterPassword.text.toString().trim()
            val confirmPassword = binding.edRegisterConfirmPassword.text.toString().trim()

            if (isValidUsername(name) && isPasswordValid(password) && isConfirmPasswordValid(password, confirmPassword)) {
                showAlert("Login button pressed", "The login button was pressed. You can now proceed with the login process.")
                registerUser(name, password, confirmPassword)
            } else {
                showToast("Please enter a valid username and password.")
            }
        }

        binding.tvToLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
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

    private fun registerUser(username: String, password: String, confirm_password: String) {
        lifecycleScope.launch {
            showLoading(true)
            try {
                val apiService = ApiConfig.getApiService()
                val registerResponse = apiService.register1(RegisterRequest(username, password, confirm_password))
                if (registerResponse.username != null) {
                    navigateToLoginActivity()
                } else {
                    showLoading(false)
                    showToast("Registration failed. Please try again.")
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

    private fun navigateToLoginActivity() {
        val intent = Intent(this@SingupActivity, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }

    private fun playAnimation() {
        val nameTextView = ObjectAnimator.ofFloat(binding.nameTextView, View.ALPHA, 1f).setDuration(100)
        val nameEditTextLayout = ObjectAnimator.ofFloat(binding.nameEditTextLayout, View.ALPHA, 1f).setDuration(100)
        val emailTextView = ObjectAnimator.ofFloat(binding.passwordTextView, View.ALPHA, 1f).setDuration(100)
        val emailEditTextLayout = ObjectAnimator.ofFloat(binding.passwordEditTextLayout, View.ALPHA, 1f).setDuration(100)
        val passwordTextView = ObjectAnimator.ofFloat(binding.confirmPasswordTextView, View.ALPHA, 1f).setDuration(100)
        val passwordEditTextLayout = ObjectAnimator.ofFloat(binding.confirmPasswordEditTextLayout, View.ALPHA, 1f).setDuration(100)
        val signup = ObjectAnimator.ofFloat(binding.signupButton, View.ALPHA, 1f).setDuration(100)

        AnimatorSet().apply {
            playSequentially(
                nameTextView,
                nameEditTextLayout,
                emailTextView,
                emailEditTextLayout,
                passwordTextView,
                passwordEditTextLayout,
                signup
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
