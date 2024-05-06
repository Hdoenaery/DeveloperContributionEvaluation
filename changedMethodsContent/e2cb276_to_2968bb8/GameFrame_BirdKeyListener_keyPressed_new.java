        public void keyPressed(KeyEvent e) {
            int keycode = e.getKeyChar();
            switch (gameState) {
                case STATE_READY:
                    if (keycode == KeyEvent.VK_SPACE) {
                        // ��Ϸ��������ʱ���¿ո�С�����һ�β���ʼ������Ӱ��
                        bird.birdUp();
                        bird.birdDown();
                        setGameState(STATE_START); // ��Ϸ״̬�ı�
                    }
                    break;
                case STATE_START:
                    if (keycode == KeyEvent.VK_SPACE) {
                        //��Ϸ�����а��¿ո������һ�Σ�������������Ӱ��
                        bird.birdUp();
                        bird.birdDown();
                    }
                    break;
                case STATE_OVER:
                    if (keycode == KeyEvent.VK_SPACE) {
                        //��Ϸ����ʱ���¿ո����¿�ʼ��Ϸ
                        resetGame();
                    }
                    break;
            }
        }