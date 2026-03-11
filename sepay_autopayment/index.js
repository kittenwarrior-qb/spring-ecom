const express = require('express');
const cors = require('cors');
const axios = require('axios');
require('dotenv').config();

const app = express();
const PORT = process.env.PORT || 3001;

// Middleware
app.use(cors());
app.use(express.json());
app.use(express.urlencoded({ extended: true }));

// Backend API configuration
const BACKEND_URL = process.env.BACKEND_URL || 'http://localhost:8080';
const BACKEND_API_KEY = process.env.BACKEND_API_KEY || process.env.API_KEY;

// Middleware để xác thực API Key từ SePay
const authenticateApiKey = (req, res, next) => {
    const apiKey = process.env.API_KEY;
    
    if (apiKey) {
        const authHeader = req.headers.authorization;
        if (!authHeader || !authHeader.startsWith('Apikey ')) {
            return res.status(401).json({ success: false, message: 'Missing or invalid API key' });
        }
        
        const providedKey = authHeader.substring(7); // Remove 'Apikey ' prefix
        if (providedKey !== apiKey) {
            return res.status(401).json({ success: false, message: 'Invalid API key' });
        }
    }
    
    next();
};

// Webhook endpoint để nhận thông báo từ SePay và forward đến Spring Boot
app.post('/webhook', authenticateApiKey, async (req, res) => {
    try {
        const data = req.body;
        
        // Validate dữ liệu đầu vào
        if (!data || !data.id) {
            return res.status(400).json({ 
                success: false, 
                message: 'Invalid webhook data' 
            });
        }

        console.log(`Received SePay webhook for transaction ID: ${data.id}, code: ${data.code}`);
        
        // Forward webhook to Spring Boot backend
        const backendResponse = await axios.post(
            `${BACKEND_URL}/v1/api/payments/sepay/webhook`,
            data,
            {
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': BACKEND_API_KEY ? `Apikey ${BACKEND_API_KEY}` : undefined
                },
                timeout: 10000 // 10 seconds timeout
            }
        );
        
        console.log(`Successfully forwarded webhook to backend: ${backendResponse.status}`);
        
        // Return success response to SePay
        res.status(201).json({ success: true });
        
    } catch (error) {
        console.error('Webhook processing error:', error.message);
        
        if (error.response) {
            // Backend returned an error
            console.error('Backend error response:', error.response.data);
            res.status(error.response.status).json({ 
                success: false, 
                message: 'Backend processing error' 
            });
        } else if (error.request) {
            // Network error
            console.error('Network error:', error.request);
            res.status(503).json({ 
                success: false, 
                message: 'Backend service unavailable' 
            });
        } else {
            // Other error
            res.status(500).json({ 
                success: false, 
                message: 'Internal server error' 
            });
        }
    }
});

// API để lấy danh sách giao dịch từ backend
app.get('/transactions', async (req, res) => {
    try {
        const page = parseInt(req.query.page) || 1;
        const limit = parseInt(req.query.limit) || 20;
        
        const backendResponse = await axios.get(
            `${BACKEND_URL}/v1/api/sepay/transactions`,
            {
                params: { page, limit },
                timeout: 10000
            }
        );
        
        res.json(backendResponse.data);
        
    } catch (error) {
        console.error('Get transactions error:', error.message);
        res.status(500).json({ 
            success: false, 
            message: 'Failed to fetch transactions' 
        });
    }
});

// Health check endpoint
app.get('/health', (req, res) => {
    res.json({ 
        success: true, 
        message: 'SePay Autopayment Service is running',
        timestamp: new Date().toISOString(),
        backend_url: BACKEND_URL
    });
});

// Test backend connection
app.get('/test-backend', async (req, res) => {
    try {
        const response = await axios.get(`${BACKEND_URL}/actuator/health`, { timeout: 5000 });
        res.json({
            success: true,
            message: 'Backend connection successful',
            backend_status: response.data
        });
    } catch (error) {
        res.status(503).json({
            success: false,
            message: 'Backend connection failed',
            error: error.message
        });
    }
});

app.listen(PORT, () => {
    console.log(`SePay Autopayment Service running on port ${PORT}`);
    console.log(`Webhook URL: http://localhost:${PORT}/webhook`);
    console.log(`Backend URL: ${BACKEND_URL}`);
    console.log(`Use ngrok to expose: ngrok http ${PORT}`);
});