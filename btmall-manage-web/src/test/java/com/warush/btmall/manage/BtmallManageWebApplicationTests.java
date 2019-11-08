package com.warush.btmall.manage;

import org.csource.common.MyException;
import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.StorageClient;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerServer;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

@RunWith(SpringRunner.class)
@SpringBootTest
class BtmallManageWebApplicationTests {

    @Test
    void contextLoads() throws IOException, MyException {
        String tracker = BtmallManageWebApplicationTests.class.getResource ("/tracker.conf").getPath ();
        ClientGlobal.init (tracker);

        TrackerClient client = new TrackerClient ();

        // 获得一个trackerServer的实例
        TrackerServer trackerServer = client.getConnection ();


        StorageClient storageClient = new StorageClient (trackerServer, null);

        String[] uploadInfos = storageClient.upload_file ("/Users/bill/Downloads/btmall/image/iPhone11.jpg", "jpg", null);
        for (String info : uploadInfos) {
            System.out.println (info);
        }

    }

}
