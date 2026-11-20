
package com.project.booking.service.queue;

import com.project.booking.dto.booking.BookingRequestSnapshot;
import com.project.booking.dto.booking.BookingResponse;
import com.project.booking.model.enums.BookingQueueStatus;
import com.project.booking.service.passenger.BookingService;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Component
public class BookingQueueManager {

    private static final Logger log = LoggerFactory.getLogger(BookingQueueManager.class);

    /**
     * Tổng số request được chấp nhận:
     * 1 request đang xử lý + 4 request đang chờ.
     */
    private static final int MAX_CAPACITY = 5;

    private static final String STATUS_DESTINATION = "/queue/booking-status";

    private final BookingService bookingService;
    private final SimpMessagingTemplate messagingTemplate;

    /**
     * Chỉ chứa các request đang chờ xử lý.
     * Request đang xử lý được quản lý riêng bởi activeTask.
     */
    private final ArrayDeque<BookingQueueTask> waitingQueue = new ArrayDeque<>();

    private final Object queueLock = new Object();

    private volatile boolean running = true;

    private BookingQueueTask activeTask;

    private Thread workerThread;

    public BookingQueueManager(
            BookingService bookingService,
            SimpMessagingTemplate messagingTemplate) {

        this.bookingService = bookingService;
        this.messagingTemplate = messagingTemplate;
    }

    /**
     * Khởi động duy nhất một Worker khi Booking Service khởi động.
     */
    @PostConstruct
    public void startWorker() {

        workerThread = new Thread(
                this::processQueue,
                "booking-queue-worker");

        workerThread.setDaemon(false);
        workerThread.start();

        log.info(
                "[BOOKING QUEUE] Worker started. Max capacity = {}",
                MAX_CAPACITY);
    }

    /**
     * Tiếp nhận request và đưa vào FIFO Queue.
     *
     * @return BookingQueueTask để Controller chờ kết quả.
     */

    public BookingQueueTask submit(
            UUID userId,
            String email,
            com.project.booking.dto.booking.CreateBookingRequest request)
            throws IOException {

        if (userId == null) {
            throw new IllegalArgumentException("User ID không được null.");
        }

        if (request == null) {
            throw new IllegalArgumentException("Booking request không được null.");
        }

        var requestSnapshot = BookingRequestSnapshot.copy(request);

        UUID requestId = UUID.randomUUID();

        BookingQueueTask task = new BookingQueueTask(
                requestId,
                userId,
                email,
                requestSnapshot);

        synchronized (queueLock) {

            int currentSize = waitingQueue.size()
                    + (activeTask != null ? 1 : 0);

            if (!running || currentSize >= MAX_CAPACITY) {

                log.warn(
                        "[BOOKING QUEUE] REJECTED | requestId={} | userId={} | currentSize={} | capacity={}",
                        requestId, userId, currentSize, MAX_CAPACITY);

                throw new QueueFullException(
                        "Hệ thống đang xử lý quá nhiều yêu cầu đặt tour. Vui lòng thử lại sau.");
            }

            waitingQueue.addLast(task);

            log.info(
                    "[BOOKING QUEUE] ACCEPTED | requestId={} | userId={} | status={} | active={} | waiting={}",
                    requestId,
                    userId,
                    task.getStatus(),
                    activeTask != null,
                    waitingQueue.size());

            // Gửi WAITING và vị trí khi vẫn đang giữ queueLock.
            // Worker chưa thể lấy request trước khi gửi xong.
            publishWaitingPositionsLocked();

            queueLock.notifyAll();
        }

        return task;
    }

