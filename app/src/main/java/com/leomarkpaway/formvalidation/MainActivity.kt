package com.leomarkpaway.formvalidation

import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.core.widget.doBeforeTextChanged
import com.leomarkpaway.formvalidation.databinding.ActivityMainBinding
import com.leomarkpaway.formvalidation.util.showDialog
import com.leomarkpaway.formvalidation.util.showToast

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: FormViewModel by viewModels()
    private var age = 0
    private var gender: String = ""
    private var isValidForm = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupForm()
        setupGenderSpinner()
        setupDatePicker()
        setupTextHelper()
        onClickSubmitButton()
        observeResponse()
    }

    private fun setupForm() = with(binding) {
        fullNameEditText.apply {
            doBeforeTextChanged { _, _, _, _ ->
                fullNameTextInputLayout.error = null
            }
            doAfterTextChanged {
                viewModel.updateFullName(it.toString())
            }
        }
        emailEditText.doAfterTextChanged { text ->
            viewModel.updateEmail(text.toString())
        }
        mobileEditText.doAfterTextChanged { text ->
            viewModel.updateMobileNumber(text.toString())
        }
    }

    private fun setupDatePicker() {
        binding.dateOfBirthEditText.setOnClickListener {
            viewModel.showDatePicker(this)
        }
    }

    private fun setupGenderSpinner() {
        val genders = listOf("Select Gender", "Male", "Female")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, genders)
        binding.genderSpinner.adapter = adapter
        binding.genderSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedGender = parent.getItemAtPosition(position).toString()
                if (selectedGender != "Select Gender") {
                    viewModel.updateGender(selectedGender)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    private fun setupTextHelper() = with(binding) {
        viewModel.fullName.observe(this@MainActivity) { fullName ->
            val fullNameRegex = Regex("^[a-zA-Z.,\\s]+$")
            if (fullName != null) {
                if (!fullNameRegex.matches(fullName)) {
                    fullNameTextInputLayout.helperText = "Invalid full name"
                } else {
                    fullNameTextInputLayout.helperText = ""
                }
            }
        }
        viewModel.email.observe(this@MainActivity) { email ->
            if (email != null) {
                if (email.isBlank() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    emailTextInputLayout.helperText = "Invalid email format"
                } else {
                    emailTextInputLayout.helperText = ""
                }
            }
        }
        viewModel.mobileNumber.observe(this@MainActivity) { mobile ->
            val mobileRegex = Regex("^09\\d{9}\$")
            if (mobile != null) {
                if (mobile.isBlank() || !mobile.matches(mobileRegex)) {
                    mobileTextInputLayout.helperText = "Invalid PH mobile number"
                } else {
                    mobileTextInputLayout.helperText = ""
                }
            }
        }
        viewModel.dateOfBirth.observe(this@MainActivity) {
            dateOfBirthEditText.setText(it)
        }
        viewModel.age.observe(this@MainActivity) {
            ageTextView.text = getString(R.string.age, it.toString())
            age = it ?: 0
            if (age < 18) dateOfBirthTextInputLayout.helperText = "You must be 18 or older"
        }
        viewModel.gender.observe(this@MainActivity) { gender ->
            this@MainActivity.gender = gender
            if (gender == "Select Gender") showToast("Please select gender")
        }
        viewModel.isFormValid.observe(this@MainActivity) {
            isValidForm = it
        }
    }

    private fun onClickSubmitButton() {
        binding.submitButton.setOnClickListener {
            viewModel.validateForm()
            if (!isValidForm) showToast("Please check if there's a invalid/blank input")
            else viewModel.submitForm()
            viewModel.submitForm()
        }
    }

    private fun observeResponse() {
        viewModel.response.observe(this) { response ->
            if (response.code == 405) {
                this@MainActivity.showDialog("Error", "response error code ${response.code}")
            }
            if (response.user != null) {
                this@MainActivity.showDialog("Success", "${response.user}")
            }
        }
    }

}