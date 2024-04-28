        } else { // 游戏结束
            gameElement.draw(bufG, bird); // 游戏元素层
        }
        bird.draw(bufG);
        g.drawImage(bufImg, 0, 0, null); // 一次性将图片绘制到屏幕上
    }

    public static void setGameState(int gameState) {
        Game.gameState = gameState;
    }