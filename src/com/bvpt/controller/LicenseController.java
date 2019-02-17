/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bvpt.controller;

import com.bvpt.common.AppConst;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;
import com.bvpt.common.CommonModel;
import com.bvpt.common.JsonParserUtil;
import com.bvpt.data.LicenseItem;
import com.bvpt.model.LicenseModel;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
/**
 *
 * @author tamvh
 */
public class LicenseController extends HttpServlet {
    protected final Logger logger = Logger.getLogger(this.getClass());
    private static final Gson gson = new Gson();
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        handle(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        handle(req, resp);
    }
    
    private void handle(HttpServletRequest req, HttpServletResponse resp) {
        try {
            processs(req, resp);
        } catch (Exception ex) {
            logger.error(getClass().getSimpleName() + ".handle: " + ex.getMessage(), ex);
        }
    }

    private void processs(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String cmd = req.getParameter("cm") != null ? req.getParameter("cm") : "";
        String data = req.getParameter("dt") != null ? req.getParameter("dt") : "";
        String content = "";
            
        CommonModel.prepareHeader(resp, CommonModel.HEADER_JS);
        
        switch (cmd) {            
            case "check":
                content = checkLicense(data);
                break;
            case "register":
                content = registerLicense(data);
                break;   
        }
        
        CommonModel.out(content, resp);
    }
    
    private String checkLicense(String data) {
        int ret = AppConst.ERROR_GENERIC;
        String content = CommonModel.FormatResponse(ret, "Unknown");
        
        try {
            JsonObject jsonObject = JsonParserUtil.parseJsonObject(data);
            if (jsonObject == null) {
                return CommonModel.FormatResponse(ret, "Invalid parameter");
            } 
            String appCode = jsonObject.get("appcode").getAsString();
            String checksum = jsonObject.get("checksum").getAsString();

            if (appCode.isEmpty() || checksum.isEmpty()) {
                return CommonModel.FormatResponse(ret, "Invalid parameter");
            }
            LicenseItem licenseItem = LicenseModel.getInstance().getLicense(appCode);
            if(licenseItem != null) {
                return CommonModel.FormatResponse(AppConst.NO_ERROR, AppConst.SUCCESS_STRING, JsonParserUtil.parseJsonObject(gson.toJson(licenseItem)));
            }
        } catch (IOException ex) {
            logger.error(getClass().getSimpleName() + ".checkLicense: " + ex.getMessage(), ex);
            return CommonModel.FormatResponse(ret, ex.getMessage());
        }
        
        return content;
    }
    
    private String registerLicense(String data) {
        int ret = AppConst.ERROR_GENERIC;        
        try {
            JsonObject jsonObject = JsonParserUtil.parseJsonObject(data);
            if (jsonObject == null) {
                return CommonModel.FormatResponse(ret, "Invalid parameter");
            } 
            String appCode = jsonObject.get("appcode").getAsString();
            String licCode = jsonObject.get("liccode").getAsString();
            int regType = jsonObject.get("regtype").getAsInt();
            String regUser = jsonObject.get("regUser").getAsString();
            String checksum = jsonObject.get("checksum").getAsString();

            if (appCode.isEmpty() || licCode.isEmpty() || regType <= 0 || regUser.isEmpty() || checksum.isEmpty()) {
                return CommonModel.FormatResponse(ret, "Invalid parameter");
            }
            int reglicense = LicenseModel.getInstance().regLicense(appCode, licCode, regType, regUser);
            if(reglicense > 0) {
                return CommonModel.FormatResponse(AppConst.NO_ERROR, AppConst.SUCCESS_STRING);
            } else {
                return CommonModel.FormatResponse(AppConst.ERROR_GENERIC, AppConst.FAILE_STRING);
            }
        } catch (IOException ex) {
            logger.error(getClass().getSimpleName() + ".checkLicense: " + ex.getMessage(), ex);
            return CommonModel.FormatResponse(ret, ex.getMessage());
        }        
    }
}
