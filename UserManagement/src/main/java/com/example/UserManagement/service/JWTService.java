package com.example.UserManagement.service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class JWTService {

	private String secretKey = "5367566B59703373367639792F423F4528482B4D6251655468576D5A71347437"; //Randomlly generated value by developer
	
	/*public JWTService() {
		try {
			KeyGenerator keyGenerator = KeyGenerator.getInstance("HmacSHA256");
			SecretKey key = keyGenerator.generateKey();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}*/
	
	// 60 -second , 60- minutes, 30 -hours
	public String generateToken(String username) {
		Map<String,Object> claims = new HashMap<>();
		return Jwts.builder()
				.claims()
				.add(claims)
				.subject(username)
				.issuedAt(new Date(System.currentTimeMillis()))
				.expiration(new Date(System.currentTimeMillis() + 100*60*30))
				.and()
				.signWith(getKey())
				.compact();
	}
	
	public SecretKey getKey() {
		byte[] keyBytes = Decoders.BASE64.decode(secretKey);
		return Keys.hmacShaKeyFor(keyBytes);
	}
	
	public String extractUsername(String token) {
		//extract the username from jwt token
		return extractClaim(token,Claims::getSubject);
	}
	
	private <T> T extractClaim(String token,Function<Claims, T> claimResolver) {
		final Claims claims = extractAllClaims(token);
		return claimResolver.apply(claims);
	}
	
	private Claims extractAllClaims(String token) {
		
		return Jwts.parser()
				.verifyWith(getKey())
				.build()
				.parseSignedClaims(token)
				.getPayload();
	}

	public boolean validateToken(String token, UserDetails userDetails){
        String username = extractUsername(token);
        return (username.equalsIgnoreCase(userDetails.getUsername()) && !isTokenExpired(token));
    }
	
	public boolean isTokenExpired(String token){
        return extractExpiration(token).before(new Date());
    }

	private Date extractExpiration(String token) {
		
		return extractClaim(token, Claims::getExpiration);
	}
}
