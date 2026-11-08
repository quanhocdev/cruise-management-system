package com.project.auth.service;

import com.project.auth.dto.UserProfileRequest;
import com.project.auth.dto.UserProfileResponse;
import com.project.auth.mapper.UserProfileMapper;
import com.project.auth.model.UserProfile;
import com.project.auth.repository.UserProfileRepository;
import com.project.common.dto.UploadResult;
import com.project.common.service.file.FileStorageService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Service
public class UserProfileService {

    private final UserProfileRepository userProfileRepository;
    private final FileStorageService fileStorageService;

    public UserProfileService(
            UserProfileRepository userProfileRepository,
            FileStorageService fileStorageService) {
        this.userProfileRepository = userProfileRepository;
        this.fileStorageService = fileStorageService;
    }

    @Transactional(readOnly = true)
    public UserProfileResponse getMyProfile(UUID userId) {

        UserProfile profile = userProfileRepository
                .findByUser_Id(userId)
                .orElseThrow(() -> new RuntimeException(
                        "Không tìm thấy hồ sơ của người dùng: " + userId));

        return UserProfileMapper.toResponse(profile);
    }

    /**
     * Cập nhật thông tin cá nhân, không thay đổi avatar.
     */
    @Transactional
    public UserProfileResponse updateMyProfile(
            UUID userId,
            UserProfileRequest request) {
        return updateMyProfile(userId, request, null);
    }

    /**
     * Cập nhật thông tin cá nhân và avatar nếu có ảnh mới.
     */
    @Transactional
    public UserProfileResponse updateMyProfile(
            UUID userId,
            UserProfileRequest request,
            MultipartFile avatar) {

        UserProfile profile = userProfileRepository
                .findByUser_Id(userId)
                .orElseThrow(() -> new RuntimeException(
                        "Không tìm thấy hồ sơ của người dùng: " + userId));

        // Cập nhật thông tin cá nhân
        UserProfileMapper.updateEntity(profile, request);

        String oldAvatarPublicId = profile.getAvatarPublicId();

        // Nếu có avatar mới thì upload lên Cloudinary
        UploadResult uploadResult = null;

        if (avatar != null && !avatar.isEmpty()) {

            uploadResult = fileStorageService.saveMultipart(
                    avatar,
                    "avatars");

            profile.setAvatarUrl(uploadResult.getUrl());
            profile.setAvatarPublicId(uploadResult.getPublicId());
        }

        try {
            UserProfile savedProfile = userProfileRepository.save(profile);

            // Chỉ xóa ảnh cũ sau khi đã lưu thông tin avatar mới
            if (uploadResult != null
                    && oldAvatarPublicId != null
                    && !oldAvatarPublicId.isBlank()
                    && !oldAvatarPublicId.equals(uploadResult.getPublicId())) {

                fileStorageService.delete(oldAvatarPublicId);
            }

            return UserProfileMapper.toResponse(savedProfile);

        } catch (RuntimeException exception) {

            // Nếu lưu database thất bại, dọn ảnh mới vừa upload
            if (uploadResult != null
                    && uploadResult.getPublicId() != null
                    && !uploadResult.getPublicId().isBlank()) {

                fileStorageService.delete(uploadResult.getPublicId());
            }

            throw exception;
        }
    }

    /**
     * Xóa avatar nhưng vẫn giữ nguyên hồ sơ người dùng.
     */
    @Transactional
    public UserProfileResponse deleteMyAvatar(UUID userId) {

        UserProfile profile = userProfileRepository
                .findByUser_Id(userId)
                .orElseThrow(() -> new RuntimeException(
                        "Không tìm thấy hồ sơ của người dùng: " + userId));

        String oldAvatarPublicId = profile.getAvatarPublicId();

        profile.setAvatarUrl(null);
        profile.setAvatarPublicId(null);

        UserProfile savedProfile = userProfileRepository.save(profile);

        if (oldAvatarPublicId != null && !oldAvatarPublicId.isBlank()) {
            fileStorageService.delete(oldAvatarPublicId);
        }

        return UserProfileMapper.toResponse(savedProfile);
    }
}