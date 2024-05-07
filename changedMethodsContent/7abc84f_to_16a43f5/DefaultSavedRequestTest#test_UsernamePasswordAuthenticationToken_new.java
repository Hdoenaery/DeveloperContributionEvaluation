    public void test_UsernamePasswordAuthenticationToken() throws Exception {
        String json = "{\"@type\":\"org.springframework.security.authentication.UsernamePasswordAuthenticationToken\",\"principal\":\"pp\"}";
        UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken)JSON.parseObject(json, Object.class);

        UsernamePasswordAuthenticationToken token1 = (UsernamePasswordAuthenticationToken) JSON.parseObject(json, Object.class, config);

        assertEquals("pp", token.getPrincipal());
        assertEquals("pp", token1.getPrincipal());
    }