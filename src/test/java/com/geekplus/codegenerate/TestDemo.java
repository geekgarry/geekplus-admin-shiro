package com.geekplus.codegenerate;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class TestDemo {

	public static void main(String[] args) {
		Date date = new Date();
		SimpleDateFormat bjSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		bjSdf.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
		SimpleDateFormat hongkongSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		hongkongSdf.setTimeZone(TimeZone.getTimeZone("Asia/Hong_Kong"));
		System.out.println("毫秒数:" + date.getTime() + ", 北京时间:" + bjSdf.format(date));
		System.out.println("毫秒数:" + date.getTime() + ", 香港时间:" + hongkongSdf.format(date));
		System.out.println(date);
	}
}
