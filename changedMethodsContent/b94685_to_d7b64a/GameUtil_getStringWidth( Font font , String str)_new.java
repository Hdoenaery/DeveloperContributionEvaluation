	public static int getStringWidth(Font font, String str) {
		FontMetrics fm = FontDesignMetrics.getMetrics(font);
		return fm.stringWidth(str);
	}