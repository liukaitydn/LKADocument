package com.lk.api.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.condition.PatternsRequestCondition;
import org.springframework.web.servlet.mvc.condition.RequestMethodsRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import com.lk.api.annotation.Api;
import com.lk.api.annotation.ApiImplicitParam;
import com.lk.api.annotation.ApiImplicitParams;
import com.lk.api.annotation.ApiModel;
import com.lk.api.annotation.ApiModelProperty;
import com.lk.api.annotation.ApiOperation;
import com.lk.api.annotation.LKADocument;
import com.lk.api.annotation.LKAGroup;
import com.lk.api.annotation.LKAMethod;
import com.lk.api.annotation.LKAModel;
import com.lk.api.annotation.LKAParam;
import com.lk.api.annotation.LKAParams;
import com.lk.api.annotation.LKAProperty;
import com.lk.api.annotation.LKARespose;
import com.lk.api.annotation.LKAResposes;
import com.lk.api.annotation.LKAType;
import com.lk.api.domain.MethodModel;
import com.lk.api.domain.ModelModel;
import com.lk.api.domain.ParamModel;
import com.lk.api.domain.PropertyModel;
import com.lk.api.domain.ResposeModel;
import com.lk.api.domain.TypeModel;

@RestController
@RequestMapping("lkad")
public class LKADController {
	
	@Autowired
	private WebApplicationContext applicationContext;
	
	/** 扫描包路径 */
	@Value("${lkad.basePackages:}")
	private String basePackages;
	/** 项目名称 */
	@Value("${lkad.projectName:Project Name}")
	private String projectName;
	/** 项目描述 */
	@Value("${lkad.description:Project Description}")
	private String description;
	/** 文档开关 */
	@Value("${lkad.enabled:true}")
	private Boolean enabled;
	
	private int reqNum = 0,respNum = 0,proNum = 0;
	
	
	
	/**
	 * 加载接口文档所有信息
	 * @return
	 * @throws Exception
	 */
	@GetMapping("doc")
	public Map<String, Object> loadLKADocument() throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		if(!"".equals(basePackages)) {
			map.put("projectName", projectName);
			map.put("description", description);
			map.put("enabled", enabled?"yes":"no");
		}else {
			Map<String, Object> beans = applicationContext.getBeansWithAnnotation(LKADocument.class);
			boolean bool = false;
			if(beans != null && beans.size()>0) {
				Set<String> keySet = beans.keySet();
				for (String key : keySet) {
					Object obj = beans.get(key);
					Class<? extends Object> bootClass = obj.getClass();
					LKADocument annotation = bootClass.getAnnotation(LKADocument.class);
					basePackages = annotation.basePackages();
					if("".equals(basePackages)) {
						bool = false;
						break;
					}
					map.put("projectName", annotation.projectName());
					map.put("description", annotation.description());
					if(!annotation.enabled()) {
						basePackages = "";
						map.put("error", "LKADocument接口文档功能已关闭");
						return map;
					}
					//map.put("enabled", annotation.enabled()?"yes":"no");
					bool = true;
					break;
				}
			}
			if(!bool) {
				basePackages = "";
				map.put("error", "没有设置要扫描的包路径");
				return map;
			}
		}
		
		//map.put("apiDoc", scanType(basePackage));
		
