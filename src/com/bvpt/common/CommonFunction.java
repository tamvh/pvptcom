/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bvpt.common;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author diepth
 */
public class CommonFunction {
    
    protected static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(CommonFunction.class);
    private static String _currentDayFormat = "";
    public static final String KEY_TOKEN_LOGIN = "token_login";
    public static final String KEY_USERNAME = "username";
    
    public static final byte HEADER_HTML = 0;
    public static final byte HEADER_JS = 1;
    public static final byte HEADER_TEXT_PLAIN = 2;

    public static String getCurrentDateTimeString() {
        Calendar cal = new GregorianCalendar(TimeZone.getTimeZone("GMT+7"));
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        fmt.setCalendar(cal);
        String currDateTime = fmt.format(cal.getTimeInMillis());
        
        return currDateTime;
    }
    
    public static long getCurrentDateTimeNum() {
        Calendar cal = new GregorianCalendar(TimeZone.getTimeZone("GMT+7"));
        return cal.getTimeInMillis();
    }
    
    public static String getCurrentDayFormat() {
        long currentTime = getCurrentDateTimeNum();
        SimpleDateFormat fmt = new SimpleDateFormat("yyMMdd");
        _currentDayFormat = fmt.format(currentTime);        
        return _currentDayFormat;
    }
    
    public static Map<String, String> convertQueryMap(Map<String, String[]> paramMap) {
        Map<String, String> tempMap = new HashMap<>();
        paramMap.forEach( (key, values) -> {
            if (values.length == 0) {
                tempMap.put(key, "");
            } else {
                tempMap.put(key, values[0]);
            }
        });
        return tempMap;
    }
    
    public static void deleteSession(HttpServletRequest req) {
        
        try {
            HttpSession session = req.getSession(true);            
            String sid = (String)session.getAttribute(KEY_TOKEN_LOGIN);
            if(sid != null){
                session.removeAttribute(sid);
            }
            
            session.removeAttribute(KEY_TOKEN_LOGIN);
        } catch (Exception ex) {
            logger.info("Ex deleteSession: " + ex.toString());
        }
    }
    
    public static String getUserSession(HttpServletRequest req) {
        
        String ret = null;        
        try {
            HttpSession session = req.getSession(true);
            String sid = (String)session.getAttribute(KEY_TOKEN_LOGIN);
            if (sid != null) {
                String usernameSession = (String)session.getAttribute(sid);
                if(usernameSession != null)
                    ret = usernameSession;
            }
        } catch (Exception ex) {
            
        }
        
        return ret;
    }
    
    public static void setSession(HttpServletRequest req, String username, String sid) {
        
        try {
            HttpSession session = req.getSession(true);
            session.setAttribute(KEY_TOKEN_LOGIN, sid);
            session.setAttribute(sid, username);
        } catch (Exception ex) {
            
        }
    }
    
    public static String toMD5(String data) {
        
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
            byte[] array = md.digest(data.getBytes());
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < array.length; ++i) {
                sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1, 3));
            }
            return sb.toString();
        } catch (Exception ex) {
            return null;
        }   
    }
    
    public static String getStringCurrentTimeMillis() {
        
        Calendar cal = new GregorianCalendar(TimeZone.getTimeZone("GMT+7"));
        return Long.toString(cal.getTimeInMillis());
    }
    
    public static long getCurrentTimeMillis() {
        
        Calendar cal = new GregorianCalendar(TimeZone.getTimeZone("GMT+7"));
        return cal.getTimeInMillis();
    }
    
    public static long getTimeMillis( String strDateTime) {
       
       Calendar cal = new GregorianCalendar(TimeZone.getTimeZone("GMT+7"));
       SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
       fmt.setCalendar(cal);
       
       try {
           Date date = fmt.parse(strDateTime);
           return date.getTime();
       } catch (Exception ex) {
           
       }
       
       return 0;
    }
    
    public static Boolean checkSession(HttpServletRequest req) {
        
        Boolean ret = false;
        try {
            HttpSession session = req.getSession(true);
            String sid = (String)session.getAttribute(KEY_TOKEN_LOGIN);
            if (sid != null) {
                ret = true;
            }
        } catch (Exception ex) {
            
        }
        
        return ret;
    }
    
    public static void prepareHeader(HttpServletResponse resp, byte type) {
        resp.setCharacterEncoding("utf-8");
        if (type == HEADER_HTML) {
            resp.setContentType("text/html; charset=utf-8");
        } else if (type == HEADER_JS) {
            resp.setContentType("text/javascript; charset=utf-8");
        } else if (type == HEADER_TEXT_PLAIN) {
            resp.setContentType("text/plain; charset=utf-8");
        }
        String appName = Config.getParam("static", "app_name");
        resp.addHeader("Server", appName);
        resp.setHeader("Access-Control-Allow-Origin", "*");
        resp.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS");
        resp.setHeader("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept, Authorization");
        resp.setHeader("Access-Control-Max-Age", "86400");

    }
}
