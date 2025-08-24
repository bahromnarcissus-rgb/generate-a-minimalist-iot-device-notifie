import kotlinx.coroutines.*
import java.net.InetSocketAddress
import java.net.Socket

object IoTNotifier {
    private const val deviceId = "your-device-id"
    private const val notificationServiceUrl = "https:// notification-service.com/api/notify"
    private val socket = Socket()

    fun startNotificationLoop() {
        GlobalScope.launch {
            while (true) {
                try {
                    if (isDeviceConnected()) {
                        sendNotification("Device is online")
                    } else {
                        sendNotification("Device is offline")
                    }
                    delay(10000) // check every 10 seconds
                } catch (e: Exception) {
                    println("Error: ${e.message}")
                }
            }
        }
    }

    private fun isDeviceConnected(): Boolean {
        val address = InetSocketAddress("your-device-ip", 22) // assume device is connected if port 22 is open
        return try {
            socket.connect(address, 2000) // 2 seconds timeout
            socket.close()
            true
        } catch (e: Exception) {
            false
        }
    }

    private suspend fun sendNotification(message: String) {
        val httpRequest = HttpRequest.newBuilder()
            .uri(URI(notificationServiceUrl))
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString("""{"deviceId": "$deviceId", "message": "$message"}""".trimIndent()))
            .build()

        withContext(Dispatchers.IO) {
            val response = HttpRequest.newHttpClient().send(httpRequest, HttpResponse.BodyHandlers.ofString())
            println("Notification sent: ${response.statusCode()}")
        }
    }
}

fun main() {
    IoTNotifier.startNotificationLoop()
}