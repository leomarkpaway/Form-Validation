package com.leomarkpaway.formvalidation

import android.app.DatePickerDialog
import android.content.Context
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.leomarkpaway.formvalidation.network.RetrofitClient
import com.leomarkpaway.formvalidation.network.model.ApiResponse
import com.leomarkpaway.formvalidation.network.model.User
import kotlinx.coroutines.launch
import java.util.Calendar

class FormViewModel : ViewModel() {

    private val _fullName = MutableLiveData<String?>()
    val fullName: LiveData<String?> = _fullName
    private val _email = MutableLiveData<String?>()
    val email: LiveData<String?> = _email
    private val _mobileNumber = MutableLiveData<String?>()
    val mobileNumber: LiveData<String?> = _mobileNumber
    private val _dateOfBirth = MutableLiveData<String?>()
    val dateOfBirth: LiveData<String?> = _dateOfBirth
    private val _age = MutableLiveData<Int?>()
    val age: LiveData<Int?> = _age
    private val _gender = MutableLiveData("")
    val gender: LiveData<String> = _gender
    private val _isFormValid  = MutableLiveData(false)
    val isFormValid: LiveData<Boolean> = _isFormValid
    private val _response = MutableLiveData<ApiResponse>()
    val response: LiveData<ApiResponse> = _response

    private var selectedYear = 0

    fun updateFullName(fullName: String) {
        _fullName.value = fullName
    }

    fun updateEmail(email: String) {
        _email.value = email
    }

    fun updateMobileNumber(mobileNumber: String) {
        _mobileNumber.value = mobileNumber
    }

    fun showDatePicker(context: Context) {
        val calendar = Calendar.getInstance()
        val yearNow = calendar.get(Calendar.YEAR)

        val datePickerDialog = DatePickerDialog(
            context,
            { _, year, month, day ->
                selectedYear = year
                _age.value = yearNow - year
                _dateOfBirth.value = "%02d/%02d/%d".format(month + 1, day, year)
            },
            yearNow,
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )

        datePickerDialog.show()
    }

    fun updateGender(gender: String) {
        _gender.value = gender
    }

     fun validateForm() {
        val fullNameRegex = Regex("^[a-zA-Z.,\\s]+\$")
        val mobileRegex = Regex("^09\\d{9}\$")

        val isValid = !(_fullName.value.isNullOrBlank() || !fullNameRegex.matches(_fullName.value!!)) &&
                !(_email.value.isNullOrBlank() || !Patterns.EMAIL_ADDRESS.matcher(_email.value!!).matches()) &&
                !(_mobileNumber.value.isNullOrBlank() || !_mobileNumber.value!!.matches(mobileRegex)) &&
                (_age.value ?: 0) <= 18 &&
                !(_gender.value.isNullOrBlank() || _gender.value == "Select Gender")

        _isFormValid.value = isValid
    }

    fun submitForm() {
        viewModelScope.launch {
            try {
                val user = User(
                    fullName = _fullName.value!!,
                    email = _email.value!!,
                    mobile = _mobileNumber.value!!,
                    dateOfBirth = _dateOfBirth.value!!,
                    age = _age.value!!,
                    gender = _gender.value!!
                )
                Log.d("qwe", "user ${user}")
                if (_isFormValid.value == true) {
                    val result = RetrofitClient.apiService.submitForm(user)
                    Log.d("qwe", "response ${_response.value}")
                    if (result.isSuccessful) {
                        _response.value = result.body()
                    } else {
                        _response.value = ApiResponse("error", "Request failed", null)
                    }
                }
            } catch (e: Exception) {
                _response.value = ApiResponse("error", "Network Error: ${e.message}", null)
            }
        }
    }

}
