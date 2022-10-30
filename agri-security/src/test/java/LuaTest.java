import com.agri.SecurityApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;

import java.util.Arrays;
import java.util.List;

@SpringBootTest(classes = {SecurityApplication.class})
public class LuaTest {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    @Qualifier("isSameVal")
    private RedisScript<Long> isSame;

    @Test
    public void test() {
        List<String> list = Arrays.asList("com.agri.service.impl.SysUserServiceImplloadUserInfoById");
        Long execute = stringRedisTemplate.execute(isSame, list, "0ac1e721-c697-4b64-b73d-e85d08ac103e", "100");
        System.out.println(execute);
    }

    @Test
    public void test1() {
        Long expire = stringRedisTemplate.getExpire("com.agri.service.impl.SysUserServiceImplloadUserInfoById");
        System.out.println(expire);
    }
}
