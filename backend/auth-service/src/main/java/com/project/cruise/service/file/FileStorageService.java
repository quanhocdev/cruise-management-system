package com.project.cruise.service.file;
import com.project.cruise.service.cloudinary.UploadResult;
import org.springframework.web.multipart.MultipartFile;

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