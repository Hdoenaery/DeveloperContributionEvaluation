	public static BufferedImage loadBUfferedImage(String imgPath) {
		try {
			return ImageIO.read(new FileInputStream(imgPath));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}