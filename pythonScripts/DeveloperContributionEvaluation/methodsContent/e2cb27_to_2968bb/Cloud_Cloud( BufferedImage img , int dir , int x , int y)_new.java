        scaleImageWidth = (int) (scale * img.getWidth());
        scaleImageHeight = (int) (scale * img.getWidth());
    }

    // »æÖÆ·½·¨
    public void draw(Graphics g, Bird bird) {
        int speed = this.speed;
        if (bird.isDead())
            speed = 1;
        x -= speed;
        g.drawImage(img, x, y, scaleImageWidth, scaleImageHeight, null);
    }
