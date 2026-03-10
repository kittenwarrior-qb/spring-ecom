# PayOS Webhook Service

Service webhook độc lập để xử lý thanh toán tự động từ payOS.

## Cài đặt

```bash
npm install
```

## Cấu hình

Tạo file `.env` từ `.env.example`:

```bash
cp .env.example .env
```

Cập nhật các biến môi trường:
- `PAYOS_CHECKSUM_KEY`: Checksum key từ payOS
- `BACKEND_API_URL`: URL của backend API chính
- `BACKEND_API_KEY`: API key để xác thực với backend

## Chạy local

```bash
npm run dev
```

Service sẽ chạy tại `http://localhost:3000`

## Deploy với ngrok

1. Cài đặt ngrok: https://ngrok.com/download
2. Chạy service local
3. Expose với ngrok:

```bash
ngrok http 3000
```

4. Copy URL ngrok và cấu hình webhook trên payOS dashboard:
   - Webhook URL: `https://your-ngrok-url.ngrok.io/webhook/payos`

## Deploy lên Vercel

1. Cài đặt Vercel CLI:

```bash
npm i -g vercel
```

2. Deploy:

```bash
vercel
```

3. Cấu hình environment variables trên Vercel dashboard
4. Copy URL production và cấu hình webhook trên payOS:
   - Webhook URL: `https://your-project.vercel.app/webhook/payos`

## Endpoints

- `GET /` - Health check
- `POST /webhook/payos` - PayOS webhook endpoint

## Webhook Flow

1. payOS gửi webhook khi có giao dịch thành công
2. Service verify signature để đảm bảo tính xác thực
3. Nếu thanh toán thành công, forward data đến backend API
4. Backend xử lý cập nhật trạng thái đơn hàng
5. Trả về status 200 cho payOS

## Lưu ý

- Luôn trả về status 200 để payOS không retry webhook
- Log tất cả webhook để debug
- Verify signature trước khi xử lý
