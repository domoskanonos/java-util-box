package com.dbr.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.*;
import java.io.*;
import java.util.function.Consumer;

/**
 * check for furher cmd command informations:
 * https://ss64.com/nt/cmd.html
 */
public class SystemUtil {

	protected static final Logger logger = LogManager.getLogger(SystemUtil.class.getSimpleName());

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
				logger.info("open file: {}", file.getAbsolutePath());
				Desktop.getDesktop().open(file);
			} catch (Throwable th) {
				logger.error("error open file, file: " + file.getAbsolutePath(), th);
				throw new RuntimeException(th);
			}
		}
	}

	public static int showFolder(File folder) throws IOException, InterruptedException {
		return executeCommand("explorer.exe", folder.getAbsolutePath());
	}

	public static int executeCommand(String... command) throws IOException, InterruptedException {
		ProcessBuilder builder = new ProcessBuilder();
		builder.command(command);
		//builder.directory(new File(System.getProperty("user.home")));
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
