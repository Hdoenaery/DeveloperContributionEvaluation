    public void test_SimpleGrantedAuthority() throws Exception {
        String json = "{\"@type\":\"org.springframework.security.core.authority.SimpleGrantedAuthority\",\"authority\":\"xx\"}";
        SimpleGrantedAuthority token = (SimpleGrantedAuthority)JSON.parseObject(json, Object.class);

        SimpleGrantedAuthority token1 = (SimpleGrantedAuthority) JSON.parseObject(json, Object.class, config);

        assertEquals("xx", token.getAuthority());
        assertEquals("xx", token1.getAuthority());

        assertEquals("{\"authority\":\"xx\"}", JSON.toJSONString(token));
    }