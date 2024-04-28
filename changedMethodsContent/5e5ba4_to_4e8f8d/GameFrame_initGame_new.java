	private void initGame() {
		background = new GameBackground();
		gameElement = new GameElementLayer();
		foreground = new GameForeground();
		ready = new GameReady();
		bird = new Bird();
		MusicUtil.load();
		setGameState(STATE_READY);

		// 启动用于刷新窗口的线程
		new Thread(this).start();
	}