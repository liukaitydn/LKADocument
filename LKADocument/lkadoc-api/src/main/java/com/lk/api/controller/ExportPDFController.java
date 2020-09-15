package com.lk.api.controller;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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
public class ExportPDFController {
	
	@Autowired
	private LKADController lkadController;
	
	/**
	 * 	导出PDF文档
	 * @param serverName 服务器名称
	 * @param response 响应对象
	 * @return object 对象
	 * @throws DocumentException 异常
	 * @throws Exception 异常
	 */
	@PostMapping("exportPdf")
	public Object exportPdf(String serverName,HttpServletResponse response) throws Exception {
		Map<String,String> result = new HashMap<String, String>();
		
		//获取数据
		Map<String, Object> data = lkadController.loadLKADocument(serverName);
		
		//创建document
		String projectName = data.get("projectName").toString();
		Document doc = createPdf(projectName+".pdf");
		int chNum = 1;
		//添加项目信息
		doc.add(PdfFontUtils.getFont(1,projectName));
		doc.add(PdfFontUtils.getFont(6,"项目描述："+data.get("description").toString()));
		if(data.get("version") != null) {
			doc.add(PdfFontUtils.getFont(6,"项目版本："+data.get("version").toString()));
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
			
			Chapter chapter = new Chapter(PdfFontUtils.getFont(2,typeName),chNum++);
			//chapter.add(PdfFontUtils.getFont(2,typeName));
			//doc.add(PdfFontUtils.getFont(2,typeName)); //添加类说明和描述
			
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
				Section section = chapter.addSection(PdfFontUtils.getFont(3,methodName));
				section.setIndentation(10);
				section.setIndentationLeft(10);
				section.setBookmarkOpen(false);
				section.setNumberStyle(Section.NUMBERSTYLE_DOTTED_WITHOUT_FINAL_DOT);
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
				section.add(PdfFontUtils.getFont(6,"版本号："+methodModel.getVersion()));
				section.add(PdfFontUtils.getFont(6,"Method Type："+methodModel.getRequestType()));
				section.add(PdfFontUtils.getFont(6,"Url："+methodModel.getUrl()));
				section.add(PdfFontUtils.getFont(6,"Content Type："+methodModel.getContentType()));
				section.add(PdfFontUtils.getFont(6,"Author："+author+" CreateTime："+createTime+" updateTime："+updateTime));
				
				//请求参数设置
				section.add(PdfFontUtils.getFont(5,"请求参数"));
				section.add(PdfFontUtils.getFont(6,""));
				/*doc.add(PdfFontUtils.getFont(2,"")); //加2个空行
				doc.add(PdfFontUtils.getFont(6,""));
				doc.add(PdfFontUtils.getFont(6,""));
				doc.add(PdfFontUtils.getFont(3,methodName)); //添加方法说明和描述
				doc.add(PdfFontUtils.getFont(6,"版本号："+methodModel.getVersion()));
				doc.add(PdfFontUtils.getFont(6,"Method Type："+methodModel.getRequestType()));
				doc.add(PdfFontUtils.getFont(6,"Url："+methodModel.getUrl()));
				doc.add(PdfFontUtils.getFont(6,"Content Type："+methodModel.getContentType()));
				doc.add(PdfFontUtils.getFont(6,"Author："+author+" CreateTime："+createTime+" updateTime："+updateTime));
				
				//请求参数设置
				doc.add(PdfFontUtils.getFont(5,"请求参数"));
				doc.add(PdfFontUtils.getFont(6,""));*/
				//创建表格
				PdfPTable requestTable = new PdfPTable(6);
				// 设置表格宽度比例为%100
				requestTable.setWidthPercentage(100);
				requestTable.getDefaultCell().setBorder(PdfPCell.NO_BORDER);
				List<ParamModel> paramModels = methodModel.getRequest();
				PdfPCell cell = null;
				cell = new PdfPCell(new Phrase(PdfFontUtils.getFont(6,"名称")));
		        requestTable.addCell(cell);
		        
		        cell = new PdfPCell(new Phrase(PdfFontUtils.getFont(6,"作用")));
		        requestTable.addCell(cell);
		        
		        cell = new PdfPCell(new Phrase(PdfFontUtils.getFont(6,"是否必须")));
		        requestTable.addCell(cell);
		        
		        cell = new PdfPCell(new Phrase(PdfFontUtils.getFont(6,"数据类型")));
		        requestTable.addCell(cell);
		        
		        cell = new PdfPCell(new Phrase(PdfFontUtils.getFont(6,"参数类型")));
		        requestTable.addCell(cell);
		        
		        cell = new PdfPCell(new Phrase(PdfFontUtils.getFont(6,"描述")));
		        requestTable.addCell(cell);
		        
		        if(paramModels != null && paramModels.size()>0) {
		        	buildRequests(paramModels,"method",cell,requestTable,"req");
		        }else {
		        	cell = new PdfPCell(new Phrase(PdfFontUtils.getFont(6,"该接口没有请求参数")));
			        requestTable.addCell(cell);
			        
			        cell = new PdfPCell(new Phrase(PdfFontUtils.getFont(6,"")));
			        requestTable.addCell(cell);
			        
			        cell = new PdfPCell(new Phrase(PdfFontUtils.getFont(6,"")));
			        requestTable.addCell(cell);
			        
			        cell = new PdfPCell(new Phrase(PdfFontUtils.getFont(6,"")));
			        requestTable.addCell(cell);
			        
			        cell = new PdfPCell(new Phrase(PdfFontUtils.getFont(6,"")));
			        requestTable.addCell(cell);
			        
			        cell = new PdfPCell(new Phrase(PdfFontUtils.getFont(6,"")));
			        requestTable.addCell(cell);
		        }
		        section.add(requestTable);
				
				//响应参数设置
		        section.add(PdfFontUtils.getFont(5,"响应参数"));
		        section.add(PdfFontUtils.getFont(6,""));
				/*doc.add(requestTable);
				
				//响应参数设置
				doc.add(PdfFontUtils.getFont(5,"响应参数"));
				doc.add(PdfFontUtils.getFont(6,""));*/
				//创建表格
				PdfPTable responseTable = new PdfPTable(4);
				// 设置表格宽度比例为%100
				responseTable.setWidthPercentage(100);
				responseTable.getDefaultCell().setBorder(PdfPCell.NO_BORDER);
				List<ResposeModel> resposeModels = methodModel.getRespose();
				cell = new PdfPCell(new Phrase(PdfFontUtils.getFont(6,"名称")));
		        responseTable.addCell(cell);
		        
		        cell = new PdfPCell(new Phrase(PdfFontUtils.getFont(6,"作用")));
		        responseTable.addCell(cell);
		        
		        cell = new PdfPCell(new Phrase(PdfFontUtils.getFont(6,"数据类型")));
		        responseTable.addCell(cell);
		        
		        cell = new PdfPCell(new Phrase(PdfFontUtils.getFont(6,"描述")));
		        responseTable.addCell(cell);
		        if(resposeModels != null && resposeModels.size()>0) {
		        	List<String> valueRecord =new ArrayList<String>();
					buildParams(resposeModels,"method",cell,responseTable,"res",valueRecord);
		        }else {
		        	cell = new PdfPCell(new Phrase(PdfFontUtils.getFont(6,"该接口没有响应参数")));
			        requestTable.addCell(cell);
			        
			        cell = new PdfPCell(new Phrase(PdfFontUtils.getFont(6,"")));
			        requestTable.addCell(cell);
			        
			        cell = new PdfPCell(new Phrase(PdfFontUtils.getFont(6,"")));
			        requestTable.addCell(cell);
			        
			        cell = new PdfPCell(new Phrase(PdfFontUtils.getFont(6,"")));
			        requestTable.addCell(cell);
		        }
		        section.add(responseTable);
		        section.add(PdfFontUtils.getFont(6,""));
				section.add(PdfFontUtils.getFont(6,""));
				section.add(PdfFontUtils.getFont(6,""));
				section.add(PdfFontUtils.getFont(6,""));
				section.add(PdfFontUtils.getFont(6,""));
			}
			doc.add(chapter);
		}
		
