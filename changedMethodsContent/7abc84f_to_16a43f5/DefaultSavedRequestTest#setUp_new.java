    protected void setUp() throws Exception {
        Field field = GenericFastJsonRedisSerializer.class.getDeclaredField("defaultRedisConfig");
        field.setAccessible(true);
        config = (ParserConfig) field.get(null);
        config.addAccept("org.springframework.security.web.savedrequest.DefaultSavedRequest");
    }