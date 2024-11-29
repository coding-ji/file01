package com.kosmo.file01.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileService {

    // 업로드 경로 설정: C:/temp/upload
    private final Path uploadPath = Paths.get("D:", "temp", "upload");

    // HTML 파일 저장
    public String saveHtmlFile(MultipartFile file) throws IOException {
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath); // 업로드 경로가 없으면 생성
        }

        // 원본 파일명 가져오기
        String originalFilename = file.getOriginalFilename();
        String fileExtension = ".html"; // HTML 파일로 강제 설정

        // 파일명 랜덤 변경 (UUID를 사용하여 고유한 이름 생성)
        String randomFilename = UUID.randomUUID().toString() + fileExtension;

        // 파일을 저장할 경로 설정
        Path destination = uploadPath.resolve(randomFilename);
        file.transferTo(destination); // 파일 저장

        return randomFilename; // 저장된 파일명 반환
    }
    // 단일 파일 저장
    public String saveFile(MultipartFile file) throws IOException {
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // 파일명 랜덤 변경
        String originalFilename = file.getOriginalFilename();
        String fileExtension = "";

        //파일에 대한 확장자
        if (originalFilename != null && originalFilename.contains(".")) {
            fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        String randomFilename = UUID.randomUUID().toString() + fileExtension;
        // 해당 파일로 파일을 보내라
        Path destination = uploadPath.resolve(randomFilename);
        file.transferTo(destination);

        return randomFilename; // 저장된 파일명 반환
    }

    // 다중 파일 저장
    public String[] saveFiles(MultipartFile[] files) throws IOException {
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        String[] savedFilenames = new String[files.length];

        for (int i = 0; i < files.length; i++) {
            MultipartFile file = files[i];

            // 파일명 랜덤 변경
            String originalFilename = file.getOriginalFilename();
            String fileExtension = "";

            if (originalFilename != null && originalFilename.contains(".")) {
                fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }

            String randomFilename = UUID.randomUUID().toString() + fileExtension;

            Path destination = uploadPath.resolve(randomFilename);
            file.transferTo(destination);

            savedFilenames[i] = randomFilename; // 저장된 파일명 저장
        }

        return savedFilenames;
    }

    // 파일 로드
    public Path load(String filename) {
        return uploadPath.resolve(filename);
    }
}
