import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';

export const connectWebSocket = (userId, onNotification) => {

    const client = new Client({
        webSocketFactory: () => new SockJS('http://localhost:8080/ws'),
        onConnect: () => {
            console.log('Connected to WebSocket');

            client.subscribe(
                `/topic/notifications/${userId}`,
                (message) => {
                    onNotification(JSON.parse(message.body));
                }
            );
        }

    });
    client.activate();

    return client;
}