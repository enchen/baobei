/**
 * 
 */
package cn.beans;

/**
 * @author chenen
 *登陆用户类
 */
public class User {
private String username;//用户名
private boolean wzread;//文字阅读权限
private boolean tpread;//图片权限
private boolean admin;//管理员权限
public String getUsername() {
	return username;
}
public void setUsername(String username) {
	this.username = username;
}
public boolean isWzread() {
	return wzread;
}
public void setWzread(boolean wzread) {
	this.wzread = wzread;
}
public boolean isTpread() {
	return tpread;
}
public void setTpread(boolean tpread) {
	this.tpread = tpread;
}
public boolean isAdmin() {
	return admin;
}
public void setAdmin(boolean admin) {
	this.admin = admin;
}


}
