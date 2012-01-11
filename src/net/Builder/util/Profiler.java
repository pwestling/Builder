package net.Builder.util;

public class Profiler {

	// PRIVATE //
	private static int fSAMPLE_SIZE = 100;
	private static long fSLEEP_INTERVAL = 100;

	private static long mark = 0;

	public static void mark() {
		mark = getMemoryUse();
	}

	public static long memorySinceMark() {
		return getMemoryUse() - mark;
	}

	public static long getMemoryUse() {
		putOutTheGarbage();
		long totalMemory = Runtime.getRuntime().totalMemory();

		putOutTheGarbage();
		long freeMemory = Runtime.getRuntime().freeMemory();

		return (totalMemory - freeMemory);
	}

	private static void putOutTheGarbage() {
		collectGarbage();
		collectGarbage();
	}

	private static void collectGarbage() {
		try {
			System.gc();
			Thread.currentThread().sleep(fSLEEP_INTERVAL);
			System.runFinalization();
			Thread.currentThread().sleep(fSLEEP_INTERVAL);
		} catch (InterruptedException ex) {
			ex.printStackTrace();
		}
	}

}
