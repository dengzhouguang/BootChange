package com.dzg.bootchange;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dzg.bootchange.bean.StepBean;

import org.junit.Test;

import java.util.Date;
import java.util.List;
import java.util.Random;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        String str="<string name=\"firstStep\">{&quot;biz&quot;:&quot;alipay&quot;,&quot;steps&quot;:0,&quot;time&quot;:1503261196824}</string>";
        System.out.print(str.replace("\"","\\\""));
    }
}