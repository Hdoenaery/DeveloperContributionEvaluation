	public void setScore(Bird bird) {
		if(!bird.isDead()){
			MusicUtil.playScore(); //ÿ�ε÷ֲ�����Ч
			score += 1;
			//С��û��ʱ�Ƿ�
		}
	}