
package com.project.booking.kafka;

import com.project.common.event.RefundApprovedEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Service
public class RefundApprovedEventProducer {

    private static final Logger log = LoggerFactory.getLogger(RefundApprovedEventProducer.class);

    private static final String TOPIC = "refund-approved-topic";

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public RefundApprovedEventProducer(
            KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendAfterCommit(RefundApprovedEvent event) {

        if (!TransactionSynchronizationManager.isSynchronizationActive()) {
            throw new IllegalStateException(
                    "No active transaction synchronization");
        }

        log.info(
                "[REFUND PRODUCER] Register event after commit. topic={}, bookingRefundId={}, bookingId={}",
                TOPIC,
                event.bookingRefundId(),
                event.bookingId());

        TransactionSynchronizationManager.registerSynchronization(
                new TransactionSynchronization() {

                    @Override
                    public void afterCommit() {

                        log.info(
                                "[REFUND PRODUCER] Transaction committed. Sending event. bookingRefundId={}",
                                event.bookingRefundId());

                        try {
                            kafkaTemplate.send(
                                    TOPIC,
                                    event.bookingRefundId().toString(),
                                    event).whenComplete((result, exception) -> {

                                        if (exception != null) {
                                            log.error(
                                                    "[REFUND PRODUCER] Kafka send FAILED. bookingRefundId={}",
                                                    event.bookingRefundId(),
                                                    exception);
                                            return;
                                        }

                                        SendResult<String, Object> sendResult = result;

                                        log.info(
                                                "[REFUND PRODUCER] Kafka send SUCCESS. topic={}, partition={}, offset={}, bookingRefundId={}",
                                                sendResult.getRecordMetadata().topic(),
                                                sendResult.getRecordMetadata().partition(),
                                                sendResult.getRecordMetadata().offset(),
                                                event.bookingRefundId());
                                    });

                        } catch (Exception e) {
                            log.error(
                                    "[REFUND PRODUCER] Exception while sending event",
                                    e);
                        }
                    }
                });
    }
}
