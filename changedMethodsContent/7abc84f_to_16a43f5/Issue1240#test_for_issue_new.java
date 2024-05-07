    public void test_for_issue() throws Exception {
        ParserConfig parserConfig = new ParserConfig();
        parserConfig.setAutoTypeSupport(true);
        LinkedMultiValueMap<String, String> result = new LinkedMultiValueMap();
        result.add("test", "11111");
        String test = JSON.toJSONString(result, SerializerFeature.WriteClassName);
        JSON.parseObject(test, Object.class, parserConfig, JSON.DEFAULT_PARSER_FEATURE);
    }