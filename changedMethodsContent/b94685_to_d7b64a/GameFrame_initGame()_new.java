	private void initGame() {
		background = new GameBackground();
		gameElement = new GameElementLayer();
		foreground = new GameForeground();
		ready = new GameReady();
		MusicUtil.load(); // װ��������Դ

		bird = new Bird();

		setGameState(STATE_READY);

		// ��������ˢ�´��ڵ��߳�
		new Thread(this).start();
	}