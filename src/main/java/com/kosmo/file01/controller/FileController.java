package com.kosmo.file01.controller;

import com.kosmo.file01.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.ServletContext;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;


@Controller
@RequiredArgsConstructor
public class FileController {
    private final FileService fileService;
    private final ServletContext servletContext;

    // HTML 파일 저장
    @PostMapping("/saveFile")
    public ResponseEntity<?> saveHtmlFile(@RequestParam("file") MultipartFile file) {
        try {
            // 파일 저장
            String savedFilename = fileService.saveHtmlFile(file);
            // 파일 저장 성공 메시지 반환
            return ResponseEntity.ok("파일 저장 성공: " + savedFilename);
        } catch (IOException e) {
            // 파일 저장 실패시 예외 처리
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("업로드 실패: " + e.getMessage());
        }
    }

    @PostMapping("/saveImg")
    @ResponseBody
    public ResponseEntity<?> uploadImg(@RequestParam("file") MultipartFile file) {
        try {
            // 파일을 저장하는 서비스 메서드를 호출 (저장된 파일명 또는 경로를 반환)
            String savedFilename = fileService.saveFile(file);

            // 이미지 URL 반환 (파일이 저장된 경로 또는 URL)
            // 예시: http://localhost:8080/images/파일명
            String imageUrl = "http://localhost:8080/images/" + savedFilename;  // 변경된 경로

            // 이미지 URL을 JSON 형식으로 반환
            return ResponseEntity.ok(imageUrl);
        } catch (IOException e) {
            // 예외 발생 시, 오류 메시지 반환
            return ResponseEntity.badRequest().body("파일 업로드 실패: " + e.getMessage());
        }
    }

    // 이미지 파일 로드
    @GetMapping("/images/{filename}")
    @ResponseBody
    public ResponseEntity<Resource> loadImage(@PathVariable("filename") String filename) {
        try {
            Path file = fileService.load(filename);
            Resource resource = new UrlResource(file.toUri());

            if (!resource.exists()) {
                return ResponseEntity.notFound().build();
            }

            String contentType = servletContext.getMimeType(resource.getFile().getAbsolutePath());

            if (contentType == null) {
                contentType = "application/octet-stream";
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .body(resource);

        } catch (MalformedURLException e) {
            return ResponseEntity.badRequest().build();
        } catch (IOException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}