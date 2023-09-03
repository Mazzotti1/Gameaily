
import com.whatsTheGame.Server.Security.JwtToken
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.OncePerRequestFilter

class JwtAuthenticationFilter(private val jwtToken: JwtToken) : OncePerRequestFilter() {

    override fun doFilterInternal(
         request: HttpServletRequest,
         response: HttpServletResponse,
         chain: FilterChain
    ) {
        val token = extractTokenFromRequest(request)

        if (token != null) {
            try {
                val claims = Jwts
                    .parserBuilder()
                    .setSigningKey(Keys.hmacShaKeyFor(jwtToken.secretKey.toByteArray()))
                    .build()
                    .parseClaimsJws(token)
                    .body

                val userId = claims.id
                val rolesFromToken = claims["roles"] as? List<String> ?: emptyList()
                val authorities = rolesFromToken.map { SimpleGrantedAuthority(it) }


                val authentication: Authentication = UsernamePasswordAuthenticationToken(userId, null, authorities)
                SecurityContextHolder.getContext().authentication = authentication
            } catch (ex: Exception) {

            }
        }

        chain.doFilter(request, response)
    }

    private fun extractTokenFromRequest(request: HttpServletRequest): String? {
        val header = request.getHeader("Authorization")
        return if (header != null && header.startsWith("Bearer ")) {
            header.substring(7)
        } else null
    }
}
