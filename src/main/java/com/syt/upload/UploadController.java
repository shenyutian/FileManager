package com.syt.upload;

import cn.hutool.core.util.RandomUtil;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;

import static com.syt.upload.MD5utils.*;

/**
 * @author YuTian
 * @date 2020/5/12 7:03 下午
 */
@RestController
public class UploadController {

    @Value("${server.port}")
    private String port;

    @Value("${ip}")
    private String ip;

    @Value("${syt.upload}")
    private String path;

    /**
     * 单个文件上传的api
     * @param file
     * @return 上传结果，已经新文件路径
     */
    @PostMapping("api/upload")
    public Object upload(MultipartFile file) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code", 0);
        Map src = new HashMap();
        try {
            // 对文件名的  空格 进行处理
            String fileNameStr = file.getOriginalFilename().replaceAll(" ", "");
            String mailRegex = "[\u4e00-\u9fa5]+";
            Pattern.compile(mailRegex).matcher(fileNameStr);

            // 设备本地文件名
            String filepath = path + "/" + RandomUtil.randomString(10) + "." + file.getContentType();
            jsonObject.put("filepath", filepath);
            File destFile = new File(filepath);
            if(!destFile.getParentFile().exists()){
                destFile.mkdirs();
            }
            // 传递保存文件
            file.transferTo(destFile);
            src.put("src", "http://" + ip + ":" + port + "/" + destFile.getName());
        } catch (Exception e) {
            e.printStackTrace();
            jsonObject.put("msg", "上传失败," + e.getMessage());
        }
        jsonObject.put("data", src);
        return jsonObject;
    }



    @PostMapping("api/uploadFiles")
    public Object uploadFiles(HttpServletRequest requestBody) {
        JSONObject jsonObject = new JSONObject();

        List<MultipartFile> files = ((MultipartHttpServletRequest) requestBody).getFiles("files");
        jsonObject.put("code", 0);

        List<String> src = new ArrayList<>();
        List<String> error = new ArrayList<>();

        for (MultipartFile file :
                files) {
            // 设备本地文件名
            File destFile = new File(path + RandomUtil.randomString(10) + "." + file.getContentType());
            if(!destFile.getParentFile().exists()){
                destFile.mkdirs();
            }
            try {
                file.transferTo(destFile);
                src.add("http://" + ip + ":" + port + "/" + file.getOriginalFilename());
            } catch (IOException e) {
                e.printStackTrace();
                error.add(file.getOriginalFilename());
            }
        }
        jsonObject.put("data", src);
        jsonObject.put("error", error);
        return jsonObject;
    }

}
