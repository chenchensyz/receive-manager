package cn.com.cyber;

import cn.com.cyber.util.JwtUtils;
import io.jsonwebtoken.Claims;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
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
        String token = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2VyIiwiYXBwSWQiOiJ3cXJ3cWVyd2VyIiwiaWF0IjoxNTg0MTYxOTk1LCJleHAiOjE1ODQ3NjY3OTV9.6CXxgbM0-O_pLlpson4AVs3xiSUVI7V4TXX3y8cCiBQ";
        Claims claims = JwtUtils.checkJsonWebToken(token);
        System.out.println(claims);
    }

}
