var sessionID = "";                                                             //Later in our functions, this value will change to the session ID of the browser.

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
    var textInput = document.getElementById("input").value;                     //Stores whatever is in the input field as a variable called textInput.
    document.getElementById("input").value = "";                                //Clears the input box - don't worry, the message to be sent is already stored in textInput.

    request({url: "putmsg", method:"PUT", body: textInput})
        .then(data => {
            sessionID = data;
        })
        .catch(error => {
            output("Error: " + error);
        });
}

var initials = "";

//function login() {
//    initials = prompt("What are your initials?");
//    request({url: "login", method: "put", body: initials})
//        .then(data => {
//            output("Hello" + data + "/br");
//        })
//        .catch(error => {
//            output("Error: " + error);
//        });
//}

function getNewMessages() {
    request({url: "getnewmessages"})
        .then(data => {
            output(data);
        })
        .catch(error => {
            output("Error: " + error);
        }); 
}

function output (message) {
    document.getElementById("output").innerHTML += message;
}

function doStress() {
    setInterval(stress, 10);
}

function stress() {
    var counter;
    request({url: "putmsg", method:"PUT", body: String(Math.random())})
        .then(data => {
            ;
        })
        .catch(error => {
            output("Error: " + error);
        });
}

setInterval(getNewMessages, 100);
    