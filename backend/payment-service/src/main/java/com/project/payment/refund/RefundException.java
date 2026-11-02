package com.project.payment.refund;

public class RefundException extends RuntimeException {
    private final int status;
    public RefundException(int status, String code) { super(code); this.status = status; }
    public int status() { return status; }
}
