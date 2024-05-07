    public void test_2() throws Exception {
        NullValue value = (NullValue) JSON.parseObject("{\"@type\":\"org.springframework.cache.support.NullValue\"}", Object.class);
        assertNotNull(value);
    }