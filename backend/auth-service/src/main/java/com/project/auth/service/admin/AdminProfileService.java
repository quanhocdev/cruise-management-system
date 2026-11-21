
package com.project.auth.service.admin;

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
public class AdminProfileService {

    private final UserProfileRepository userProfileRepository;
    private final UserRepository userRepository;
    private final FileStorageService fileStorageService;

    public AdminProfileService(
            UserProfileRepository userProfileRepository,
            UserRepository userRepository,
            FileStorageService fileStorageService) {

        this.userProfileRepository = userProfileRepository;
        this.userRepository = userRepository;
        this.fileStorageService = fileStorageService;
    }

    /**
     * ADMIN tạo profile cho tài khoản đã tồn tại.
     */
    @Transactional
    public UserProfileResponse createProfile(
            UUID userId,
            UserProfileRequest request) {

        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Không tìm thấy tài khoản: " + userId));

        if (userProfileRepository.existsByUser_Id(userId)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Tài khoản đã có hồ sơ cá nhân");
        }

        UserProfile profile = new UserProfile();
        profile.setUser(user);

        UserProfileMapper.updateEntity(profile, request);

        return UserProfileMapper.toResponse(
                userProfileRepository.save(profile));
    }

    /**
     * ADMIN xem profile của bất kỳ tài khoản nào.
     */
    @Transactional(readOnly = true)
    public UserProfileResponse getProfile(UUID userId) {

        return UserProfileMapper.toResponse(
                getProfileOrThrow(userId));
    }

    /**
     * ADMIN cập nhật profile của bất kỳ tài khoản nào.
     */
    @Transactional
    public UserProfileResponse updateProfile(
            UUID userId,
            UserProfileRequest request) {

        UserProfile profile = getProfileOrThrow(userId);

        UserProfileMapper.updateEntity(profile, request);

        return UserProfileMapper.toResponse(
                userProfileRepository.save(profile));
    }

    /**
     * ADMIN xóa profile và avatar.
     * Không xóa tài khoản Users.
     */
    @Transactional
    public void deleteProfile(UUID userId) {

        UserProfile profile = getProfileOrThrow(userId);

        String avatarPublicId = profile.getAvatarPublicId();

        userProfileRepository.delete(profile);

        if (avatarPublicId != null && !avatarPublicId.isBlank()) {
            fileStorageService.delete(avatarPublicId);
        }
    }

    /**
     * ADMIN upload hoặc thay avatar của tài khoản bất kỳ.
     */
    @Transactional
    public UserProfileResponse uploadAvatar(
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

            if (oldAvatarPublicId != null
                    && !oldAvatarPublicId.isBlank()
                    && !oldAvatarPublicId.equals(uploadResult.getPublicId())) {

                fileStorageService.delete(oldAvatarPublicId);
            }

            return UserProfileMapper.toResponse(savedProfile);

        } catch (RuntimeException exception) {

            if (uploadResult.getPublicId() != null
                    && !uploadResult.getPublicId().isBlank()) {

                fileStorageService.delete(uploadResult.getPublicId());
            }

            throw exception;
        }
    }

    /**
     * ADMIN xóa avatar nhưng giữ nguyên profile.
     */
    @Transactional
    public UserProfileResponse deleteAvatar(UUID userId) {

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
                        "Không tìm thấy hồ sơ của tài khoản: " + userId));
    }
}
