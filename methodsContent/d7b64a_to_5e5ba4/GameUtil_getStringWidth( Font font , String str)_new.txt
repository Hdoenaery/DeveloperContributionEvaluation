	public static int getStringWidth(Font font, String str) {
		AffineTransform affinetransform = new AffineTransform();     
		FontRenderContext frc = new FontRenderContext(affinetransform,true,true); 
		int textHeight = (int)(font.getStringBounds(str, frc).getWidth());
		return textHeight;
	}