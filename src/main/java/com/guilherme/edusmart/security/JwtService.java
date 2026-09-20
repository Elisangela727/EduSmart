package com.guilherme.edusmart.security;

import com.guilherme.edusmart.model.Usuario;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

@Service
public class JwtService {

    private final SecretKey chave;
    private final long expiracao;

    public JwtService(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration}") long expiracao) {

        this.chave = Keys.hmacShaKeyFor(
                secret.getBytes(StandardCharsets.UTF_8)
        );

        this.expiracao = expiracao;
    }

    public String gerarToken(Usuario usuario) {

        Instant agora = Instant.now();
        Instant vencimento = agora.plusMillis(expiracao);

        return Jwts.builder()
                .subject(usuario.getEmail())
                .claim("idUsuario", usuario.getIdUsuario())
                .claim("nome", usuario.getNome())
                .claim("tipoUsuario", usuario.getTipoUsuario())
                .issuedAt(Date.from(agora))
                .expiration(Date.from(vencimento))
                .signWith(chave)
                .compact();
    }

    public String extrairEmail(String token) {
        return extrairClaims(token).getSubject();
    }

    public boolean tokenValido(String token, String email) {

        try {
            Claims claims = extrairClaims(token);

            return claims.getSubject().equals(email)
                    && claims.getExpiration().after(new Date());

        } catch (Exception exception) {
            return false;
        }
    }

    private Claims extrairClaims(String token) {

        return Jwts.parser()
                .verifyWith(chave)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}