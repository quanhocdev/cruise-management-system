# Payment Service - VNPay Sandbox

This service uses VNPay's test environment. No real money is charged.

## 1. Obtain sandbox credentials

Register a test merchant at:

https://sandbox.vnpayment.vn/devreg/

VNPay supplies these two secret values:

- `VNPAY_TMN_CODE`: website/merchant code
- `VNPAY_HASH_SECRET`: HMAC-SHA512 signing secret

Copy `.env.example` to `.env` and replace only the placeholder values. Never
commit `.env` or send the hash secret to the frontend.

## 2. Local URLs

```env
VNPAY_PAYMENT_URL=https://sandbox.vnpayment.vn/paymentv2/vpcpay.html
VNPAY_RETURN_URL=http://localhost:8080/api/v1/payments/vnpay/return
VNPAY_FRONTEND_RESULT_URL=http://localhost:5173/payment/result
```

`VNPAY_RETURN_URL` may use localhost because VNPay redirects the customer's
browser to it. The frontend should provide `/payment/result` and read the
`paymentId` and `status` query parameters.

## 3. IPN URL and public tunnels

VNPay calls the IPN endpoint from its server, so localhost is not reachable.
For a full IPN test, expose port `8080` with ngrok, Cloudflare Tunnel, or a
deployed HTTPS test domain, then configure:

```env
VNPAY_IPN_URL=https://your-public-test-domain/api/v1/payments/vnpay/ipn
```

Register that same IPN URL in the VNPay sandbox merchant configuration. A
public tunnel is optional while testing only payment URL generation and the
browser Return URL.

## 4. Create a sandbox payment

Send an access JWT through the gateway:

```http
POST http://localhost:8080/api/v1/payments
Content-Type: application/json
Authorization: Bearer <access-token>

{
  "referenceId": 100,
  "referenceType": "BOOKING",
  "amount": 1000000,
  "method": "VNPAY"
}
```

Open the returned `paymentUrl` in a browser and use VNPay sandbox test payment
details supplied by VNPay. The amount is VND and is entirely simulated.

## 5. Run tests

```powershell
cd backend/payment-service
mvn clean test
```

The tests use fake credentials and never contact VNPay or transfer money.
