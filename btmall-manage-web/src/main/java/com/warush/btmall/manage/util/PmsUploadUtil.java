package com.warush.btmall.manage.util;

import org.csource.common.MyException;
import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.StorageClient;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerServer;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * @version 1.0
 * @created by bill
 * @on 2019-11-08 03:36
 **/
public class PmsUploadUtil {
    public static String uploadImage(MultipartFile multipartFile) throws IOException, MyException {
        String tracker = PmsUploadUtil.class.getResource ("/tracker.conf").getPath ();
        ClientGlobal.init (tracker);

        TrackerClient client = new TrackerClient ();

        // 获得一个trackerServer的实例
        TrackerServer trackerServer = client.getConnection ();


        StorageClient storageClient = new StorageClient (trackerServer, null);

        byte[] bytes = multipartFile.getBytes ();
        String filename = multipartFile.getOriginalFilename ();
        String[] uploadInfos = storageClient.upload_file (bytes, filename.substring (filename.lastIndexOf (".") + 1), null);
        String url = "http://172.16.66.132";
        for (String info : uploadInfos) {
            url += "/" + info;
        }
        return url;
    }
}
