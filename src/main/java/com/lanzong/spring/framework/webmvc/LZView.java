package com.lanzong.spring.framework.webmvc;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//自定义模板解析引擎，每个页面可以有不同的处理引擎，只要页面名称和引擎对应起来
public class LZView {
    public static final String DEFAULT_CONTENT_TYPE = "text/html;charset=utf-8";
    private File viewFile;

    public LZView(File viewFile) {
        this.viewFile = viewFile;
    }
    public String getContentType(){
        return DEFAULT_CONTENT_TYPE;
    }

    //完成对模板的渲染
    public void render(Map<String,?> model, HttpServletRequest req, HttpServletResponse resp)throws Exception{
        StringBuffer sb = new StringBuffer();
        RandomAccessFile ra = new RandomAccessFile(this.viewFile,"r");

        try {
            String line = null;
            while (null != (line = ra.readLine())){
                line = new String(line.getBytes(StandardCharsets.ISO_8859_1),StandardCharsets.UTF_8);
                Pattern pattern = Pattern.compile("￥\\{[^\\}]+\\}",Pattern.CASE_INSENSITIVE);
                Matcher matcher = pattern.matcher(line);

                while (matcher.find()){
                    String paramName = matcher.group();
                    paramName = paramName.replaceAll("￥\\{|\\}","");
                    Object paramValue = model.get(paramName);
                    if(null == paramValue){continue;}
                    //把￥{}中间的字符串取出来
                    line = matcher.replaceFirst(makeStringForRegExp(paramValue.toString()));
                    matcher = pattern.matcher(line);
                }
                sb.append(line);
            }
        }finally {
            ra.close();
        }
        resp.setCharacterEncoding("utf-8");
        //输出到页面
        resp.getWriter().write(sb.toString());
    }

    private String makeStringForRegExp(String str) {
        return str.replace("\\","\\\\").replace("*","\\*")
                .replace("+","\\+").replace("|","\\|")
                .replace("{","\\{").replace("}","\\}")
                .replace("(","\\(").replace(")","\\)")
                .replace("^","\\^").replace("$","\\$")
                .replace("[","\\[").replace("]","\\]")
                .replace("?","\\?").replace(",","\\,")
                .replace(".","\\.").replace("&","\\&");


    }
}
