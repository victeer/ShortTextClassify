package org.action;

import org.apache.struts2.ServletActionContext;
import org.service.Predict;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.opensymphony.xwork2.ActionSupport;
public class GetClassAction extends ActionSupport{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String poi_name;
	private String className;
	private String result;
	public String execute() {
		try{
			String path = ServletActionContext.getRequest().getSession().getServletContext().getRealPath("/");
			className=Predict.predict(poi_name, "SVM", path);
			System.out.println("className is "+className);
			Gson GSON_BUILDER = (new GsonBuilder()).disableHtmlEscaping().create();			
			setResult(GSON_BUILDER.toJson(className));
			return SUCCESS;
		}catch(Exception e){
			e.printStackTrace();
			return "fail";
		}
	}
	public String getPoi_name() {
		return poi_name;
	}
	public void setPoi_name(String poi_name) {
		this.poi_name=poi_name;
	}
	public String getClassName() {
		return className;
	}
	public void setClassName(String className) {
		this.className = className;
	}
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}
}
