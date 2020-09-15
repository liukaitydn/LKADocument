package com.lk.api.controller;


/**
 * 字符串处理工具类
 * @author liukai
 *
 */
public class StringUtil {
	
	/**
	 * 私有构造方法
	 */
    private StringUtil() {

    }
    
    /**
     * 获取根路径
     * @param path path
     * @return string
     */
    public static String getRootPath(String path) {
    	if(path == null) {
    		return null;
    	}
        int pos = path.indexOf('!');

        if (-1 == pos) {
            return path;
        }

        return path.substring(5, pos);
    }

    /**
     * dotToSplash
     * @param name 名称
     * @return string
     */
    public static String dotToSplash(String name) {
        return name.replaceAll("\\.", "/");
    }

    /**
     * trimExtension
     * @param name 名称
     * @return string
     */
    public static String trimExtension(String name) {
        int pos = name.indexOf('.');
        if (-1 != pos) {
            return name.substring(0, pos);
        }

        return name;
    }

    /**
     * trimURI
     * @param uri uri
     * @return string
     */
    public static String trimURI(String uri) {
        String trimmed = uri.substring(1);
        int splashIndex = trimmed.indexOf('/');

        return trimmed.substring(splashIndex);
    }
}
