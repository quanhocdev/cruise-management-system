package com.project.auth.service.cloudinary;

import java.io.IOException;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

@Service
public class CloudinaryService {

    private final Cloudinary cloudinary;

    public CloudinaryService(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    public UploadResult uploadImage(
            MultipartFile file,
            String folder
    ) {
        try {
            Map<?, ?> result = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap(
                            "folder", folder,
                            "resource_type", "image"
                    )
            );

            return extractResult(result);

        } catch (IOException e) {
            throw new RuntimeException("Upload image failed: " + e.getMessage(), e);
        }
    }

    public UploadResult uploadVideo(
            MultipartFile file,
            String folder
    ) {
        try {
            Map<?, ?> result = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap(
                            "folder", folder,
                            "resource_type", "video" // Bỏ "async", true để nhận URL đồng bộ ngay lập tức
                    )
            );

            return extractResult(result);

        } catch (IOException e) {
            throw new RuntimeException("Upload video failed: " + e.getMessage(), e);
        }
    }

    // Hàm helper kiểm tra null an toàn
    private UploadResult extractResult(Map<?, ?> result) {
        if (result == null) {
            throw new RuntimeException("Cloudinary trả về kết quả rỗng (null)");
        }

        // Ưu tiên lấy secure_url, nếu không có thì lấy url thường
        Object urlObj = result.get("secure_url") != null ? result.get("secure_url") : result.get("url");
        Object publicIdObj = result.get("public_id");

        String url = urlObj != null ? urlObj.toString() : "";
        String publicId = publicIdObj != null ? publicIdObj.toString() : "";

        return new UploadResult(url, publicId);
    }
    public void deleteFile(String publicId) {
    if (publicId == null || publicId.trim().isEmpty()) {
        return;
    }
    try {
        // Gọi API phá hủy (destroy) tài nguyên trên Cloudinary
        Map<?, ?> result = cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
        System.out.println("Xóa file trên Cloudinary (" + publicId + "): " + result.get("result"));
    } catch (Exception e) {
        // Log lỗi lại nhưng không nên làm dừng chương trình nếu xóa file xịt
        System.err.println("Lỗi khi xóa file trên Cloudinary: " + e.getMessage());
    }
}
}