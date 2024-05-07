    public void test_for_bug() throws Exception {
        Model m = JSON.parseObject("{\"value\":{\"@type\":\"com.alibaba.fastjson.util.AntiCollisionHashMap\"}}", Model.class);
        assertTrue(m.value.getInnerMap() instanceof AntiCollisionHashMap);
    }