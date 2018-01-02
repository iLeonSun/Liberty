package Liberty;


/**
 * 
 * @author CN0748
 *	this interface is used to parse lib names,
 *	will be implemented by various libs.
 */

public interface Vender {
	
	/*
	 * will return a array:
	 * node,track,channel,vt
	 */
	public String[] parselibname(String libname);
	
}
