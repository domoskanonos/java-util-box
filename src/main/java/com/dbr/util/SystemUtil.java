package com.dbr.util;

import java.awt.*;
import java.io.*;
import java.util.function.Consumer;
import java.util.logging.Logger;

/**
 * check for furher cmd command informations:
 * https://ss64.com/nt/cmd.html
 */
public class SystemUtil {

	private static Logger logger = java.util.logging.Logger.getLogger(SystemUtil.class.getName());

	/**
	 * @param args
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public static void main(String[] args) throws IOException, InterruptedException {
		int exitCode = executeCommand("cmd.exe", "/C", "dir");
		System.out.println(exitCode);
	}

	private static boolean isWindows = System.getProperty("os.name")
			.toLowerCase().startsWith("windows");

	public static void openFile(File file) {
		if (Desktop.isDesktopSupported()) {
			try {
				logger.info(String.format("open file: {}%s", file.getAbsolutePath()));
				Desktop.getDesktop().open(file);
			} catch (Throwable th) {
				logger.severe(String.format("error open file, file: %s%s", file.getAbsolutePath(), th));
				throw new RuntimeException(th);
			}
		}
	}

	public static int showFolder(File folder) throws IOException, InterruptedException {
		return executeCommand("explorer.exe", folder.getAbsolutePath());
	}

	public static int executeCommand(String... command) throws IOException, InterruptedException {
		return executeCommand(null, command);
	}

	public static int executeCommand(File executeDirectory, String... command) throws IOException, InterruptedException {
		ProcessBuilder builder = new ProcessBuilder();
		builder.command(command);
		if (executeDirectory != null) {
			builder.directory(executeDirectory);
		}
		Process process = builder.start();
		InputStreamUtil inputStreamUtil =
				new InputStreamUtil(process.getInputStream(), SystemUtil::log);
		inputStreamUtil.run();
		int exitCode = process.waitFor();
		return exitCode;
	}

	public static void log(String line) {
		logger.info(line);
	}

	private static class InputStreamUtil {
		private InputStream inputStream;
		private Consumer<String> consumer;

		public InputStreamUtil(InputStream inputStream, Consumer<String> consumer) {
			this.inputStream = inputStream;
			this.consumer = consumer;
		}

		public void run() {
			new BufferedReader(new InputStreamReader(inputStream)).lines()
					.forEach(consumer);
		}
	}

}
