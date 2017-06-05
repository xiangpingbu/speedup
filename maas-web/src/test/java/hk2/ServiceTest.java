package hk2;

import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.api.ServiceLocatorFactory;
import org.jvnet.hk2.annotations.Service;

import javax.inject.Inject;

/**
 * @author lifeng
 * @version 1.0 on 2017/6/4.
 */
@Service
public class ServiceTest {
    @Inject
    ServiceInterface serviceInterface;

    public void test() {

        serviceInterface.say();
//        serviceInterface.say();
    }
}
