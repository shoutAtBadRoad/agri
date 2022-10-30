import com.agri.SecurityApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;

import java.util.List;

@SpringBootTest(classes = {SecurityApplication.class})
public class DiscoveryClientTest {

    @Autowired
    private DiscoveryClient discoveryClient;

    @Test
    public void test() {
        String id = "security";
        List<ServiceInstance> instances = discoveryClient.getInstances(id);
        instances.forEach(e-> {
            System.out.println(e.getUri());
        });
    }
}
