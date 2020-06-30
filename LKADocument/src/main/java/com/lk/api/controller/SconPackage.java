package com.lk.api.controller;


import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * 扫描包工具类
 * @author liukai
 *
 */
public class SconPackage implements SconPackageInterface{
    private String basePackage;
    private ClassLoader cl;

    /**
     * 初始化1
     * @param basePackage 基础包名
     */
    public SconPackage(String basePackage) {
        this.basePackage = basePackage;
        this.cl = getClass().getClassLoader();
    }
    
    /**
     * 初始化2
     * @param basePackage 基础包名
     * @param cl 类装载器
     */
    public SconPackage(String basePackage, ClassLoader cl) {
        this.basePackage = basePackage;
        this.cl = cl;
    }
    
    /**
     * 获取指定包下的所有字节码文件的全类名
     * @return list 字节码文件名集合
     */
    public List<String> getFullyQualifiedClassNameList() throws IOException {
        //logger.info("开始扫描包{}下的所有类", basePackage);
        return doScan(basePackage, new ArrayList<String>());
    }

    /**
     *doScan函数
     * @param basePackage 基础包名
     * @param nameList 名称列表
     * @return list 字节码文件名集合
     * @throws IOException 异常
     */
    private List<String> doScan(String basePackage, List<String> nameList) throws IOException {
        String splashPath = StringUtil.dotToSplash(basePackage);
        URL url = cl.getResource(splashPath);   //file:/D:/WorkSpace/java/ScanTest/target/classes/com/scan
        String filePath = StringUtil.getRootPath(url);
        List<String> names = null; // contains the name of the class file. e.g., Apple.class will be stored as "Apple"
        if (isJarFile(filePath)) {// 先判断是否是jar包，如果是jar包，通过JarInputStream产生的JarEntity去递归查询所有类
        	names = readFromJarFile(filePath, splashPath);
            for (String name : names) {
                if (isClassFile(name)) {
                	nameList.add(name.substring(0,name.lastIndexOf(".class")));
                }else {
                    doScan(name, nameList);
                }
            }
        } else {
            names = readFromDirectory(filePath);
            for (String name : names) {
                if (isClassFile(name)) {
                    nameList.add(toFullyQualifiedName(name, basePackage));
                } else {
                    doScan(basePackage + "." + name, nameList);
                }
            }
        }
        
       /* if (logger.isDebugEnabled()) {
            for (String n : nameList) {
                logger.debug("找到{}", n);
            }
        }*/
        return nameList;
    }
    
    /**
     * 文件路径格式转换
     * @param shortName shortName
     * @param basePackage basePackage
     * @return string string
     */
    private String toFullyQualifiedName(String shortName, String basePackage) {
        StringBuilder sb = new StringBuilder(basePackage);
        sb.append('.');
        sb.append(StringUtil.trimExtension(shortName));
        return sb.toString();
    }
    
    /**
     * 读取jar里面的文件
     * @param jarPath jar包名
     * @param splashedPackageName jar包路径
     * @return list 集合
     * @throws IOException 异常
     */
    private List<String> readFromJarFile(String jarPath, String splashedPackageName) throws IOException {
    	JarFile jarFile = new JarFile(new File(jarPath));
        Enumeration<JarEntry> entries = jarFile.entries();
        List<String> nameList = new ArrayList<String>();
        while (entries.hasMoreElements()) {
            JarEntry entry = entries.nextElement();
            String name = entry.getName();
            if (name.contains(splashedPackageName) && isClassFile(name)) {
            	name = name.substring(name.indexOf(splashedPackageName)).replaceAll("\\/",".");
                nameList.add(name);
            }
        }
        return nameList;
    }
    
    /**
     * 读取指定目录里的文件
     * @param path 路径
     * @return list 集合
     */
    private List<String> readFromDirectory(String path) {
    	if(path == null) return new ArrayList<String>();
        File file = new File(path);
        String[] names = file.list();

        if (null == names) {
            return null;
        }

        return Arrays.asList(names);
    }
    
    /**
     * 判断是否是字节码文件
     * @param name 文件名
     * @return boolean
     */
    private boolean isClassFile(String name) {
    	if(name == null) return false;
        return name.endsWith(".class");
    }
    
    /**
     * 判断是否是jar包文件
     * @param name
     * @return boolean
     */
    private boolean isJarFile(String name) {
    	if(name == null) return false;
        return name.endsWith(".jar");
    }
}