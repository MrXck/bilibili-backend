package com.bilibili.controller;

import com.bilibili.handler.NonstaticResourceHttpRequestHandler;
import com.bilibili.pojo.Message;
import com.bilibili.utils.FileUtils;
import com.bilibili.utils.NoAuthorization;
import com.bilibili.utils.NotControllerResponseAdvice;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static com.bilibili.utils.Constant.PATH;
import static com.bilibili.utils.Constant.SEND_IMAGE_TYPE;

/**
 * @author xck
 */
@RestController
@RequestMapping("/file")
public class CommonController {

    private final NonstaticResourceHttpRequestHandler nonStaticResourceHttpRequestHandler;

    public CommonController(NonstaticResourceHttpRequestHandler nonStaticResourceHttpRequestHandler) {
        this.nonStaticResourceHttpRequestHandler = nonStaticResourceHttpRequestHandler;
    }

    @NoAuthorization
    @NotControllerResponseAdvice
    @GetMapping("/download/{filename}")
    public void download(@PathVariable("filename") String filename, HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            String path = PATH + filename;
            File file = new File(path);
            if (file.exists()) {
                request.setAttribute(NonstaticResourceHttpRequestHandler.ATTR_FILE, path);
                nonStaticResourceHttpRequestHandler.handleRequest(request, response);
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.setCharacterEncoding(StandardCharsets.UTF_8.toString());
            }
        } catch (Exception e) {
            throw new IOException();
        }
    }

    @PostMapping("/upload")
    public Message upload(MultipartFile file, Message message) throws Exception {
        String filename = FileUtils.uploadFile(file);
        message.setType(SEND_IMAGE_TYPE);
        message.setContent("/file/download/" + filename);
        return message;
    }

    @PostMapping("/uploadVideo")
    public String uploadVideo(MultipartFile file) throws Exception {
        String filename = FileUtils.uploadFile(file);
        return "/file/download/" + filename;
    }

    @PostMapping("/uploadImage")
    public String uploadImage(@RequestParam("editormd-image-file") MultipartFile file) throws Exception {
        String filename = FileUtils.uploadFile(file);
        return "/file/download/" + filename;
    }
}
