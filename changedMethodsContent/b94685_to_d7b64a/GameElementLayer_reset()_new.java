	public void reset() {
		for (int i = 0; i < pipes.size(); i++) {
			Pipe pipe = pipes.get(i);
			PipePool.giveBack(pipe);
		}
		pipes.clear();
	}