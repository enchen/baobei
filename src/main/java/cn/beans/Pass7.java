/**
 * 
 */
package cn.beans;

/**
 * @author chenen
 * 过去7天所有的查询，全文，下载次数统计 不涉及具体客户端
 *
 */
public class Pass7 {
	
	private String [] days;
	private String [] type;
	private int [][] counts;
	public String[] getDays() {
		return days;
	}
	public void setDays(String[] days) {
		this.days = days;
	}
	public String[] getType() {
		return type;
	}
	public void setType(String[] type) {
		this.type = type;
	}
	public int[][] getCounts() {
		return counts;
	}
	public void setCounts(int[][] counts) {
		this.counts = counts;
	}

}
