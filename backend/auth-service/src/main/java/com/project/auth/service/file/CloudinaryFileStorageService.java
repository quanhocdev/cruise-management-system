package com.project.auth.service.file;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.project.auth.service.cloudinary.CloudinaryService;
import com.project.auth.service.cloudinary.UploadResult;

@Service
@Primary
public class CloudinaryFileStorageService implements FileStorageService {

    private final CloudinaryService cloudinaryService;

    public CloudinaryFileStorageService(CloudinaryService cloudinaryService) {
        this.cloudinaryService = cloudinaryService;
    }

    @Override
    public UploadResult saveMultipart(
            MultipartFile file,
            String folder
    ) {
        System.out.println("CloudinaryFileStorageService");
        System.out.println("Folder = " + folder);
        System.out.println("File = " + file.getOriginalFilename());

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File upload không được để rỗng");
        }

        String contentType = file.getContentType();

        // Kiểm tra nếu là file Video
        if (contentType != null && contentType.startsWith("video")) {
            System.out.println("-> Đang upload file Video...");
            return cloudinaryService.uploadVideo(file, folder);
        }

        // Mặc định upload Image
        return cloudinaryService.uploadImage(file, folder);
    }

    @Override
    public UploadResult saveBase64(
            String base64,
            String folder,
            String prefix,
            String extension
    ) {
        throw new UnsupportedOperationException("Chưa implement");
    }

    @Override
public void delete(String publicId) {
    if (publicId != null && !publicId.trim().isEmpty()) {
        System.out.println("CloudinaryFileStorageService -> Delete publicId: " + publicId);
        cloudinaryService.deleteFile(publicId);
    }
}
}