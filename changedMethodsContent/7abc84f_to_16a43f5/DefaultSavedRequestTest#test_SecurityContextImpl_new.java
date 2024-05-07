    public void test_SecurityContextImpl() throws Exception {
        String json = "{\"@type\":\"org.springframework.security.core.context.SecurityContextImpl\"}";
        JSON.parseObject(json, Object.class);

        JSON.parseObject(json, Object.class, config);
    }