        } else { // ��Ϸ����
            gameElement.draw(bufG, bird); // ��ϷԪ�ز�
        }
        bird.draw(bufG);
        g.drawImage(bufImg, 0, 0, null); // һ���Խ�ͼƬ���Ƶ���Ļ��
    }

    public static void setGameState(int gameState) {
        Game.gameState = gameState;
    }