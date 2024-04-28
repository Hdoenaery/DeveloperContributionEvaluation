    private void pipeBornLogic(Bird bird) {
        if (bird.isDead()) {
            // 鸟死后不再添加水管
            return;
        }
        if (pipes.size() == 0) {
            // 若容器为空，则添加一对水管
            int topHeight = GameUtil.getRandomNumber(MIN_HEIGHT, MAX_HEIGHT + 1); // 随机生成水管高度

            Pipe top = PipePool.get("Pipe");
            top.setAttribute(Constant.FRAME_WIDTH, -Constant.TOP_PIPE_LENGTHENING,
                    topHeight + Constant.TOP_PIPE_LENGTHENING, Pipe.TYPE_TOP_NORMAL, true);

            Pipe bottom = PipePool.get("Pipe");
            bottom.setAttribute(Constant.FRAME_WIDTH, topHeight + VERTICAL_INTERVAL,
                    Constant.FRAME_HEIGHT - topHeight - VERTICAL_INTERVAL, Pipe.TYPE_BOTTOM_NORMAL, true);

            pipes.add(top);
            pipes.add(bottom);
        } else {
            // 判断最后一对水管是否完全进入游戏窗口，若进入则添加水管
            Pipe lastPipe = pipes.get(pipes.size() - 1); // 获得容器中最后一个水管
            int currentDistance = lastPipe.getX() - bird.getBirdX() + Bird.BIRD_WIDTH / 2; // 小鸟和最后一根水管的距离
            final int SCORE_DISTANCE = Pipe.PIPE_WIDTH * 2 + HORIZONTAL_INTERVAL; // 小于得分距离则得分
            if (lastPipe.isInFrame()) {
                if (pipes.size() >= PipePool.FULL_PIPE - 2) {
                    ScoreCounter.getInstance().score(bird);
                }
                try {
                    int currentScore = (int) ScoreCounter.getInstance().getCurrentScore() + 1; // 获取当前分数
                    // 移动水管刷新的概率随当前分数递增，当得分大于19后全部刷新移动水管
                    if (GameUtil.isInProbability(currentScore, 20)) {
                        if (GameUtil.isInProbability(1, 4)) // 生成移动水管和移动悬浮水管的概率
                            addMovingHoverPipe(lastPipe);
                        else
                            addMovingNormalPipe(lastPipe);
                    } else {
                        if (GameUtil.isInProbability(1, 2)) // 生成静止普通水管和静止悬浮水管的概率
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