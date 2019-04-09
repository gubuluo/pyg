package com.pinyougou.manager.controller;

import org.apache.commons.io.FilenameUtils;
import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.StorageClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
public class UploadController {

    //注入fileServerUrl
    @Value("${fileServerUrl}")
    private String fileServerUrl;

    @PostMapping("/upload")
    public Map<String, Object> upload(@RequestParam("file") MultipartFile multipartFile){
        Map<String, Object> map = new HashMap<>();
        map.put("status", 500);
        try {
            byte[] bytes = multipartFile.getBytes();
            String fileName = multipartFile.getOriginalFilename();

            //上传文件到fastdfs服务器中
            //加载fastdfs-client.conf文件，获得服务器地址
            String path = this.getClass().getResource("/fastdfs-client.conf").getPath();

            //初始化客户端全局的对象
            ClientGlobal.init(path);

            //创建存储客户端对象
            StorageClient storageClient = new StorageClient();

            String[] arr = storageClient.upload_file(bytes, FilenameUtils.getExtension(fileName), null);

            StringBuilder url = new StringBuilder(fileServerUrl);
            for (String s : arr) {
                url.append("/" + s);
            }
            map.put("status", 200);
            map.put("url", url.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }
}
