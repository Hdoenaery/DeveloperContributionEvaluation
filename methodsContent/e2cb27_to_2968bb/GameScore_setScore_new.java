	public void setScore(Bird bird) {
		if(!bird.isDead()){
			MusicUtil.playScore(); //每次得分播放音效
			score += 1;
			//小鸟没死时记分
		}
	}