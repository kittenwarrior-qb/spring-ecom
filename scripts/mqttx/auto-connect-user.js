/**
 * MQTTX Script: Auto-generate user connection payload
 * 
 * Script này dùng cho MQTTX Desktop/CLI
 * Khi user login, FE call API -> BE publish message -> MQTTX subscribe và hiênr thông báo
 * 
 * Cách dùng MQTTX CLI:
 * mqttx conn -h localhost -p 1883 -u "admin" -P "password" -t "notifications/+/#" -t "notifications/broadcast/#"
 * 
 * Ho dùng script này trong MQTTX Desktop:
 * 1. MqttX -> Script -> New Function
 * 2. Paste code này
 * 3. Save as "UserNotification"
 * 4. Khi subscribe, select "Use Script" -> Chose "UserNotification"
 */

/**
 * @description Transform received notification message
 * @param {any} value - Payload received from MQTT
 * @param {string} msgType - 'received' or 'publish'
 * @param {number} index - Message index (for timed messages)
 * @return {any} - Transformed payload
 */
function handlePayload(value, msgType, index) {
  // Parse payload if string
  let payload = value
  if (typeof value === 'string') {
    try {
      payload = JSON.parse(value)
    } catch (e) {
      return value
    }
  }

  // Format notification for display
  const timestamp = new Date(payload.timestamp || Date.now()).toLocaleString('vi-VN')
  
  const formattedMessage = {
    time: timestamp,
    type: payload.type || 'UNKNOWN',
    title: payload.title || 'No Title',
    message: payload.message || '',
    userId: payload.userId || 'broadcast',
    actionUrl: payload.actionUrl || '',
    eventId: payload.eventId || '',
  }

  // Log to console for debugging
  console.log(`[NOTIFICATION] ${formattedMessage.type}: ${formattedMessage.title}`)
  console.log(`  User: ${formattedMessage.userId}`)
  console.log(`  Message: ${formattedMessage.message}`)
  console.log(`  Action: ${formattedMessage.actionUrl}`)

  return JSON.stringify(formattedMessage, null, 2)
}

execute(handlePayload)
