package cn.com.cyber;

import cn.com.cyber.util.JwtUtils;
import io.jsonwebtoken.Claims;
import org.junit.Test;

public class JwtTest {

    @Test
    public void geneJwt(){
        String token = JwtUtils.geneJsonWebToken("wqrwqerwer");
        System.out.println(token);
        JwtUtils.checkJsonWebToken(token);
    }

    @Test
    public void checkJwt(){
//        String token = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2VyIiwiYXBwSWQiOiJ3cXJ3cWVyd2VyIiwiaWF0IjoxNTgyNDY3MTUzLCJleHAiOjE1ODMwNzE5NTN9.BmmwsoCm4nIziV47eMVQSxg8Q_CJI7uLK-rK8xF_awg";
        String token = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2VyIiwiYXBwSWQiOiJkZnNkNHRmZWRncmZnIiwiaWF0IjoxNTgyNDY3MTUzLCJleHAiOjE1ODMwNzE5NTN9.BmmwsoCm4nIziV47eMVQSxg8Q_CJI7uLK-rK8xF_awg";
        Claims claims = JwtUtils.checkJsonWebToken(token);
        System.out.println(claims.get("appId"));
    }

}
