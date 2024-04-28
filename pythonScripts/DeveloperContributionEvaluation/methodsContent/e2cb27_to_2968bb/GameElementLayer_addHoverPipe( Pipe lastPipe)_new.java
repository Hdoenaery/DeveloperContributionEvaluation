    private void addHoverPipe(Pipe lastPipe) {

        // �������ˮ�ܸ߶�,��Ļ�߶ȵ�[1/4,1/6]
        int topHoverHeight = GameUtil.getRandomNumber(Constant.FRAME_HEIGHT / 6, Constant.FRAME_HEIGHT / 4);
        int x = lastPipe.getX() + HORIZONTAL_INTERVAL; // ��ˮ�ܵ�x���� = ���һ��ˮ�ܵ�x���� + ˮ�ܵļ��
        int y = GameUtil.getRandomNumber(Constant.FRAME_HEIGHT / 12, Constant.FRAME_HEIGHT / 6); // ���ˮ�ܵ�y���꣬���ڵ�[1/6,1/12]

        int type = Pipe.TYPE_HOVER_NORMAL;

        // �����ϲ�������ˮ��
        Pipe topHover = PipePool.get("Pipe");
        topHover.setAttribute(x, y, topHoverHeight, type, true);

        // �����²�������ˮ��
        int bottomHoverHeight = Constant.FRAME_HEIGHT - 2 * y - topHoverHeight - VERTICAL_INTERVAL;
        Pipe bottomHover = PipePool.get("Pipe");
        bottomHover.setAttribute(x, y + topHoverHeight + VERTICAL_INTERVAL, bottomHoverHeight, type, true);

        pipes.add(topHover);
        pipes.add(bottomHover);

    }