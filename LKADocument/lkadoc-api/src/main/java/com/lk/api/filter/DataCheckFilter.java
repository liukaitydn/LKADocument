package com.lk.api.filter;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.condition.PatternsRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import com.lk.api.annotation.*;
import com.lk.api.constant.ParamType;
import com.lk.api.domain.CheckFieldModel;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

@WebFilter(filterName = "dataCheckFilter",urlPatterns="/*")
@Order(1)
public class DataCheckFilter implements Filter {
	
	@Autowired
	private WebApplicationContext applicationContext;
	
	private List<CheckFieldModel> checkFieldModels = new ArrayList<CheckFieldModel>();
	
	private int reqNum = 0;
	
    public void destroy() {
    	
    }

    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws ServletException, IOException {
    	if(checkFieldModels != null && checkFieldModels.size()>0) {
    		req.setAttribute("checkFieldModels",checkFieldModels);
    		if(req.getContentType() != null && req.getContentType().contains("application/json")) {
	    		ServletRequest request = new RequestWrapper((HttpServletRequest)req);
	    		chain.doFilter(request, resp);
    		}else {
    			chain.doFilter(req, resp);
    		}
    	}else {
    		chain.doFilter(req, resp);
    	}
    }

    
    
    
    public void init(FilterConfig config) throws ServletException {
    	Map<String, Object> beans = applicationContext.getBeansWithAnnotation(LKADocument.class);
		boolean bool = false;
		if(beans != null && beans.size()>0) {
			Set<String> keySet = beans.keySet();
			for (String key : keySet) {
				Object obj = beans.get(key);
				Class<? extends Object> bootClass = obj.getClass();
				LKADocument annotation = bootClass.getAnnotation(LKADocument.class);
				if(annotation.validation()) {
					bool = true;
				}
				break;
			}
		}
		if(!bool)return;
    	
    	//RequestMappingHandlerMapping类的作用：将url地址和被@RequestMapping标注过的方法进行绑定
    	RequestMappingHandlerMapping rmhm = applicationContext.getBean(RequestMappingHandlerMapping.class);
		// 获取所有类中被@RequestMapping标注过的方法的对象
		Map<RequestMappingInfo, HandlerMethod> map = rmhm.getHandlerMethods();
		if(map != null && map.size() > 0) {
			for (Map.Entry<RequestMappingInfo, HandlerMethod> mappingInfoHandlerMethodEntry : map.entrySet()) {
				RequestMappingInfo requestMappingInfo = mappingInfoHandlerMethodEntry.getKey();
				HandlerMethod handlerMethod = mappingInfoHandlerMethodEntry.getValue();
				
				Method method = handlerMethod.getMethod();
				Method interfaceMehtod = null;
				Class<?>[] interfaces = handlerMethod.getMethod().getDeclaringClass().getInterfaces();
				if (interfaces != null) {
					for (Class<?> c : interfaces) {// 基于接口继承模式,反向获取接口注解
						try {
							interfaceMehtod = c.getMethod(method.getName(), method.getParameterTypes());
						} catch (Exception e) {
							//啥也不干
						}
					}
				}
				Annotation[] annotations = method.getAnnotations();
				Class<?>[] parameterTypes = method.getParameterTypes();
				Parameter[] parameters = method.getParameters();
				if(parameters != null && parameters.length > 0) {
					boolean b = true;
					for (Parameter parameter : parameters) {
						if(parameter.isAnnotationPresent(LKAGroup.class)) {
							b = false;
							break;
						}
					}
					if(b) {
						if(interfaceMehtod != null) {
							parameters = interfaceMehtod.getParameters();
						}
					}
				}
				
				if(interfaceMehtod != null) {
					Annotation[] annotations2 = interfaceMehtod.getAnnotations();
					List<Annotation> list = new ArrayList<>(Arrays.asList(annotations2));
					list.addAll(new ArrayList<Annotation>(Arrays.asList(annotations)));
					annotations = new Annotation[list.size()];
					list.toArray(annotations);
				}
				
				PatternsRequestCondition p = requestMappingInfo.getPatternsCondition();
				String url = "";
				if(p != null && p.getPatterns().size()>0) {
					for (String u : p.getPatterns()) {
						url = u;// 请求URL
					}
				}
				
				if(parameters != null && parameters.length > 0) {
					for (int i = 0;i<parameterTypes.length;i++) {
						Class<?> argument = parameterTypes[i];
						Class<?> type = parameters[i].getType();
						try {
							if(type.equals(List.class)) {//list集合
								// 当前集合的泛型类型
							    Type genericType = parameters[i].getParameterizedType();
							    if (null == genericType) {
							        continue;
							    }
							    if (genericType instanceof ParameterizedType) {
							        ParameterizedType pt = (ParameterizedType) genericType;
							        //得到泛型里的class类型对象
							        argument = (Class<?>)pt.getActualTypeArguments()[0];
							    }
							}
							if(type.isArray()){//数组
								// 获取数组元素的类型
								argument = type.getComponentType();					                    
							}
						} catch (Exception e) {
							continue;
						}
						String group = "";
						if(argument.isAnnotationPresent(LKAModel.class) || argument.isAnnotationPresent(ApiModel.class)){
							if(parameters[i].isAnnotationPresent(LKAGroup.class)) {
								group = parameters[i].getAnnotation(LKAGroup.class).value();
							}
							analysisModel("",argument,group,url);
						}
					}
				}
				
				if(annotations != null && annotations.length > 0) {
					for (Annotation annotation : annotations) {
						if(annotation instanceof LKAParam || annotation instanceof LKAParams) {
							LKAParam[] params = null;
							if(annotation instanceof LKAParam) {
								params = new LKAParam[]{(LKAParam)annotation};
							}else {
								params = ((LKAParams)annotation).value();
							}
							if(params == null || params.length < 1) {
								continue;
							}
							for (LKAParam param : params) {
								if((param.range()!=null && !"".equals(param.range())) ||
										(param.length()!=null && !"".equals(param.length())) ||
										(param.size()!=null && !"".equals(param.size())) ||
										(param.valids() != null && param.valids().length>0) && 
										param.value() != null && !"".equals(param.value())) {
									CheckFieldModel cfmModel = new CheckFieldModel();
									cfmModel.setLength(param.length());
									cfmModel.setSize(param.size());
									cfmModel.setRange(param.range());
									cfmModel.setFieldName(param.name().split("-")[0]);
									cfmModel.setValids(param.valids());
									cfmModel.setParamType(param.paramType());
									cfmModel.setMsgs(param.msgs());
									cfmModel.setCls(param.dataType());
									cfmModel.setUrl(url);// 请求URL
									checkFieldModels.add(cfmModel);
								}
								if(param.valids() != null && param.valids().length>0 && param.values() != null && param.values().length > 0) {
									String[] names = param.names();
									if(names == null || names.length == 0) {
										names = new String[parameters.length];
										for (int i = 0;i < parameters.length;i++) {
											names[i] = parameters[i].getName();
										}
									}
									for (int i = 0;i<names.length;i++) {
										CheckFieldModel cfmModel = new CheckFieldModel();
										cfmModel.setFieldName(names[i].split("-")[0]);
										cfmModel.setUrl(url);// 请求URL
										
										if(param.dataTypes() == null || param.dataTypes().length == 0) {
											cfmModel.setCls(parameters[i].getType());
										}else {
											if(param.dataTypes() != null && param.dataTypes().length == 1) {
												cfmModel.setCls(param.dataTypes()[0]);
											}else {
												cfmModel.setCls(param.dataTypes()[i]);
											}
										}
										if(param.valids().length < names.length) {
											cfmModel.setValids(new String[] {param.valids()[0]});
										}else {
											cfmModel.setValids(new String[] {param.valids()[i]});
										}
										if(param.paramTypes() == null || param.paramTypes().length == 0) {
											if(parameters[i].isAnnotationPresent(PathVariable.class)) {
												cfmModel.setParamType(ParamType.PATH);
											}else if(parameters[i].isAnnotationPresent(RequestHeader.class)) {
												cfmModel.setParamType(ParamType.HEADER);
											}else {
												cfmModel.setParamType(param.paramType());
											}
										}else {
											if(param.paramTypes() != null && param.paramTypes().length == 1) {
												cfmModel.setParamType(param.paramTypes()[0]);
											}else {
												cfmModel.setParamType(param.paramTypes()[i]);
											}
										}
										if(param.msgs() == null || param.msgs().length == 0) {
											cfmModel.setMsgs(param.msgs());
										}else {
											if(param.msgs() != null && param.msgs().length == 1) {
												cfmModel.setMsgs(new String[] {param.msgs()[0]});
											}else {
												cfmModel.setMsgs(new String[] {param.msgs()[i]});
											}
										}
										checkFieldModels.add(cfmModel);
									}
								}
							}
							
						}
						
						if(annotation instanceof ApiImplicitParam || annotation instanceof ApiImplicitParams) {
							ApiImplicitParam[] params = null;
							if(annotation instanceof ApiImplicitParam) {
								params = new ApiImplicitParam[]{(ApiImplicitParam)annotation};
							}else {
								params = ((ApiImplicitParams)annotation).value();
							}
							if(params == null || params.length < 1) {
								continue;
							}
							for (ApiImplicitParam param : params) {
								if((param.range()!=null && !"".equals(param.range())) ||
										(param.length()!=null && !"".equals(param.length())) ||
										(param.size()!=null && !"".equals(param.size())) ||
										(param.valids() != null && param.valids().length>0) && 
										param.value() != null && !"".equals(param.value())) {
									CheckFieldModel cfmModel = new CheckFieldModel();
									cfmModel.setLength(param.length());
									cfmModel.setSize(param.size());
									cfmModel.setRange(param.range());
									cfmModel.setFieldName(param.name().split("-")[0]);
									cfmModel.setValids(param.valids());
									cfmModel.setParamType(param.paramType());
									cfmModel.setMsgs(param.msgs());
									cfmModel.setUrl(url);// 请求URL
									checkFieldModels.add(cfmModel);
								}
								if(param.valids() != null && param.valids().length>0 && param.values() != null && param.values().length > 0) {
									String[] names = param.names();
									if(names == null || names.length == 0) {
										names = new String[parameters.length];
										for (int i = 0;i < parameters.length;i++) {
											names[i] = parameters[i].getName();
										}
									}
									for (int i = 0;i<names.length;i++) {
										CheckFieldModel cfmModel = new CheckFieldModel();
										cfmModel.setFieldName(names[i].split("-")[0]);
										cfmModel.setUrl(url);// 请求URL
										
										if(param.dataTypes() == null || param.dataTypes().length == 0) {
											cfmModel.setCls(parameters[i].getType());
										}else {
											if(param.dataTypes() != null && param.dataTypes().length == 1) {
												cfmModel.setCls(param.dataTypes()[0]);
											}else {
												cfmModel.setCls(param.dataTypes()[i]);
											}
										}
										
										if(param.valids().length < names.length) {
											cfmModel.setValids(new String[] {param.valids()[0]});
										}else {
											cfmModel.setValids(new String[] {param.valids()[i]});
										}
										if(param.paramTypes() == null || param.paramTypes().length == 0) {
											if(parameters[i].isAnnotationPresent(PathVariable.class)) {
												cfmModel.setParamType(ParamType.PATH);
											}else if(parameters[i].isAnnotationPresent(RequestHeader.class)) {
												cfmModel.setParamType(ParamType.HEADER);
											}else {
												cfmModel.setParamType(param.paramType());
											}
										}else {
											if(param.paramTypes() != null && param.paramTypes().length == 1) {
												cfmModel.setParamType(param.paramTypes()[0]);
											}else {
												cfmModel.setParamType(param.paramTypes()[i]);
											}
										}
										if(param.msgs() == null || param.msgs().length == 0) {
											cfmModel.setMsgs(param.msgs());
										}else {
											if(param.msgs() != null && param.msgs().length == 1) {
												cfmModel.setMsgs(new String[] {param.msgs()[0]});
											}else {
												cfmModel.setMsgs(new String[] {param.msgs()[i]});
											}
										}
										checkFieldModels.add(cfmModel);
									}
								}
							}
						}
					}
				}
			}
		}
    }
    
    
    
