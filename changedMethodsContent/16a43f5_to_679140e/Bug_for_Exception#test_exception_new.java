	public void test_exception() throws Exception {
		RuntimeException ex = new RuntimeException("e1");
		String text = JSON.toJSONString(ex);
		System.out.println(text);


		Object obj = JSON.parse(text);
		assertEquals(JSONObject.class, obj.getClass());

		Throwable throwable = JSON.parseObject(text, Throwable.class);
		assertEquals(RuntimeException.class, throwable.getClass());

		Object obj2 = JSON.parse(text);
		assertEquals(JSONObject.class, obj2.getClass());
	}