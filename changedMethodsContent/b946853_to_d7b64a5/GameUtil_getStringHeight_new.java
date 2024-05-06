	public static int getStringHeight(Font font, String str) {
		FontMetrics fm = FontDesignMetrics.getMetrics(font);
		return fm.getHeight();
	}