    public void test_User() throws Exception {
        String json = "{\"@type\":\"org.springframework.security.core.userdetails.User\",\"username\":\"xx\",\"authorities\":[]}";
        User token = (User)JSON.parseObject(json, Object.class);

        User token1 = (User) JSON.parseObject(json, Object.class, config);

        assertEquals("xx", token.getUsername());
        assertEquals("xx", token1.getUsername());

        assertEquals("", token.getPassword());
        assertEquals("", token1.getPassword());
    }