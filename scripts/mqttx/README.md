# MQTTX Scripts & Configuration

## 1. MQTTX CLI - Auto Connect User

### Install MQTTX CLI
```bash
# Windows (PowerShell)
scoop install mqttx

# macOS
brew install emqx/mqttx/mqttx

# Linux
snap install mqttx
```

### Connect và Subscribe User Notifications
```bash
# Connect as specific user và subscribe notification topics
mqttx conn -h localhost -p 1883 \
  -u "user-2" \
  -P "your-jwt-token" \
  -t "notifications/2/#" \
  -t "notifications/broadcast/#" \
  --format json

# Ho c dùng config file
mqttx conn -c user-connection.yml
```

### Subscribe Only (n u có connection)
```bash
mqttx sub -t "notifications/2/#" -t "notifications/broadcast/#" --format json
```

## 2. EMQX Rule Engine - Capture Connection Events

### Webhook cho Connection Events
Trong EMQX Dashboard -> Rule Engine -> Create Rule:

**SQL:**
```sql
SELECT
  clientid,
  username,
  ip_address,
  connected_at
FROM
  "$events/client_connected"
```

**Action: Webhook**
```json
{
  "url": "http://localhost:8080/api/v1/internal/mqtt/connected",
  "method": "POST",
  "headers": {
    "Content-Type": "application/json"
  },
  "body": "${payload}"
}
```

### Backend Endpoint (Landing)
```java
@PostMapping("/api/v1/internal/mqtt/connected")
public void onMqttConnected(@RequestBody MqttConnectEvent event) {
    log.info("[MQTT] User connected: clientId={}, username={}, ip={}",
        event.getClientid(), event.getUsername(), event.getIpAddress());
    // Optionally: Store connection state, send welcome notification, etc.
}
```

## 3. MQTTX Desktop - Script Function

### Setup
1. MqttX -> Script -> New Function
2. Paste `auto-connect-user.js` content
3. Save as "UserNotification"
4. Create connection to EMQX
5. Subscribe to topics:
   - `notifications/{userId}/#`
   - `notifications/broadcast/#`
6. Click dropdown -> "Use Script" -> Select "UserNotification"

## 4. Test Notification Flow

### Publish Test Notification (via MQTTX)
```json
// Topic: notifications/2/new
{
  "eventId": "test-001",
  "eventType": "NEW_COUPON",
  "timestamp": "2024-01-01T12:00:00Z",
  "userId": 2,
  "type": "NEW_COUPON",
  "title": "Test Coupon",
  "message": "Test notification from MQTTX",
  "actionUrl": "/coupons",
  "isRead": false
}
```

### Publish Broadcast
```json
// Topic: notifications/broadcast/new
{
  "eventId": "broadcast-001",
  "eventType": "NEW_COUPON",
  "timestamp": "2024-01-01T12:00:00Z",
  "userId": null,
  "type": "NEW_COUPON",
  "title": "Broadcast Coupon",
  "message": "Everyone gets this!",
  "actionUrl": "/coupons",
  "isRead": false
}
```

## 5. Connection Config File (YAML)

```yaml
# user-connection.yml
connections:
  - name: "User-2-Notifications"
    host: localhost
    port: 1883
    username: "user-2"
    password: "${JWT_TOKEN}"
    clientId: "mqttx-user-2-${timestamp}"
    clean: true
    subscriptions:
      - topic: "notifications/2/#"
        qos: 1
      - topic: "notifications/broadcast/#"
        qos: 1
```

## 6. Programmatic Connection (Node.js)

```javascript
// test-mqtt-connection.js
const mqtt = require('mqtt')

const userId = 2
const token = 'your-jwt-token'

const client = mqtt.connect('mqtt://localhost:1883', {
  clientId: `test-user-${userId}-${Date.now()}`,
  username: `user-${userId}`,
  password: token,
  clean: true,
})

client.on('connect', () => {
  console.log('Connected!')
  
  // Subscribe to user-specific notifications
  client.subscribe(`notifications/${userId}/#`)
  client.subscribe('notifications/broadcast/#')
})

client.on('message', (topic, message) => {
  console.log(`[${topic}] ${message.toString()}`)
})
```
