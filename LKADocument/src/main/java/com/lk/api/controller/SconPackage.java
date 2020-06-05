package com.lk.api.controller;

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;
import java.util.zip.ZipInputStream;

public class SconPackage implements SconPackageInterface{
   // private Logger logger = LoggerFactory.getLogger(SconPackage.class);
    private String basePackage;
    private ClassLoader cl;

    /**
     * 初始化
     * @param basePackage
     */
    public SconPackage(String basePackage) {
        this.basePackage = basePackage;
        this.cl = getClass().getClassLoader();
    }
    public SconPackage(String basePackage, ClassLoader cl) {
        this.basePackage = basePackage;
        this.cl = cl;
    }
    /**
     *获取指定包下的所有字节码文件的全类名
     */
    public List<String> getFullyQualifiedClassNameList() throws IOException {
        //logger.info("开始扫描包{}下的所有类", basePackage);
        return doScan(basePackage, new ArrayList<String>());
    }

    /**
     *doScan函数
     * @param basePackage
     * @param nameList
     * @return
     * @throws IOException
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

    private String toFullyQualifiedName(String shortName, String basePackage) {
        StringBuilder sb = new StringBuilder(basePackage);
        sb.append('.');
        sb.append(StringUtil.trimExtension(shortName));
        return sb.toString();
    }

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

    private List<String> readFromDirectory(String path) {
    	if(path == null) return new ArrayList<String>();
        File file = new File(path);
        String[] names = file.list();

        if (null == names) {
            return null;
        }

        return Arrays.asList(names);
    }

    private boolean isClassFile(String name) {
    	if(name == null) return false;
        return name.endsWith(".class");
    }

    private boolean isJarFile(String name) {
    	if(name == null) return false;
        return name.endsWith(".jar");
    }

    /**
     * For test purpose.
     */
    public static void main(String[] args) throws Exception {
    	/*InputStream is = new FileInputStream("G:\\yqz.jar");
    	System.out.println(is.available());
    	//JarInputStream jar = new JarInputStream(is,false);
    	ZipInputStream jar = new ZipInputStream(is);
    	jar.available();
		//JarEntry entry = jar.getNextJarEntry();
        System.out.println("96->"+"---->"+jar.available()+"---->");*/
    	
    	JarFile jarFile = new JarFile(new File("G:/yqz.jar"));
        Enumeration<JarEntry> entries = jarFile.entries();
        while (entries.hasMoreElements()) {

            JarEntry entry = entries.nextElement();
            String entryName = entry.getName();
            //读取文件后缀名为.java的文件
            if (!entry.isDirectory() && entryName.endsWith(".class")){
            	//System.out.println(entryName); 
            }

        }
    }
}