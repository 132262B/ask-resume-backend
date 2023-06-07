package app.askresume.global.jwt.service

import app.askresume.domain.member.constant.Role
import app.askresume.global.error.ErrorCode
import app.askresume.global.error.exception.AuthenticationException
import app.askresume.global.jwt.constant.JwtGrantType
import app.askresume.global.jwt.constant.JwtTokenType
import app.askresume.global.jwt.dto.JwtResponse
import app.askresume.global.util.LoggerUtil.log
import io.jsonwebtoken.Claims
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import java.nio.charset.StandardCharsets
import java.util.*

class TokenManager(
    private val accessTokenExpirationTime: String,
    private val refreshTokenExpirationTime: String,
    private val tokenSecret: String,
) {

    private val log = log()

    private val MEMBER_ID_KEY = "memberId"
    private val ROLE_KEY = "role"

    fun createJwtTokenSet(memberId: Long?, role: Role): JwtResponse.TokenSet {
        val accessTokenResponse = createAccessToken(memberId, role)
        val refreshTokenResponse = createRefreshToken(memberId, role)

        return JwtResponse.TokenSet(
            accessToken = accessTokenResponse,
            refreshToken = refreshTokenResponse,
        )
    }

    private fun createTokenExpiration(expirationTime: Long): Date {
        return Date(System.currentTimeMillis() + expirationTime)
    }

    private fun createToken(memberId: Long?, role: Role, tokenType: JwtTokenType, expirationTime: Long): JwtResponse.Token {
        val expiration = createTokenExpiration(expirationTime)

        val token = Jwts.builder()
            .setSubject(tokenType.name)                  // 토큰 제목
            .setIssuedAt(Date())                    // 토큰 발급 시간
            .setExpiration(expiration)              // 토큰 만료 시간
            .claim(MEMBER_ID_KEY, memberId)      // 회원 아이디
            .claim(ROLE_KEY, role)              // 유저 role
            .signWith(SignatureAlgorithm.HS512, tokenSecret.toByteArray(StandardCharsets.UTF_8))
            .setHeaderParam("typ", "JWT")
            .compact()

        return JwtResponse.Token(
            grantType = JwtGrantType.BEARER.type,
            tokenType = tokenType,
            token = token,
            expirationTime = expirationTime,
            expireDate = expiration,
        )
    }

    fun createAccessToken(memberId: Long?, role: Role): JwtResponse.Token {
        return createToken(memberId, role, JwtTokenType.ACCESS, accessTokenExpirationTime.toLong())
    }

    fun createRefreshToken(memberId: Long?, role: Role): JwtResponse.Token {
        return createToken(memberId, role, JwtTokenType.REFRESH, refreshTokenExpirationTime.toLong())
    }

    fun validateToken(token: String) {
        try {
            Jwts.parser().setSigningKey(tokenSecret.toByteArray(StandardCharsets.UTF_8))
                .parseClaimsJws(token)
        } catch (e: ExpiredJwtException) {
            log.info("token 만료", e)
            throw AuthenticationException(ErrorCode.TOKEN_EXPIRED)
        } catch (e: Exception) {
            log.info("유효하지 않은 token", e)
            throw AuthenticationException(ErrorCode.NOT_VALID_TOKEN)
        }
    }

    fun getTokenClaims(token: String): Claims {
        val claims =
            try {
                Jwts.parser().setSigningKey(tokenSecret.toByteArray(StandardCharsets.UTF_8))
                    .parseClaimsJws(token).body
            } catch (e: Exception) {
                log.info("유효하지 않은 token", e)
                throw AuthenticationException(ErrorCode.NOT_VALID_TOKEN)
            }

        return claims
    }

    fun getMemberIdFromAccessToken(token: String): Long {
        val tokenClaims = getTokenClaims(token)
        val tokenType = tokenClaims.subject

        if (!JwtTokenType.isAccessToken(tokenType)) {
            throw AuthenticationException(ErrorCode.NOT_ACCESS_TOKEN_TYPE)
        }

        return (tokenClaims[MEMBER_ID_KEY] as Int?)?.toLong()
            ?: throw Exception("미구현 예외") // TODO
    }

}

