package com.papatriz.jsfdemo.postprocessors;

import com.papatriz.jsfdemo.annotations.CustomProfile;
import com.papatriz.jsfdemo.controllers.ProfilingController;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Configuration;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class CustomProfilerBeanPostProcessor implements BeanPostProcessor {

    private Map<String, Class> classForProfiling = new HashMap<>();
    private ProfilingController profiling = new ProfilingController();
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {

        Class beanClass = bean.getClass();
        if (beanClass.isAnnotationPresent(CustomProfile.class)) classForProfiling.put(beanName, beanClass);
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Class beanClass = classForProfiling.get(beanName);

        if (beanClass != null) {
            return Proxy.newProxyInstance(beanClass.getClassLoader(), beanClass.getInterfaces(), new InvocationHandler() {
                @Override
                public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                    System.out.println("Profiling method "+method.getName()+" from class "+beanName);
                    long start = System.nanoTime();
                    Object retVal = method.invoke(bean, args);
                    long result = System.nanoTime() - start;
                    System.out.println("Time took: "+(double)result/1000000+" ms");
                    return retVal;
                }
            });
        }

        return bean;
    }
}
