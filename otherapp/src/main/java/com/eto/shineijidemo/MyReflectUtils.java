package com.eto.shineijidemo;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.List;

public class MyReflectUtils {

	static boolean IsDebug = false;


	public static String ObjToJson(Object obj){
		return ObjToJsonObj(obj).toString();
	}

	public static String ObjToJson(Object obj, String[] noFields){
		return ObjToJsonObj(obj,noFields).toString();
	}

	public static boolean JsonToObj(String json, Object obj){
		JSONObject jobj = null;
		try {
			jobj = new JSONObject(json);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return JsonObjToObj(jobj,obj);
	}

	/////////////////////////////////////////////////

	public static JSONObject ObjToJsonObj(Object obj){

		JSONObject jobj = new JSONObject();

		//Field[] fields = obj.getClass().getDeclaredFields(); //不包括父类成员
		Field[] fields = obj.getClass().getFields();

		for(Field f : fields){
			try {
				String fieldName = f.getName();
				if(CheckField(f)){
					Object o = f.get(obj);
					if(o != null){
						if(o.getClass().toString().startsWith("class java.lang")){
							jobj.put(fieldName, f.get(obj));
						}else if(o instanceof java.util.ArrayList){
							List<Object> olist = (List<Object>)o;
							JSONArray arr =  new JSONArray();

							for(int i = 0 ;i < olist.size();i++){
								JSONObject tmpJo = ObjToJsonObj(olist.get(i));
								arr.put(tmpJo);
							}
							jobj.put(fieldName, arr);
						}else {
							JSONObject tmpJo = ObjToJsonObj(o);
							jobj.put(fieldName, tmpJo);
						}
					}
				}

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}


		return jobj;
	}

	public static JSONObject ObjToJsonObj(Object obj, String[] noFields){

		JSONObject jobj = new JSONObject();

		//Field[] fields = obj.getClass().getDeclaredFields(); //不包括父类成员
		Field[] fields = obj.getClass().getFields();

		for(Field f : fields){
			try {
				String fieldName = f.getName();

				if(noFields != null){
					boolean flag = false;
					for (String item:noFields) {
						if(fieldName.equals(item)){
							flag = true;
							break;
						}

					}

					if(flag){
						continue;
					}
				}
				if(CheckField(f)){
					Object o = f.get(obj);
					if(o != null){
						if(o.getClass().toString().startsWith("class java.lang")){
							jobj.put(fieldName, f.get(obj));
						}else if(o instanceof java.util.ArrayList){
							List<Object> olist = (List<Object>)o;
							JSONArray arr =  new JSONArray();

							for(int i = 0 ;i < olist.size();i++){
								JSONObject tmpJo = ObjToJsonObj(olist.get(i));
								arr.put(tmpJo);
							}
							jobj.put(fieldName, arr);
						}else {
							JSONObject tmpJo = ObjToJsonObj(o);
							jobj.put(fieldName, tmpJo);
						}
					}
				}

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}


		return jobj;
	}


	public static boolean JsonObjToObj(JSONObject jobj, Object obj){


		//Field[] fields = obj.getClass().getDeclaredFields(); //不包括父类成员
		Field[] fields = obj.getClass().getFields();

		for(Field f : fields){
			try {


				String fieldName = f.getName();

				if(CheckField(f)){


					if(jobj.has(fieldName)){
						Object jo = jobj.get(fieldName);
						Object o = f.get(obj);


						if(jo != null){

							if (jo.getClass().toString().startsWith("class java.lang")) {
								if(IsDebug){
									System.out.println(jo.getClass()+","+f.getName()+"->:"+jo.toString());
								}
								f.set(obj, jobj.get(fieldName));

							}else if(jo.toString().equals("null")){
								if(IsDebug){
									System.out.println(jo.getClass()+","+f.getName()+":"+jo.toString());
								}
							}else{
								if(jo.getClass().equals(JSONObject.class)){
									JsonBean tmpO = (JsonBean)f.getType().newInstance();
									tmpO.fromJson((JSONObject) jo);    // 使用 fromJson 进行递归 2016 10 10
//									JsonObjToObj((JSONObject) jo,tmpO);   //递归
									f.set(obj, tmpO);
								}else if(jo.getClass().equals(JSONArray.class)){
									if( o instanceof java.util.ArrayList){
										List<Object> olist = (List<Object>)o;
										JSONArray arr = (JSONArray)jo;
										if(IsDebug) {
											System.out.println("ucard array:" + arr);
											System.out.println("ucard list:" + olist.size());
										}
										if(olist.size() >0){  //包含一个对象，  在对象的 fromJson 插入，删除

											Object demo = olist.get(0);



											for(int i = 0 ;i <arr.length();i++){
												Object item = arr.get(i);
												if(item instanceof String){
													olist.add(item);
												}else if( item instanceof JSONObject){
													JSONObject arrObj = (JSONObject)item;
													Object tmpO = demo.getClass().newInstance();
													JsonObjToObj(arrObj, tmpO);   //递归
													olist.add(tmpO);
												}



											}

										}
									}else{//第二层 数组，直接将数组转换成 字符串进行复制。
										f.set(obj, jo.toString());
									}

								}
								if(IsDebug){

									if(o != null) {
										System.out.println(o.getClass() + "," + f.getName() + o.toString());
									}
								}
							}


						}

					}

				}


			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			}
		}


		return true;
	}


	public static String toString(Object obj){

		String str="";
		//Field[] fields = obj.getClass().getDeclaredFields(); //不包括父类成员
		Field[] fields = obj.getClass().getFields();

		for(Field f : fields){
			try {
				if (CheckField(f)) {
					str += f.get(obj);
				}

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}


		return str;
	}

	/**
	 *  obj2的值 复制到obj1
	 * @param obj1  目标
	 * @param obj2 源
	 * @return
	 */
	public static boolean CopyValues(Object obj1, Object obj2){



		//Field[] fields = obj1.getClass().getDeclaredFields(); //不包括父类成员
		Field[] fields = obj1.getClass().getFields();

		for(Field f : fields){
			try {
				if(CheckField(f)){
					f.set(obj1, f.get(obj2));
				}

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			}
		}


		return true;
	}


	public static boolean initString(Object obj){


		//Field[] fields = obj.getClass().getDeclaredFields(); //不包括父类成员
		Field[] fields = obj.getClass().getFields();

		for(Field f : fields){
			try {
				if(CheckField(f)){
					Object o = f.get(obj);
					if(f.getType().equals(String.class) && o == null)
						f.set(obj, "");
				}

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			}
		}


		return true;
	}


	static boolean CheckField(Field f){
		//静态成员 不转换成 json
		if((f.getModifiers() & java.lang.reflect.Modifier.STATIC)
				== java.lang.reflect.Modifier.STATIC){
			return false;
		}
		//不是public 的 也不转换
		if(!((f.getModifiers() & java.lang.reflect.Modifier.PUBLIC)
				== java.lang.reflect.Modifier.PUBLIC)){
			return false;
		}

		return true;
	}



}
