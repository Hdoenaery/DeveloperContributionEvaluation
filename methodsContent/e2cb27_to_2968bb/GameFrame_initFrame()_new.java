    private void initFrame() {
        setSize(FRAME_WIDTH, FRAME_HEIGHT); // ���ô��ڴ�С
        setTitle(GAME_TITLE); // ���ô��ڱ���
        setLocation(FRAME_X, FRAME_Y); // ���ڳ�ʼλ��
        setResizable(false); // ���ô��ڴ�С���ɱ�
        // ��ӹرմ����¼����������ڷ������¼����ɷ����������󣬲���������ö�Ӧ�ķ�����
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0); // ��������
            }
        });
        addKeyListener(new BirdKeyListener()); // ��Ӱ�������
    }