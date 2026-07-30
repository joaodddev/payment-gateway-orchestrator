package br.com.joaodddev.paymentgateway.application.usecase

import br.com.joaodddev.paymentgateway.config.JwtService
import br.com.joaodddev.paymentgateway.domain.entity.User
import br.com.joaodddev.paymentgateway.infrastructure.persistence.JpaUserRepository
import br.com.joaodddev.paymentgateway.web.dto.AuthResponse
import br.com.joaodddev.paymentgateway.web.dto.RegisterRequest
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class RegisterUserUseCase(
    private val jpaUserRepository: JpaUserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtService: JwtService
) {

    fun execute(request: RegisterRequest): AuthResponse {
        if (jpaUserRepository.findByEmail(request.email) != null)
            throw IllegalArgumentException("Email already registered")

        val user = User(
            email = request.email,
            password = passwordEncoder.encode(request.password)
        )

        jpaUserRepository.save(user)
        val token = jwtService.generateToken(user.email)
        return AuthResponse(token = token, email = user.email)
    }
}