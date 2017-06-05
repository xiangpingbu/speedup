package hk2;

import org.jvnet.hk2.annotations.Service;

/**
 * @author lifeng
 * @version 1.0 on 2017/6/4.
 */
@Service
public class ServiceImpl implements  ServiceInterface{
    @Override
    public void say() {
        System.out.println("hello world");
    }
}
