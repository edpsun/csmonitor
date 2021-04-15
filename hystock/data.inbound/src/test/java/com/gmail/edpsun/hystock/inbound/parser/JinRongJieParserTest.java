package com.gmail.edpsun.hystock.inbound.parser;

import com.gmail.edpsun.hystock.model.Stock;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.io.File;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class JinRongJieParserTest {
    private JinRongJieParser underTest;

    @Before
    public void setUp() {
        underTest = new JinRongJieParser();
    }

    @Test
    public void testParseForInvalidStock() throws Exception {
        final URL url = getClass().getClassLoader().getResource("jrj_600036.response");

        final String content = FileUtils.readFileToString(new File(url.getFile()), "UTF-8");

        System.out.println(content);

        final Stock stock = underTest.parse("000748", "Invalid", content);
//        assertEquals("000748", stock.getId());
//        assertEquals("Invalid", stock.getName());
    }

    @Test
    public void testJS() throws Exception {
        final String jsStr = "function myFuc(param){return \"the param is:\"+param;}";//js脚本内容
        System.out.println(jsObjFunc());
    }

    public Object jsObjFunc() throws Exception {
        final ScriptEngineManager sem = new ScriptEngineManager();
        final ScriptEngine scriptEngine = sem.getEngineByName("graal.js");

        final URL url = getClass().getClassLoader().getResource("jrj_600036.response");
        final String content = FileUtils.readFileToString(new File(url.getFile()), StandardCharsets.UTF_8);

        final String myJs = "function get_list(){return stockgudongList.data[0];}";

        try {
            scriptEngine.eval(content);
            scriptEngine.eval(myJs);

            final Invocable inv2 = (Invocable) scriptEngine;

            System.out.println(inv2.invokeFunction("get_list"));
            return null;
        } catch (final Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}