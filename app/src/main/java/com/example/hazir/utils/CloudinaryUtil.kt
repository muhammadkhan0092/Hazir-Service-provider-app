package com.example.hazir.utils
import com.cloudinary.Cloudinary
object CloudinaryUtil {
    val cloudinary = Cloudinary(
        mapOf(
            "cloud_name" to "djd7stvwg",
            "api_key" to "138931765972126",
            "api_secret" to "LVzZS46qrFQiVRuXsjjEEHbRptE"
        )
    )
}
