		gameElement = new GameElementLayer();
		foreground = new GameForeground();
		ready = new GameReady();
		bird = new Bird();
		setGameState(STATE_READY);

		// 启动用于刷新窗口的线程
		new Thread(this).start();
	}

	// 项目中存在两个线程：系统线程，自定义的线程：调用repaint()。
	// 系统线程：屏幕内容的绘制，窗口事件的监听与处理