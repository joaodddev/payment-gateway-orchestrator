package br.com.joaodddev.paymentgateway.config

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.util.*

@Service
class JwtService {

    @Value("\${app.jwt.secret}")
    private lateinit var secret: String

    @Value("\${app.jwt.expiration-ms}")
    private var expirationMs: Long = 0

    fun generateToken(email: String): String {
        val key = Keys.hmacShaKeyFor(secret.toByteArray())
        return Jwts.builder()
            .subject(email)
            .issuedAt(Date())
            .expiration(Date(System.currentTimeMillis() + expirationMs))
            .signWith(key)
            .compact()
    }

    fun extractEmail(token: String): String =
        extractClaim(token, Claims::getSubject)

    fun isTokenValid(token: String, email: String): Boolean =
        extractEmail(token) == email && !isTokenExpired(token)

    private fun isTokenExpired(token: String): Boolean =
        extractClaim(token, Claims::getExpiration).before(Date())

    private fun <T> extractClaim(token: String, resolver: (Claims) -> T): T {
        val key = Keys.hmacShaKeyFor(secret.toByteArray())
        val claims = Jwts.parser()
            .verifyWith(key)
            .build()
            .parseSignedClaims(token)
            .payload
        return resolver(claims)
    }
}