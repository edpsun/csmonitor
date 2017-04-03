package com.edpsun.st.monitor.data.sohu;

import com.edpsun.st.monitor.configuration.HttpClientProvider;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.time.LocalDate;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {HttpClientProvider.class})
public class SohuDataRetrieverTest {

    @Autowired
    private HttpClientProvider httpClientProvider;

    private SohuDataRetriever underTest;

    @Before
    public void before() throws Exception {
        underTest = new SohuDataRetriever(httpClientProvider.closeableHttpClient());
    }

    @Test
    public void testGetData() throws Exception {
        final String code = "002214";
        final String expectedData = "[{\"status\":0,\"hq\":[[\"2017-03-31\",\"12.31\",\"12.66\"," +
                "\"0.31\",\"2.51%\",\"12.25\",\"12.75\",\"152348\",\"19179.16\",\"4.18%\"]],\"code\":\"cn_002214\",";
        final LocalDate date = LocalDate.of(2017, 3, 31);
        final String actualData = underTest.getData(code, date);

        Assert.assertTrue(actualData.indexOf(expectedData) > -1);
    }

} 
