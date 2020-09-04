package com.lk.api.controller;

import java.io.IOException;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.BaseFont;

/**
 * PDF工具类
 * @author liukai
 *
 */
public class PdfFontUtils {

    // 字体
    private static BaseFont baseFont = null;
    
    static{
        try {
            /**
             * 设置字体
             * 
             * windows路径字体
             * FONT_TYPE=C:/Windows/fonts/simhei.ttf
             * linux路径字体 宋体 (如果没有这个字体文件，就将windows的字体传上去)
             * FONT_TYPE=/usr/share/fonts/win/simsun.ttc
             */
            //可以用配置文件读取
            //获取配置
            //PropertiesLoader pl = new PropertiesLoader("/config/config.properties");  
            //拼接文件web访问路径
            //String FONT_TYPE = pl.getProperty("FONT_TYPE");  
            //解决中文问题  幼圆
        	//String path = PdfFontUtils.class.getResource("/simsun.ttc").getPath();
        	//System.out.println(path);
        	//String FONT_TYPE="/simsun.ttc";
        	String FONT_TYPE="C:/Windows/fonts/simsun.ttc";
        	if(System.getProperty("os.name").toLowerCase().contains("linux")) {
        		FONT_TYPE="/usr/share/fonts/win/simsun.ttc";
        	}if(System.getProperty("os.name").toLowerCase().contains("mac")) {
        		FONT_TYPE="/System/Library/Fonts/simsun.ttc";
        	}
            baseFont = BaseFont.createFont(FONT_TYPE+",1", BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
            
    /**
     * 	文档超级  排版
     * @param type 1-标题 2-标题一  3-标题二 4-标题三  5-正文  6-左对齐
     * @param text 文本
     * @return Paragraph 对象
     */
    public static Paragraph getFont(int type, String text){
        Font font = new Font(baseFont);
        if(1 == type){//1-标题
            font.setSize(16f);
            font.setStyle(Font.BOLD);
        } else if(2 == type){//2-标题一
            font.setSize(12f);
            font.setStyle(Font.BOLD);
        } else if(3 == type){//3-标题二
            font.setSize(8.5f);
            font.setStyle(Font.BOLD);
        } else if(4 == type){//4-标题三
            font.setSize(8.5f);
        } else if(5 == type){//5-正文
            font.setSize(8.5f);
        } else if(6 == type){//6-左对齐
            font.setSize(8.5f);
        } else {
            font.setSize(8.5f);//默认大小
        }
        //注： 字体必须和 文字一起new
        Paragraph paragraph = new Paragraph(text, font);
        if(1 == type){
            paragraph.setAlignment(Paragraph.ALIGN_CENTER);//居中
            paragraph.setSpacingBefore(10f);//上间距
            paragraph.setSpacingAfter(10f);//下间距
        } else if(2 == type){//2-标题一
            paragraph.setAlignment(Element.ALIGN_JUSTIFIED); //默认
            paragraph.setSpacingBefore(2f);//上间距
            paragraph.setSpacingAfter(2f);//下间距
        } else if(3 == type){
            paragraph.setSpacingBefore(2f);//上间距
            paragraph.setSpacingAfter(1f);//下间距
        } else if(4 == type){//4-标题三
            //paragraph.setAlignment(Element.ALIGN_RIGHT);//右对齐 
            paragraph.setSpacingBefore(2f);//上间距
            paragraph.setSpacingAfter(2f);//下间距
        } else if(5 == type){
            paragraph.setAlignment(Paragraph.ALIGN_CENTER); 
            paragraph.setFirstLineIndent(24);//首行缩进
            paragraph.setSpacingBefore(1f);//上间距
            paragraph.setSpacingAfter(1f);//下间距
        } else if(6 == type){//左对齐
            paragraph.setAlignment(Element.ALIGN_LEFT); 
            paragraph.setSpacingBefore(1f);//上间距
            paragraph.setSpacingAfter(1f);//下间距
        }
        //paragraph.setIndentationLeft(50);//整体缩进左边
        //paragraph.setFirstLineIndent(40);//首行缩进
        return paragraph;
    }
}