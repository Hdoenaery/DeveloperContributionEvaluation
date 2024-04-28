    private void die() {
        counter.saveScore();
        state = BIRD_DEAD;
        Game.setGameState(Game.STATE_OVER);
    }