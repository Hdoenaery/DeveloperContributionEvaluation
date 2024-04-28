	private void pipeLogic() {
		//x坐标的运动逻辑与普通水管相同
		x -= speed;
		pipeRect.x -= speed;
		if (x < -1 * PIPE_HEAD_WIDTH) {// 水管完全离开了窗口
			visible = false;
		}

		//水管上下移动的逻辑
		if (dir == DIR_DOWN) {
			dealtY++;
			if (dealtY > MAX_DEALY) {
				dir = DIR_UP;
			}
		} else {
			dealtY--;
			if (dealtY <= 0) {
				dir = DIR_DOWN;
			}
		}
		pipeRect.y = this.y + dealtY;
	}