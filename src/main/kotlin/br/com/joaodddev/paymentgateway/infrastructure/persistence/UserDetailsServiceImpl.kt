package br.com.joaodddev.paymentgateway.infrastructure.persistence

import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class UserDetailsServiceImpl(
    private val jpaUserRepository: JpaUserRepository
) : UserDetailsService {

    override fun loadUserByUsername(email: String): UserDetails {
        val user = jpaUserRepository.findByEmail(email)
            ?: throw UsernameNotFoundException("User not found: $email")

        return org.springframework.security.core.userdetails.User
            .withUsername(user.email)
            .password(user.password)
            .roles(user.role.name)
            .build()
    }
}