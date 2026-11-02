package com.project.payment.service.refund;

public class RefundException extends RuntimeException {

    private final int status;

    public RefundException(int status, String code) {
        super(code);
        this.status = status;
    }

    public int getStatus() {
        return status;
    }
}