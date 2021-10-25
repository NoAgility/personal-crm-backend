package com.noagility.personalcrm.Util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import com.noagility.personalcrm.model.Account;
import com.noagility.personalcrm.service.AccountService;


@Slf4j
@Component("jwtTokenUtil")
public class JwtTokenUtil implements Serializable {

    private static final long serialVersionUID = -2550185165626007488L;

    public static final long JWT_TOKEN_VALIDITY = 5 * 60 * 60;

    @Value("${jwt.secret}")
    private String secret;

    @Autowired
    AccountService accountService;

    /**
     * Method to retrieve the username from a token
     * @param token The JWT token
     * @return A String containing the username
     */
    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    /**
     * Method to retrieve the expiration date of a token
     * @param token The JWT token
     * @return A Date object indicating the expiration date
     */
    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }
    //for retrieveing any information from token we will need the secret key
    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
    }

    /**
     * Method to check if a token has expired
     * @param token The token to check
     * @return A boolean indicating if the token's expired
     */
    private Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    /**
     * Wrapper method for generating a token
     * @param userDetails The user details to generate a token for
     * @return A String - the token
     */
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        return doGenerateToken(claims, userDetails.getUsername());
    }

    /**
     * Generates a token for a subject -
     * Define claims of the token and the signs the JWT using the HS512 algorithm and the application's secret key.
     */
    private String doGenerateToken(Map<String, Object> claims, String subject) {
        return Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY * 1000))
                .signWith(SignatureAlgorithm.HS512, secret).compact();
    }

    /**
     * Method to validate a JWT token
     * @param token The token to validate
     * @param userDetails The user details to compare against
     * @return a boolean indicating the result of the validation
     */
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = getUsernameFromToken(token);
        try {
            return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
        } catch (NullPointerException e) {
            log.error("Failed to fetch username - userDetails is not found");
            return false;
        }

    }

    /**
     * Method to get an account from a JWT token
     * @param token The JWT token
     * @return An account object
     */
    public Account getAccountFromToken(String token){
        return accountService.getByUsername(getUsernameFromToken(token));
    }
    /**
     * Method to validate an account is the sender of a token
     * @param token The JWT token
     * @param accountID The id of the account to verify against
     * @return A boolean indicating the result of the verification
     */
    public boolean validateTokenSender(String token, int accountID){
        return accountID == (getAccountFromToken(token).getAccountID());
    }

    
}