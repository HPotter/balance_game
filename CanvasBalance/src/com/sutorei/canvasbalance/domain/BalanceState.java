package com.sutorei.canvasbalance.domain;

public enum BalanceState {
	LEFT_IS_HEAVIER, EQUAL, RIGHT_IS_HEAVIER;

	/**
	 * -1 and +1 for left- and right-unbalanced, 0 for balanced
	 * @param i
	 * @return
	 */
	public static BalanceState fromInt(int i) {
		return values()[i+1];
	}
}
