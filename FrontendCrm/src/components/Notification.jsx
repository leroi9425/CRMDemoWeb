import { useState } from "react";
export default function Login() {
    useEffect(() => {

    const userId = 10;

    const client = connectWebSocket(
        userId,
        (notification) => {
            console.log(notification);
            alert(notification.message);
        }
    );

    return () => {
        client.deactivate();
    };

}, []);
}


