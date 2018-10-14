// Websocket Endpoint url
var endPointURL = "ws://localhost:8081/websockets/chat";
 
var chatClient = null;
 
function connect () {
    chatClient = new WebSocket(endPointURL);
    chatClient.onmessage = function (event) {
        var messagesArea = document.getElementById("messages");
        var jsonObj = JSON.parse(event.data);
        var message = jsonObj.user + ": " + jsonObj.message + "\r\n";
        messagesArea.value = messagesArea.value + message;
        messagesArea.scrollTop = messagesArea.scrollHeight;
    };
}
 
function disconnect () {
    chatClient.close();
} 
 
function sendMessage() {
    var user = document.getElementById("userName").value.trim();
    var inputElement = document.getElementById("messageInput");
    var message = inputElement.value.trim();
        if (user === ""){
			var words = message.split(' ');
			if(words.length==3 && "/register"==words[0]){
				var namearea=document.getElementById("userName");
				namearea.value = namearea.value + words[1]+" "+words[2];
				var jsonObj = {"user" : words[1]+" "+words[2], "message" : message};
				chatClient.send(JSON.stringify(jsonObj));
			}
			inputElement.value = "";
		}
     else{
		if (message !== "") {
			if(message=="/exit"){
				user.value="";
			}
        var jsonObj = {"user" : user, "message" : message};
        chatClient.send(JSON.stringify(jsonObj));
        inputElement.value = "";
		}
	 }
	inputElement.focus();
}