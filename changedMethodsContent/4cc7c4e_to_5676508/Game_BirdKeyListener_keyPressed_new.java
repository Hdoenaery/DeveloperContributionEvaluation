        public void keyPressed(KeyEvent e) {
            int keycode = e.getKeyChar();
            switch (gameState) {
                case GAME_READY:
                    if (keycode == KeyEvent.VK_SPACE) {
                        // 游戏启动界面时按下空格，小鸟振翅一次并开始受重力影响
                        bird.birdFlap();
                        bird.birdFall();
                        setGameState(GAME_START); // 游戏状态改变
                    }
                    break;
                case GAME_START:
                    if (keycode == KeyEvent.VK_SPACE) {
                        //游戏过程中按下空格则振翅一次，并持续受重力影响
                        bird.birdFlap();
                        bird.birdFall();
                    }
                    break;
                case STATE_OVER:
                    if (keycode == KeyEvent.VK_SPACE) {
                        //游戏结束时按下空格，重新开始游戏
                        resetGame();
                    }
                    break;
            }
        }