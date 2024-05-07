    public void test_exception() throws Exception {
        IllegalAccessError ex = new IllegalAccessError();

        String text = JSON.toJSONString(ex);
        assertTrue(JSON.parse(text) instanceof IllegalAccessError);
    }