    private void freeFall() {
        if (velocity < MAX_VEL_Y)
            velocity -= ACC_Y;
        y = Math.min((y - velocity), BOTTOM_BOUNDARY);
        birdCollisionRect.y = birdCollisionRect.y - velocity;
    }