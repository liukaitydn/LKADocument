package com.lk.api.controller;

import java.net.URL;

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
     * @param url url
     * @return string
     */
    public static String getRootPath(URL url) {
    	if(url == null) {
    		return null;
    	}
        String fileUrl = url.getFile();
        int pos = fileUrl.indexOf('!');

        if (-1 == pos) {
            return fileUrl;
        }

        return fileUrl.substring(5, pos);
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
