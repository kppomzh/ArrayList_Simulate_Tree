package System;

import org.junit.Test;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

public class addClassPath {
    @Test
    public void addClassPathtoDefaultClassLoader() throws MalformedURLException, NoSuchMethodException, InvocationTargetException, IllegalAccessException, ClassNotFoundException, InstantiationException, UnsupportedEncodingException {
        String url=java.net.URLDecoder.decode("D:\\Document\\OneDrive\\CodeRepo\\junit-4.13-rc-1.jar","utf-8");
        File file1 = new File(url);
        ClassLoader classloader = ClassLoader.getSystemClassLoader();
        Method add = URLClassLoader.class.getDeclaredMethod("addURL", new Class[] { URL.class });
        add.setAccessible(true);
        add.invoke(classloader, new Object[] { file1.toURI().toURL() });

        Object o=Class.forName("org.junit.function.ThrowingRunnable").getConstructor().newInstance();
        if(o!=null){
            System.out.println(true);
        }
        else{
            System.out.println(false);
        }
    }
}
