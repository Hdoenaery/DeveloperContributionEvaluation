		public void keyPressed(KeyEvent e) {
			int keycode = e.getKeyChar();
			switch (gameState) {
			case STATE_READY:
				if (keycode == KeyEvent.VK_SPACE) {
					bird.BirdUp();
					bird.BirdDown();
					setGameState(STATE_START);
					bird.startTiming();
				}
				break;
			case STATE_START:
				if (keycode == KeyEvent.VK_SPACE) {
					bird.BirdUp();
					bird.BirdDown();
				}
				break;
			case STATE_OVER:
				if (keycode == KeyEvent.VK_SPACE) {
					resetGame();
				}
				break;
			}
		}