package com.syt.upload;

import cn.hutool.core.util.RandomUtil;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.io.FileExistsException;
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
            String fileName;
            if (file.getOriginalFilename() != null) {
                fileName = updateFileName(file.getOriginalFilename());
            } else {
                throw new FileExistsException("文件名为空");
            }

            // 设备本地文件名
            String filepath = path + "/" + fileName;
            jsonObject.put("filepath", filepath);
            File destFile = new File(filepath);
            if(!destFile.getParentFile().exists()){
                destFile.mkdirs();
            }
            // 传递保存文件
            file.transferTo(destFile);
            src.put("src", "http://" + ip + ":" + port + "/" + fileName);
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
            try {
                String fileName;
                if (file.getOriginalFilename() != null) {
                    fileName = updateFileName(file.getOriginalFilename());
                } else {
                    throw new FileExistsException("文件名为空");
                }

                // 设备本地文件名
                File destFile = new File(path + "/" + fileName);
                if(!destFile.getParentFile().exists()){
                    destFile.mkdirs();
                }
                file.transferTo(destFile);
                src.add("http://" + ip + ":" + port + "/" + fileName);
            } catch (IOException e) {
                e.printStackTrace();
                error.add(file.getOriginalFilename());
            }
        }
        jsonObject.put("data", src);
        jsonObject.put("error", error);
        return jsonObject;
    }

    private static String updateFileName(String oldName) {
        // 取出文件格式名
        String format = ".";
        if (oldName.contains(format)) {
            String[] split = oldName.split("\\.");
            format += split[split.length - 1];
        } else {
            format = "";
        }
        // 随机文件名
        return RandomUtil.randomString(6) + System.currentTimeMillis() + format;
    }
}
