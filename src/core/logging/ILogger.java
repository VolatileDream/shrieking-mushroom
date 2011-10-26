package core.logging;

public interface ILogger {

	public enum LogLevel {
		Debug(1), Info(2), Warn(4), Error(8), Fatal(16);

		private final int flag;

		private LogLevel(int i) {
			flag = i;
		}

		public int getFlag() {
			return flag;
		}
	};

	public void Log(String str, LogLevel l);

	public void Log(Exception e, LogLevel l);

	public void Log(String str, Exception e, LogLevel l);

}
