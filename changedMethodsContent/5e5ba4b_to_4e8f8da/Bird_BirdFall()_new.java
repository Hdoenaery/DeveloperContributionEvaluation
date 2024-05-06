		if (state == STATE_DEAD || state == STATE_FALL)
			return; // 小鸟死亡或坠落时返回
		state = STATE_DOWN;
	}

	// 小鸟坠落（已死）