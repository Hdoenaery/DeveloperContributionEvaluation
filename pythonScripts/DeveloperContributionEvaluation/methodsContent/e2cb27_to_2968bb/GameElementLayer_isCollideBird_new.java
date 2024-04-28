    public void isCollideBird(Bird bird) {
        // 若鸟已死则不再判断
        if (bird.isDead()) {
            return;
        }
        // 遍历水管容器
        for (Pipe pipe : pipes) {
            // 判断碰撞矩形是否有交集
            if (pipe.getPipeRect().intersects(bird.getBirdRect())) {
                bird.birdFall(); // 有交集则小鸟坠落
                return;
            }
        }
    }