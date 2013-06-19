package zswi.protocols.caldav;

/**
 * ServerCalendar represents a Calendar collection downloaded from the server.
 * Every collection that is a calendar must have calendar-order, calendar-color and calendar-description property.
 * Also (like every other collection) it has a cTag property.
 * 
 * @author Jindřich Pouba
 *
 */
public class ServerCalendar {
	
	private final String ctag;
	private final String displayName;
	private String color;
	private int order;
	private String path;
	private String description;
	
	/**
	 * Returns order of the calendar on the server or -1 if the property is not set right.
	 * 
	 * @return order of the calendar
	 */
	public int getOrder() {
		return order;
	}
	
	/**
	 * Returns String path of the calendar on the server.
	 * 
	 * @return String path of the calendar
	 */
	public String getPath() {
		return path;
	}
	
	/**
	 * Returns String description of the calendar according to the server.
	 * 
	 * @return String description
	 */
	public String getDescription() {
		return description;
	}
	
	/**
	 * Returns ctag of the calendar on the server.
	 * 
	 * @return String cTag
	 */
	public String getCtag() {
		return ctag;
	}
	
	/**
	 * Returns name of the collection on the server.
	 * 
	 * @return String name
	 */
	public String getDisplayName() {
		return displayName;
	}
	
	/**
	 * Returns color of the calendar on the server.
	 * 
	 * @return color in String format, according to the server
	 */
	public String getColor() {
		return color;
	}
	
	/**
	 * Creates a new ServerCalendar according to the values provided.
	 * 
	 * @param ctag
	 * @param displayName
	 * @param color
	 * @param order
	 * @param path
	 * @param description
	 */
	public ServerCalendar(String ctag, String displayName, String color, int order, String path, String description) {
		super();
		this.ctag = ctag;
		this.displayName = displayName;
		this.color = color;
		this.order = order;
		this.path = path;
		this.description = description;
	}
	
	/**
	 * Creates a new ServerCalendar according to the values provided.
	 * Order is set to -1. Path and description to null.
	 * 
	 * @param ctag
	 * @param displayName
	 * @param color
	 */
	public ServerCalendar(String ctag, String displayName, String color) {
		this(ctag, displayName, color, -1, null, null);
	}
	
	@Override
	public String toString() {
		return "Calendar, name: " + displayName + ", ctag: " + ctag + ", color: " + color.toString();
	}

	
}
