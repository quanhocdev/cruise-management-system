package com.project.common.service.file;

import com.project.common.dto.UploadResult;
import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {

    UploadResult saveMultipart(MultipartFile file, String folder);

    UploadResult saveBase64(String base64, String folder, String prefix, String extension);

    void delete(String publicId);
}