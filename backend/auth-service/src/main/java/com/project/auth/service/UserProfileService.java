
package com.project.auth.service;

import com.project.auth.dto.UserProfileRequest;
import com.project.auth.dto.UserProfileResponse;
import com.project.auth.mapper.UserProfileMapper;
import com.project.auth.model.UserProfile;
import com.project.auth.model.Users;
import com.project.auth.repository.UserProfileRepository;
import com.project.auth.repository.UserRepository;
import com.project.common.dto.UploadResult;
import com.project.common.service.file.FileStorageService;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Service
public class UserProfileService {

    private final UserProfileRepository userProfileRepository;
    private final UserRepository userRepository;
    private final FileStorageService fileStorageService;

    public UserProfileService(
            UserProfileRepository userProfileRepository,
            UserRepository userRepository,
            FileStorageService fileStorageService) {

        this.userProfileRepository = userProfileRepository;
        this.userRepository = userRepository;
        this.fileStorageService = fileStorageService;
    }

    /**
     * Tạo profile cho tài khoản đang đăng nhập.
     * Không tạo tài khoản Users mới.
     */
    @Transactional
    public UserProfileResponse createMyProfile(
            UUID userId,
            UserProfileRequest request) {

        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Không tìm thấy tài khoản người dùng"));

        if (userProfileRepository.existsByUser_Id(userId)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Hồ sơ cá nhân đã tồn tại");
        }

        UserProfile profile = new UserProfile();
        profile.setUser(user);

        UserProfileMapper.updateEntity(profile, request);

        UserProfile savedProfile = userProfileRepository.save(profile);

        return UserProfileMapper.toResponse(savedProfile);
    }

    /**
     * Lấy profile của người dùng.
     */
    @Transactional(readOnly = true)
    public UserProfileResponse getMyProfile(UUID userId) {

        UserProfile profile = getProfileOrThrow(userId);

        return UserProfileMapper.toResponse(profile);
    }

    /**
     * Cập nhật thông tin cá nhân.
     * Không thay đổi avatar.
     */
    @Transactional
    public UserProfileResponse updateMyProfile(
            UUID userId,
            UserProfileRequest request) {

        UserProfile profile = getProfileOrThrow(userId);

        UserProfileMapper.updateEntity(profile, request);

        UserProfile savedProfile = userProfileRepository.save(profile);

        return UserProfileMapper.toResponse(savedProfile);
    }

    /**
     * Upload hoặc thay avatar của người dùng.
     */
    @Transactional
    public UserProfileResponse uploadMyAvatar(
            UUID userId,
            MultipartFile avatar) {

        UserProfile profile = getProfileOrThrow(userId);

        if (avatar == null || avatar.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Vui lòng chọn ảnh đại diện");
        }

        String oldAvatarPublicId = profile.getAvatarPublicId();

        UploadResult uploadResult = fileStorageService.saveMultipart(
                avatar,
                "avatars");

        profile.setAvatarUrl(uploadResult.getUrl());
        profile.setAvatarPublicId(uploadResult.getPublicId());

        try {
            UserProfile savedProfile = userProfileRepository.save(profile);

            // Xóa ảnh cũ sau khi đã lưu avatar mới.
            if (oldAvatarPublicId != null
                    && !oldAvatarPublicId.isBlank()
                    && !oldAvatarPublicId.equals(uploadResult.getPublicId())) {

                fileStorageService.delete(oldAvatarPublicId);
            }

            return UserProfileMapper.toResponse(savedProfile);

        } catch (RuntimeException exception) {

            // Nếu lưu DB thất bại thì dọn ảnh vừa upload.
            if (uploadResult.getPublicId() != null
                    && !uploadResult.getPublicId().isBlank()) {

                fileStorageService.delete(uploadResult.getPublicId());
            }

            throw exception;
        }
    }

    /**
     * Xóa avatar nhưng vẫn giữ nguyên profile.
     */
    @Transactional
    public UserProfileResponse deleteMyAvatar(UUID userId) {

        UserProfile profile = getProfileOrThrow(userId);

        String oldAvatarPublicId = profile.getAvatarPublicId();

        profile.setAvatarUrl(null);
        profile.setAvatarPublicId(null);

        UserProfile savedProfile = userProfileRepository.save(profile);

        if (oldAvatarPublicId != null && !oldAvatarPublicId.isBlank()) {
            fileStorageService.delete(oldAvatarPublicId);
        }

        return UserProfileMapper.toResponse(savedProfile);
    }

    private UserProfile getProfileOrThrow(UUID userId) {

        return userProfileRepository.findByUser_Id(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Không tìm thấy hồ sơ cá nhân"));
    }
}
