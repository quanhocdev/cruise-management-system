
package com.project.auth.service.redis;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.util.HexFormat;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.project.auth.exception.AppException;

@Service
public class LoginAttemptRedisService {

    private final StringRedisTemplate redisTemplate;

    @Value("${security.login.account.max-failures:5}")
    private int maxFailures;

    @Value("${security.login.account.failure-window:2m}")
    private Duration failureWindow;

    @Value("${security.login.account.lock-duration:10m}")
    private Duration lockDuration;

    @Value("${security.login.ip.max-requests:30}")
    private int maxIpRequests;

    @Value("${security.login.ip.window:1m}")
    private Duration ipWindow;

    // Tăng bộ đếm và đặt TTL trong cùng một thao tác Redis.
    private static final DefaultRedisScript<Long> INCREMENT_WITH_TTL = new DefaultRedisScript<>(
            "local count = redis.call('INCR', KEYS[1]) " +
                    "if count == 1 then " +
                    "  redis.call('PEXPIRE', KEYS[1], ARGV[1]) " +
                    "end " +
                    "return count",
            Long.class);

    // Ghi nhận lần đăng nhập sai và khóa tài khoản ngay khi đạt ngưỡng.
    private static final DefaultRedisScript<Long> RECORD_FAILURE = new DefaultRedisScript<>(
            "local count = redis.call('INCR', KEYS[1]) " +
                    "if count == 1 then " +
                    "  redis.call('PEXPIRE', KEYS[1], ARGV[1]) " +
                    "end " +
                    "if count >= tonumber(ARGV[2]) then " +
                    "  redis.call('SET', KEYS[2], '1', 'PX', ARGV[3]) " +
                    "  redis.call('DEL', KEYS[1]) " +
                    "  return tonumber(ARGV[2]) " +
                    "end " +
                    "return count",
            Long.class);

    public LoginAttemptRedisService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * Kiểm tra giới hạn request đăng nhập theo IP.
     * Mỗi request đến bước này đều được tính, kể cả đăng nhập đúng.
     */
    public void checkIpRateLimit(String clientIp) {
        String key = "auth:login:rate:ip:" + hash(normalizeIp(clientIp));

        Long count = redisTemplate.execute(
                INCREMENT_WITH_TTL,
                List.of(key),
                String.valueOf(ipWindow.toMillis()));

        if (count == null) {
            throw new AppException(
                    "Không thể kiểm tra giới hạn đăng nhập. Vui lòng thử lại.",
                    HttpStatus.SERVICE_UNAVAILABLE);
        }

        if (count > maxIpRequests) {
            throw new AppException(
                    "Bạn đã gửi quá nhiều yêu cầu đăng nhập. Vui lòng thử lại sau.",
                    HttpStatus.TOO_MANY_REQUESTS);
        }
    }

    /**
     * Kiểm tra tài khoản có đang bị khóa đăng nhập hay không.
     */
    public void checkAccountLock(String username) {
        String lockKey = lockKey(username);

        if (Boolean.TRUE.equals(redisTemplate.hasKey(lockKey))) {
            throw new AppException(
                    "Tài khoản tạm thời bị khóa do đăng nhập sai nhiều lần. Vui lòng thử lại sau 10 phút.",
                    HttpStatus.LOCKED);
        }
    }

    /**
     * Chỉ gọi khi username tồn tại và mật khẩu không đúng.
     */
    public void recordFailedLogin(String username) {
        String failKey = failureKey(username);
        String lockedKey = lockKey(username);

        Long count = redisTemplate.execute(
                RECORD_FAILURE,
                List.of(failKey, lockedKey),
                String.valueOf(failureWindow.toMillis()),
                String.valueOf(maxFailures),
                String.valueOf(lockDuration.toMillis()));

        if (count == null) {
            throw new AppException(
                    "Không thể kiểm tra trạng thái đăng nhập. Vui lòng thử lại.",
                    HttpStatus.SERVICE_UNAVAILABLE);
        }

        if (count >= maxFailures) {
            throw new AppException(
                    "Bạn đã nhập sai mật khẩu 5 lần trong 2 phút. Tài khoản bị khóa đăng nhập trong 10 phút.",
                    HttpStatus.LOCKED);
        }
    }

    /**
     * Xóa bộ đếm sai sau khi đăng nhập thành công hoàn toàn.
     * Không xóa bộ giới hạn IP ở đây.
     */
    public void clearFailedLogins(String username) {
        redisTemplate.delete(failureKey(username));
    }

    private String failureKey(String username) {
        return "auth:login:fail:" + hash(normalizeUsername(username));
    }

    private String lockKey(String username) {
        return "auth:login:lock:" + hash(normalizeUsername(username));
    }

    private String normalizeUsername(String username) {
        return username == null ? "" : username.trim().toLowerCase();
    }

    private String normalizeIp(String ip) {
        return ip == null || ip.isBlank() ? "unknown" : ip.trim();
    }

    private String hash(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] bytes = digest.digest(value.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(bytes);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("Không hỗ trợ SHA-256", e);
        }
    }
}
