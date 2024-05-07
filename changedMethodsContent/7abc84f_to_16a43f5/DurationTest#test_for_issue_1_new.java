    public void test_for_issue_1() throws Exception {
        String text = "{\"zero\":false,\"seconds\":5184000,\"negative\":false,\"nano\":0,\"units\":[\"SECONDS\",\"NANOS\"]}";
        Duration duration = JSON.parseObject(text, Duration.class);
        assertEquals("PT1440H", duration.toString());
    }