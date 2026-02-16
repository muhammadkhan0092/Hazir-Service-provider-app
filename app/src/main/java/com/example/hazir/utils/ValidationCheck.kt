package com.example.ecommerceapp.utils

import android.util.Patterns
import com.example.hazir.utils.RegisterValidation

fun validateEmail(email : String) : RegisterValidation {
    if(email.isEmpty())
        return RegisterValidation.Failed("Email cant be empty")
    if(!Patterns.EMAIL_ADDRESS.matcher(email).matches())
        return RegisterValidation.Failed("Wrong Email format")
    return RegisterValidation.Success
}

fun validatePassword(pass : String) : RegisterValidation {
    if(pass.isEmpty())
        return RegisterValidation.Failed("Password cant be empty")
    if(pass.length<6)
        return RegisterValidation.Failed("Password should contain at least 6 characters")
    return RegisterValidation.Success
}