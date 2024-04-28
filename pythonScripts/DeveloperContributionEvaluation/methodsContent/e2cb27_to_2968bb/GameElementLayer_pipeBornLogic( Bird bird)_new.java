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
            if (lastPipe.isInFrame()) {
                if (pipes.size() >= Constant.FULL_PIPE - 2)// �������п����ɵ�ˮ��������˵��С���ѷɵ���һ��ˮ�ܵ�λ�ã���ʼ�Ƿ�
                    GameScore.getInstance().setScore(bird);
                try {
                    int currentScore = (int) GameScore.getInstance().getScore() + 1; // ��ȡ��ǰ����
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