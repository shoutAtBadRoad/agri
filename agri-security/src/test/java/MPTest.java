import com.agri.SecurityApplication;
import com.agri.controller.SysRolePermController;
import com.agri.model.CommonResult;
import com.agri.model.SysRole;
import com.agri.service.ISysRoleService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@SpringBootTest(classes = {SecurityApplication.class})
public class MPTest {

    @Autowired
    public SysRolePermController sysRolePermController;

    @Test
    public void test() {
        CommonResult permsOfRoles = sysRolePermController.getPermsOfRoles(new ArrayList<Long>() {{
            add(1L);
        }}, 1L, 1L);
        List<Map<String,String>> body = (List<Map<String, String>>) permsOfRoles.getBody();
        for(Map<String,String> map : body) {
            map.entrySet().forEach(System.out::println);
        }
    }

    @Autowired
    private ISysRoleService roleService;

    @Test
    public void test1() {
        SysRole sysRole = new SysRole();
        sysRole.setName("ebor");
        sysRole.setRoleKey("ebor");
        roleService.save(sysRole);
        System.out.println(sysRole.getId());
    }
}
