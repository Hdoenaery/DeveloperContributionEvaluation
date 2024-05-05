        public void keyPressed(KeyEvent e) {
            int keycode = e.getKeyChar();
            switch (gameState) {
                case GAME_READY:
                    if (keycode == KeyEvent.VK_SPACE) {
                        // ��Ϸ��������ʱ���¿ո�С�����һ�β���ʼ������Ӱ��
                        bird.birdFlap();
                        bird.birdFall();
                        setGameState(GAME_START); // ��Ϸ״̬�ı�
                    }
                    break;
                case GAME_START:
                    if (keycode == KeyEvent.VK_SPACE) {
                        //��Ϸ�����а��¿ո������һ�Σ�������������Ӱ��
                        bird.birdFlap();
                        bird.birdFall();
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