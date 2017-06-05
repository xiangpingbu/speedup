package hk2;

import javafx.scene.effect.Reflection;
import org.glassfish.hk2.api.DynamicConfiguration;
import org.glassfish.hk2.api.DynamicConfigurationService;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.api.ServiceLocatorFactory;
import org.glassfish.hk2.utilities.BuilderHelper;
import org.junit.Before;
import org.junit.Test;
import org.jvnet.hk2.annotations.Contract;
import org.jvnet.hk2.annotations.Service;
import org.reflections.Reflections;

import javax.inject.Singleton;
import java.util.Set;

/**
 * @author lifeng
 * @version 1.0 on 2017/6/4.
 */
public class ServiceLocatorTest {
    private Set<Class<?>> serviceClasses;
    private Set<Class<?>> contractClasses;
    @Before
    public void scan() {
        Reflections reflections =  new Reflections("hk2");
         serviceClasses = reflections.getTypesAnnotatedWith(Service.class);


         contractClasses = reflections.getTypesAnnotatedWith(Contract.class);
    }

    @Test
    public void init() {
        ServiceLocatorFactory factory = ServiceLocatorFactory.getInstance();
        ServiceLocator locator = factory.create("HelloWorld");
        DynamicConfigurationService dcs = locator.getService(DynamicConfigurationService.class);
        DynamicConfiguration config = dcs.createDynamicConfiguration();

        for(Class<?> serviceClass:serviceClasses){
            Class<?> linkTo = serviceClass;
            for (Class<?> contractClass : contractClasses) {
                if(contractClass.isAssignableFrom(serviceClass) && contractClass.isInterface() ){
                    linkTo = contractClass;
                    break;
                }
            }
            config.bind( BuilderHelper.link(serviceClass).to(linkTo).in(Singleton.class).build() );
        }
        config.commit();


        locator = ServiceLocatorFactory.getInstance().find("HelloWorld");
        ServiceTest serviceTest = locator.getService(ServiceTest.class);
        serviceTest.test();
    }
}
