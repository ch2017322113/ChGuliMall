package cn.pandacoder.gulimall.thirdparty;

import com.aliyun.oss.OSSClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

@SpringBootTest
class GulimallThirdPartyApplicationTests {

    @Test
    void contextLoads() {
    }

    @Autowired
    OSSClient ossClient;

    @Test
    public void testUpload() throws FileNotFoundException {

        // 上传文件流。
        InputStream inputStream = new FileInputStream("C:\\Users\\admin\\Desktop\\202001211.png");
        ossClient.putObject("gulimall-ezebk", "test.png", inputStream);

        // 关闭OSSClient。
        ossClient.shutdown();

        System.out.println("上传成功!");
    }


}
