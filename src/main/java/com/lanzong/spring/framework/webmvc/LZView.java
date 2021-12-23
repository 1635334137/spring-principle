package com.lanzong.spring.framework.webmvc;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.RandomAccess;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LZView {
    public static final String DEFAULT_CONTENT_TYPE = "text/html;charset=utf-8";
    private File viewFile;

    public LZView(File viewFile) {
        this.viewFile = viewFile;
    }
    public String getContentType(){
        return DEFAULT_CONTENT_TYPE;
    }

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
                    line = matcher.replaceFirst(makeStringForRegExp(paramValue.toString()));
                    matcher = pattern.matcher(line);
                }
                sb.append(line);
            }
        }finally {
            ra.close();
        }
        resp.setCharacterEncoding("utf-8");
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
