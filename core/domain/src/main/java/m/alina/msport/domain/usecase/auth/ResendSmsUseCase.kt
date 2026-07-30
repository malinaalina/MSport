package m.alina.msport.domain.usecase.auth

import m.alina.msport.domain.model.AuthResult
import m.alina.msport.domain.repository.AuthRepository
import javax.inject.Inject

class ResendSmsUseCase @Inject constructor(private val repository: AuthRepository) {
    suspend operator fun invoke(sessionId: String): AuthResult = repository.resendSms(sessionId)
}
