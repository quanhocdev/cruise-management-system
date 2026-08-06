package com.project.auth.service.file;
import org.springframework.web.multipart.MultipartFile;

import com.project.auth.service.cloudinary.UploadResult;

public interface FileStorageService {

    UploadResult saveBase64(
            String base64,
            String folder,
            String prefix,
            String extension
    );

    UploadResult saveMultipart(
            MultipartFile file,
            String folder
    );

    void delete(String publicId);
}