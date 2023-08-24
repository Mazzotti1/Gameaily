import com.whatsTheGame.Server.Repository.UserRepository
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter


class JwtAuthenticationFilter : UsernamePasswordAuthenticationFilter() {

    private val userRepository: UserRepository = TODO()
    fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        chain: FilterChain
    ) {
        val token = extractTokenFromRequest(request)

        if (token != null) {
            // Valide e decodifique o token JWT aqui (use a biblioteca jjwt)
            // Verifique se o token é válido e não expirou, etc.
            val claims = Jwts.parser()
                .setSigningKey("asdasdas")
                .parseClaimsJws(token)
                .body as Claims

            // Extraia o userId das reivindicações
            val userId = claims["userId"] as String

            // Crie um objeto de autenticação com o userId
            val authentication: Authentication = UsernamePasswordAuthenticationToken(userId, null)

            // Defina a autenticação no contexto de segurança
            SecurityContextHolder.getContext().authentication = authentication
        }
        chain.doFilter(request, response)
    }
    private fun extractTokenFromRequest(request: HttpServletRequest): String? {
        val authorizationHeader = request.getHeader("Authorization")
        return authorizationHeader?.replace("Bearer ", "")

    }
}

