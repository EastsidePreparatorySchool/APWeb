var sessionID = "";                                                             //Later in our functions, this value will change to the session ID of the browser.
var messageCount = 0;
var initials = "";

function request(obj) {
    return new Promise((resolve, reject) => {
        let xhr = new XMLHttpRequest();
        xhr.open(obj.method || "GET", obj.url);
       
        xhr.onload = () => {
            if (xhr.status >= 200 && xhr.status < 300) {
                resolve(xhr.response);
            } else {
                reject(xhr.statusText);
            }
        };
        xhr.onerror = () => reject(xhr.statusText);
        
        xhr.send(obj.body);
    });
    
};

function sendMessage() {
    var objectToSend = {
        "id": messageCount,
        "parent": parseInt(document.getElementById("reply").value),
        "initials": initials,
        "message": document.getElementById("input").value
    };
    var stringObjectToSend = JSON.stringify(objectToSend);
    console.log(JSON.stringify(objectToSend));
    console.log(objectToSend);
    
    document.getElementById("input").value = "";                                //Clears the input box - don't worry, the message to be sent is already stored in objectToSend.message.
    document.getElementById("reply").value = "";                                //Clears the reply box - don't worry, the message to reply to is already stored in objectToSend.parent.

    request({url: "protected/putmsg", method:"PUT", body: stringObjectToSend})
        .then(data => {
            sessionID = data;
        })
        .catch(error => {
            output("Error: " + error);
        });    
    
    
    
    
//    var textInput = document.getElementById("input").value;                     //Stores whatever is in the input field as a variable called textInput.
//    document.getElementById("input").value = "";                                //Clears the input box - don't worry, the message to be sent is already stored in textInput.
//
//    request({url: "protected/putmsg", method:"PUT", body: textInput})
//        .then(data => {
//            sessionID = data;
//        })
//        .catch(error => {
//            output("Error: " + error);
//        });

    messageCount++;
}

function login() {
    initials = window.prompt("What are your initials?");
    request({url: "login", method: "put", body: initials})
        .then(data => {
            output("Hello " + data + "<br>");
        })
        .catch(error => {
            output("Error: " + error);
        });
}

function getNewMessages() {
    if (initials !== "") {
        request({url: "protected/getnewmessages"})
            .then(data => {
                console.log(data);
                let messageArray = JSON.parse(data);
                console.log(messageArray);
                for (var counter = 0; counter < messageArray.length; counter++) {
                    let messageAtIndex = messageArray[counter];
                    if (messageAtIndex.initials !== initials) {
                        messageAtIndex = "&nbsp;&nbsp;&nbsp;&nbsp;" + messageAtIndex;
                    }
                    output(messageAtIndex.initials + ": " + messageAtIndex.message + "<br>");
                }
            })
            .catch(error => {
                output("Error: " + error);
            }); 
    }
}

function output (message) {
    document.getElementById("output").innerHTML += message;
}

//function doStress() {
//    setInterval(stress, 10);
//}
//
//function stress() {
//    var counter;
//    request({url: "protected/putmsg", method:"PUT", body: String(Math.random())})
//        .then(data => {
//            ;
//        })
//        .catch(error => {
//            output("Error: " + error);
//        });
//}

setInterval(getNewMessages, 100);
    