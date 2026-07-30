package m.alina.msport.domain.usecase.auth

import m.alina.msport.domain.model.AuthResult
import m.alina.msport.domain.repository.AuthRepository
import javax.inject.Inject

class VerifyCodeUseCase @Inject constructor(private val repository: AuthRepository) {
    suspend operator fun invoke(sessionId: String, code: String): AuthResult =
        repository.verifyCode(sessionId, code)
}
