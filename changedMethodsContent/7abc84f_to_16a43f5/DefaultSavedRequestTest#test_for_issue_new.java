    public void test_for_issue() throws Exception {
        MockHttpServletRequest mockReq = new MockHttpServletRequest();
        DefaultSavedRequest request = new DefaultSavedRequest(mockReq, new PortResolver() {

            public int getServerPort(ServletRequest servletRequest) {
                return 0;
            }
        });

        String str = JSON.toJSONString(request, SerializerFeature.WriteClassName);
//        System.out.println(str);


        JSON.parseObject(str, Object.class, config);

        JSON.parseObject(str);
    }