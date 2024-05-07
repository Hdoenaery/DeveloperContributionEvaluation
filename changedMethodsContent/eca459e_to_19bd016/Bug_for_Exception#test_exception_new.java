	public void test_exception() throws Exception {
		RuntimeException ex = new RuntimeException("e1");
		String text = JSON.toJSONString(ex);
		System.out.println(text);


		Object obj = JSON.parse(text);
		assertTrue(obj instanceof Map);

		RuntimeException ex2 = (RuntimeException) JSON.parseObject(text, Throwable.class);
	}