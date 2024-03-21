package com.zxc;

import org.junit.Assert;
import org.junit.Test;

public class DemoTest {
    @Test
    public void testSay() {
        Demo d = new Demo();
        String res = d.say("jsq");
        Assert.assertEquals("hello jsq", res);
    }
}
