    private void pipeBornLogic(Bird bird) {
        if (bird.isDead()) {
            // �����������ˮ��
            return;
        }
        if (pipes.size() == 0) {
            // ������Ϊ�գ������һ��ˮ��
            int topHeight = GameUtil.getRandomNumber(MIN_HEIGHT, MAX_HEIGHT + 1); // �������ˮ�ܸ߶�

            Pipe top = PipePool.get("Pipe");
            top.setAttribute(Constant.FRAME_WIDTH, -Constant.TOP_PIPE_LENGTHENING,
                    topHeight + Constant.TOP_PIPE_LENGTHENING, Pipe.TYPE_TOP_NORMAL, true);

            Pipe bottom = PipePool.get("Pipe");
            bottom.setAttribute(Constant.FRAME_WIDTH, topHeight + VERTICAL_INTERVAL,
                    Constant.FRAME_HEIGHT - topHeight - VERTICAL_INTERVAL, Pipe.TYPE_BOTTOM_NORMAL, true);

            pipes.add(top);
            pipes.add(bottom);
        } else {
            // �ж����һ��ˮ���Ƿ���ȫ������Ϸ���ڣ������������ˮ��
            Pipe lastPipe = pipes.get(pipes.size() - 1); // ������������һ��ˮ��
            int currentDistance = lastPipe.getX() - bird.getBirdX() + Bird.BIRD_WIDTH / 2; // С������һ��ˮ�ܵľ���
            final int SCORE_DISTANCE = Pipe.PIPE_WIDTH * 2 + HORIZONTAL_INTERVAL; // С�ڵ÷־�����÷�
            if (lastPipe.isInFrame()) {
                if (pipes.size() >= PipePool.FULL_PIPE - 2) {
                    ScoreCounter.getInstance().score(bird);
                }
                try {
                    int currentScore = (int) ScoreCounter.getInstance().getCurrentScore() + 1; // ��ȡ��ǰ����
                    // �ƶ�ˮ��ˢ�µĸ����浱ǰ�������������÷ִ���19��ȫ��ˢ���ƶ�ˮ��
                    if (GameUtil.isInProbability(currentScore, 20)) {
                        if (GameUtil.isInProbability(1, 4)) // �����ƶ�ˮ�ܺ��ƶ�����ˮ�ܵĸ���
                            addMovingHoverPipe(lastPipe);
                        else
                            addMovingNormalPipe(lastPipe);
                    } else {
                        if (GameUtil.isInProbability(1, 2)) // ���ɾ�ֹ��ͨˮ�ܺ;�ֹ����ˮ�ܵĸ���
                            addNormalPipe(lastPipe);
                        else
                            addHoverPipe(lastPipe);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }
    }