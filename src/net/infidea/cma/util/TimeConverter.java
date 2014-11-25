package net.infidea.cma.util;

import java.util.Date;

public class TimeConverter {
	public static long convertToMilliseconds(long timestamp) {
		return (new Date()).getTime()+(timestamp-System.nanoTime())/1000000L;
	}
}
