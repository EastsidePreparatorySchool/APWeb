
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












function simpleSyncRequest(obj) {
    
    let xhr = new XMLHttpRequest();
    xhr.open(obj.method || "GET", obj.url, false);

    //output("after send");
    xhr.send(obj.body);
    //output("after send");

    if (xhr.status >= 200 && xhr.status < 300) {
        output("Result: " + xhr.response);
    } else {
        output("Error: " + xhr.statusText);
    }
}














function simpleAsyncRequest(obj, callback) {
    
        let xhr = new XMLHttpRequest();
        xhr.open(obj.method || "GET", obj.url, true);
        xhr.onload = ()=>{callback(xhr)};

        //output("after send");
        xhr.send(obj.body);
        //output("after send");
   
   
    
}


function testAsync() {
    simpleAsyncRequest ({url:"hello"}, (xhr)=> {
        if (xhr.status >= 200 && xhr.status < 300) {
            output("Result: " + xhr.response);
        } else {
            output("Error: " + xhr.statusText);
        }
    });
}


function test () {
    request({url: "hello"})
        .then(data => {
            output("Result: " + data);
        })
        .catch(error => {
            output("Error: " + error);
        });
}



function testParams () {
    request({url: "params?arg1=1&arg2=2+3"})
        .then(data => {
            output("Result: " + data);
        })
        .catch(error => {
            output("Error: " + error);
        });
}


function testSession () {
    request({url: "session"})
        .then(data => {
            output("Result: " + data);
        })
        .catch(error => {
            output("Error: " + error);
        });
}



function testSync() {
    //output("simple sync request");
    simpleSyncRequest({url: "hello"});
}


function shutdown() {
    //output("simple sync request");
    simpleAsyncRequest({url: "shutdown"}, null);
}






function output (message) {
    //output("promise request");
    document.getElementById("output").innerHTML += message + "<br>";
}
    
output("Ready");