    public void test_0() throws Exception {
        DefaultOAuth2AccessToken token = new DefaultOAuth2AccessToken("123");
        token.setExpiration(new Date());
        String json = JSON.toJSONString(token, SerializerFeature.WriteClassName);
        DefaultOAuth2AccessToken token2 = (DefaultOAuth2AccessToken) JSON.parse(json);
        assertEquals(token.getValue(), token2.getValue());
        assertEquals(token.getExpiration(), token2.getExpiration());
    }