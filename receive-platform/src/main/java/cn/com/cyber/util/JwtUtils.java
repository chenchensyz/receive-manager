package cn.com.cyber.util;

import cn.com.cyber.model.Developer;
import io.jsonwebtoken.*;

import javax.crypto.SecretKey;
import java.util.Date;

public class JwtUtils {

    private static final String SUBJECT = "user";

    private static final long EXPIRE = 1000 * 60 * 60 * 24 * 7; //过期时间 毫秒 1周

    private static final String APPSECRET = "*#!curd"; //密钥

    public static String geneJsonWebToken(String appId) {
        String token = Jwts.builder().setSubject(SUBJECT).claim("appId", appId)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRE))
                .signWith(SignatureAlgorithm.HS256, APPSECRET)
                .compact();
        return token;
    }

    public static Claims checkJsonWebToken(String token){
        Claims claims = null;
        try {
            claims = Jwts.parser().setSigningKey(APPSECRET).parseClaimsJws(token).getBody();
        } catch (ExpiredJwtException e) {
            e.printStackTrace();
        } catch (UnsupportedJwtException e) {
            e.printStackTrace();
        } catch (MalformedJwtException e) {
            e.printStackTrace();
        } catch (SignatureException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        return claims;
    }

}