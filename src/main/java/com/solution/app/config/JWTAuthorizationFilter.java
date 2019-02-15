package com.solution.app.config;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;

public class JWTAuthorizationFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        specifyAllowedHeaders(httpServletResponse);
        if (httpServletRequest.getMethod().equals("OPTIONS")) {
            httpServletResponse.setStatus(HttpServletResponse.SC_OK);
        } else {
            String jwtToken = httpServletRequest.getHeader(SecurityParams.HEADER_NAME);
            checkJwtCriteria(httpServletRequest, httpServletResponse, filterChain);
            DecodedJWT decodedJWT = verifyAndDecodeJWT(jwtToken);
            UsernamePasswordAuthenticationToken user = extractUserNameAndRolesFromJwtandAssociateThemToUser(decodedJWT);
            SecurityContextHolder.getContext().setAuthentication(user);
            filterChain.doFilter(httpServletRequest, httpServletResponse);
        }
    }

    //Check if Jwt is null or starts with Bearer
    private void checkJwtCriteria(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws IOException, ServletException {
        String jwtToken = httpServletRequest.getHeader(SecurityParams.HEADER_NAME);
        if (jwtToken == null || !jwtToken.startsWith(SecurityParams.HEADER_PREFIX)) {
            filterChain.doFilter(httpServletRequest, httpServletResponse);
        }
    }

    private DecodedJWT verifyAndDecodeJWT(String jwt) {
        JWTVerifier verifier = JWT.require(Algorithm.HMAC256(SecurityParams.SECRET)).build();
        String tokenAfterSubstring = jwt.substring(SecurityParams.HEADER_PREFIX.length());
        return verifier.verify(tokenAfterSubstring);
    }

    private UsernamePasswordAuthenticationToken extractUserNameAndRolesFromJwtandAssociateThemToUser(DecodedJWT decodedJWT) {
        String username = decodedJWT.getSubject();
        Collection<String> roles = decodedJWT.getClaims().get("roles").asList(String.class);
        Collection<GrantedAuthority> authorities = new HashSet<>();
        roles.forEach(rn -> authorities.add(new SimpleGrantedAuthority(rn)));
        return
                new UsernamePasswordAuthenticationToken(username, null, authorities);
    }

    private void specifyAllowedHeaders(HttpServletResponse httpServletResponse) {
        httpServletResponse.addHeader("Access-Control-Allow-Origin", "*");
        httpServletResponse.addHeader("Access-Control-Allow-Headers", "Origin, Accept, X-Requested-With, Content-Type, Access-Control-Request-Method, Access-Control-Request-Headers,authorization");
        httpServletResponse.addHeader("Access-Control-Expose-Headers", "Access-Control-Allow-Origin, Access-Control-Allow-Credentials, authorization");
        httpServletResponse.addHeader("Access-Control-Allow-Methods", "GET,POST,PUT,DELETE,PATCH");
    }
}

