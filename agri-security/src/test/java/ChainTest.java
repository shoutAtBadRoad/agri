import com.agri.SecurityApplication;
import com.agri.filter.jwtfilter.JwtFilterChain;
import com.agri.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = {SecurityApplication.class})
public class ChainTest {

    @Autowired
    JwtFilterChain chain;

    @Test
    public void test() {
        chain.doCheck("123", null, "123");
    }

    @Test
    public void parseJwt() {
        Claims claims = null;
        try {
            claims = JwtUtil.parseJWT("eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxIiwic3ViamVjdCI6IjEiLCJleHAiOjE2NjE3MjI3NDgsImlhdCI6MTY2MTcxOTE0OCwianRpIjoiODUxODc3ODItMTNiNi00NzdiLTk4YmQtNmExMmU5YjUxMDM5In0.qQp1vIZq9fbs4Wk3MYIG4U-gwoCo28xDy8qQZHauSwQ");
        }catch (Exception e) {
            e.printStackTrace();
            System.out.println("token非法");
        }
        System.out.println("token合法");
    }

}
