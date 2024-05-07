    public void test_color() throws Exception {
        JSONSerializer serializer = new JSONSerializer();
        Assert.assertEquals(AwtCodec.class, serializer.getObjectWriter(Rectangle.class).getClass());
        
        Rectangle v = new Rectangle(3, 4, 100, 200);
        String text = JSON.toJSONString(v, SerializerFeature.WriteClassName);
        
        System.out.println(text);

        Rectangle v2 = (Rectangle) JSON.parse(text);

        Assert.assertEquals(v, v2);
    }