package com.xyxl.tianyingn3.util;

import com.xyxl.tianyingn3.bean.SortModel;

import java.util.Comparator;

public class PinyinComparator implements Comparator<SortModel> {

	public int compare(SortModel o1, SortModel o2) {
		if (o1.getLetters().equals("@")
				|| o2.getLetters().equals("#")) {
			return 1;
		} else if (o1.getLetters().equals("#")
				|| o2.getLetters().equals("@")) {
			return -1;
		} else {
			return o1.getLetters().compareTo(o2.getLetters());
		}
	}

}
