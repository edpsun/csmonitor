package com.gmail.edpsun.hystock;

import java.util.Properties;

import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.gmail.edpsun.hystock.test.GreetMeBean;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:spring/app-beans.xml" })
public class AppTest extends AbstractJUnit4SpringContextTests {

    @Test
    public void testApp() throws Exception {
        assertTrue(true);
        Properties p = new Properties();
        p.load(this.getClass().getClassLoader()
                .getSystemResourceAsStream("properties/conf.properties"));
        System.out.println(p.get("scope"));
        applicationContext.getBean("greetMeBean",GreetMeBean.class).execute();
    }
}
