import express from 'express';
import crypto from 'crypto';
import dotenv from 'dotenv';

dotenv.config();

const app = express();
const PORT = process.env.PORT || 3000;

app.use(express.json());
app.use(express.urlencoded({ extended: true }));

// Verify payOS signature
function verifySignature(data, signature) {
  const checksumKey = process.env.PAYOS_CHECKSUM_KEY;
  if (!checksumKey) {
    console.error('PAYOS_CHECKSUM_KEY not configured');
    return false;
  }

  const sortedData = Object.keys(data)
    .sort()
    .map(key => `${key}=${data[key]}`)
    .join('&');
  
  const hash = crypto
    .createHmac('sha256', checksumKey)
    .update(sortedData)
    .digest('hex');

  return hash === signature;
}

// Process payment to backend
async function processPayment(paymentData) {
  const backendUrl = process.env.BACKEND_API_URL;
  const apiKey = process.env.BACKEND_API_KEY;

  if (!backendUrl) {
    throw new Error('BACKEND_API_URL not configured');
  }

  const response = await fetch(`${backendUrl}/payments/webhook`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${apiKey}`
    },
    body: JSON.stringify(paymentData)
  });

  if (!response.ok) {
    throw new Error(`Backend API error: ${response.status}`);
  }

  return await response.json();
}

// Health check endpoint
app.get('/', (req, res) => {
  res.json({ 
    status: 'ok', 
    service: 'PayOS Webhook Service',
    timestamp: new Date().toISOString()
  });
});

// PayOS webhook endpoint
app.post('/webhook/payos', async (req, res) => {
  try {
    const { code, desc, success, data, signature } = req.body;

    console.log('Received webhook:', JSON.stringify(req.body, null, 2));

    // Verify signature
    if (!verifySignature(data, signature)) {
      console.error('Invalid signature');
      return res.status(400).json({ error: 'Invalid signature' });
    }

    // Check if payment is successful
    if (code === '00' && success && data.code === '00') {
      console.log('Payment successful:', data.orderCode);
      
      // Process payment to backend
      await processPayment(data);
      
      console.log('Payment processed successfully');
    } else {
      console.log('Payment not successful:', code, desc);
    }

    // Always return 200 to acknowledge webhook
    res.status(200).json({ received: true });
  } catch (error) {
    console.error('Webhook processing error:', error);
    // Still return 200 to prevent payOS from retrying
    res.status(200).json({ received: true, error: error.message });
  }
});

app.listen(PORT, () => {
  console.log(`PayOS Webhook Service running on port ${PORT}`);
  console.log(`Webhook URL: http://localhost:${PORT}/webhook/payos`);
});