    public void analysisModel(String parentName,Class<?> typeCls,String group,String url){
		reqNum++; //防止递归死循环
		if(reqNum > 10) {
			reqNum = 0;
			return;
		}
		if (!typeCls.isAnnotationPresent(LKAModel.class) && !typeCls.isAnnotationPresent(ApiModel.class)) {
			reqNum = 0;
			return;
		}
		// 获取所有属性对象
		Field[] fields = typeCls.getDeclaredFields();
		//获取父类所有属性对象
		Field[] declaredField;
		try {
			declaredField = getDeclaredField(typeCls.newInstance());
		} catch (Exception e) {
			declaredField = null;
		}
		Object[] arrays = null;
		//合并数组
		if(declaredField != null) {
			List<Field> list = new ArrayList<>(Arrays.asList(declaredField));
			arrays = list.toArray();
		}else {
			arrays = fields;
		}
		
		if (arrays != null && arrays.length > 0) {
			for (Object obj : arrays) {
				Field field = (Field)obj;
				if (!field.isAnnotationPresent(LKAProperty.class) && !field.isAnnotationPresent(ApiModelProperty.class))continue;
				if(field.isAnnotationPresent(LKAProperty.class)) {
					LKAProperty param = field.getAnnotation(LKAProperty.class);
					if((param.valids() == null || param.valids().length <1) && 
							"".equals(param.range()) && 
							"".equals(param.size()) && 
							"".equals(param.length()) &&
							param.type().getName().equals("java.lang.Object")) {
						continue;
					}
					if(group != null && !"".equals(group)) {
						String[] groups = param.groups();
						boolean bool = false;
						if(groups != null && groups.length > 0) {
							for (String gst : groups) {
								if(gst == null) continue;
								String[] gs = gst.split("-");
								if(gs == null || gs[0] == null || "".equals(gs[0]) || !gs[0].equals(group)) {
									continue;
								}else {
									bool = true;
									break;
								}
							}
						}
						if(!bool) continue;
					}
					Class<?> ctype = param.type();
					CheckFieldModel checkFieldModel = new CheckFieldModel();
					String name = field.getName();
					if(!"".equals(parentName)) {
						name = parentName+"."+field.getName();
					}
					if (!ctype.getName().equals("java.lang.Object")) {
						if((param.valids() != null && param.valids().length >0) || 
								!"".equals(param.size())) {
							checkFieldModel.setCls(field.getType());
							checkFieldModel.setMsgs(param.msgs());
							checkFieldModel.setFieldName(name);
							checkFieldModel.setParamType("query");
							checkFieldModel.setValids(param.valids());
							checkFieldModel.setRange(param.range());
							checkFieldModel.setSize(param.size());
							checkFieldModel.setLength(param.length());
							checkFieldModel.setUrl(url);
							checkFieldModels.add(checkFieldModel);
						}
						analysisModel(name,ctype,group,url);
					} else {
						checkFieldModel.setCls(field.getType());
						checkFieldModel.setMsgs(param.msgs());
						checkFieldModel.setFieldName(name);
						checkFieldModel.setParamType("query");
						checkFieldModel.setValids(param.valids());
						checkFieldModel.setRange(param.range());
						checkFieldModel.setSize(param.size());
						checkFieldModel.setLength(param.length());
						checkFieldModel.setUrl(url);
						checkFieldModels.add(checkFieldModel);
					}
				}else {
					ApiModelProperty param = field.getAnnotation(ApiModelProperty.class);
					if((param.valids() == null || param.valids().length <1) && 
							"".equals(param.range()) && 
							"".equals(param.size()) && 
							"".equals(param.length()) &&
							param.type().getName().equals("java.lang.Object")) {
						continue;
					}
					if(group != null && !"".equals(group)) {
						String[] groups = param.groups();
						boolean bool = false;
						if(groups != null && groups.length > 0) {
							for (String gst : groups) {
								if(gst == null) continue;
								String[] gs = gst.split("-");
								if(gs == null || gs[0] == null || "".equals(gs[0]) || !gs[0].equals(group)) {
									continue;
								}else {
									bool = true;
									break;
								}
							}
						}
						if(!bool) continue;
					}
					Class<?> ctype = param.type();
					CheckFieldModel checkFieldModel = new CheckFieldModel();
					String name = field.getName();
					if(!"".equals(parentName)) {
						name = parentName+"."+field.getName();
					}
					if (!ctype.getName().equals("java.lang.Object")) {
						if((param.valids() != null && param.valids().length > 0) || 
								!"".equals(param.size())) {
							checkFieldModel.setCls(field.getType());
							checkFieldModel.setMsgs(param.msgs());
							checkFieldModel.setFieldName(name);
							checkFieldModel.setParamType("query");
							checkFieldModel.setValids(param.valids());
							checkFieldModel.setRange(param.range());
							checkFieldModel.setSize(param.size());
							checkFieldModel.setLength(param.length());
							checkFieldModel.setUrl(url);
							checkFieldModels.add(checkFieldModel);
						}
						analysisModel(name,ctype,group,url);
					} else {
						checkFieldModel.setCls(field.getType());
						checkFieldModel.setMsgs(param.msgs());
						checkFieldModel.setFieldName(name);
						checkFieldModel.setParamType("query");
						checkFieldModel.setValids(param.valids());
						checkFieldModel.setRange(param.range());
						checkFieldModel.setSize(param.size());
						checkFieldModel.setLength(param.length());
						checkFieldModel.setUrl(url);
						checkFieldModels.add(checkFieldModel);
					}
				}
			}
		}
		reqNum = 0;
	}
    
    public  Field[] getDeclaredField(Object object) {
    	Class<?> clazz = object.getClass();
        List<Field> fieldList = new ArrayList<>();
        while (clazz != null) {
            fieldList.addAll(new ArrayList<>(Arrays.asList(clazz.getDeclaredFields())));
            clazz = clazz.getSuperclass();
        }
        Field[] fields = new Field[fieldList.size()];
        fieldList.toArray(fields);
        return fields;
    }
}