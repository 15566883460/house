package com.dlyong.house.biz.service;

import com.google.common.collect.Lists;
import com.google.common.io.Files;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.time.Instant;
import java.util.List;

@Service
public class FileService {

    @Value("${file.path}")
    private String filePath;

    public List<String> getImgPath(List<MultipartFile> files) {
        List<String> paths = Lists.newArrayList();
        files.forEach(file -> {
            File localFile = null;
            if(!file.isEmpty()) {
                try {
                    // 头像保存本地
                    localFile = saveToLocal(file,filePath);
                    String path = StringUtils.substringAfterLast(localFile.getAbsolutePath().replace("\\","/"),filePath);
                    paths.add(path);
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }


        } );
        return  paths;
    }

    private File saveToLocal(MultipartFile file, String filePath) throws Exception {

        // Instant.now().getEpochSecond() 以秒为单位的时间戳
        File newFile = new File(filePath + "/" + Instant.now().getEpochSecond() + "/"+file.getOriginalFilename());
        if(!newFile.exists()) {
            newFile.getParentFile().mkdirs();
            newFile.createNewFile();
        }
        Files.write(file.getBytes(),newFile);
        return newFile;
    }


}
