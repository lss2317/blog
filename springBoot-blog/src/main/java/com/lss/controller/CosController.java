package com.lss.controller;

import com.alibaba.fastjson.JSONObject;
import com.lss.config.COSConfigProperties;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.http.HttpProtocol;
import com.qcloud.cos.model.GetObjectRequest;
import com.qcloud.cos.model.ObjectMetadata;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.model.PutObjectResult;
import com.qcloud.cos.region.Region;
import com.qcloud.cos.transfer.TransferManager;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.File;
import java.util.UUID;

/**
 * 腾讯云COS文件储存
 *
 * @author lss
 * @create 2022年02月22日 13:43
 */
@RestController
@RequestMapping("files")
public class CosController {

    @Resource
    COSConfigProperties cosConfigProperties;

    private COSCredentials cred;
    private TransferManager transferManager;
    private COSClient cosClient;

    @PostConstruct
    public void init() {
        cred = new BasicCOSCredentials(cosConfigProperties.getSecretId(), cosConfigProperties.getSecretKey());
        // 2 设置bucket的区域,
        ClientConfig clientConfig = new ClientConfig(new Region(cosConfigProperties.getRegion()));
        // 3 这里建议设置使用 https 协议
        // 从 5.6.54 版本开始，默认使用了 https
        clientConfig.setHttpProtocol(HttpProtocol.https);
        cosClient = new COSClient(cred, clientConfig);
    }

    /**
     * 上传图片到COS储存
     */
    @PostMapping("upload")
    public String upload(MultipartFile file) {
        String key;
        File tempFile;
        try {
            // 获取文件名
            String fileName = file.getOriginalFilename();
            // 获取文件后缀
            String prefix = fileName.substring(fileName.lastIndexOf("."));
            String uuid = UUID.randomUUID().toString();
            tempFile = File.createTempFile(uuid, prefix);
            file.transferTo(tempFile);
            // 指定文件上传到 COS 上的路径，即对象键。例如对象键为folder/picture.jpg，则表示将文件 picture.jpg 上传到 folder 路径下
            key = uuid + prefix;
            PutObjectRequest putObjectRequest = new PutObjectRequest(cosConfigProperties.getBucketName(), key, tempFile);
            PutObjectResult putObjectResult = cosClient.putObject(putObjectRequest);
        } catch (Throwable tb) {
            tb.printStackTrace();
            //返回默认图片
            return "上传失败";
        }
        //删除文件
        tempFile.delete();
        return cosConfigProperties.getUrl() + key;
    }

    /**
     * 上传音频到COS储存
     */
    public String uploadVoice(MultipartFile file) {
        String key;
        File tempFile;
        try {
            // 获取文件名
            String fileName = file.getOriginalFilename();
            // 获取文件后缀
            String prefix = fileName.substring(fileName.lastIndexOf("."));
            String uuid = UUID.randomUUID().toString();
            tempFile = File.createTempFile(uuid, prefix);
            file.transferTo(tempFile);
            // 指定文件上传到 COS 上的路径，即对象键。例如对象键为folder/picture.jpg，则表示将文件 picture.jpg 上传到 folder 路径下
            key = "voice/" + uuid + prefix;
            PutObjectRequest putObjectRequest = new PutObjectRequest(cosConfigProperties.getBucketName(), key, tempFile);
            PutObjectResult putObjectResult = cosClient.putObject(putObjectRequest);
        } catch (Throwable tb) {
            tb.printStackTrace();
            //返回默认
            return "发送失败";
        }
        //删除文件
        tempFile.delete();
        return cosConfigProperties.getUrl() + key;
    }

    /**
     * 下载文件到本地
     */
    @GetMapping("download")
    public void download(@RequestParam("key") String key) {
        // 指定被删除的文件在 COS 上的路径，即对象键。例如对象键为folder/picture.jpg，则表示删除位于 folder 路径下的文件 picture.jpg
        String downloadKey = key.substring(key.indexOf(cosConfigProperties.getUrl()) + cosConfigProperties.getUrl().length());
        //参数为文件下载路径
        File downFile = new File("D://" + downloadKey);
        GetObjectRequest getObjectRequest = new GetObjectRequest(cosConfigProperties.getBucketName(), downloadKey);
        ObjectMetadata downObjectMeta = cosClient.getObject(getObjectRequest, downFile);
    }

    /**
     * 删除 COS 上指定路径的对象
     */
    @GetMapping("delete")
    public JSONObject delete(@RequestParam("key") String key) {
        JSONObject json = new JSONObject();
        // 指定被删除的文件在 COS 上的路径，即对象键。例如对象键为folder/picture.jpg，则表示删除位于 folder 路径下的文件 picture.jpg
        String deleteKey = key.substring(key.indexOf(cosConfigProperties.getUrl()) + cosConfigProperties.getUrl().length());
        try {
            cosClient.deleteObject(cosConfigProperties.getBucketName(), deleteKey);
        } catch (Exception e) {
            json.put("code", 500);
            json.put("message", "删除失败");
            return json;
        }
        json.put("code", 200);
        json.put("message", "删除成功");
        return json;
    }
}
