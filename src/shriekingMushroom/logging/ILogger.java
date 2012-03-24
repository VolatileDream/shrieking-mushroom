package shriekingMushroom.logging;

public interface ILogger {

	public enum LogLevel {
		Verbose(1), Debug(2), Info(4), Warn(8), Error(16), Fatal(32);

		private final int flag;

		private LogLevel(int i) {
			flag = i;
		}

		public int getFlag() {
			return flag;
		}
	};

	public void Log(String str, LogLevel l);

	public void Log(Throwable e, LogLevel l);

	public void Log(String str, Throwable e, LogLevel l);

}