    /**
     * Worker duy nhất lấy request theo FIFO và xử lý lần lượt.
     */
    private void processQueue() {

        while (true) {

            BookingQueueTask task;

            synchronized (queueLock) {

                while (running && waitingQueue.isEmpty()) {
                    try {
                        queueLock.wait();
                    } catch (InterruptedException exception) {
                        Thread.currentThread().interrupt();

                        log.warn(
                                "[BOOKING QUEUE] Worker interrupted.");

                        return;
                    }
                }

                if (!running && waitingQueue.isEmpty()) {
                    return;
                }

                task = waitingQueue.removeFirst();

                activeTask = task;

                task.setStatus(BookingQueueStatus.PROCESSING);

                // Gửi PROCESSING trước khi cho request mới thay đổi Queue.
                publishStatus(
                        task,
                        BookingQueueStatus.PROCESSING,
                        0,
                        "Đang xử lý yêu cầu đặt tour.");

                // Cập nhật lại vị trí các request còn chờ.
                publishWaitingPositionsLocked();
            }

            Instant processingStartedAt = Instant.now();

            log.info(
                    "[BOOKING QUEUE] PROCESSING | requestId={} | userId={} | " +
                            "receivedAt={} | processingStartedAt={} | waiting={}",
                    task.getRequestId(),
                    task.getUserId(),
                    task.getReceivedAt(),
                    processingStartedAt,
                    getWaitingCount());

            try {

                // BookingService được inject qua Spring Bean Proxy.
                // Transaction của create() bắt đầu trong Worker thread.
                BookingResponse response = bookingService.create(
                        task.getRequest(),
                        task.getUserId(),
                        task.getEmail());

                Instant completedAt = Instant.now();

                task.setStatus(BookingQueueStatus.COMPLETED);

                task.getResultFuture().complete(response);

                log.info(
                        "[BOOKING QUEUE] COMPLETED | requestId={} | userId={} | " +
                                "receivedAt={} | processingStartedAt={} | completedAt={} | " +
                                "waitingDurationMs={} | processingDurationMs={} | totalDurationMs={}",
                        task.getRequestId(),
                        task.getUserId(),
                        task.getReceivedAt(),
                        processingStartedAt,
                        completedAt,
                        Duration.between(
                                task.getReceivedAt(),
                                processingStartedAt).toMillis(),
                        Duration.between(
                                processingStartedAt,
                                completedAt).toMillis(),
                        Duration.between(
                                task.getReceivedAt(),
                                completedAt).toMillis());

                publishStatus(
                        task,
                        BookingQueueStatus.COMPLETED,
                        0,
                        "Đặt tour thành công.");

            } catch (Exception exception) {

                Instant failedAt = Instant.now();

                task.setStatus(BookingQueueStatus.FAILED);

                task.getResultFuture().completeExceptionally(exception);

                log.error(
                        "[BOOKING QUEUE] FAILED | requestId={} | userId={} | " +
                                "receivedAt={} | processingStartedAt={} | failedAt={} | " +
                                "durationMs={} | error={}",
                        task.getRequestId(),
                        task.getUserId(),
                        task.getReceivedAt(),
                        processingStartedAt,
                        failedAt,
                        Duration.between(
                                processingStartedAt,
                                failedAt).toMillis(),
                        exception.getMessage(),
                        exception);

                publishStatus(
                        task,
                        BookingQueueStatus.FAILED,
                        0,
                        "Đặt tour thất bại. Vui lòng kiểm tra lại thông tin.");

            } finally {

                synchronized (queueLock) {

                    activeTask = null;

                    queueLock.notifyAll();
                }

                log.info(
                        "[BOOKING QUEUE] SLOT RELEASED | requestId={} | " +
                                "active=false | waiting={}",
                        task.getRequestId(),
                        getWaitingCount());
            }
        }
    }

    /**
     * Gửi lại vị trí FIFO của toàn bộ request đang chờ.
     *
     * Phương thức này phải được gọi khi đang giữ queueLock.
     */
    private void publishWaitingPositionsLocked() {

        int position = 1;

        for (BookingQueueTask task : waitingQueue) {

            if (task.getStatus() != BookingQueueStatus.WAITING) {
                continue;
            }

            publishStatus(
                    task,
                    BookingQueueStatus.WAITING,
                    position,
                    "Vui lòng chờ. Vị trí trong hàng đợi: " + position);

            position++;
        }
    }

    /**
     * Gửi trạng thái riêng cho user tương ứng.
     */
    private void publishStatus(
            BookingQueueTask task,
            BookingQueueStatus status,
            int queuePosition,
            String message) {

        Map<String, Object> event = new LinkedHashMap<>();

        event.put("requestId", task.getRequestId().toString());
        event.put("status", status.name());
        event.put("queuePosition", queuePosition);
        event.put("message", message);
        event.put("receivedAt", task.getReceivedAt().toString());
        event.put("timestamp", Instant.now().toString());

        try {

            messagingTemplate.convertAndSendToUser(
                    task.getUserId().toString(),
                    STATUS_DESTINATION,
                    event);

        } catch (Exception exception) {

            // Lỗi WebSocket không được làm chết Worker
            // hoặc ảnh hưởng đến nghiệp vụ đặt tour.
            log.warn(
                    "[BOOKING QUEUE] Cannot publish WebSocket event | " +
                            "requestId={} | userId={} | error={}",
                    task.getRequestId(),
                    task.getUserId(),
                    exception.getMessage());
        }
    }

    private int getWaitingCount() {

        synchronized (queueLock) {
            return waitingQueue.size();
        }
    }

    /**
     * Khi Booking Service tắt:
     * - Không nhận request mới.
     * - Đánh dấu các request còn chờ là thất bại.
     * - Request đang xử lý được phép hoàn thành.
     */
    @PreDestroy
    public void shutdown() {

        List<BookingQueueTask> pendingTasks;

        synchronized (queueLock) {

            running = false;

            pendingTasks = new ArrayList<>(waitingQueue);

            waitingQueue.clear();

            queueLock.notifyAll();
        }

        for (BookingQueueTask task : pendingTasks) {

            task.setStatus(BookingQueueStatus.FAILED);

            task.getResultFuture().completeExceptionally(
                    new IllegalStateException(
                            "Booking Service đang tắt. Vui lòng thử lại."));

            publishStatus(
                    task,
                    BookingQueueStatus.FAILED,
                    0,
                    "Hệ thống đang khởi động lại. Vui lòng thử lại.");
        }

        log.info(
                "[BOOKING QUEUE] Shutdown requested. Pending requests={}",
                pendingTasks.size());
    }

    /**
     * Exception riêng khi Queue đã đầy.
     * Controller sẽ dùng exception này để trả HTTP 429.
     */
    public static class QueueFullException extends RuntimeException {

        public QueueFullException(String message) {
            super(message);
        }
    }
}
