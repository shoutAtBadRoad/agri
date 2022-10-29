import com.agri.SecurityApplication;
import com.agri.utils.RedisUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootTest(classes = {SecurityApplication.class})
public class RedisTest {

    @Autowired
    private RedisUtil redisUtil;

    @Test
    public void test() {
        Map<String, List<String>> map = new HashMap<>();
//        Map<String,Object> map1 = new HashMap<>();
        map.put("1", new ArrayList<>());
//        redisUtil.set("123", "123");
        boolean set = redisUtil.hmset("123", map);

    }
}
