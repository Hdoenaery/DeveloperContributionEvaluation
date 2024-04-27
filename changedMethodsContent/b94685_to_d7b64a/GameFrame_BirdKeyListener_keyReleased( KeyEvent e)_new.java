		public void keyReleased(KeyEvent e) {
			int keycode = e.getKeyChar();
			if (keycode == KeyEvent.VK_SPACE) {
				bird.keyReleased();
			}
		}