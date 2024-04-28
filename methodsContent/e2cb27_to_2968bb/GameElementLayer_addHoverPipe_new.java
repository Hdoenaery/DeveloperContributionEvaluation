    private void addHoverPipe(Pipe lastPipe) {

        // 随机生成水管高度,屏幕高度的[1/4,1/6]
        int topHoverHeight = GameUtil.getRandomNumber(Constant.FRAME_HEIGHT / 6, Constant.FRAME_HEIGHT / 4);
        int x = lastPipe.getX() + HORIZONTAL_INTERVAL; // 新水管的x坐标 = 最后一对水管的x坐标 + 水管的间隔
        int y = GameUtil.getRandomNumber(Constant.FRAME_HEIGHT / 12, Constant.FRAME_HEIGHT / 6); // 随机水管的y坐标，窗口的[1/6,1/12]

        int type = Pipe.TYPE_HOVER_NORMAL;

        // 生成上部的悬浮水管
        Pipe topHover = PipePool.get("Pipe");
        topHover.setAttribute(x, y, topHoverHeight, type, true);

        // 生成下部的悬浮水管
        int bottomHoverHeight = Constant.FRAME_HEIGHT - 2 * y - topHoverHeight - VERTICAL_INTERVAL;
        Pipe bottomHover = PipePool.get("Pipe");
        bottomHover.setAttribute(x, y + topHoverHeight + VERTICAL_INTERVAL, bottomHoverHeight, type, true);

        pipes.add(topHover);
        pipes.add(bottomHover);

    }