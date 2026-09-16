const webpush = require("web-push");
// tạo vapidKey
const vapidKeys = webpush.generateVAPIDKeys();

console.log("PUBLIC KEY:");
console.log(vapidKeys.publicKey);

console.log("PRIVATE KEY:");
console.log(vapidKeys.privateKey);