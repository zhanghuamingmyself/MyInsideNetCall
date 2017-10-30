package com.eto.shineijidemo;

import org.json.JSONObject;

/**
 * 只操作 pubilc 的  非 static 成员；
 * 成员使用范围：基本类型，自定义类型，List类型
 * @author Administrator
 *
 * 如果成员含有ArrayList，则子类重写 fromJson(JSONObject obj)方法，在 调用父类方法fromJson(JSONObject obj)前，需要给ArrayList 添加一个元素，否则ArrayList元素类型无法确定，则无法反射成功。
 *UserDbBean user = new UserDbBean();
 *DATA.add(user);
 *super.fromJson(obj);
 *DATA.remove(0);
 */
public class JsonBean {




	/**
	 * 拷贝值
	 * @param obj
	 */
	public void copyValue(Object obj){
		if(obj == null) return;
		MyReflectUtils.CopyValues(this, obj);
	}

	public void fromJson(String jsonString){
		MyReflectUtils.JsonToObj(jsonString, this);
	}

	public void fromJson(JSONObject obj){
		MyReflectUtils.JsonObjToObj(obj, this);
	}

	public String toJson(){
		return MyReflectUtils.ObjToJson(this);
	}

	public JSONObject toJsonObj(){
		return MyReflectUtils.ObjToJsonObj(this);
	}

	public String toString(){
		return MyReflectUtils.toString(this);
	}

	public void initString(){
		MyReflectUtils.initString(this);
	}





}