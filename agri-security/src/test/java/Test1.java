import com.agri.SecurityApplication;
import com.agri.mapper.MenuMapper;
import com.agri.mapper.PermsMapper;
import com.agri.mapper.UserMapper;
import com.agri.model.User;
import com.agri.security.model.LoginUser;
import com.agri.service.PermsRolesService;
import com.agri.utils.RedisUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@SpringBootTest(classes = {SecurityApplication.class})
public class Test1 {

    @Test
    public void test() {
        System.out.println("test");
    }

    @Test
    public void testPasswordEncoder() {
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        System.out.println(bCryptPasswordEncoder.encode("123"));
        System.out.println(bCryptPasswordEncoder.matches("123", "$2a$10$fjH1yvdw6Z6JxoJ.K7drIOK1WQzWqnUEDQr7vyK6nDiQO5ybTQR26"));
    }

    @Autowired
    private RedisUtil redisUtil;

    @Test
    public void jwtTest() {
        JSONObject jsonObject = (JSONObject) redisUtil.get("1");
        LoginUser loginUser = JSON.parseObject(jsonObject.toJSONString(), LoginUser.class);
        System.out.println(loginUser.toString());
    }

    @Test
    public void redisDeleteTest() {
        redisUtil.set("idd","18");
    }

    @Resource
    private UserMapper userMapper;

    @Test
    public void mapperTest() {
        List<User> users = userMapper.selectList(null);
        for(User user : users) {
            System.out.println(user.toString());
        }
    }


    @Autowired
    private PermsMapper permsMapper;

    @Test
    public void selectPermsOfRoles() {
        List<Map<String, String>> permsOfRole = permsMapper.getPermsOfRole();
        System.out.println(permsOfRole);
    }

    @Resource
    private PermsRolesService permsRolesService;

    @Test
    public void getPerms() {
//        Boolean aBoolean = permsRolesService.checkPerms("/test", new ArrayList<>() {{
//            add("/**");
//        }});
//        Map<String, List<String>> permsOfRoles = permsRolesService.getPermsOfRoles();
//        System.out.println(permsOfRoles);
    }
}
