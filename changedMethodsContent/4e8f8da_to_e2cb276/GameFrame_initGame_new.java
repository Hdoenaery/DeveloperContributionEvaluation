		gameElement = new GameElementLayer();
		foreground = new GameForeground();
		ready = new GameReady();
		bird = new Bird();
		setGameState(STATE_READY);

		// ��������ˢ�´��ڵ��߳�
		new Thread(this).start();
	}

	// ��Ŀ�д��������̣߳�ϵͳ�̣߳��Զ�����̣߳�����repaint()��
	// ϵͳ�̣߳���Ļ���ݵĻ��ƣ������¼��ļ����봦��