    public void test_3() throws Exception {
        Exception ex = (Exception) JSON.parseObject("{\"@type\":\"org.springframework.dao.CannotAcquireLockException\",\"message\":\"xxx\"}", Object.class);
        assertEquals("xxx", ex.getMessage());
    }