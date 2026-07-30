package m.alina.msport.common

import androidx.annotation.StringRes
import m.alina.msport.feature.auth.R

data class AuthError(
    @StringRes val messageRes: Int = R.string.error_unknown,
    val message: String? = null,
)
