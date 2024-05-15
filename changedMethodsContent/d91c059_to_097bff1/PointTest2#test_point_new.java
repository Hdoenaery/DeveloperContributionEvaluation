    public void test_point() throws Exception {
        JSONSerializer serializer = new JSONSerializer();
        Assert.assertEquals(AwtCodec.class, serializer.getObjectWriter(Point.class).getClass());
        
        Point point = new Point(3, 4);
        String text = JSON.toJSONString(point, SerializerFeature.WriteClassName);

        System.out.println(text);
        Object obj = JSON.parse(text);
        Point point2 = (Point) obj;

        Assert.assertEquals(point, point2);

        Point point3 = (Point) JSON.parseObject(text, Point.class);

        Assert.assertEquals(point, point3);
    }