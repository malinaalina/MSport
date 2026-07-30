package m.alina.msport.mapper

import m.alina.msport.domain.model.AuthResult
import m.alina.msport.model.AuthResponseDto

fun AuthResponseDto.toDomain(): AuthResult {
    sessionId?.let { return AuthResult.CodeSent(it, retryDelaySeconds ?: 60) }
    interimToken?.let { return AuthResult.PinRequired(it, isNewUser ?: false) }
    accessToken?.let { return AuthResult.Authenticated(it, refreshToken, expiresIn) }
    error("Unrecognized auth response: status=$status")
}
