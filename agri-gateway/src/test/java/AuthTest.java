import com.agri.GatewayApplication;
import com.agri.service.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = {GatewayApplication.class})
public class AuthTest {

    @Autowired
    private AuthService authService;

    @Test
    public void test() {
        String res = authService.verifyAuthentication("eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxIiwic3ViamVjdCI6IjEiLCJleHAiOjE2NjE3MDg3NDIsImlhdCI6MTY2MTcwNTE0MiwianRpIjoiZDdiMDE3ZmUtMjJkMy00M2E0LWJmZjYtODE2OWFlNjAxNGFlIn0.vdNkyVlk3bgkaawGPJQMYlzqGZckhS5GXRAROWCXqIY", "/123");
        System.out.println(res);
    }
}