		//排序算法
		List<TypeModel> typeModels = scanType(basePackages.split(","));
		for (TypeModel typeModel : typeModels) {
			List<MethodModel> methods = typeModel.getMethodModels();
			for (MethodModel method : methods) {
				List<ResposeModel> ResposeModels = method.getRespose();
				List<ResposeModel> rms = new ArrayList<ResposeModel>();
				List<ResposeModel> rms2 = new ArrayList<ResposeModel>();
				for (ResposeModel m : ResposeModels) {
					if(m.getParentName()==null || "".equals(m.getParentName())) {
						rms.add(m);
					}else {
						rms2.add(m);
					}
				}
				int n = 0;
				sortResposeModel(rms,rms2,n);
				method.setRespose(rms);
			}
		}
		map.put("apiDoc",typeModels);
		return map;
	}
	
	/**
	 * 字段信息排序
	 * @param rms  排序前
	 * @param rms2  排序后
	 * @param n 记录递归层数，防止死循环
	 */
	public void sortResposeModel(List<ResposeModel> rms,List<ResposeModel> rms2,int n){
		n++;
		if(rms2 == null || rms2.size() < 1 || n == 20){
			return;
		}
		Iterator<ResposeModel> iterator = rms2.iterator();
		while(iterator.hasNext()) {
			ResposeModel next = iterator.next();
			for (ResposeModel m : rms) {
				if(m.getValue().equals(next.getParentName())) {
					rms.add(next);
					iterator.remove();
					break;
				}
			}
		}
		sortResposeModel(rms,rms2,n);
	}

	/**
	 * 根据包名获取文件对象
	 * 
	 * @param basePackage
	 * @return
	 */
	public List<File> getFile(String[] basePackages) {
		List<File> packageFiles = new ArrayList<File>();
		for (String basePackage : basePackages) {
			String path = basePackage.replace(".", "/");
			URL url = this.getClass().getClassLoader().getResource(path);
			if (url == null)
				return null;
			path = url.getPath().replace("!","");
			File packageFile = new File(path);
			packageFiles.add(packageFile);
		}
		
		
		
		return packageFiles;
	}

	/*public static void main(String[] args) {
		String path = "com/lk";
		LKADController lkadController = new LKADController();
		URL resource = lkadController.getClass().getResource("/");
		System.out.println(resource.toString()+"src/main/java/com/lk");
		File packageFile = new File(resource.toString()+"src/main/java/com/lk");
		System.out.println(packageFile.getPath());
	}*/
	
	
	/**
	 * 扫描所有子包
	 * @param baseFile
	 * @return
	 */
	public List<File> queryFiles(File baseFile) {
		File[] files = baseFile.listFiles();
		List<File> allFiles = new ArrayList<File>();
		if (files == null || files.length < 1) return null;
		for (File file : files) {
			if (file == null || file.getName() == null) continue;
			if (file.isDirectory()) {
				List<File> queryFiles = queryFiles(file);
				if(queryFiles == null) return allFiles;
				allFiles.addAll(queryFiles);
			} else {
				allFiles.add(file);
			}
		}
		return allFiles;
	}
	
	
	/**
	 * 扫描所有满足条件的接口，获取它们的入参出参相关信息
	 * @param basePackages 要扫描的包
	 * @return
	 * @throws Exception
	 */
	public List<TypeModel> scanType(String[] basePackages) throws Exception {
		List<TypeModel> typeModels = new ArrayList<TypeModel>();
		//List<File> packageFiles = getFile(basePackages);
		// 判断是否是目录
		if (basePackages != null) {
			// 获取所有方法的请求信息
			List<Map<String, Object>> methodURLs = getMethodURL();
			List<String> sconPackages = new ArrayList<>();
			for (String basePackage : basePackages) {
				SconPackage sconPackage = new SconPackage(basePackage);
				sconPackages.addAll(sconPackage.getFullyQualifiedClassNameList());
			}
			
			for (String sconPackage : sconPackages) {
				Class<?> cls = Class.forName(sconPackage);
				// 判断是否有LKAType或者Api注解
				if (!cls.isAnnotationPresent(LKAType.class) && !cls.isAnnotationPresent(Api.class))
					continue;
				TypeModel typeModel = new TypeModel();
				if(cls.isAnnotationPresent(LKAType.class)) {
					LKAType lkaType = cls.getAnnotation(LKAType.class);
					if(lkaType.hidden())continue;
					String cName = lkaType.value();
					String cDescription = lkaType.description();
					typeModel.setName(cName);
					typeModel.setDescription(cDescription);
				}else {
					Api api = cls.getAnnotation(Api.class);
					if(api.hidden())continue;
					String cName = api.tags();
					String cDescription = api.description();
					typeModel.setName(cName);
					typeModel.setDescription(cDescription);
				}
				// 获取类描述信息
				typeModel.setValue(cls.getSimpleName());
				// 获取所有方法
				Method[] methods = cls.getMethods();

				if (methods != null && methods.length > 0) {
					List<MethodModel> methodModels = new ArrayList<MethodModel>();
					for (Method method : methods) {
						if (method == null)continue;	
						// 判断是否有LKAMethod注解
						if (!method.isAnnotationPresent(LKAMethod.class) && !method.isAnnotationPresent(ApiOperation.class))continue;
						MethodModel methodModel = new MethodModel();
						if(method.isAnnotationPresent(LKAMethod.class)) {
							LKAMethod lkaMethod = method.getAnnotation(LKAMethod.class);
							if(lkaMethod.hidden())continue;
							// 获取方法描述信息
							String mName = lkaMethod.value();
							String mDescription = lkaMethod.description();
							methodModel.setName(mName);
							methodModel.setDescription(mDescription);
							methodModel.setValue(method.getName());
							methodModel.setUrl("该API未设置请求路径");
							methodModel.setVersion(lkaMethod.version());
							methodModel.setContentType(lkaMethod.contentType());
							methodModel.setRequestType("未知");
						}else {
							ApiOperation lkaMethod  = method.getAnnotation(ApiOperation.class);
							if(lkaMethod.hidden())continue;
							// 获取方法描述信息
							String mName = lkaMethod.value();
							String mDescription = lkaMethod.notes();
							methodModel.setName(mName);
							methodModel.setDescription(mDescription);
							methodModel.setValue(method.getName());
							methodModel.setUrl("该API未设置请求路径");
							methodModel.setVersion(lkaMethod.version());
							methodModel.setContentType(lkaMethod.contentType());
							methodModel.setRequestType("未知");
						}
						
						for (Map<String, Object> map : methodURLs) {
							if(map.get("className") != null) {
								if (method.getDeclaringClass().getName().equals(map.get("className").toString())
										&& method.getName().equals(map.get("methodName"))) {
									Object url = map.get("methodURL");
									Object requestType = map.get("requestType");
									if (url == null) {
										url = "该API未设置请求路径";
										requestType = "未知";
									}
									if (url != null && requestType == null) {
										requestType = "通用";
									}
									methodModel.setUrl(url.toString());
									methodModel.setRequestType(requestType.toString());
								}else {
									List<String> list = (List<String>)map.get("interfacesNames");
									for (String str : list) {
										if(method.getDeclaringClass().getName().equals(str) && method.getName().equals(map.get("methodName"))) {
											Object url = map.get("methodURL");
											Object requestType = map.get("requestType");
											if (url == null) {
												url = "该API未设置请求路径";
												requestType = "未知";
											}
											if (url != null && requestType == null) {
												requestType = "通用";
											}
											methodModel.setUrl(url.toString());
											methodModel.setRequestType(requestType.toString());
											break;
										}
									}
								}
							}
						}

						List<ParamModel> request = new ArrayList<ParamModel>();
						List<ResposeModel> respose = new ArrayList<ResposeModel>();
						/******/
						//自动判断出参类型是否是model对象
						Class<?> returnType = method.getReturnType();
						if(returnType != null && !"void".equals(returnType.getName())) {
							boolean bool2 = false;
							if(returnType.equals(List.class)) {//list集合
								//当前集合的泛型类型
								Type genericReturnType = method.getGenericReturnType();
								if(genericReturnType instanceof ParameterizedType) {
			                        ParameterizedType pt = (ParameterizedType) genericReturnType;
			                        //得到泛型里的class类型对象
			                        returnType = (Class<?>)pt.getActualTypeArguments()[0];
								}
							}
							if(returnType.isArray()) {//数组
								// 获取数组元素的类型
								returnType = returnType.getComponentType();					                    
							}
							if(returnType.isAnnotationPresent(LKAModel.class) || returnType.isAnnotationPresent(ApiModel.class)){
								// 获取model描述信息
								ModelModel modelModel = new ModelModel();
								modelModel.setValue(returnType.getSimpleName());
								// 获取所有属性对象
								Field[] fields = returnType.getDeclaredFields();
								
								//获取父类所有属性对象
								Field[] declaredField;
								try {
									declaredField = getDeclaredField(returnType.newInstance());
								} catch (Exception e) {
									declaredField = null;
								}
								Object[] arrays = null;
								//合并数组
								if(declaredField != null) {
									List<Field> list = new ArrayList<>(Arrays.asList(fields));
									list.addAll(Arrays.asList(declaredField));
									arrays = list.toArray();
								}else {
									arrays = fields;
								}
								
								if (arrays != null && arrays.length > 0) {
									List<PropertyModel> propertyModels = new ArrayList<PropertyModel>();
									for (Object obj : arrays) {
										Field field = (Field)obj;
										boolean bool = false;
										if (!field.isAnnotationPresent(LKAProperty.class) && !field.isAnnotationPresent(ApiModelProperty.class))continue;
										String pValue = field.getName();
										if (method.isAnnotationPresent(LKAResposes.class)) {
											LKAResposes lKAResposes = method.getAnnotation(LKAResposes.class);
											LKARespose[] resps = lKAResposes.value();
											if(resps != null && resps.length > 0) {
												for (LKARespose resp : resps) {
													if(resp.type() != null && resp.type().getTypeName().equals(returnType.getTypeName())) {
														bool2 = true;
														break;
													}
													if(resp.name().equals(pValue)) {
														bool = true;
														break;
													}
												}
											}
										}
										if (method.isAnnotationPresent(LKARespose.class)) {
											LKARespose resp = method.getAnnotation(LKARespose.class);
											if(resp.type() != null && resp.type().getTypeName().equals(returnType.getTypeName())) {
												bool2 = true;
												break;
											}
											if(resp.name().equals(pValue)) {
												bool = true;
											}
										}
										if(bool2) {
											break;
										}
										if(!bool) {
											PropertyModel propertyModel = null;
											if(field.isAnnotationPresent(LKAProperty.class)){
												LKAProperty property = field.getAnnotation(LKAProperty.class);
												if(property.hidden()) continue;
												//System.out.println(property.type().getName());
												if(property.type().getName().equals("java.lang.Object")) {
													propertyModel = new PropertyModel();
												}else {
													propertyModel = analysisProModel(property.type(),null);
													if(propertyModel == null) propertyModel = new PropertyModel();
												}
												propertyModel.setArray(property.isArray());
												propertyModel.setValue(pValue);
												propertyModel.setName(property.value());
												propertyModel.setDescription(property.description());
												propertyModel.setDataType(field.getType().getSimpleName());
												propertyModels.add(propertyModel);
											}else {
												ApiModelProperty property = field.getAnnotation(ApiModelProperty.class);
												if(property.hidden()) continue;
												if(property.type().getName().equals("java.lang.Object")) {
													propertyModel = new PropertyModel();
												}else {
													propertyModel = analysisProModel(property.type(),null);
													if(propertyModel == null) propertyModel = new PropertyModel();
												}
												propertyModel.setArray(property.isArray());
												propertyModel.setValue(pValue);
												propertyModel.setName(property.value());
												propertyModel.setDescription(property.description());
												propertyModel.setDataType(field.getType().getSimpleName());
												propertyModels.add(propertyModel);
											}	
										}
									}
									modelModel.setPropertyModels(propertyModels);
									ResposeModel resposeModel = new ResposeModel();
									resposeModel.setValue(modelModel.getValue());
									resposeModel.setModelModel(modelModel);
									respose.add(resposeModel);
								}
							}
						}		
						
						
						
							
						//自动判断入参类型是否是model对象
						Class<?>[] parameterTypes = method.getParameterTypes();
						Parameter[] parameters = method.getParameters();
						if(parameters != null && parameters.length > 0) {
							for (int i = 0;i<parameterTypes.length;i++) {
								boolean bool = false,bool2=false;
								Class<?> argument = parameterTypes[i];
								Class<?> type = parameters[i].getType();
								boolean isArray = false;
								if(type.equals(List.class)) { //list集合
									isArray = true;
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
								if(type.isArray()) {//数组
									// 获取数组元素的类型
									isArray = true;
									argument = type.getComponentType();					                    
								}

								if(argument.isAnnotationPresent(LKAModel.class) || argument.isAnnotationPresent(ApiModel.class)){
									if (method.isAnnotationPresent(LKAParams.class)) {
										LKAParams lkaParams = method.getAnnotation(LKAParams.class);
										LKAParam[] params = lkaParams.value();
										for (LKAParam pa : params) {
											if(pa.type().getTypeName().equals(argument.getTypeName())) {
												bool2 = true;
												break;
											}
											if(pa.value().equals(parameters[i].getName())) {
												bool = true;
												break;
											}
										}
									}
									if (method.isAnnotationPresent(LKAParam.class)) {
										LKAParam pa = method.getAnnotation(LKAParam.class);
										if(pa.type().getTypeName().equals(argument.getTypeName())) {
											bool2 = true;
										}
										if(pa.value().equals(parameters[i].getName())) {
											bool = true;
										}
									}
									if (method.isAnnotationPresent(ApiImplicitParams.class)) {
										ApiImplicitParams lkaParams = method.getAnnotation(ApiImplicitParams.class);
										ApiImplicitParam[] params = lkaParams.value();
										for (ApiImplicitParam pa : params) {
											if(pa.type().getTypeName().equals(argument.getTypeName())) {
												bool2 = true;
											}
											if(pa.value().equals(parameters[i].getName())) {
												bool = true;
												break;
											}
										}
									}
									if (method.isAnnotationPresent(ApiImplicitParam.class)) {
										ApiImplicitParam pa = method.getAnnotation(ApiImplicitParam.class);
										if(pa.type().getTypeName().equals(argument.getTypeName())) {
											bool2 = true;
										}
										if(pa.value().equals(parameters[i].getName())) {
											bool = true;
										}
									}
									if(bool2) {
										break;
									}
									if(bool) {
										continue;
									}
									
									String  group= "";
									if(parameters[i].isAnnotationPresent(LKAGroup.class)) {
										group = parameters[i].getAnnotation(LKAGroup.class).value();
										
									}
									ParamModel paramModel = analysisModel(argument,group);
									if (paramModel != null) {
										if(argument.isAnnotationPresent(LKAModel.class)) {
											paramModel.setArray(isArray);
											paramModel.setValue(argument.getSimpleName());
											paramModel.setName(argument.getAnnotation(LKAModel.class).value());
											paramModel.setDescription(argument.getAnnotation(LKAModel.class).description());
											paramModel.setDataType("");
											request.add(paramModel);
										}else {
											paramModel.setArray(isArray);
											paramModel.setValue(argument.getSimpleName());
											paramModel.setName(argument.getAnnotation(ApiModel.class).value());
											paramModel.setDescription(argument.getAnnotation(ApiModel.class).description());
											paramModel.setDataType("");
											request.add(paramModel);
										}
									}
								}
							}
						}
						/******/
						
						// 判断入参注解
						if (method.isAnnotationPresent(LKAParams.class) || method.isAnnotationPresent(ApiImplicitParams.class)) {
							if(method.isAnnotationPresent(LKAParams.class)) {
								LKAParams lkaParams = method.getAnnotation(LKAParams.class);
								LKAParam[] params = lkaParams.value();
								if (params != null && params.length > 0) {
									for (LKAParam param : params) {
										// 获取参数描述信息
										Class<?> type = param.type();
										if (!type.getName().equals("java.lang.Object")) { // 说明入参是对象
											ParamModel paramModel = analysisModel(type,param.group());
											if (paramModel != null) {
												paramModel.setArray(param.isArray());
												paramModel.setValue(param.name());
												paramModel.setName(param.value());
												paramModel.setDescription(param.description());
												paramModel.setDataType("");
												request.add(paramModel);
											}
										} else {
											if(param.name() != null && !"".equals(param.name())) {
												ParamModel paramModel = new ParamModel();
												String[] split = param.name().split("-");
												if(split != null && split.length==2) {
													if("n".equals(split[1].toLowerCase())) {
														paramModel.setRequired(false);
													}else {
														paramModel.setRequired(true);
													}
													paramModel.setValue(split[0]);
												}else {
													paramModel.setRequired(param.required());
													paramModel.setValue(param.name());
												}
												paramModel.setDataType(param.dataType());
												paramModel.setDescription(param.description());
												paramModel.setName(param.value());
												paramModel.setParamType(param.paramType());
												paramModel.setArray(param.isArray());
												paramModel.setTestData(param.testData());
												request.add(paramModel);
											}
											if(param.names() != null && param.names().length>0) {
												String[] names = param.names();
												for(int i = 0;i<names.length;i++) {
													ParamModel paramModel = new ParamModel();
													String[] split = names[i].split("-");
													if(split != null && split.length==2) {
														if("n".equals(split[1].toLowerCase())) {
															paramModel.setRequired(false);
														}else {
															paramModel.setRequired(true);
														}
														paramModel.setValue(split[0]);
													}else {
														if(param.requireds()!= null && param.requireds().length>0){
															try {
																paramModel.setRequired(param.requireds()[i]);
															} catch (Exception e) {
																paramModel.setRequired(param.requireds()[0]);
															}
														}else {
															paramModel.setRequired(param.required());
														}
														paramModel.setValue(names[i]);
													}
													
													if(param.dataTypes()!= null && param.dataTypes().length>0){
														try {
															paramModel.setDataType(param.dataTypes()[i]);
														} catch (Exception e) {
															paramModel.setDataType(param.dataTypes()[0]);
														}
													}else {
														paramModel.setDataType(param.dataType());
													}
													if(param.descriptions()!= null && param.descriptions().length>0){
														try {
															paramModel.setDescription(param.descriptions()[i]);
														} catch (Exception e) {
															paramModel.setDescription(param.descriptions()[0]);
														}
													}else {
														paramModel.setDescription(param.description());
													}
													if(param.values()!= null && param.values().length>0){
														try {
															paramModel.setName(param.values()[i]);
														} catch (Exception e) {
															paramModel.setName(param.values()[0]);
														}
													}else {
														paramModel.setName(param.value());
													}
													
													if(param.paramTypes()!= null && param.paramTypes().length>0){
														try {
															paramModel.setParamType(param.paramTypes()[i]);
														} catch (Exception e) {
															paramModel.setParamType(param.paramTypes()[0]);
														}
													}else {
														paramModel.setParamType(param.paramType());
													}
													if(param.isArrays()!= null && param.isArrays().length>0){
														try {
															paramModel.setArray(param.isArrays()[i]);
														} catch (Exception e) {
															paramModel.setArray(param.isArrays()[0]);
														}
													}else {
														paramModel.setArray(param.isArray());
													}
													if(param.testDatas()!= null && param.testDatas().length>0){
														try {
															paramModel.setTestData(param.testDatas()[i]);
														} catch (Exception e) {
															paramModel.setTestData(param.testDatas()[0]);
														}
													}else {
														paramModel.setTestData(param.testData());
													}
													request.add(paramModel);
												}
											}
										}
									}
								}
							}else {
								ApiImplicitParams lkaParams = method.getAnnotation(ApiImplicitParams.class);
								ApiImplicitParam[] params = lkaParams.value();
								if (params != null && params.length > 0) {
									for (ApiImplicitParam param : params) {
										// 获取参数描述信息
										Class<?> type = param.type();
										if (!type.getName().equals("java.lang.Object")) { // 说明入参是对象
											ParamModel paramModel = analysisModel(type,param.group());
											if (paramModel != null) {
												paramModel.setArray(param.isArray());
												paramModel.setValue(param.name());
												paramModel.setName(param.value());
												paramModel.setDescription(param.description());
												paramModel.setDataType("");
												request.add(paramModel);
											}
										} else {
											if(param.name() != null && !"".equals(param.name())) {
												ParamModel paramModel = new ParamModel();
												String[] split = param.name().split("-");
												if(split != null && split.length==2) {
													if("n".equals(split[1].toLowerCase())) {
														paramModel.setRequired(false);
													}else {
														paramModel.setRequired(true);
													}
													paramModel.setValue(split[0]);
												}else {
													paramModel.setRequired(param.required());
													paramModel.setValue(param.name());
												}
												paramModel.setDataType(param.dataType());
												paramModel.setDescription(param.description());
												paramModel.setName(param.value());
												paramModel.setParamType(param.paramType());
												paramModel.setArray(param.isArray());
												paramModel.setTestData(param.testData());
												request.add(paramModel);
											}
											if(param.names() != null && param.names().length>0) {
												String[] names = param.names();
												for(int i = 0;i<names.length;i++) {
													ParamModel paramModel = new ParamModel();
													String[] split = names[i].split("-");
													if(split != null && split.length==2) {
														if("n".equals(split[1].toLowerCase())) {
															paramModel.setRequired(false);
														}else {
															paramModel.setRequired(true);
														}
														paramModel.setValue(split[0]);
													}else {
														if(param.requireds()!= null && param.requireds().length>0){
															try {
																paramModel.setRequired(param.requireds()[i]);
															} catch (Exception e) {
																paramModel.setRequired(param.requireds()[0]);
															}
														}else {
															paramModel.setRequired(param.required());
														}
														paramModel.setValue(names[i]);
													}
													if(param.dataTypes()!= null && param.dataTypes().length>0){
														try {
															paramModel.setDataType(param.dataTypes()[i]);
														} catch (Exception e) {
															paramModel.setDataType(param.dataTypes()[0]);
														}
													}else {
														paramModel.setDataType(param.dataType());
													}
													if(param.descriptions()!= null && param.descriptions().length>0){
														try {
															paramModel.setDescription(param.descriptions()[i]);
														} catch (Exception e) {
															paramModel.setDescription(param.descriptions()[0]);
														}
													}else {
														paramModel.setDescription(param.description());
													}
													if(param.values()!= null && param.values().length>0){
														try {
															paramModel.setName(param.values()[i]);
														} catch (Exception e) {
															paramModel.setName(param.values()[0]);
														}
													}else {
														paramModel.setName(param.value());
													}
													
													if(param.paramTypes()!= null && param.paramTypes().length>0){
														try {
															paramModel.setParamType(param.paramTypes()[i]);
														} catch (Exception e) {
															paramModel.setParamType(param.paramTypes()[0]);
														}
													}else {
														paramModel.setParamType(param.paramType());
													}
													if(param.isArrays()!= null && param.isArrays().length>0){
														try {
															paramModel.setArray(param.isArrays()[i]);
														} catch (Exception e) {
															paramModel.setArray(param.isArrays()[0]);
														}
													}else {
														paramModel.setArray(param.isArray());
													}
													if(param.testDatas()!= null && param.testDatas().length>0){
														try {
															paramModel.setTestData(param.testDatas()[i]);
														} catch (Exception e) {
															paramModel.setTestData(param.testDatas()[0]);
														}
													}else {
														paramModel.setTestData(param.testData());
													}
													request.add(paramModel);
												}
											}
										}
									}
								}
							}
							
						} else if(method.isAnnotationPresent(LKAParam.class) || method.isAnnotationPresent(ApiImplicitParam.class)) {
							if(method.isAnnotationPresent(LKAParam.class)) {
								LKAParam param = method.getAnnotation(LKAParam.class);
								// 获取参数描述信息
								Class<?> type = param.type();
								if (!type.getName().equals("java.lang.Object")) { // 说明入参是对象
									ParamModel paramModel = analysisModel(type,param.group());
									if (paramModel != null) {
										paramModel.setArray(param.isArray());
										paramModel.setValue(param.name());
										paramModel.setName(param.value());
										paramModel.setDescription(param.description());
										paramModel.setDataType("");
										request.add(paramModel);
									}
								} else {
									if(param.name() != null && !"".equals(param.name())) {
										ParamModel paramModel = new ParamModel();
										String[] split = param.name().split("-");
										if(split != null && split.length==2) {
											if("n".equals(split[1].toLowerCase())) {
												paramModel.setRequired(false);
											}else {
												paramModel.setRequired(true);
											}
											paramModel.setValue(split[0]);
										}else {
											paramModel.setRequired(param.required());
											paramModel.setValue(param.name());
										}
										paramModel.setDataType(param.dataType());
										paramModel.setDescription(param.description());
										paramModel.setName(param.value());
										paramModel.setParamType(param.paramType());
										paramModel.setArray(param.isArray());
										paramModel.setTestData(param.testData());
										request.add(paramModel);
									}
									if(param.names() != null && param.names().length>0) {
										String[] names = param.names();
										for(int i = 0;i<names.length;i++) {
											ParamModel paramModel = new ParamModel();
											String[] split = names[i].split("-");
											if(split != null && split.length==2) {
												if("n".equals(split[1].toLowerCase())) {
													paramModel.setRequired(false);
												}else {
													paramModel.setRequired(true);
												}
												paramModel.setValue(split[0]);
											}else {
												if(param.requireds()!= null && param.requireds().length>0){
													try {
														paramModel.setRequired(param.requireds()[i]);
													} catch (Exception e) {
														paramModel.setRequired(param.requireds()[0]);
													}
												}else {
													paramModel.setRequired(param.required());
												}
												paramModel.setValue(names[i]);
											}
											if(param.dataTypes()!= null && param.dataTypes().length>0){
												try {
													paramModel.setDataType(param.dataTypes()[i]);
												} catch (Exception e) {
													paramModel.setDataType(param.dataTypes()[0]);
												}
											}else {
												paramModel.setDataType(param.dataType());
											}
											if(param.descriptions()!= null && param.descriptions().length>0){
												try {
													paramModel.setDescription(param.descriptions()[i]);
												} catch (Exception e) {
													paramModel.setDescription(param.descriptions()[0]);
												}
											}else {
												paramModel.setDescription(param.description());
											}
											if(param.values()!= null && param.values().length>0){
												try {
													paramModel.setName(param.values()[i]);
												} catch (Exception e) {
													paramModel.setName(param.values()[0]);
												}
											}else {
												paramModel.setName(param.value());
											}
											
											if(param.paramTypes()!= null && param.paramTypes().length>0){
												try {
													paramModel.setParamType(param.paramTypes()[i]);
												} catch (Exception e) {
													paramModel.setParamType(param.paramTypes()[0]);
												}
											}else {
												paramModel.setParamType(param.paramType());
											}
											if(param.isArrays()!= null && param.isArrays().length>0){
												try {
													paramModel.setArray(param.isArrays()[i]);
												} catch (Exception e) {
													paramModel.setArray(param.isArrays()[0]);
												}
											}else {
												paramModel.setArray(param.isArray());
											}
											if(param.testDatas()!= null && param.testDatas().length>0){
												try {
													paramModel.setTestData(param.testDatas()[i]);
												} catch (Exception e) {
													paramModel.setTestData(param.testDatas()[0]);
												}
											}else {
												paramModel.setTestData(param.testData());
											}
											request.add(paramModel);
										}
									}
								}
							}else {
								ApiImplicitParam param = method.getAnnotation(ApiImplicitParam.class);
								// 获取参数描述信息
								Class<?> type = param.type();
								if (!type.getName().equals("java.lang.Object")) { // 说明入参是对象
									ParamModel paramModel = analysisModel(type,param.group());
									if (paramModel != null) {
										paramModel.setArray(param.isArray());
										paramModel.setValue(param.name());
										paramModel.setName(param.value());
										paramModel.setDescription(param.description());
										paramModel.setDataType("");
										request.add(paramModel);
									}
								} else {
									if(param.name() != null && !"".equals(param.name())) {
										ParamModel paramModel = new ParamModel();
										String[] split = param.name().split("-");
										if(split != null && split.length==2) {
											if("n".equals(split[1].toLowerCase())) {
												paramModel.setRequired(false);
											}else {
												paramModel.setRequired(true);
											}
											paramModel.setValue(split[0]);
										}else {
											paramModel.setRequired(param.required());
											paramModel.setValue(param.name());
										}
										paramModel.setDataType(param.dataType());
										paramModel.setDescription(param.description());
										paramModel.setName(param.value());
										paramModel.setParamType(param.paramType());
										paramModel.setArray(param.isArray());
										paramModel.setTestData(param.testData());
										request.add(paramModel);
									}
									if(param.names() != null && param.names().length>0) {
										String[] names = param.names();
										for(int i = 0;i<names.length;i++) {
											ParamModel paramModel = new ParamModel();
											String[] split = names[i].split("-");
											if(split != null && split.length==2) {
												if("n".equals(split[1].toLowerCase())) {
													paramModel.setRequired(false);
												}else {
													paramModel.setRequired(true);
												}
												paramModel.setValue(split[0]);
											}else {
												if(param.requireds()!= null && param.requireds().length>0){
													try {
														paramModel.setRequired(param.requireds()[i]);
													} catch (Exception e) {
														paramModel.setRequired(param.requireds()[0]);
													}
												}else {
													paramModel.setRequired(param.required());
												}
												paramModel.setValue(names[i]);
											}
											if(param.dataTypes()!= null && param.dataTypes().length>0){
												try {
													paramModel.setDataType(param.dataTypes()[i]);
												} catch (Exception e) {
													paramModel.setDataType(param.dataTypes()[0]);
												}
											}else {
												paramModel.setDataType(param.dataType());
											}
											if(param.descriptions()!= null && param.descriptions().length>0){
												try {
													paramModel.setDescription(param.descriptions()[i]);
												} catch (Exception e) {
													paramModel.setDescription(param.descriptions()[0]);
												}
											}else {
												paramModel.setDescription(param.description());
											}
											if(param.values()!= null && param.values().length>0){
												try {
													paramModel.setName(param.values()[i]);
												} catch (Exception e) {
													paramModel.setName(param.values()[0]);
												}
											}else {
												paramModel.setName(param.value());
											}
											
											if(param.paramTypes()!= null && param.paramTypes().length>0){
												try {
													paramModel.setParamType(param.paramTypes()[i]);
												} catch (Exception e) {
													paramModel.setParamType(param.paramTypes()[0]);
												}
											}else {
												paramModel.setParamType(param.paramType());
											}
											if(param.isArrays()!= null && param.isArrays().length>0){
												try {
													paramModel.setArray(param.isArrays()[i]);
												} catch (Exception e) {
													paramModel.setArray(param.isArrays()[0]);
												}
											}else {
												paramModel.setArray(param.isArray());
											}
											if(param.testDatas()!= null && param.testDatas().length>0){
												try {
													paramModel.setTestData(param.testDatas()[i]);
												} catch (Exception e) {
													paramModel.setTestData(param.testDatas()[0]);
												}
											}else {
												paramModel.setTestData(param.testData());
											}
											request.add(paramModel);
										}
									}
								}
							}
						}
						// 判断出参注解
						if (method.isAnnotationPresent(LKAResposes.class)) {
							LKAResposes lkaResposes = method.getAnnotation(LKAResposes.class);
							LKARespose[] resposes = lkaResposes.value();
							if (resposes != null && resposes.length > 0) {
								for (LKARespose resp : resposes) {
									// 获取参数描述信息
									Class<?> type = resp.type();
									if (!type.getName().equals("java.lang.Object")) { // 说明入参是对象
										ResposeModel resposeModel = analysisResModel(type,resp.group());
										if (resposeModel != null) {
											resposeModel.setArray(resp.isArray());
											//###################父参######################
											if(resp.parentName()!=null && !"".equals(resp.parentName())) {
												boolean bl = true;
												for (ResposeModel res: respose) {
													if(res.getValue().equals(resp.parentName())) {
														bl = false;
														break;
													}
													if(res.getModelModel()!=null) {
														List<PropertyModel> propertyModels = res.getModelModel().getPropertyModels();
														if(propertyModels != null && propertyModels.size()>0) {
															Iterator<PropertyModel> iterator = propertyModels.iterator();
															while(iterator.hasNext()) {
																PropertyModel pm = iterator.next();
																if(pm.getValue() != null && pm.getValue().equals(resp.parentName())) {
																	iterator.remove();
																	break;
																}
															}
														}
													}
												}
												if(bl) {
													ResposeModel pn = new ResposeModel();
													pn.setValue(resp.parentName());
													pn.setName(resp.parentValue());
													pn.setDescription(resp.parentDescription());
													pn.setArray(resp.parentIsArray());
													if(resp.grandpaName()!=null && !"".equals(resp.grandpaName())) {
														pn.setParentName(resp.grandpaName());
													}
													respose.add(pn);
												}
											}
											//###################爷参######################
											if(resp.grandpaName()!=null && !"".equals(resp.grandpaName())) {
												boolean b1 = true;
												for (ResposeModel res: respose) {
													if(res.getValue().equals(resp.grandpaName())) {
														b1 = false;
														break;
													}
													if(res.getModelModel()!=null) {
														List<PropertyModel> propertyModels = res.getModelModel().getPropertyModels();
														if(propertyModels != null && propertyModels.size()>0) {
															Iterator<PropertyModel> iterator = propertyModels.iterator();
															while(iterator.hasNext()) {
																PropertyModel pm = iterator.next();
																if(pm.getValue() != null && pm.getValue().equals(resp.grandpaName())) {
																	iterator.remove();
																	break;
																}
															}
														}
													}
												}
												if(b1) {
													ResposeModel pn = new ResposeModel();
													pn.setValue(resp.grandpaName());
													pn.setName(resp.grandpaValue());
													pn.setDescription(resp.grandpaDescription());
													pn.setArray(resp.grandpaIsArray());
													respose.add(pn);
												}
											}
											//###################结束######################
											resposeModel.setParentName(resp.parentName());
											resposeModel.setValue(resp.name());
											resposeModel.setName(resp.value());
											resposeModel.setDescription(resp.description());
											resposeModel.setDataType("");
											respose.add(resposeModel);
										}
									} else {
										if(resp.names()!= null && resp.names().length>0) {
											String[] names = resp.names();
											for (int i = 0;i<names.length;i++) {
												String[] descriptions = resp.descriptions();
												String[] dataTypes = resp.dataTypes();
												boolean[] arrays = resp.isArrays();
												String[] values = resp.values();
												String[] parentNames = resp.parentNames();
												String[] parentDescriptions = resp.parentDescriptions();
												boolean[] parentIsArrays = resp.parentIsArrays();
												String[] parentValues = resp.parentValues();
												String[] grandpaNames = resp.grandpaNames();
												String[] grandpaDescriptions = resp.grandpaDescriptions();
												boolean[] grandpaIsArrays = resp.grandpaIsArrays();
												String[] grandpaValues = resp.grandpaValues();
												
												
												ResposeModel resposeModel = new ResposeModel();
												resposeModel.setValue(names[i]);
												
												if(descriptions!=null && descriptions.length > 0) {
													try {
														resposeModel.setDescription(descriptions[i]);
													} catch (Exception e) {
														resposeModel.setDescription(descriptions[0]);
													}
												}else {
													resposeModel.setDescription(resp.description());
												}
												
												if(values!=null && values.length > 0) {
													try {
														resposeModel.setName(values[i]);
													} catch (Exception e) {
														resposeModel.setName(values[0]);
													}
												}else {
													resposeModel.setName(resp.value());
												}
												
												if(dataTypes!=null && dataTypes.length > 0) {
													try {
														resposeModel.setDataType(dataTypes[i]);
													} catch (Exception e) {
														resposeModel.setDataType(dataTypes[0]);
													}
												}else {
													resposeModel.setDataType(resp.dataType());
												}
												
												if(arrays!=null && arrays.length > 0) {
													try {
														resposeModel.setArray(arrays[i]);
													} catch (Exception e) {
														resposeModel.setArray(arrays[0]);
													}
												}else {
													resposeModel.setArray(resp.isArray());
												}
												
												if(parentNames!=null && parentNames.length > 0) {
													try {
														//###################父参######################
														boolean bl = true;
														for (ResposeModel res: respose) {
															if(res.getValue().equals(parentNames[i])) {
																bl = false;
																break;
															}
															if(res.getModelModel()!=null) {
																List<PropertyModel> propertyModels = res.getModelModel().getPropertyModels();
																if(propertyModels != null && propertyModels.size()>0) {
																	Iterator<PropertyModel> iterator = propertyModels.iterator();
																	while(iterator.hasNext()) {
																		PropertyModel pm = iterator.next();
																		if(pm.getValue() != null && pm.getValue().equals(parentNames[i])) {
																			iterator.remove();
																			break;
																		}
																	}
																}
															}
														}
														if(bl) {
															ResposeModel pn = new ResposeModel();
															pn.setValue(parentNames[i]);
															if(parentValues != null && parentValues.length>0) {
																try {
																	pn.setName(parentValues[i]);
																} catch (Exception e) {
																	pn.setName(parentValues[0]);
																}
															}else {
																pn.setName(resp.parentValue());
															}
															if(parentDescriptions != null && parentDescriptions.length>0) {
																try {
																	pn.setDescription(parentDescriptions[i]);
																} catch (Exception e) {
																	pn.setDescription(parentDescriptions[0]);
																}
															}else {
																pn.setDescription(resp.parentDescription());
															}
															if(parentIsArrays != null && parentIsArrays.length>0) {
																try {
																	pn.setArray(parentIsArrays[i]);
																} catch (Exception e) {
																	pn.setArray(parentIsArrays[0]);
																}
															}else {
																pn.setArray(resp.parentIsArray());
															}
															if(grandpaNames!=null && grandpaNames.length>0) {
																try {
																	pn.setParentName(grandpaNames[i]);
																} catch (Exception e) {
																	pn.setParentName(grandpaNames[0]);
																}
															}else {
																if(resp.grandpaName()!=null && !"".equals(resp.grandpaName())) {
																	pn.setParentName(resp.grandpaName());
																}
															}
															respose.add(pn);
														}
														resposeModel.setParentName(parentNames[i]);
													} catch (Exception e) {
														//###################父参######################
														boolean bl = true;
														for (ResposeModel res: respose) {
															if(res.getValue().equals(parentNames[0])) {
																bl = false;
																break;
															}
															if(res.getModelModel()!=null) {
																List<PropertyModel> propertyModels = res.getModelModel().getPropertyModels();
																if(propertyModels != null && propertyModels.size()>0) {
																	Iterator<PropertyModel> iterator = propertyModels.iterator();
																	while(iterator.hasNext()) {
																		PropertyModel pm = iterator.next();
																		if(pm.getValue() != null && pm.getValue().equals(parentNames[0])) {
																			iterator.remove();
																			break;
																		}
																	}
																}
															}
														}
														if(bl) {
															ResposeModel pn = new ResposeModel();
															pn.setValue(parentNames[0]);
															if(parentValues != null && parentValues.length>0) {
																try {
																	pn.setName(parentValues[i]);
																} catch (Exception e1) {
																	pn.setName(parentValues[0]);
																}
															}else {
																pn.setName(resp.parentValue());
															}
															if(parentDescriptions != null && parentDescriptions.length>0) {
																try {
																	pn.setDescription(parentDescriptions[i]);
																} catch (Exception e1) {
																	pn.setDescription(parentDescriptions[0]);
																}
															}else {
																pn.setDescription(resp.parentDescription());
															}
															if(parentIsArrays != null && parentIsArrays.length>0) {
																try {
																	pn.setArray(parentIsArrays[i]);
																} catch (Exception e1) {
																	pn.setArray(parentIsArrays[0]);
																}
															}else {
																pn.setArray(resp.parentIsArray());
															}
															if(grandpaNames!=null && grandpaNames.length>0) {
																try {
																	pn.setParentName(grandpaNames[i]);
																} catch (Exception e1) {
																	pn.setParentName(grandpaNames[0]);
																}
															}else {
																if(resp.grandpaName()!=null && !"".equals(resp.grandpaName())) {
																	pn.setParentName(resp.grandpaName());
																}
															}
															respose.add(pn);
														}
														resposeModel.setParentName(parentNames[0]);
													}
												}else {
													//###################父参######################
													if(resp.parentName()!=null && !"".equals(resp.parentName())) {
														boolean bl = true;
														for (ResposeModel res: respose) {
															if(res.getValue().equals(resp.parentName())) {
																bl = false;
																break;
															}
															if(res.getModelModel()!=null) {
																List<PropertyModel> propertyModels = res.getModelModel().getPropertyModels();
																if(propertyModels != null && propertyModels.size()>0) {
																	Iterator<PropertyModel> iterator = propertyModels.iterator();
																	while(iterator.hasNext()) {
																		PropertyModel pm = iterator.next();
																		if(pm.getValue() != null && pm.getValue().equals(resp.parentName())) {
																			iterator.remove();
																			break;
																		}
																	}
																}
															}
														}
														if(bl) {
															ResposeModel pn = new ResposeModel();
															pn.setValue(resp.parentName());
															pn.setName(resp.parentValue());
															pn.setDescription(resp.parentDescription());
															pn.setArray(resp.parentIsArray());
															if(resp.grandpaName()!=null && !"".equals(resp.grandpaName())) {
																pn.setParentName(resp.grandpaName());
															}
															respose.add(pn);
														}
													}
													resposeModel.setParentName(resp.parentName());
												}
												
												//###################爷参######################
												if(grandpaNames != null && grandpaNames.length>0) {
													try {
														boolean b1 = true;
														for (ResposeModel res: respose) {
															if(res.getValue().equals(grandpaNames[i])) {
																b1 = false;
																break;
															}
															if(res.getModelModel()!=null) {
																List<PropertyModel> propertyModels = res.getModelModel().getPropertyModels();
																if(propertyModels != null && propertyModels.size()>0) {
																	Iterator<PropertyModel> iterator = propertyModels.iterator();
																	while(iterator.hasNext()) {
																		PropertyModel pm = iterator.next();
																		if(pm.getValue() != null && pm.getValue().equals(grandpaNames[i])) {
																			iterator.remove();
																			break;
																		}
																	}
																}
															}
														}
														if(b1) {
															ResposeModel pn = new ResposeModel();
															pn.setValue(grandpaNames[i]);
															if(grandpaValues != null && grandpaValues.length>0) {
																try {
																	pn.setName(grandpaValues[i]);
																} catch (Exception e1) {
																	pn.setName(grandpaValues[0]);
																}
															}else {
																pn.setName(resp.grandpaValue());
															}
															if(grandpaDescriptions != null && grandpaDescriptions.length>0) {
																try {
																	pn.setDescription(grandpaDescriptions[i]);
																} catch (Exception e1) {
																	pn.setDescription(grandpaDescriptions[0]);
																}
															}else {
																pn.setDescription(resp.grandpaDescription());
															}
															if(grandpaIsArrays != null && grandpaIsArrays.length>0) {
																try {
																	pn.setArray(grandpaIsArrays[i]);
																} catch (Exception e1) {
																	pn.setArray(grandpaIsArrays[0]);
																}
															}else {
																pn.setArray(resp.grandpaIsArray());
															}
															respose.add(pn);
														}
													} catch (Exception e) {
														boolean b1 = true;
														for (ResposeModel res: respose) {
															if(res.getValue().equals(grandpaNames[0])) {
																b1 = false;
																break;
															}
															if(res.getModelModel()!=null) {
																List<PropertyModel> propertyModels = res.getModelModel().getPropertyModels();
																if(propertyModels != null && propertyModels.size()>0) {
																	Iterator<PropertyModel> iterator = propertyModels.iterator();
																	while(iterator.hasNext()) {
																		PropertyModel pm = iterator.next();
																		if(pm.getValue() != null && pm.getValue().equals(grandpaNames[0])) {
																			iterator.remove();
																			break;
																		}
																	}
																}
															}
														}
														if(b1) {
															ResposeModel pn = new ResposeModel();
															pn.setValue(grandpaNames[0]);
															if(grandpaValues != null && grandpaValues.length>0) {
																try {
																	pn.setName(grandpaValues[i]);
																} catch (Exception e1) {
																	pn.setName(grandpaValues[0]);
																}
															}else {
																pn.setName(resp.grandpaValue());
															}
															if(grandpaDescriptions != null && grandpaDescriptions.length>0) {
																try {
																	pn.setDescription(grandpaDescriptions[i]);
																} catch (Exception e1) {
																	pn.setDescription(grandpaDescriptions[0]);
																}
															}else {
																pn.setDescription(resp.grandpaDescription());
															}
															if(grandpaIsArrays != null && grandpaIsArrays.length>0) {
																try {
																	pn.setArray(grandpaIsArrays[i]);
																} catch (Exception e1) {
																	pn.setArray(grandpaIsArrays[0]);
																}
															}else {
																pn.setArray(resp.grandpaIsArray());
															}
															respose.add(pn);
														}
													}
												}else {
													//###################爷参######################
													if(resp.grandpaName()!=null && !"".equals(resp.grandpaName())) {
														boolean b1 = true;
														for (ResposeModel res: respose) {
															if(res.getValue().equals(resp.grandpaName())) {
																b1 = false;
																break;
															}
															if(res.getModelModel()!=null) {
																List<PropertyModel> propertyModels = res.getModelModel().getPropertyModels();
																if(propertyModels != null && propertyModels.size()>0) {
																	Iterator<PropertyModel> iterator = propertyModels.iterator();
																	while(iterator.hasNext()) {
																		PropertyModel pm = iterator.next();
																		if(pm.getValue() != null && pm.getValue().equals(resp.grandpaName())) {
																			iterator.remove();
																			break;
																		}
																	}
																}
															}
														}
														if(b1) {
															ResposeModel pn = new ResposeModel();
															pn.setValue(resp.grandpaName());
															pn.setName(resp.grandpaValue());
															pn.setDescription(resp.grandpaDescription());
															pn.setArray(resp.grandpaIsArray());
															respose.add(pn);
														}
													}
													//###################结束######################
												}

												respose.add(resposeModel);
											}
										}else {
											ResposeModel resposeModel = new ResposeModel();
											resposeModel.setDataType(resp.dataType());
											resposeModel.setDescription(resp.description());
											resposeModel.setName(resp.value());
											resposeModel.setArray(resp.isArray());
											//###################父参######################
											if(resp.parentName()!=null && !"".equals(resp.parentName())) {
												boolean bl = true;
												for (ResposeModel res: respose) {
													if(res.getValue().equals(resp.parentName())) {
														bl = false;
														break;
													}
													if(res.getModelModel()!=null) {
														List<PropertyModel> propertyModels = res.getModelModel().getPropertyModels();
														if(propertyModels != null && propertyModels.size()>0) {
															Iterator<PropertyModel> iterator = propertyModels.iterator();
															while(iterator.hasNext()) {
																PropertyModel pm = iterator.next();
																if(pm.getValue() != null && pm.getValue().equals(resp.parentName())) {
																	iterator.remove();
																	break;
																}
															}
														}
													}
												}
												if(bl) {
													ResposeModel pn = new ResposeModel();
													pn.setValue(resp.parentName());
													pn.setName(resp.parentValue());
													pn.setDescription(resp.parentDescription());
													pn.setArray(resp.parentIsArray());
													if(resp.grandpaName()!=null && !"".equals(resp.grandpaName())) {
														pn.setParentName(resp.grandpaName());
													}
													respose.add(pn);
												}
											}
											//###################爷参######################
											if(resp.grandpaName()!=null && !"".equals(resp.grandpaName())) {
												boolean b1 = true;
												for (ResposeModel res: respose) {
													if(res.getValue().equals(resp.grandpaName())) {
														b1 = false;
														break;
													}
													if(res.getModelModel()!=null) {
														List<PropertyModel> propertyModels = res.getModelModel().getPropertyModels();
														if(propertyModels != null && propertyModels.size()>0) {
															Iterator<PropertyModel> iterator = propertyModels.iterator();
															while(iterator.hasNext()) {
																PropertyModel pm = iterator.next();
																if(pm.getValue() != null && pm.getValue().equals(resp.grandpaName())) {
																	iterator.remove();
																	break;
																}
															}
														}
													}
												}
												if(b1) {
													ResposeModel pn = new ResposeModel();
													pn.setValue(resp.grandpaName());
													pn.setName(resp.grandpaValue());
													pn.setDescription(resp.grandpaDescription());
													pn.setArray(resp.grandpaIsArray());
													respose.add(pn);
												}
											}
											//###################结束######################
											resposeModel.setParentName(resp.parentName());
											resposeModel.setValue(resp.name());
											respose.add(resposeModel);
										}
									}
								}
							}
						} else if (method.isAnnotationPresent(LKARespose.class)) {
							LKARespose resp = method.getAnnotation(LKARespose.class);
							// 获取参数描述信息
							Class<?> type = resp.type();
							if (!type.getName().equals("java.lang.Object")) { // 说明入参是对象
								ResposeModel resposeModel = analysisResModel(type,resp.group());
								if (resposeModel != null) {
									resposeModel.setArray(resp.isArray());
									//###################父参######################
									if(resp.parentName()!=null && !"".equals(resp.parentName())) {
										boolean bl = true;
										for (ResposeModel res: respose) {
											if(res.getValue().equals(resp.parentName())) {
												bl = false;
												break;
											}
											if(res.getModelModel()!=null) {
												List<PropertyModel> propertyModels = res.getModelModel().getPropertyModels();
												if(propertyModels != null && propertyModels.size()>0) {
													Iterator<PropertyModel> iterator = propertyModels.iterator();
													while(iterator.hasNext()) {
														PropertyModel pm = iterator.next();
														if(pm.getValue() != null && pm.getValue().equals(resp.parentName())) {
															iterator.remove();
															break;
														}
													}
												}
											}
										}
										if(bl) {
											ResposeModel pn = new ResposeModel();
											pn.setValue(resp.parentName());
											pn.setName(resp.parentValue());
											pn.setDescription(resp.parentDescription());
											pn.setArray(resp.parentIsArray());
											if(resp.grandpaName()!=null && !"".equals(resp.grandpaName())) {
												pn.setParentName(resp.grandpaName());
											}
											respose.add(pn);
										}
									}
									//###################爷参######################
									if(resp.grandpaName()!=null && !"".equals(resp.grandpaName())) {
										boolean b1 = true;
										for (ResposeModel res: respose) {
											if(res.getValue().equals(resp.grandpaName())) {
												b1 = false;
												break;
											}
											if(res.getModelModel()!=null) {
												List<PropertyModel> propertyModels = res.getModelModel().getPropertyModels();
												if(propertyModels != null && propertyModels.size()>0) {
													Iterator<PropertyModel> iterator = propertyModels.iterator();
													while(iterator.hasNext()) {
														PropertyModel pm = iterator.next();
														if(pm.getValue() != null && pm.getValue().equals(resp.grandpaName())) {
															iterator.remove();
															break;
														}
													}
												}
											}
										}
										if(b1) {
											ResposeModel pn = new ResposeModel();
											pn.setValue(resp.grandpaName());
											pn.setName(resp.grandpaValue());
											pn.setDescription(resp.grandpaDescription());
											pn.setArray(resp.grandpaIsArray());
											respose.add(pn);
										}
									}
									//###################结束######################
									resposeModel.setParentName(resp.parentName());
									resposeModel.setValue(resp.name());
									resposeModel.setName(resp.value());
									resposeModel.setDescription(resp.description());
									resposeModel.setDataType("");
									respose.add(resposeModel);
								}
							} else {

								if(resp.names()!= null && resp.names().length>0) {
									String[] names = resp.names();
									for (int i = 0;i<names.length;i++) {
										String[] descriptions = resp.descriptions();
										String[] dataTypes = resp.dataTypes();
										boolean[] arrays = resp.isArrays();
										String[] values = resp.values();
										String[] parentNames = resp.parentNames();
										String[] parentDescriptions = resp.parentDescriptions();
										boolean[] parentIsArrays = resp.parentIsArrays();
										String[] parentValues = resp.parentValues();
										String[] grandpaNames = resp.grandpaNames();
										String[] grandpaDescriptions = resp.grandpaDescriptions();
										boolean[] grandpaIsArrays = resp.grandpaIsArrays();
										String[] grandpaValues = resp.grandpaValues();
										
										
										ResposeModel resposeModel = new ResposeModel();
										resposeModel.setValue(names[i]);
										
										if(descriptions!=null && descriptions.length > 0) {
											try {
												resposeModel.setDescription(descriptions[i]);
											} catch (Exception e) {
												resposeModel.setDescription(descriptions[0]);
											}
										}else {
											resposeModel.setDescription(resp.description());
										}
										
										if(values!=null && values.length > 0) {
											try {
												resposeModel.setName(values[i]);
											} catch (Exception e) {
												resposeModel.setName(values[0]);
											}
										}else {
											resposeModel.setName(resp.value());
										}
										
										if(dataTypes!=null && dataTypes.length > 0) {
											try {
												resposeModel.setDataType(dataTypes[i]);
											} catch (Exception e) {
												resposeModel.setDataType(dataTypes[0]);
											}
										}else {
											resposeModel.setDataType(resp.dataType());
										}
										
										if(arrays!=null && arrays.length > 0) {
											try {
												resposeModel.setArray(arrays[i]);
											} catch (Exception e) {
												resposeModel.setArray(arrays[0]);
											}
										}else {
											resposeModel.setArray(resp.isArray());
										}
										
										if(parentNames!=null && parentNames.length > 0) {
											try {
												//###################父参######################
												boolean bl = true;
												for (ResposeModel res: respose) {
													if(res.getValue().equals(parentNames[i])) {
														bl = false;
														break;
													}
													if(res.getModelModel()!=null) {
														List<PropertyModel> propertyModels = res.getModelModel().getPropertyModels();
														if(propertyModels != null && propertyModels.size()>0) {
															Iterator<PropertyModel> iterator = propertyModels.iterator();
															while(iterator.hasNext()) {
																PropertyModel pm = iterator.next();
																if(pm.getValue() != null && pm.getValue().equals(parentNames[i])) {
																	iterator.remove();
																	break;
																}
															}
														}
													}
												}
												if(bl) {
													ResposeModel pn = new ResposeModel();
													pn.setValue(parentNames[i]);
													if(parentValues != null && parentValues.length>0) {
														try {
															pn.setName(parentValues[i]);
														} catch (Exception e) {
															pn.setName(parentValues[0]);
														}
													}else {
														pn.setName(resp.parentValue());
													}
													if(parentDescriptions != null && parentDescriptions.length>0) {
														try {
															pn.setDescription(parentDescriptions[i]);
														} catch (Exception e) {
															pn.setDescription(parentDescriptions[0]);
														}
													}else {
														pn.setDescription(resp.parentDescription());
													}
													if(parentIsArrays != null && parentIsArrays.length>0) {
														try {
															pn.setArray(parentIsArrays[i]);
														} catch (Exception e) {
															pn.setArray(parentIsArrays[0]);
														}
													}else {
														pn.setArray(resp.parentIsArray());
													}
													if(grandpaNames!=null && grandpaNames.length>0) {
														try {
															pn.setParentName(grandpaNames[i]);
														} catch (Exception e) {
															pn.setParentName(grandpaNames[0]);
														}
													}else {
														if(resp.grandpaName()!=null && !"".equals(resp.grandpaName())) {
															pn.setParentName(resp.grandpaName());
														}
													}
													respose.add(pn);
												}
												resposeModel.setParentName(parentNames[i]);
											} catch (Exception e) {
												//###################父参######################
												boolean bl = true;
												for (ResposeModel res: respose) {
													if(res.getValue().equals(parentNames[0])) {
														bl = false;
														break;
													}
													if(res.getModelModel()!=null) {
														List<PropertyModel> propertyModels = res.getModelModel().getPropertyModels();
														if(propertyModels != null && propertyModels.size()>0) {
															Iterator<PropertyModel> iterator = propertyModels.iterator();
															while(iterator.hasNext()) {
																PropertyModel pm = iterator.next();
																if(pm.getValue() != null && pm.getValue().equals(parentNames[0])) {
																	iterator.remove();
																	break;
																}
															}
														}
													}
												}
												if(bl) {
													ResposeModel pn = new ResposeModel();
													pn.setValue(parentNames[0]);
													if(parentValues != null && parentValues.length>0) {
														try {
															pn.setName(parentValues[i]);
														} catch (Exception e1) {
															pn.setName(parentValues[0]);
														}
													}else {
														pn.setName(resp.parentValue());
													}
													if(parentDescriptions != null && parentDescriptions.length>0) {
														try {
															pn.setDescription(parentDescriptions[i]);
														} catch (Exception e1) {
															pn.setDescription(parentDescriptions[0]);
														}
													}else {
														pn.setDescription(resp.parentDescription());
													}
													if(parentIsArrays != null && parentIsArrays.length>0) {
														try {
															pn.setArray(parentIsArrays[i]);
														} catch (Exception e1) {
															pn.setArray(parentIsArrays[0]);
														}
													}else {
														pn.setArray(resp.parentIsArray());
													}
													if(grandpaNames!=null && grandpaNames.length>0) {
														try {
															pn.setParentName(grandpaNames[i]);
														} catch (Exception e1) {
															pn.setParentName(grandpaNames[0]);
														}
													}else {
														if(resp.grandpaName()!=null && !"".equals(resp.grandpaName())) {
															pn.setParentName(resp.grandpaName());
														}
													}
													respose.add(pn);
												}
												resposeModel.setParentName(parentNames[0]);
											}
										}else {
											//###################父参######################
											if(resp.parentName()!=null && !"".equals(resp.parentName())) {
												boolean bl = true;
												for (ResposeModel res: respose) {
													if(res.getValue().equals(resp.parentName())) {
														bl = false;
														break;
													}
													if(res.getModelModel()!=null) {
														List<PropertyModel> propertyModels = res.getModelModel().getPropertyModels();
														if(propertyModels != null && propertyModels.size()>0) {
															Iterator<PropertyModel> iterator = propertyModels.iterator();
															while(iterator.hasNext()) {
																PropertyModel pm = iterator.next();
																if(pm.getValue() != null && pm.getValue().equals(resp.parentName())) {
																	iterator.remove();
																	break;
																}
															}
														}
													}
												}
												if(bl) {
													ResposeModel pn = new ResposeModel();
													pn.setValue(resp.parentName());
													pn.setName(resp.parentValue());
													pn.setDescription(resp.parentDescription());
													pn.setArray(resp.parentIsArray());
													if(resp.grandpaName()!=null && !"".equals(resp.grandpaName())) {
														pn.setParentName(resp.grandpaName());
													}
													respose.add(pn);
												}
											}
											resposeModel.setParentName(resp.parentName());
										}
										
										//###################爷参######################
										if(grandpaNames != null && grandpaNames.length>0) {
											try {
												boolean b1 = true;
												for (ResposeModel res: respose) {
													if(res.getValue().equals(grandpaNames[i])) {
														b1 = false;
														break;
													}
													if(res.getModelModel()!=null) {
														List<PropertyModel> propertyModels = res.getModelModel().getPropertyModels();
														if(propertyModels != null && propertyModels.size()>0) {
															Iterator<PropertyModel> iterator = propertyModels.iterator();
															while(iterator.hasNext()) {
																PropertyModel pm = iterator.next();
																if(pm.getValue() != null && pm.getValue().equals(grandpaNames[i])) {
																	iterator.remove();
																	break;
																}
															}
														}
													}
												}
												if(b1) {
													ResposeModel pn = new ResposeModel();
													pn.setValue(grandpaNames[i]);
													if(grandpaValues != null && grandpaValues.length>0) {
														try {
															pn.setName(grandpaValues[i]);
														} catch (Exception e1) {
															pn.setName(grandpaValues[0]);
														}
													}else {
														pn.setName(resp.grandpaValue());
													}
													if(grandpaDescriptions != null && grandpaDescriptions.length>0) {
														try {
															pn.setDescription(grandpaDescriptions[i]);
														} catch (Exception e1) {
															pn.setDescription(grandpaDescriptions[0]);
														}
													}else {
														pn.setDescription(resp.grandpaDescription());
													}
													if(grandpaIsArrays != null && grandpaIsArrays.length>0) {
														try {
															pn.setArray(grandpaIsArrays[i]);
														} catch (Exception e1) {
															pn.setArray(grandpaIsArrays[0]);
														}
													}else {
														pn.setArray(resp.grandpaIsArray());
													}
													respose.add(pn);
												}
											} catch (Exception e) {
												boolean b1 = true;
												for (ResposeModel res: respose) {
													if(res.getValue().equals(grandpaNames[0])) {
														b1 = false;
														break;
													}
													if(res.getModelModel()!=null) {
														List<PropertyModel> propertyModels = res.getModelModel().getPropertyModels();
														if(propertyModels != null && propertyModels.size()>0) {
															Iterator<PropertyModel> iterator = propertyModels.iterator();
															while(iterator.hasNext()) {
																PropertyModel pm = iterator.next();
																if(pm.getValue() != null && pm.getValue().equals(grandpaNames[0])) {
																	iterator.remove();
																	break;
																}
															}
														}
													}
												}
												if(b1) {
													ResposeModel pn = new ResposeModel();
													pn.setValue(grandpaNames[0]);
													if(grandpaValues != null && grandpaValues.length>0) {
														try {
															pn.setName(grandpaValues[i]);
														} catch (Exception e1) {
															pn.setName(grandpaValues[0]);
														}
													}else {
														pn.setName(resp.grandpaValue());
													}
													if(grandpaDescriptions != null && grandpaDescriptions.length>0) {
														try {
															pn.setDescription(grandpaDescriptions[i]);
														} catch (Exception e1) {
															pn.setDescription(grandpaDescriptions[0]);
														}
													}else {
														pn.setDescription(resp.grandpaDescription());
													}
													if(grandpaIsArrays != null && grandpaIsArrays.length>0) {
														try {
															pn.setArray(grandpaIsArrays[i]);
														} catch (Exception e1) {
															pn.setArray(grandpaIsArrays[0]);
														}
													}else {
														pn.setArray(resp.grandpaIsArray());
													}
													respose.add(pn);
												}
											}
										}else {
											//###################爷参######################
											if(resp.grandpaName()!=null && !"".equals(resp.grandpaName())) {
												boolean b1 = true;
												for (ResposeModel res: respose) {
													if(res.getValue().equals(resp.grandpaName())) {
														b1 = false;
														break;
													}
													if(res.getModelModel()!=null) {
														List<PropertyModel> propertyModels = res.getModelModel().getPropertyModels();
														if(propertyModels != null && propertyModels.size()>0) {
															Iterator<PropertyModel> iterator = propertyModels.iterator();
															while(iterator.hasNext()) {
																PropertyModel pm = iterator.next();
																if(pm.getValue() != null && pm.getValue().equals(resp.grandpaName())) {
																	iterator.remove();
																	break;
																}
															}
														}
													}
												}
												if(b1) {
													ResposeModel pn = new ResposeModel();
													pn.setValue(resp.grandpaName());
													pn.setName(resp.grandpaValue());
													pn.setDescription(resp.grandpaDescription());
													pn.setArray(resp.grandpaIsArray());
													respose.add(pn);
												}
											}
											//###################结束######################
										}

										respose.add(resposeModel);
									}
								}else {
									ResposeModel resposeModel = new ResposeModel();
									resposeModel.setDataType(resp.dataType());
									resposeModel.setDescription(resp.description());
									resposeModel.setName(resp.value());
									resposeModel.setArray(resp.isArray());
									//###################父参######################
									if(resp.parentName()!=null && !"".equals(resp.parentName())) {
										boolean bl = true;
										for (ResposeModel res: respose) {
											if(res.getValue().equals(resp.parentName())) {
												bl = false;
												break;
											}
											if(res.getModelModel()!=null) {
												List<PropertyModel> propertyModels = res.getModelModel().getPropertyModels();
												if(propertyModels != null && propertyModels.size()>0) {
													Iterator<PropertyModel> iterator = propertyModels.iterator();
													while(iterator.hasNext()) {
														PropertyModel pm = iterator.next();
														if(pm.getValue() != null && pm.getValue().equals(resp.parentName())) {
															iterator.remove();
															break;
														}
													}
												}
											}
										}
										if(bl) {
											ResposeModel pn = new ResposeModel();
											pn.setValue(resp.parentName());
											pn.setName(resp.parentValue());
											pn.setDescription(resp.parentDescription());
											pn.setArray(resp.parentIsArray());
											if(resp.grandpaName()!=null && !"".equals(resp.grandpaName())) {
												pn.setParentName(resp.grandpaName());
											}
											respose.add(pn);
										}
									}
									//###################爷参######################
									if(resp.grandpaName()!=null && !"".equals(resp.grandpaName())) {
										boolean b1 = true;
										for (ResposeModel res: respose) {
											if(res.getValue().equals(resp.grandpaName())) {
												b1 = false;
												break;
											}
											if(res.getModelModel()!=null) {
												List<PropertyModel> propertyModels = res.getModelModel().getPropertyModels();
												if(propertyModels != null && propertyModels.size()>0) {
													Iterator<PropertyModel> iterator = propertyModels.iterator();
													while(iterator.hasNext()) {
														PropertyModel pm = iterator.next();
														if(pm.getValue() != null && pm.getValue().equals(resp.grandpaName())) {
															iterator.remove();
															break;
														}
													}
												}
											}
										}
										if(b1) {
											ResposeModel pn = new ResposeModel();
											pn.setValue(resp.grandpaName());
											pn.setName(resp.grandpaValue());
											pn.setDescription(resp.grandpaDescription());
											pn.setArray(resp.grandpaIsArray());
											respose.add(pn);
										}
									}
									//###################结束######################
									resposeModel.setParentName(resp.parentName());
									resposeModel.setValue(resp.name());
									respose.add(resposeModel);
								}
							
							}
						}
						
						methodModel.setRequest(request);
						methodModel.setRespose(respose);
						methodModels.add(methodModel);
					}
					typeModel.setMethodModels(methodModels);
					typeModels.add(typeModel);
				}
			}
		}
		return typeModels;
	}
	
	/**
	 * 获取所有方法的请求方式，请求路径等相关信息
	 * @return
	 */
	public List<Map<String,Object>> getMethodURL() {
		RequestMappingHandlerMapping requestMappingHandlerMapping = applicationContext
				.getBean(RequestMappingHandlerMapping.class);
		// 获取url与类和方法的对应信息
		Map<RequestMappingInfo, HandlerMethod> map = requestMappingHandlerMapping.getHandlerMethods();
		List<Map<String,Object>> resultList = new ArrayList<Map<String, Object>>();
		for (Map.Entry<RequestMappingInfo, HandlerMethod> mappingInfoHandlerMethodEntry : map.entrySet()) {
			Map<String, Object> resultMap = new LinkedHashMap<String, Object>();

			RequestMappingInfo requestMappingInfo = mappingInfoHandlerMethodEntry.getKey();
			HandlerMethod handlerMethod = mappingInfoHandlerMethodEntry.getValue();

			resultMap.put("className", handlerMethod.getMethod().getDeclaringClass().getName()); // 类名
			Class<?>[] interfaces = handlerMethod.getMethod().getDeclaringClass().getInterfaces();
			List<String> interfacesNames = new ArrayList<String>();
			if (interfaces != null) {
				for (Class<?> c : interfaces) {// 基于接口继承模式,反向获取接口注解
					interfacesNames.add(c.getName());
				}
			}
			resultMap.put("interfacesNames",interfacesNames);
			Annotation[] parentAnnotations = handlerMethod.getBeanType().getAnnotations();
			for (Annotation annotation : parentAnnotations) {
				if (annotation instanceof RequestMapping) {
					RequestMapping requestMapping = (RequestMapping) annotation;
					if (null != requestMapping.value() && requestMapping.value().length > 0) {
						resultMap.put("classURL", requestMapping.value()[0]);// 类URL
					}
				}
			}
			resultMap.put("methodName", handlerMethod.getMethod().getName()); // 方法名
			PatternsRequestCondition p = requestMappingInfo.getPatternsCondition();
			for (String url : p.getPatterns()) {
				resultMap.put("methodURL", url);// 请求URL
			}
			RequestMethodsRequestCondition methodsCondition = requestMappingInfo.getMethodsCondition();
			for (RequestMethod requestMethod : methodsCondition.getMethods()) {
				resultMap.put("requestType", requestMethod.toString());// 请求方式：POST/PUT/GET/DELETE
			}
			resultList.add(resultMap);
		}
		return resultList;
	}
	
	
	/**
	 * 解析请求参数的LKAModel对象注解
	 * 
	 * @param modelCls
	 * @throws Exception
	 */
	public ParamModel analysisModel(Class<?> typeCls,String group) throws Exception {
		reqNum++; //防止递归死循环
		if(reqNum > 10) {
			reqNum = 0;
			return null;
		}
		if (!typeCls.isAnnotationPresent(LKAModel.class) && !typeCls.isAnnotationPresent(ApiModel.class)) {
			reqNum = 0;
			return null;
		}
		ParamModel pm = new ParamModel();
		// 获取model描述信息
		ModelModel modelModel = new ModelModel();
		if(typeCls.isAnnotationPresent(LKAModel.class)) {
			LKAModel lkaModel = typeCls.getAnnotation(LKAModel.class);
			modelModel.setValue(typeCls.getSimpleName());
			modelModel.setName(lkaModel.value());
			modelModel.setDescription(lkaModel.description());
		}else {
			ApiModel lkaModel = typeCls.getAnnotation(ApiModel.class);
			modelModel.setValue(typeCls.getSimpleName());
			modelModel.setName(lkaModel.value());
			modelModel.setDescription(lkaModel.description());
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
			List<Field> list = new ArrayList<>(Arrays.asList(fields));
			list.addAll(Arrays.asList(declaredField));
			arrays = list.toArray();
		}else {
			arrays = fields;
		}
		
		if (arrays != null && arrays.length > 0) {
			List<PropertyModel> propertyModels = new ArrayList<PropertyModel>();
			for (Object obj : arrays) {
				Field field = (Field)obj;
				if (!field.isAnnotationPresent(LKAProperty.class) && !field.isAnnotationPresent(ApiModelProperty.class))
					continue;
				boolean bool2=false;
				if(field.isAnnotationPresent(LKAProperty.class)) {
					LKAProperty param = field.getAnnotation(LKAProperty.class);
					if(param.hidden())continue;
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
									if(gs.length > 1 && gs[1].equals("n")) {
										bool2 = true;
									}
									bool = true;
									break;
								}
							}
						}
						if(!bool) continue;
					}
					Class<?> ctype = param.type();
					String pValue = field.getName();
					String pType = field.getType().getSimpleName();
					PropertyModel propertyModel = new PropertyModel();
					if (ctype.getName() != "java.lang.Object") {
						propertyModel = analysisProModel(ctype,group);
						if (propertyModel == null)
							continue;
						propertyModel.setArray(param.isArray());
						propertyModel.setValue(pValue);
						propertyModel.setName(param.value());
						propertyModel.setDescription(param.description());
						propertyModel.setDataType("");
						propertyModels.add(propertyModel);
					} else {
						propertyModel.setDataType(pType);
						propertyModel.setDescription(param.description());
						propertyModel.setName(param.value());
						if(bool2) {
							propertyModel.setRequired(false);
						}else {
							propertyModel.setRequired(param.required());
						}
						propertyModel.setParamType(param.paramType());
						propertyModel.setArray(param.isArray());
						propertyModel.setValue(pValue);
						propertyModel.setTestData(param.testData());
						propertyModels.add(propertyModel);
					}
				}else {
					ApiModelProperty param = field.getAnnotation(ApiModelProperty.class);
					if(param.hidden())continue;
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
									if(gs.length > 1 && gs[1].equals("n")) {
										bool2 = true;
									}
									bool = true;
									break;
								}
							}
						}
						if(!bool) continue;
					}
					Class<?> ctype = param.type();
					String pValue = field.getName();
					String pType = field.getType().getSimpleName();
					PropertyModel propertyModel = new PropertyModel();
					if (ctype.getName() != "java.lang.Object") {
						propertyModel = analysisProModel(ctype,group);
						if (propertyModel == null)
							continue;
						propertyModel.setArray(param.isArray());
						propertyModel.setValue(pValue);
						propertyModel.setName(param.value());
						propertyModel.setDescription(param.description());
						propertyModel.setDataType("");
						propertyModels.add(propertyModel);
					} else {
						propertyModel.setDataType(pType);
						propertyModel.setDescription(param.description());
						propertyModel.setName(param.value());
						if(bool2) {
							propertyModel.setRequired(false);
						}else {
							propertyModel.setRequired(param.required());
						}
						propertyModel.setParamType(param.paramType());
						propertyModel.setArray(param.isArray());
						propertyModel.setValue(pValue);
						propertyModel.setTestData(param.testData());
						propertyModels.add(propertyModel);
					}
				}
			}
			modelModel.setPropertyModels(propertyModels);
		}
		pm.setModelModel(modelModel);
		reqNum = 0;
		return pm;
	}

	/**
	 * 解析对象属性的LKAModel对象注解
	 * 
	 * @param modelCls
	 * @throws Exception
	 */
	public PropertyModel analysisProModel(Class<?> typeCls,String group) throws Exception {
		proNum++;
		if(proNum > 10) {
			proNum = 0;
			return null;
		}
		if (!typeCls.isAnnotationPresent(LKAModel.class) && !typeCls.isAnnotationPresent(ApiModel.class)) {
			proNum = 0;
			return null;
		}
		PropertyModel pm = new PropertyModel();
		// 获取model描述信息
		ModelModel modelModel = new ModelModel();
		if(typeCls.isAnnotationPresent(LKAModel.class)) {
			LKAModel lkaModel = typeCls.getAnnotation(LKAModel.class);
			modelModel.setValue(typeCls.getSimpleName());
			modelModel.setName(lkaModel.value());
			modelModel.setDescription(lkaModel.description());
		}else {
			ApiModel lkaModel = typeCls.getAnnotation(ApiModel.class);
			modelModel.setValue(typeCls.getSimpleName());
			modelModel.setName(lkaModel.value());
			modelModel.setDescription(lkaModel.description());
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
			List<Field> list = new ArrayList<>(Arrays.asList(fields));
			list.addAll(Arrays.asList(declaredField));
			arrays = list.toArray();
		}else {
			arrays = fields;
		}
		
		if (arrays != null && arrays.length > 0) {
			List<PropertyModel> propertyModels = new ArrayList<PropertyModel>();
			for (Object obj: arrays) {
				Field field = (Field)obj;
				if (!field.isAnnotationPresent(LKAProperty.class) && !field.isAnnotationPresent(ApiModelProperty.class))
					continue;
				boolean bool2=false;
				if(field.isAnnotationPresent(LKAProperty.class)) {
					LKAProperty param = field.getAnnotation(LKAProperty.class);
					if(param.hidden())continue;
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
									if(gs.length > 1 && gs[1].equals("n")) {
										bool2 = true;
									}
									bool = true;
									break;
								}
							}
						}
						if(!bool) continue;
					}
					Class<?> ctype = param.type();
					String pValue = field.getName();
					String pType = field.getType().getSimpleName();
					PropertyModel propertyModel = new PropertyModel();
					if (ctype.getName() != "java.lang.Object") {
						propertyModel = analysisProModel(ctype,group);
						if (propertyModel == null)
							continue;
						propertyModel.setArray(param.isArray());
						propertyModel.setValue(pValue);
						propertyModel.setName(param.value());
						propertyModel.setDescription(param.description());
						propertyModel.setDataType("");
						propertyModels.add(propertyModel);
					} else {
						propertyModel.setDataType(pType);
						propertyModel.setDescription(param.description());
						propertyModel.setName(param.value());
						if(bool2) {
							propertyModel.setRequired(false);
						}else {
							propertyModel.setRequired(param.required());
						}
						propertyModel.setParamType(param.paramType());
						propertyModel.setArray(param.isArray());
						propertyModel.setValue(pValue);
						propertyModel.setTestData(param.testData());
						propertyModels.add(propertyModel);
					}
				}else {
					ApiModelProperty param = field.getAnnotation(ApiModelProperty.class);
					if(param.hidden())continue;
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
									if(gs.length > 1 && gs[1].equals("n")) {
										bool2 = true;
									}
									bool = true;
									break;
								}
							}
						}
						if(!bool) continue;
					}
					Class<?> ctype = param.type();
					String pValue = field.getName();
					String pType = field.getType().getSimpleName();
					PropertyModel propertyModel = new PropertyModel();
					if (ctype.getName() != "java.lang.Object") {
						propertyModel = analysisProModel(ctype,group);
						if (propertyModel == null)
							continue;
						propertyModel.setArray(param.isArray());
						propertyModel.setValue(pValue);
						propertyModel.setName(param.value());
						propertyModel.setDescription(param.description());
						propertyModel.setDataType("");
						propertyModels.add(propertyModel);
					} else {
						propertyModel.setDataType(pType);
						propertyModel.setDescription(param.description());
						propertyModel.setName(param.value());
						if(bool2) {
							propertyModel.setRequired(false);
						}else {
							propertyModel.setRequired(param.required());
						}
						propertyModel.setParamType(param.paramType());
						propertyModel.setArray(param.isArray());
						propertyModel.setValue(pValue);
						propertyModel.setTestData(param.testData());
						propertyModels.add(propertyModel);
					}
				}
				
			}
			modelModel.setPropertyModels(propertyModels);
		}
		pm.setModelModel(modelModel);
		proNum = 0;
		return pm;
	}

	/**
	 *  解析响应参数的LKAModel对象注解
	 * 
	 * @param modelCls
	 * @throws Exception
	 */
	public ResposeModel analysisResModel(Class<?> typeCls,String group) throws Exception {
		respNum++;
		if(respNum > 10) {
			respNum = 0;
			return null;
		}
		if (!typeCls.isAnnotationPresent(LKAModel.class) && !typeCls.isAnnotationPresent(ApiModel.class)) {
			respNum = 0;
			return null;
		}
		ResposeModel rm = new ResposeModel();
		
		// 获取model描述信息
		ModelModel modelModel = new ModelModel();
		if(typeCls.isAnnotationPresent(LKAModel.class)) {
			LKAModel lkaModel = typeCls.getAnnotation(LKAModel.class);
			modelModel.setValue(typeCls.getSimpleName());
			modelModel.setName(lkaModel.value());
			modelModel.setDescription(lkaModel.description());
		}else {
			ApiModel lkaModel = typeCls.getAnnotation(ApiModel.class);
			modelModel.setValue(typeCls.getSimpleName());
			modelModel.setName(lkaModel.value());
			modelModel.setDescription(lkaModel.description());
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
			List<Field> list = new ArrayList<>(Arrays.asList(fields));
			list.addAll(Arrays.asList(declaredField));
			arrays = list.toArray();
		}else {
			arrays = fields;
		}
		
		if (arrays != null && arrays.length > 0) {
			List<PropertyModel> propertyModels = new ArrayList<PropertyModel>();
			for (Object obj : arrays) {
				Field field =  (Field)obj;
				if (!field.isAnnotationPresent(LKAProperty.class) && !field.isAnnotationPresent(ApiModelProperty.class))
					continue;
				boolean bool2 = false;
				if(field.isAnnotationPresent(LKAProperty.class)) {
					LKAProperty param = field.getAnnotation(LKAProperty.class);
					if(param.hidden())continue;
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
									if(gs.length > 1 && gs[1].equals("n")) {
										bool2 = true;
									}
									bool = true;
									break;
								}
							}
						}
						if(!bool) continue;
					}
					Class<?> ctype = param.type();
					String pValue = field.getName();
					String pType = field.getType().getSimpleName();
					PropertyModel propertyModel = new PropertyModel();
					if (ctype.getName() != "java.lang.Object") {
						propertyModel = analysisProModel(ctype,group);
						if (propertyModel == null)
							continue;
						propertyModel.setArray(param.isArray());
						propertyModel.setValue(pValue);
						propertyModel.setName(param.value());
						propertyModel.setDescription(param.description());
						propertyModel.setDataType("");
						propertyModels.add(propertyModel);
					} else {
						propertyModel.setDataType(pType);
						propertyModel.setDescription(param.description());
						propertyModel.setName(param.value());
						if(bool2) {
							propertyModel.setRequired(false);
						}else {
							propertyModel.setRequired(param.required());
						}
						propertyModel.setParamType(param.paramType());
						propertyModel.setArray(param.isArray());
						propertyModel.setValue(pValue);
						propertyModel.setTestData(param.testData());
						propertyModels.add(propertyModel);
					}
				}else {
					ApiModelProperty param = field.getAnnotation(ApiModelProperty.class);
					if(param.hidden())continue;
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
									if(gs.length > 1 && gs[1].equals("n")) {
										bool2 = true;
									}
									bool = true;
									break;
								}
							}
						}
						if(!bool) continue;
					}
					Class<?> ctype = param.type();
					String pValue = field.getName();
					String pType = field.getType().getSimpleName();
					PropertyModel propertyModel = new PropertyModel();
					if (ctype.getName() != "java.lang.Object") {
						propertyModel = analysisProModel(ctype,group);
						if (propertyModel == null)
							continue;
						propertyModel.setArray(param.isArray());
						propertyModel.setValue(pValue);
						propertyModel.setName(param.value());
						propertyModel.setDescription(param.description());
						propertyModel.setDataType("");
						propertyModels.add(propertyModel);
					} else {
						propertyModel.setDataType(pType);
						propertyModel.setDescription(param.description());
						propertyModel.setName(param.value());
						if(bool2) {
							propertyModel.setRequired(false);
						}else {
							propertyModel.setRequired(param.required());
						}
						propertyModel.setParamType(param.paramType());
						propertyModel.setArray(param.isArray());
						propertyModel.setValue(pValue);
						propertyModel.setTestData(param.testData());
						propertyModels.add(propertyModel);
					}
				}
				
			}
			modelModel.setPropertyModels(propertyModels);
		}
		rm.setModelModel(modelModel);
		respNum = 0;
		return rm;
	}
	
	/**
	 * 添加字段备注信息
	 * @param value 字段值
	 * @param type 字段类型(1.请求参数 2.响应参数)
	 * @param url 请求地址
	 * @param modaltype 修改类型(1.添加 2.修改 3.删除 4.other)
	 * @param content 备注信息
	 * @return
	 */
	@PostMapping("addParamInfo")
	public String addParamInfo(String value,String type,String url,String modaltype,String content){
		File file = new File("lkadParamInfo.properties");
		FileOutputStream outStream = null;
        try {
        	outStream = new FileOutputStream(file,true); 
            Properties prop= new Properties();
             prop.setProperty(url+"."+type+"."+value,modaltype+"-"+content);
             prop.store(outStream, null);
             return "操作成功";
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        	if(outStream != null) {
        		try {
					outStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
        	}
        }
		return "操作失败";
	}
	
	/**
	 * 删除字段备注信息 
	 * @param value 字段值
	 * @param type type 字段类型(1.请求参数 2.响应参数)
	 * @param url 请求地址
	 * @return
	 */
	@PostMapping("delParamInfo")
	public String delParamInfo(String value,String type,String url){
		File file = new File("lkadParamInfo.properties");
		FileOutputStream outStream = null;
		FileInputStream inStream = null;
        try {
        	Properties prop= new Properties();
        	inStream = new FileInputStream(file);
            prop.load(inStream);
            prop.remove(url+"."+type+"."+value);
            outStream = new FileOutputStream(file);
            prop.store(outStream, null);
            return "操作成功";
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        	if(outStream != null) {
        		try {
					outStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
        	}
        	if(inStream != null) {
        		try {
        			inStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
        	}
        }
		return "操作失败";
	}
	
	/**
	 * 获取字段备注信息 
	 * @return
	 */
	@GetMapping("getParamInfo")
	public Map<Object, Object> getParamInfo(){
		Map<Object, Object> map = new HashMap<Object, Object>();
		File file = new File("lkadParamInfo.properties");
		FileInputStream inStream = null;
        try {
        	inStream = new FileInputStream(file); 
            Properties prop = new Properties();
            prop.load(inStream);
            Set<Map.Entry<Object, Object>> entrySet = prop.entrySet();//返回的属性键值对实体
            for (Map.Entry<Object, Object> entry : entrySet) {
                map.put(entry.getKey(),entry.getValue());
            }
            return map;
        } catch (Exception e) {
           // e.printStackTrace();
        } finally {
        	if(inStream != null) {
        		try {
        			inStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
        	}
        }       
		return null;
	}
	
	/** 
     * 循环向上转型, 获取对象的 DeclaredField 
     * @param object : 子类对象 
     * @param fieldName : 父类中的属性名 
     * @return 父类中的属性对象 
     */  
    public Field[] getDeclaredField(Object object){ 
        Class<?> clazz = object.getClass();
        if(clazz != Object.class){
            try {
            	clazz = clazz.getSuperclass();
            	Field[] declaredFields = clazz.getDeclaredFields();
                return declaredFields;
            } catch (Exception e) {  
                //这里甚么都不要做！并且这里的异常必须这样写，不能抛出去。  
                //如果这里的异常打印或者往外抛，则就不会执行clazz = clazz.getSuperclass(),最后就不会进入到父类中了  
            }   
        }  
        return null;  
    }     

}
