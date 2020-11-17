package com.lk.api.filter;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lk.api.constant.ParamType;
import com.lk.api.constant.V;
import com.lk.api.domain.CheckFieldModel;
import com.lk.api.exception.ValidDataException;


public class DataCheckInterceptor implements  HandlerInterceptor {
	
    @SuppressWarnings("unchecked")
	@Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
    	Object object = request.getAttribute("checkFieldModels");
    	if(object != null && object instanceof List) {
    		ObjectMapper mapper = new ObjectMapper();
	    	List<CheckFieldModel> checkFieldModels = (List<CheckFieldModel>)object;
	    	if(checkFieldModels != null &  checkFieldModels.size()>0) {
	    		Map<String,Object> map = new HashMap<String, Object>();
	        	String contentType = request.getContentType();
	        	int cType = 0;
	        	if(contentType != null) {
		        	if(contentType.contains("application/json")) {
		        		ServletRequest req = new RequestWrapper((HttpServletRequest)request);
			        		try {
			        			cType = 1;
								String body = ((RequestWrapper)req).getBodyString();
								map = mapper.readValue(body,Map.class);
							} catch (Exception e) {
								return true;
							}
		        	}
		        	if(contentType.contains("multipart/form-data")) {
		        		cType = 2;
		        	}
	        	}
	        	Map<String,String> errorMap = new HashMap<String,String>();
	        	String uri = request.getRequestURI();
		    	for (CheckFieldModel model : checkFieldModels) {
		    		String param = null;
		    		if(uri.equals(model.getUrl()) || (model.getUrl().contains("{") && model.getUrl().contains("}"))) {
		    			if(model.getUrl().contains("{") && model.getUrl().contains("}")) {
		    				String subUrl = model.getUrl().substring(0, model.getUrl().indexOf("{")-1);
			    			if(!uri.startsWith(subUrl)) {
			    				continue;
			    			}
			    			String[] s1 = uri.split("[/]");
			    			String[] s2 = model.getUrl().split("[/]");
			    			if(s1.length != s2.length) {
			    				int num1 = 0,num2=0;
			    				char[] charArray1 = uri.toCharArray();
			    				char[] charArray2 = model.getUrl().toCharArray();
			    				for (char c : charArray1) {
									if(c == '/') {
										num1++;
									}
								}
			    				for (char c : charArray2) {
									if(c == '/') {
										num2++;
									}
								}
			    				if(num1 != num2) {
			    					continue;
			    				}
			    			}
			    			if(ParamType.PATH.equals(model.getParamType())) {
				    			for (int i = 0;i < s2.length;i++) {
				    				if(("{"+model.getFieldName()+"}").equals(s2[i])) {
				    					if(i >= s1.length) {
				    						param = "";
				    					}else {
				    						param = s1[i];
				    					}
				    					break;
					    			}
								}
			    			}
		    			}
		    			if(ParamType.QUERY.equals(model.getParamType())) {
		    				String fieldName = model.getFieldName();
		    				if(cType == 0)param = request.getParameter(fieldName);
		    				if(cType == 1) {
		    					try {
									param = getParamValue(fieldName,map,model,errorMap);
								} catch (Exception e) {
									param = null;
								}
		    				}
						}
						if(ParamType.HEADER.equals(model.getParamType())) {
							param = request.getHeader(model.getFieldName());
						}
						
			    		if("".equals(param) && !"java.lang.String".equals(model.getCls().getTypeName())) {
		    				param = null;
		    			}
			    		
			    		//校验字符串长度限制
			    		if(model.getLength() != null && !"".equals(model.getLength())) {
			    			if(param != null) {
				    			String length = model.getLength();
				    			if(length.contains("-")) {
				    				String[] split = length.split("-");
				    				if(split.length > 1) {
				    					String min = split[0];
				    					String max = split[1];
				    					String msg = null;
				    					if(max.contains("^")) {
				    						String[] split2 = max.split("\\^");
				    						if(split2.length > 1) {
					    						max = split2[0];
					    						msg = split2[1];
					    						if(split2.length > 2) {
					    							for(int i = 2;i<split2.length;i++) {
					    								msg+="^"+split2[i];
					    							}
					    						}
					    						if(split.length > 2) {
					    							for(int i = 2;i<split.length;i++) {
					    								msg+="-"+split[i];
					    							}
					    						}
				    						}
				    					}
					    				if(!"*".equals(min)) {
					    					if(param.length() < Integer.valueOf(min)) {
					    						if(msg == null) {
					    							errorMap.put(model.getFieldName(),model.getFieldName()+"字符串长度不能少于"+min);
					    						}else {
					    							errorMap.put(model.getFieldName(),msg);
					    						}
					    					}
					    				}
					    				if(!"*".equals(max)) {
					    					if(param.length() > Integer.valueOf(max)) {
					    						if(msg == null) {
					    							errorMap.put(model.getFieldName(),model.getFieldName()+"字符串长度不能大于"+max);
					    						}else {
					    							errorMap.put(model.getFieldName(),msg);
					    						}
					    					}
					    				}
				    				}
				    			}
			    			}
			    		}
			    		//校验数值大小
			    		if(model.getRange() != null && !"".equals(model.getRange())) {
			    			if(param != null) {
				    			String range = model.getRange();
				    			if(range.contains("-")) {
				    				String[] split = range.split("-");
				    				if(split.length > 1) {
				    					String min = split[0];
				    					String max = split[1];
				    					String msg = null;
				    					if(max.contains("^")) {
				    						String[] split2 = max.split("\\^");
				    						if(split2.length > 1) {
					    						max = split2[0];
					    						msg = split2[1];
					    						if(split2.length > 2) {
					    							for(int i = 2;i<split2.length;i++) {
					    								msg+="^"+split2[i];
					    							}
					    						}
					    						if(split.length > 2) {
					    							for(int i = 2;i<split.length;i++) {
					    								msg+="-"+split[i];
					    							}
					    						}
				    						}
				    					}
				    					BigDecimal num = new BigDecimal(param);
					    				if(!"*".equals(min)) {
					    					BigDecimal bmin = new BigDecimal(min);
					    					if(num.compareTo(bmin) == -1) {
					    						if(msg == null) {
					    							errorMap.put(model.getFieldName(),model.getFieldName()+"数值不能少于"+min);
					    						}else {
					    							errorMap.put(model.getFieldName(),msg);
					    						}
					    					}
					    				}
					    				if(!"*".equals(max)) {
					    					BigDecimal bmax = new BigDecimal(max);
					    					if(num.compareTo(bmax) == 1) {
					    						if(msg == null) {
					    							errorMap.put(model.getFieldName(),model.getFieldName()+"数值不能大于"+max);
					    						}else {
					    							errorMap.put(model.getFieldName(),msg);
					    						}
					    					}
					    				}
				    				}
				    			}
			    			}
			    		}
			    		//校验无参数规则
			    		if(model.getValids() != null && model.getValids().length > 0) {
			    			int i = 0;
			    			for (String valid : model.getValids()) {
			    				//数据校验
					    		if(V.NOTBLANK.equals(valid)) {
					    			if((param == null || "".equals(param.trim())) && "java.lang.String".equals(model.getCls().getTypeName())) {
					    				if(model.getMsgs() == null || model.getMsgs().length < 1) {
					    					if(param != null && "".equals(param.trim())) {
					    						errorMap.put(model.getFieldName(),model.getFieldName()+"值不能为空");
					    					}
					    					if(param == null) {
					    						errorMap.put(model.getFieldName(),model.getFieldName()+"值不能为NULL");
					    					}
					    				}else if(model.getMsgs() != null && model.getMsgs().length == 1) {
					    					errorMap.put(model.getFieldName(),model.getMsgs()[0]);
					    				}else {
					    					errorMap.put(model.getFieldName(),model.getMsgs()[i]);
					    				}
					    			}
					    		}else if(V.NOTNULL.equals(valid)) {
					    			if(param == null) {
					    				if(model.getMsgs() == null || model.getMsgs().length < 1) {
					    					errorMap.put(model.getFieldName(),model.getFieldName()+"值不能为NULL");
					    				}else if(model.getMsgs() != null && model.getMsgs().length == 1) {
					    					errorMap.put(model.getFieldName(),model.getMsgs()[0]);
					    				}else {
					    					errorMap.put(model.getFieldName(),model.getMsgs()[i]);
					    				}
					    			}
					    		}else if(V.NULL.equals(valid)) {
					    			if(param != null) {
					    				if(model.getMsgs() == null || model.getMsgs().length < 1) {
					    					errorMap.put(model.getFieldName(),model.getFieldName()+"值只能为NULL");
					    				}else if(model.getMsgs() != null && model.getMsgs().length == 1) {
					    					errorMap.put(model.getFieldName(),model.getMsgs()[0]);
					    				}else {
					    					errorMap.put(model.getFieldName(),model.getMsgs()[i]);
					    				}
					    			}
					    		}else if(V.EMAIL.equals(valid)) {
					    			if(param != null) {
						    			boolean email = RegexUtils.isEmail(param);
						    			if(!email) {
						    				if(model.getMsgs() == null || model.getMsgs().length < 1) {
						    					errorMap.put(model.getFieldName(),model.getFieldName()+"邮箱格式不正确");
						    				}else if(model.getMsgs() != null && model.getMsgs().length == 1) {
						    					errorMap.put(model.getFieldName(),model.getMsgs()[0]);
						    				}else {
						    					errorMap.put(model.getFieldName(),model.getMsgs()[i]);
						    				}
						    			}
					    			}
					    		}else if(V.URL.equals(valid)) {
					    			if(param != null) {
						    			boolean url = RegexUtils.isURL(param);
						    			if(!url) {
						    				if(model.getMsgs() == null || model.getMsgs().length < 1) {
						    					errorMap.put(model.getFieldName(),model.getFieldName()+"地址格式不正确");
						    				}else if(model.getMsgs() != null && model.getMsgs().length == 1) {
						    					errorMap.put(model.getFieldName(),model.getMsgs()[0]);
						    				}else {
						    					errorMap.put(model.getFieldName(),model.getMsgs()[i]);
						    				}
						    			}
					    			}
					    		}else if(V.PAST.equals(valid)) {
					    			if(param == null || "".equals(param.trim())) {
					    				i++;
					    				continue;
					    			}
					    			Date date = stringToDate(param);
				    				Date nowDate = new Date();
				    				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				    				String format = sdf.format(nowDate);
				    				try {
										nowDate = sdf.parse(format);
									} catch (ParseException e) {
										e.printStackTrace();
									}
					    			if(date != null && nowDate.after(date)) {
					    				i++;
					    				continue;
					    			}
					    			if(model.getMsgs() == null || model.getMsgs().length < 1) {
				    					errorMap.put(model.getFieldName(),model.getFieldName()+"日期必须在当前日期的过去");
				    				}else if(model.getMsgs() != null && model.getMsgs().length == 1) {
				    					errorMap.put(model.getFieldName(),model.getMsgs()[0]);
				    				}else {
				    					errorMap.put(model.getFieldName(),model.getMsgs()[i]);
				    				}
					    		}else if(V.FUTURE.equals(valid)) {
					    			if(param == null || "".equals(param.trim())) {
					    				i++;
					    				continue;
					    			}
					    			Date date = stringToDate(param);
				    				Date nowDate = new Date();
				    				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				    				String format = sdf.format(nowDate);
				    				try {
										nowDate = sdf.parse(format);
									} catch (ParseException e) {
										e.printStackTrace();
									}
					    			if(date != null && date.after(nowDate)) {
					    				i++;
					    				continue;
					    			}
					    			if(model.getMsgs() == null || model.getMsgs().length < 1) {
				    					errorMap.put(model.getFieldName(),model.getFieldName()+"日期必须在当前日期的未来");
				    				}else if(model.getMsgs() != null && model.getMsgs().length == 1) {
				    					errorMap.put(model.getFieldName(),model.getMsgs()[0]);
				    				}else {
				    					errorMap.put(model.getFieldName(),model.getMsgs()[i]);
				    				}
					    		}else {
					    			if(!V.NOTEMPTY.equals(valid)) {
					    				if(valid == null || "".equals(valid.trim())) {
					    					continue;
					    				}else {
						    				if(param != null) {
							    				boolean match = RegexUtils.isMatch(valid, param);
							    				if(!match) {
							    					if(model.getMsgs() == null || model.getMsgs().length < 1) {
								    					errorMap.put(model.getFieldName(),model.getFieldName()+"格式不符合要求");
								    				}else if(model.getMsgs() != null && model.getMsgs().length == 1) {
								    					errorMap.put(model.getFieldName(),model.getMsgs()[0]);
								    				}else {
								    					errorMap.put(model.getFieldName(),model.getMsgs()[i]);
								    				}
							    				}
						    				}
					    				}
					    			}
					    		}
					    		i++;
							}
			    		}
		    		}
				}
		    	if(errorMap.size() > 0) {
		    		Set<String> keySet = errorMap.keySet();
		    		String msg = "";
		    		Map<String,String> errors = new HashMap<String,String>();
		    		for (String key : keySet) {
		    			errors.put(key, errorMap.get(key));
		    			if("".equals(msg)) {
		    				msg += errorMap.get(key);
		    			}else {
		    				msg += ";"+errorMap.get(key);
		    			}
					}
		    		ValidDataException e = new ValidDataException(msg);
		    		e.setErrors(errors);
		    		throw e;
		    	}
	    	}
    	}
        return true;
    }
    
    public String getParamValue(String fieldName,Map<String,Object> map,CheckFieldModel model,Map<String,String> errorMap) {
    	
    	String subName = fieldName;
    	if(fieldName.contains(".")) subName = fieldName.substring(0,fieldName.indexOf("."));
    	String subName2 = null;
    	if(fieldName.contains(".")) subName2 = fieldName.substring(subName.length()+1);
		Object obj = map.get(subName);
		
		String[] valids = model.getValids();
    	if(!fieldName.contains(".") && valids != null && valids.length > 0) {
			int n = 0;
			int j = 0;
			for (String v : valids) {
				if(V.NOTEMPTY.equals(v)) {
					if(obj == null) {
						n=1;
						break;
					}
					if(obj instanceof String) {
						if(obj.toString().length() < 1) {
							n=2;
						}
					}
					if(obj instanceof List) {
						if(((List)obj).size() < 1) {
							n=3;
						}
					}
					if(obj instanceof Map) {
						if(((Map)obj).size() < 1) {
							n=4;
						}
					}
					break;
				}
				j++;
			}
			if(n != 0) {
				if(model.getMsgs() == null || model.getMsgs().length < 1) {
					if(n == 1) {
						errorMap.put(model.getFieldName(),model.getFieldName()+"值不能为NULL");
					}else {
						errorMap.put(model.getFieldName(),model.getFieldName()+"值不能为空");
					}
				}else if(model.getMsgs() != null && model.getMsgs().length == 1) {
					errorMap.put(model.getFieldName(),model.getMsgs()[0]);
				}else {
					errorMap.put(model.getFieldName(),model.getMsgs()[j]);
				}
			}
    	}
    	if(!fieldName.contains(".") && model.getSize() != null && !"".equals(model.getSize())) {
    		int n = 0;
    		if(obj == null) {
    			n = 1;
    		}
    		if(n == 0) {
    			String size = model.getSize();
    			if(size.contains("-")) {
    				String[] split = size.split("-");
    				if(split.length > 1) {
    					String min = split[0];
    					String max = split[1];
    					String msg = null;
    					if(max.contains("^")) {
    						String[] split2 = max.split("\\^");
    						if(split2.length > 1) {
	    						max = split2[0];
	    						msg = split2[1];
	    						if(split2.length > 2) {
	    							for(int i = 2;i<split2.length;i++) {
	    								msg+="^"+split2[i];
	    							}
	    						}
	    						if(split.length > 2) {
	    							for(int i = 2;i<split.length;i++) {
	    								msg+="-"+split[i];
	    							}
	    						}
    						}
    					}
    					if(!"*".equals(min)) { //限制最小长度
    						if(obj instanceof List) {
        						if(((List)obj).size() < Integer.valueOf(min)) {
        							if(msg == null) {
        								errorMap.put(model.getFieldName(),model.getFieldName()+"集合size大小不能少于"+min);
        							}else {
        								errorMap.put(model.getFieldName(),msg);
        							}
        						}
        					}
        					if(obj instanceof Map) {
        						if(((Map)obj).size() < Integer.valueOf(min)) {
        							if(msg == null) {
        								errorMap.put(model.getFieldName(),model.getFieldName()+"集合size大小不能少于"+min);
        							}else {
        								errorMap.put(model.getFieldName(),msg);
        							}
        						}
        					}
	    				}
    					if(!"*".equals(max)) { //限制最大长度
    						if(obj instanceof List) {
        						if(((List)obj).size() > Integer.valueOf(max)) {
        							if(msg == null) {
        								errorMap.put(model.getFieldName(),model.getFieldName()+"集合size大小不能大于"+max);
        							}else {
        								errorMap.put(model.getFieldName(),msg);
        							}
        						}
        					}
        					if(obj instanceof Map) {
        						if(((Map)obj).size() < Integer.valueOf(max)) {
        							if(msg == null) {
        								errorMap.put(model.getFieldName(),model.getFieldName()+"集合size大小不能大于"+max);
        							}else {
        								errorMap.put(model.getFieldName(),msg);
        							}
        						}
        					}
	    				}
    				}
    			}
    		}
    	}
		
		
		
		
		
		if(obj instanceof Map) {
			Map<String,Object> m = (Map)obj;
			if(subName2 != null && subName2.contains(".")) {
				return getParamValue(subName2,m,model,errorMap);
			}
			if(subName2 != null && !subName2.contains(".")) {
				getParamValue(subName2,m,model,errorMap);
			}
			return subName2 != null && m.get(subName2) == null?"":subName2 != null ?m.get(subName2).toString():" ";
		}
		if(obj instanceof String) {
			return obj.toString();
		}
		if(obj instanceof List) {
			List l = (List)obj;
			String params = null;
			if(l.size() == 0 || subName2 == null) {
				params = " ";
			}
			for (Object o : l) {
				if(o instanceof Map) {
					Map<String,Object> m = (Map)o;
					if(subName2 != null && subName2.contains(".")) {
						return getParamValue(subName2,m,model,errorMap);
					}
					if(subName2 != null && !subName2.contains(".")) {
						getParamValue(subName2,m,model,errorMap);
					}
					if(subName2 != null) {
						Object ob = m.get(subName2);
						if(ob != null) {
							if(ob instanceof List) {
								if(params != null && !"".equals(params)) {
									params+=";";
								}
								List<String> list = (List)ob;
								for (String str : list) {
									if(params == null || "".equals(params)) {
										params = str;
									}else {
										params += ","+str;
									}
								}
							}else {
								if(params == null || "".equals(params)) {
									params = ob.toString();
								}else {
									params += ";"+ob.toString();
								}
							}
						}
					}
				}
				if( o instanceof String) {
					if(params == null || "".equals(params)) {
						params = o.toString();
					}else {
						params += ";"+o;
					}
				}
			}
			return params;
		}
    	return null;
    }
    
    public Date stringToDate(String param) {
    	SimpleDateFormat sdf = new SimpleDateFormat();
		Date date = null;
		try {
			sdf.applyPattern("yyyy-MM-dd");
			date = sdf.parse(param);
		} catch (ParseException e) {
			try {
				sdf.applyPattern("yyyy-MM-dd HH:mm:ss");
				date = sdf.parse(param);
			} catch (ParseException e1) {
				try {
					sdf.applyPattern("yyyy/MM/dd");
					date = sdf.parse(param);
				} catch (ParseException e2) {
					try {
						sdf.applyPattern("yyyy/MM/dd HH:mm:ss");
						date = sdf.parse(param);
					} catch (ParseException e3) {
						try {
							sdf.applyPattern("yyyyMMdd");
							date = sdf.parse(param);
						} catch (ParseException e4) {
							try {
								sdf.applyPattern("yyyy.MM.dd HH:mm:ss");
								date = sdf.parse(param);
							} catch (ParseException e6) {
								try {
									sdf.applyPattern("yyyy.MM.dd");
									date = sdf.parse(param);
								} catch (ParseException e7) {
									date = new Date(Long.parseLong(param));
								}
							}
						}
					}
				}
			}
		}
		return date;
    }
    
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {

    }
 
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
    }
    
}