    public void update(Graphics g) {
        Graphics bufG = bufImg.getGraphics(); // 获得图片画笔
        // 使用图片画笔将需要绘制的内容绘制到图片

        background.draw(bufG, bird); // 背景层
        foreground.draw(bufG, bird); // 前景层

        // 鸟
        if (gameState == STATE_READY) { // 游戏未开始
            ready.draw(bufG);
        } else { // 游戏结束
            gameElement.draw(bufG, bird); // 游戏元素层
        }
        bird.draw(bufG); // 鸟
        g.drawImage(bufImg, 0, 0, null); // 一次性将图片绘制到屏幕上
    }