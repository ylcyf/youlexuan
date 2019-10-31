package com.offcn.shop.controller;

import com.offcn.entity.Result;
import com.offcn.uitl.FastDFSClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author ：以吾之名义裁决
 * @version : 1.0
 * @description：
 * @date ：2019/10/30 15:36
 */
@RestController
public class UpLoadController {

    @Value("${FILE_SERVER_URL}")
    private String FILE_SERVER_URL;//文件服务器地址

    @RequestMapping("/upload")
    public Result upload(MultipartFile file){
        //1、取文件的扩展名
        String originalFilename = file.getOriginalFilename();
        String extName = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
        try {
            //2、创建一个 FastDFS 的客户端
            // 注意,工程名中最好不要有空格等其他字符, 否则此处可能报错找不到文件
            FastDFSClient fastDFSClient = new FastDFSClient("classpath:config/fdfs_client.conf");
            //3、执行上传处理
            String path = fastDFSClient.uploadFile(file.getBytes(), extName);
            //4、拼接返回的 url 和 ip 地址，拼装成完整的 url
            String url = FILE_SERVER_URL + path;
            return new Result(true,url);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "上传失败");
        }
    }
}


