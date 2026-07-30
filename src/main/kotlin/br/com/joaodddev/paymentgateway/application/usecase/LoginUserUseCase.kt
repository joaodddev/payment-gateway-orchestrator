package br.com.joaodddev.paymentgateway.application.usecase

import br.com.joaodddev.paymentgateway.config.JwtService
import br.com.joaodddev.paymentgateway.infrastructure.persistence.JpaUserRepository
import br.com.joaodddev.paymentgateway.web.dto.AuthRequest
import br.com.joaodddev.paymentgateway.web.dto.AuthResponse
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.stereotype.Service

@Service
class LoginUserUseCase(
    private val jpaUserRepository: JpaUserRepository,
    private val jwtService: JwtService,
    private val authenticationManager: AuthenticationManager
) {

    fun execute(request: AuthRequest): AuthResponse {
        authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(request.email, request.password)
        )

        val user = jpaUserRepository.findByEmail(request.email)
            ?: throw NoSuchElementException("User not found")

        val token = jwtService.generateToken(user.email)
        return AuthResponse(token = token, email = user.email)
    }
}