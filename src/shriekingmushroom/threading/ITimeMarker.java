package shriekingmushroom.threading;

public interface ITimeMarker {

	/**
	 * Sets the current time as the last marker
	 */
	public void markCurrentTime();

	/**
	 * Returns the value of the last marker
	 */
	public long getMark();

}
