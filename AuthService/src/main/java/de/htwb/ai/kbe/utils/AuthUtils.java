package de.htwb.ai.kbe.utils;

import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.Key;
import java.util.Date;

public class AuthUtils {

    public static String generateNewToken(String user, String password) {
        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);
        String private_key = "oeRaYY7Wo24sDqKSX3IM9ASGmdGPmkTd9jo1QTy4b7P9Ze5_9hKo"; // should be saved elsewhere
        byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(private_key);
        Key signingKey = new SecretKeySpec(apiKeySecretBytes, SignatureAlgorithm.HS256.getJcaName());

        JwtBuilder builder = Jwts.builder()
                .setIssuedAt(now)
                .claim("user", user)
                .claim("password", password)
                .signWith(signingKey, SignatureAlgorithm.HS256);
        return builder.compact();
    }

}