		//关闭
		doc.close();
		this.download(projectName+".pdf", response);
		return null;
	}
	
	/**
	 * 下载
	 * @param path 路径
	 * @param response 响应对象
	 */
	public void download(String path, HttpServletResponse response) {
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
	      OutputStream toClient = new BufferedOutputStream(response.getOutputStream());
	      response.setContentType("application/octet-stream");
	      toClient.write(buffer);
	      toClient.flush();
	      toClient.close();
	    } catch (IOException ex) {
	      ex.printStackTrace();
	    }
	  }
	
	/**
	 * 创建请求信息
	 * @param rms 参数对象
	 * @param loc 前缀
	 * @param cell pdfcell
	 * @param requestTable 表格
	 * @param type 类型
	 */
	public void buildRequests(List<ParamModel> rms,String loc,PdfPCell cell,PdfPTable requestTable,String type) {
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
					cell = new PdfPCell(new Phrase(PdfFontUtils.getFont(6,value)));
			        requestTable.addCell(cell);
			        
			        cell = new PdfPCell(new Phrase(PdfFontUtils.getFont(6,name)));
			        requestTable.addCell(cell);
			        
			        cell = new PdfPCell(new Phrase(PdfFontUtils.getFont(6,resp.getRequired()?"是":"否")));
			        requestTable.addCell(cell);
			        
			        cell = new PdfPCell(new Phrase(PdfFontUtils.getFont(6,resp.getDataType())));
			        requestTable.addCell(cell);
			        
			        cell = new PdfPCell(new Phrase(PdfFontUtils.getFont(6,resp.getParamType())));
			        requestTable.addCell(cell);
			        
			        cell = new PdfPCell(new Phrase(PdfFontUtils.getFont(6,resp.getDescription())));
			        requestTable.addCell(cell);
				}else {
					if("req".equals(type)) {
						buildPropertys(modelModel.getPropertyModels(),"",cell,requestTable,"reqs");
					}
					if("reqs".equals(type)) {
						cell = new PdfPCell(new Phrase(PdfFontUtils.getFont(6,value)));
				        requestTable.addCell(cell);
				        
				        cell = new PdfPCell(new Phrase(PdfFontUtils.getFont(6,name)));
				        requestTable.addCell(cell);
				        
				        cell = new PdfPCell(new Phrase(PdfFontUtils.getFont(6,resp.getRequired()?"是":"否")));
				        requestTable.addCell(cell);
				        
				        cell = new PdfPCell(new Phrase(PdfFontUtils.getFont(6,"")));
				        requestTable.addCell(cell);
				        
				        cell = new PdfPCell(new Phrase(PdfFontUtils.getFont(6,"")));
				        requestTable.addCell(cell);
				        
				        cell = new PdfPCell(new Phrase(PdfFontUtils.getFont(6,resp.getDescription())));
				        requestTable.addCell(cell);
						buildPropertys(modelModel.getPropertyModels(),value,cell,requestTable,"reqs");
					}
				}
			}
		}
	}
	
	/**
	 * 创建参数信息
	 * @param rms 对象
	 * @param loc loc
	 * @param cell cell
	 * @param responseTable responseTable
	 * @param type type
	 * @param valueRecord valueRecord
	 */
	public void buildParams(List<ResposeModel> rms,String loc,PdfPCell cell,PdfPTable responseTable,String type,List<String> valueRecord) {
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
							cell = new PdfPCell(new Phrase(PdfFontUtils.getFont(6,value)));
					        responseTable.addCell(cell);
					        valueRecord.add(value);
					        cell = new PdfPCell(new Phrase(PdfFontUtils.getFont(6,name)));
					        responseTable.addCell(cell);
					        cell = new PdfPCell(new Phrase(PdfFontUtils.getFont(6,"")));
					        responseTable.addCell(cell);
					        
					        cell = new PdfPCell(new Phrase(PdfFontUtils.getFont(6,description)));
					        responseTable.addCell(cell);
						}
						buildParams(arr,value,cell,responseTable,"resp",valueRecord);
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
								cell = new PdfPCell(new Phrase(PdfFontUtils.getFont(6,value)));
						        responseTable.addCell(cell);
						        valueRecord.add(value);
						        cell = new PdfPCell(new Phrase(PdfFontUtils.getFont(6,name)));
						        responseTable.addCell(cell);
						        
						        cell = new PdfPCell(new Phrase(PdfFontUtils.getFont(6,dataType)));
						        responseTable.addCell(cell);
						        
						        cell = new PdfPCell(new Phrase(PdfFontUtils.getFont(6,description)));
						        responseTable.addCell(cell);
							}
						}else {
							if("res".equals(type)) {
								buildPropertys(modelModel.getPropertyModels(),"",cell,responseTable,"resps");
							}
							if("resp".equals(type)) {
								buildPropertys(modelModel.getPropertyModels(),value,cell,responseTable,"resps");
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
									cell = new PdfPCell(new Phrase(PdfFontUtils.getFont(6,value)));
							        responseTable.addCell(cell);
							        valueRecord.add(value);
							        cell = new PdfPCell(new Phrase(PdfFontUtils.getFont(6,name)));
							        responseTable.addCell(cell);
							        
							        cell = new PdfPCell(new Phrase(PdfFontUtils.getFont(6,"")));
							        responseTable.addCell(cell);
							        
							        cell = new PdfPCell(new Phrase(PdfFontUtils.getFont(6,description)));
							        responseTable.addCell(cell);
								}
								buildPropertys(modelModel.getPropertyModels(),value,cell,responseTable,"resps");
							}
						}
					}
				}
			}
		} catch (ConcurrentModificationException e) {
			buildParams(rms,loc,cell,responseTable,type,valueRecord);
		}
	}
	
	/**
	 * 创建对象属性信息
	 * @param rms rms
	 * @param loc loc
	 * @param cell cell
	 * @param responseTable responseTable
	 * @param type type
	 */
	public void buildPropertys(List<PropertyModel> rms,String loc,PdfPCell cell,PdfPTable responseTable,String type) {
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
						cell = new PdfPCell(new Phrase(PdfFontUtils.getFont(6,value)));
						responseTable.addCell(cell);
				        
				        cell = new PdfPCell(new Phrase(PdfFontUtils.getFont(6,name)));
				        responseTable.addCell(cell);
				        
				        cell = new PdfPCell(new Phrase(PdfFontUtils.getFont(6,resp.getRequired()?"是":"否")));
				        responseTable.addCell(cell);
				        
				        cell = new PdfPCell(new Phrase(PdfFontUtils.getFont(6,resp.getDataType())));
				        responseTable.addCell(cell);
				        
				        cell = new PdfPCell(new Phrase(PdfFontUtils.getFont(6,resp.getParamType())));
				        responseTable.addCell(cell);
				        
				        cell = new PdfPCell(new Phrase(PdfFontUtils.getFont(6,resp.getDescription())));
				        responseTable.addCell(cell);
					}else {
						cell = new PdfPCell(new Phrase(PdfFontUtils.getFont(6,value)));
				        responseTable.addCell(cell);
				        
				        cell = new PdfPCell(new Phrase(PdfFontUtils.getFont(6,name)));
				        responseTable.addCell(cell);
				        
				        cell = new PdfPCell(new Phrase(PdfFontUtils.getFont(6,dataType)));
				        responseTable.addCell(cell);
				        
				        cell = new PdfPCell(new Phrase(PdfFontUtils.getFont(6,description)));
				        responseTable.addCell(cell);
					}
				}else {
					if("res".equals(type)) {
						buildPropertys(modelModel.getPropertyModels(),"",cell,responseTable,"resps");
					}
					if("resp".equals(type)) {
						buildPropertys(modelModel.getPropertyModels(),value,cell,responseTable,"resps");
					}
					if("resps".equals(type)) {
						cell = new PdfPCell(new Phrase(PdfFontUtils.getFont(6,value)));
				        responseTable.addCell(cell);
				        
				        cell = new PdfPCell(new Phrase(PdfFontUtils.getFont(6,name)));
				        responseTable.addCell(cell);
				        
				        cell = new PdfPCell(new Phrase(PdfFontUtils.getFont(6,"")));
				        responseTable.addCell(cell);
				        
				        cell = new PdfPCell(new Phrase(PdfFontUtils.getFont(6,description)));
				        responseTable.addCell(cell);
						buildPropertys(modelModel.getPropertyModels(),value,cell,responseTable,"resps");
					}
					if("reqs".equals(type)) {
						cell = new PdfPCell(new Phrase(PdfFontUtils.getFont(6,value)));
						responseTable.addCell(cell);
				        
				        cell = new PdfPCell(new Phrase(PdfFontUtils.getFont(6,name)));
				        responseTable.addCell(cell);
				        
				        cell = new PdfPCell(new Phrase(PdfFontUtils.getFont(6,resp.getRequired()?"是":"否")));
				        responseTable.addCell(cell);
				        
				        cell = new PdfPCell(new Phrase(PdfFontUtils.getFont(6,"")));
				        responseTable.addCell(cell);
				        
				        cell = new PdfPCell(new Phrase(PdfFontUtils.getFont(6,"")));
				        responseTable.addCell(cell);
				        
				        cell = new PdfPCell(new Phrase(PdfFontUtils.getFont(6,resp.getDescription())));
				        responseTable.addCell(cell);
						buildPropertys(modelModel.getPropertyModels(),value,cell,responseTable,"reqs");
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
	
	/**
	 * 创建PDF
	 * @param outpath 输出路径
	 * @return document 文档对象
	 * @throws Exception 异常
	 */
    public Document createPdf(String outpath) throws Exception{
        //页面大小
        Rectangle rect = new Rectangle(PageSize.A4);//文档竖方向
        Document doc = new Document(rect);
        PdfWriter writer = PdfWriter.getInstance(doc, new FileOutputStream(outpath));
        //PDF版本(默认1.4)
        writer.setPdfVersion(PdfWriter.PDF_VERSION_1_2);
        //文档属性
        doc.addTitle("Title@lk");
        doc.addAuthor("Author@lk");
        doc.addSubject("Subject@lk");
        doc.addKeywords("Keywords@lk");
        doc.addCreator("Creator@lk");
        //页边空白
        doc.setMargins(40, 40, 40, 40);
        //打开文档
        doc.open();
        return doc;
    }
}
