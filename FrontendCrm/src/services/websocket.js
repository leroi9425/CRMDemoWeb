import { Client } from "@stomp/stompjs";

export const connectWebSocket = (userId, onNotification) => {
    const client = new Client({
        brokerURL: "ws://localhost:8080/ws",
        onConnect: () => {
            console.log("Connected to WebSocket");
            client.subscribe(
                `/topic/notifications/${userId}`,
                (message) => {
                    const notification = JSON.parse(message.body);
                    console.log("Nhận notification:", notification);
                    onNotification(notification);
                }
            );
        },
        onStompError: (frame) => {
            console.error("STOMP error:", frame);
        }
    });

    client.activate();
    return client;
};