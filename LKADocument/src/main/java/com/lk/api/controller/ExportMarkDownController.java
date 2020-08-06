package com.lk.api.controller;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.itextpdf.text.Chapter;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.Section;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.lk.api.domain.MethodModel;
import com.lk.api.domain.ModelModel;
import com.lk.api.domain.ParamModel;
import com.lk.api.domain.PropertyModel;
import com.lk.api.domain.ResposeModel;
import com.lk.api.domain.TypeModel;

@RestController
@RequestMapping("lkad")
public class ExportMarkDownController {
	
	@Autowired
	private LKADController lkadController;
	
	/**
	 * 导出PDF文档
	 * @param serverName 服务器名称
	 * @param response 响应对象
	 * @return object 对象
	 * @throws Exception 异常
	 */
	@PostMapping("exportMarkDown")
	public Object exportPdf(String serverName,HttpServletResponse response) throws Exception {
		Map<String,String> result = new HashMap<String, String>();
		
		//获取数据
		Map<String, Object> data = lkadController.loadLKADocument(serverName);
		
		//创建文件
		String projectName = data.get("projectName").toString();
		PrintWriter pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(projectName+".md"),  
		        "UTF-8"));
		//添加项目信息
		pw.println("# "+projectName);
		pw.println("##### "+"项目描述："+data.get("description").toString());
		if(data.get("version") != null) {
			pw.println("##### "+"项目版本："+data.get("version").toString());
		}
		//获取类信息
		Object object = data.get("apiDoc");
		if(object == null) {
			result.put("code","500");
			result.put("msg","没有找到接口信息!");
			return result;
		}
		
		if(!(object instanceof List)) {
			result.put("code","500");
			result.put("msg","接口信息结构异常!");
			return result;
		}
		List typeModels = (List)object;
		if(typeModels.size() == 0) {
			result.put("code","500");
			result.put("msg","没有找到接口信息!");
			return result;
		}
		for (Object typeObj : typeModels) {
			if(!(typeObj instanceof TypeModel)) {
				/*if(typeObj instanceof Map) {
					
				}*/
				continue;
			}
			TypeModel typeModel = (TypeModel)typeObj;
			String typeName = typeModel.getName();
			String typeDesc = typeModel.getDescription();
			if(typeDesc != null && !"".equals(typeDesc)) {
				typeName += ","+typeDesc;
			}
			
			//获取方法信息
			List<MethodModel> methodModels = typeModel.getMethodModels();
			if(methodModels == null || methodModels.size()==0) {
				continue;
			}
			for (MethodModel methodModel : methodModels) {
				String methodName = methodModel.getName();
				String methodDesc = methodModel.getDescription();
				if(methodDesc != null && !"".equals(methodDesc)) {
					methodName += ","+methodDesc;
				}
				pw.println();
				pw.println("### "+methodName);
				String author = methodModel.getAuthor();
				String createTime = methodModel.getCreateTime();
				String updateTime = methodModel.getUpdateTime();
				if(author == null || "".equals(author)) {
					author = "未设置";
				}
				if(createTime == null || "".equals(createTime)) {
					createTime = "未设置";
				}
				if(updateTime == null || "".equals(updateTime)) {
					updateTime = "未设置";
				}
				pw.println("**版本号：**"+methodModel.getVersion());
				pw.println("**Method Type：**"+methodModel.getRequestType());
				pw.println("**Url：**"+methodModel.getUrl());
				pw.println("**Content Type：**"+methodModel.getContentType());
				pw.println("**Author：**"+author+" **CreateTime：**"+createTime+" **updateTime：**"+updateTime);
				pw.println("##### 请求参数");
				//请求参数设置
				pw.println("| 名称 | 作用 | 是否必须 | 数据类型 | 参数类型 | 描述 |");
				pw.println("| :---- | :---- | :---- | :---- | :---- | :---- |");
				List<ParamModel> paramModels = methodModel.getRequest();
		        if(paramModels != null && paramModels.size()>0) {
		        	buildRequests(paramModels,"method","req",pw);
		        }else {
			        pw.println("| 该接口没有请求参数 |  |  |  |  |  |");
		        }
				
				//响应参数设置
		        pw.println("##### 响应参数");
				//创建表格
				PdfPTable responseTable = new PdfPTable(4);
				// 设置表格宽度比例为%100
				List<ResposeModel> resposeModels = methodModel.getRespose();
				pw.println("| 名称 | 作用 |  数据类型 | 描述 |");
				pw.println("| :---- | :---- | :---- | :---- |");
		        if(resposeModels != null && resposeModels.size()>0) {
		        	List<String> valueRecord =new ArrayList<String>();
					buildParams(resposeModels,"method","res",valueRecord,pw);
		        }else {
		        	pw.println("| 该接口没有响应参数 |  |   |  |");
		        }
			}
		}
		//关闭
		pw.flush();
		pw.close();
		this.download(projectName+".md", response);
		return null;
	}
	
	/**
	 * 下载
	 * @param path 路径
	 * @param response 响应对象
	 */
	public void download(String path, HttpServletResponse response) {
		OutputStream toClient = null;
	    try {
	      // path是指欲下载的文件的路径。
	      File file = new File(path);
	      // 取得文件名。
	      String filename = file.getName();
	      // 取得文件的后缀名。
	      String ext = filename.substring(filename.lastIndexOf(".") + 1).toUpperCase();

	      // 以流的形式下载文件。
	      InputStream fis = new BufferedInputStream(new FileInputStream(path));
	      byte[] buffer = new byte[fis.available()];
	      fis.read(buffer);
	      fis.close();
	      // 清空response
	      response.reset();
	      // 设置response的Header
	      response.addHeader("Content-Disposition", "attachment;filename=" + new String(filename.getBytes()));
	      response.addHeader("Content-Length", "" + file.length());
	      toClient = new BufferedOutputStream(response.getOutputStream());
	      response.setContentType("application/octet-stream");
	      toClient.write(buffer);
	      toClient.flush();
	    } catch (IOException ex) {
	      ex.printStackTrace();
	    }finally {
			if(toClient != null) {
				try {
					toClient.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	  }
	
	/**
	 * 创建请求信息
	 * @param rms 参数对象
	 * @param loc 前缀
	 * @param type 类型
	 * @param pw 输出流
	 */
	public void buildRequests(List<ParamModel> rms,String loc,String type,PrintWriter pw) {
		if(rms != null && rms.size() > 0){
			for (ParamModel resp : rms) {
				String value = resp.getValue();
				boolean array = resp.getArray();
				String name = resp.getName();
				if(loc != null && !"".equals(loc) && !"method".equals(loc) && value != null && !"".equals(value)) {
					value = loc+"."+value;
				}
				if(value == null || "".equals(value)) {
					value = loc;
				}
				if(array) {
					value = value+"[]";
				}
				String tValue = value;
				if(value.contains(".")) {
					int lastIndexOf = value.lastIndexOf(".");
					String[] split = value.split("[.]");
					int len = split.length;
					tValue = value.substring(lastIndexOf+1);
					while(len > 1) {
						tValue = "   "+tValue;
						len--;
					}
				}
				
				ModelModel modelModel = resp.getModelModel();
				if(modelModel == null) {
					pw.println("| "+value+" | "+name+" | "+(resp.getRequired()?"是":"否")+" | "+resp.getDataType()
							+ " | "+resp.getParamType()+" | "+resp.getDescription()+" |");
				}else {
					if("req".equals(type)) {
						buildPropertys(modelModel.getPropertyModels(),"","reqs",pw);
					}
					if("reqs".equals(type)) {
						pw.println("| "+value+" | "+name+" | "+(resp.getRequired()?"是":"否")+" | | | "+resp.getDescription()+" |");
						buildPropertys(modelModel.getPropertyModels(),value,"reqs",pw);
					}
				}
			}
		}
	}
	
	/**
	 * 创建参数信息
	 * @param rms 对象
	 * @param loc loc
	 * @param type 类型
	 * @param valueRecord valueRecord
	 * @param pw 输出流
	 */
	public void buildParams(List<ResposeModel> rms,String loc,String type,List<String> valueRecord,PrintWriter pw) {
		try {
			if(rms != null && rms.size() > 0){
				for (ResposeModel resp : rms) {
					String value = resp.getValue();
					boolean array = resp.getArray();
					String dataType = resp.getDataType();
					String name = resp.getName();
					String description = resp.getDescription();
					List<ResposeModel> arr = new ArrayList<ResposeModel>();
					filter(value,rms,arr);
					
					if(loc != null && !"".equals(loc) && !"method".equals(loc) && value != null && !"".equals(value)) {
						value = loc+"."+value;
					}
					if(value == null || "".equals(value)) {
						value = loc;
					}
					if(array) {
						value = value+"[]";
					}
					String tValue = value;
					if(value.contains(".")) {
						int lastIndexOf = value.lastIndexOf(".");
						String[] split = value.split("[.]");
						int len = split.length;
						tValue = value.substring(lastIndexOf+1);
						while(len > 1) {
							tValue = "   "+tValue;
							len--;
						}
					}
					if(arr != null && arr.size() > 0){
						boolean bool = true;
						if(valueRecord != null && valueRecord.size()>0) {
							for (String vr : valueRecord) {
								if(vr.equals(value)) {
									bool = false;
									break;
								}
							}
						}
						if(bool) {
							pw.println("| "+value+" | "+name+" |  | "+description +" |");
						}
						buildParams(arr,value,"resp",valueRecord,pw);
					}else {
						ModelModel modelModel = resp.getModelModel();
						if(modelModel == null) {
							boolean bool = true;
							if(valueRecord != null && valueRecord.size()>0) {
								for (String vr : valueRecord) {
									if(vr.equals(value)) {
										bool = false;
										break;
									}
								}
							}
							if(bool) {
								pw.println("| "+value+" | "+name+" | "+dataType+" | "+description +" |");
							}
						}else {
							if("res".equals(type)) {
								buildPropertys(modelModel.getPropertyModels(),"","resps",pw);
							}
							if("resp".equals(type)) {
								buildPropertys(modelModel.getPropertyModels(),value,"resps",pw);
							}
							if("resps".equals(type)) {
								boolean bool = true;
								if(valueRecord != null && valueRecord.size()>0) {
									for (String vr : valueRecord) {
										if(vr.equals(value)) {
											bool = false;
											break;
										}
									}
								}
								if(bool) {
									pw.println("| "+value+" | "+name+" |  | "+description +" |");
								}
								buildPropertys(modelModel.getPropertyModels(),value,"resps",pw);
							}
						}
					}
				}
			}
		} catch (ConcurrentModificationException e) {
			buildParams(rms,loc,type,valueRecord,pw);
		}
	}
	
	/**
	 * 创建对象属性信息
	 * @param rms rms
	 * @param loc loc
	 * @param type type
	 * @param pw pw
	 */
	public void buildPropertys(List<PropertyModel> rms,String loc,String type,PrintWriter pw) {
		if(rms != null && rms.size() > 0){
			for (PropertyModel resp : rms) {
				String value = resp.getValue();
				boolean array = resp.getArray();
				String dataType = resp.getDataType();
				String name = resp.getName();
				String description = resp.getDescription();
				if(loc != null && !"".equals(loc) && !"method".equals(loc) && value != null && !"".equals(value)) {
					value = loc+"."+value;
				}
				if(value == null || "".equals(value)) {
					value = loc;
				}
				if(array) {
					value = value+"[]";
				}
				
				String tValue = value;
				if(value.contains(".")) {
					int lastIndexOf = value.lastIndexOf(".");
					String[] split = value.split("[.]");
					int len = split.length;
					tValue = value.substring(lastIndexOf+1);
					while(len > 1) {
						tValue = "   "+tValue;
						len--;
					}
				}
				
				ModelModel modelModel = resp.getModelModel();
				if(modelModel == null) {
					if("reqs".equals(type) || "req".equals(type)) {
						pw.println("| "+value+" | "+name+" | "+(resp.getRequired()?"是":"否")+" | "+resp.getDataType()
						+ " | "+resp.getParamType()+" | "+resp.getDescription()+" |");
					}else {
						pw.println("| "+value+" | "+name+" | "+dataType+" | "+description +" |");
					}
				}else {
					if("res".equals(type)) {
						buildPropertys(modelModel.getPropertyModels(),"","resps",pw);
					}
					if("resp".equals(type)) {
						buildPropertys(modelModel.getPropertyModels(),value,"resps",pw);
					}
					if("resps".equals(type)) {
						pw.println("| "+value+" | "+name+" |  | "+description +" |");
						buildPropertys(modelModel.getPropertyModels(),value,"resps",pw);
					}
					if("reqs".equals(type)) {
						pw.println("| "+value+" | "+name+" | "+(resp.getRequired()?"是":"否")+" | | | "+resp.getDescription()+" |");
						buildPropertys(modelModel.getPropertyModels(),value,"reqs",pw);
					}
				}
			}
		}
	}
	
	/**
	 * 过滤器
	 * @param value 值
	 * @param rms rms
	 * @param arr arr
	 */
	 public void filter(String value,List<ResposeModel> rms,List<ResposeModel> arr) {
		 	if(rms == null || rms.size() == 0 || value == null || "".equals(value)) {
		 		return;
		 	}
	    	Iterator<ResposeModel> iterator = rms.iterator();
	    	while(iterator.hasNext()) {
	    		try {
					ResposeModel rm = iterator.next();
					if(value.equals(rm.getParentName())) {
						String var = rm.getValue();
						arr.add(rm);
						iterator.remove();
						filter(var, rms, arr);
					}
				} catch (Exception e) {
					filter(value, rms, arr);
					break;
				}
	    	}
	 }
}
