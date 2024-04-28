package com.kingyu.flappybird.game;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import com.kingyu.flappybird.util.Constant;
import com.kingyu.flappybird.util.MusicUtil;

/**
 * 游戏计时类, 单例类，方便调用
 *
 * @author Kingyu
 *
 */
public class ScoreCounter {
	private static final ScoreCounter scoreCounter = new ScoreCounter();

	private long score = 0; // 分数
	private long bestScore; // 最高分数

	private ScoreCounter() {
		bestScore = -1;
		try {
			loadBestScore();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static ScoreCounter getInstance() {
		return scoreCounter;
	}

	// 装载最高纪录
	private void loadBestScore() throws Exception {
		File file = new File(Constant.SCORE_FILE_PATH);
		if (file.exists()) {
			DataInputStream dis = new DataInputStream(new FileInputStream(file));
			bestScore = dis.readLong();
			dis.close();
		}
	}

	public void saveScore() {
		bestScore = Math.max(bestScore, getCurrentScore());
		try {
			File file = new File(Constant.SCORE_FILE_PATH);
			DataOutputStream dos = new DataOutputStream(new FileOutputStream(file));
			dos.writeLong(bestScore);
			dos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void score(Bird bird) {
		if (!bird.isDead()) {
			MusicUtil.playScore();
			score += 1;
		}
	}

	public long getBestScore() {
		return bestScore;
	}

	public long getCurrentScore() {
		return score;
	}

	public void reset() {
		score = 0;
	}

}
