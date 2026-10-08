const VAPID_PUBLIC_KEY = import.meta.env.VITE_VAPID_PUBLIC_KEY;

export async function subscribeToPush() {
    // 1. Kiểm tra browser có hỗ trợ Service Worker không
    if (!("serviceWorker" in navigator)) {
        throw new Error("Browser không hỗ trợ Service Worker");
    }

    // 2. Lấy Service Worker đang hoạt động
    const registration = await navigator.serviceWorker.ready;

    // 3. Tạo Push Subscription
    const subscription =
        await registration.pushManager.subscribe({
            userVisibleOnly: true,
            applicationServerKey: urlBase64ToUint8Array(
                VAPID_PUBLIC_KEY
            )
        });

    console.log("PUSH SUBSCRIPTION:", subscription);

    function urlBase64ToUint8Array(base64String) {
    const padding = "=".repeat(
        (4 - (base64String.length % 4)) % 4
    );

    const base64 = (
        base64String + padding
    )
        .replace(/-/g, "+")
        .replace(/_/g, "/");

    const rawData = window.atob(base64);

    return Uint8Array.from(
        [...rawData].map(char => char.charCodeAt(0))
    );
}

    return subscription;
}