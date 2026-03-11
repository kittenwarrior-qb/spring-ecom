const axios = require('axios');

// Test webhook với ngrok URL
const WEBHOOK_URL = 'https://epizoutically-hormic-arlean.ngrok-free.dev/webhook';
const API_KEY = 'my-secret-webhook-key-12345';

// Webhook data với order number thật
const testData = {
    id: 99999,
    gateway: "Vietcombank",
    transactionDate: "2024-03-10 16:30:00",
    accountNumber: "0123456789",
    code: "ORD202603101629444678", // Order number thật
    content: "Test payment ORD202603101629444678",
    transferType: "in",
    transferAmount: 1000, // Total từ order
    accumulated: 1000000,
    subAccount: null,
    referenceCode: "TEST.123456789",
    description: "Manual test webhook"
};

async function testWebhook() {
    try {
        console.log('🚀 Testing SePay webhook...');
        console.log('URL:', WEBHOOK_URL);
        console.log('Order:', testData.code);
        console.log('Amount:', testData.transferAmount);
        
        const response = await axios.post(WEBHOOK_URL, testData, {
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Apikey ${API_KEY}`,
                'ngrok-skip-browser-warning': 'true'
            },
            timeout: 15000
        });
        
        console.log('✅ SUCCESS!');
        console.log('Status:', response.status);
        console.log('Response:', response.data);
        console.log('\n🎉 Order should be PAID now!');
        
    } catch (error) {
        console.error('❌ FAILED!');
        if (error.response) {
            console.error('Status:', error.response.status);
            console.error('Data:', error.response.data);
        } else {
            console.error('Error:', error.message);
        }
    }
}

testWebhook();