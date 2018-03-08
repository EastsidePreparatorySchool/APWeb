let initials = "";
var k;
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
}
;
function getimage() {

    request({url: "/download"}) //request URL to send number of images in DB to JS

            //variable for total number of images
            .then(data => {
                k = JSON.parse(data) - 1;
                var table = document.getElementById("MyTable"); //reference table

                //run loop while there are still images left to get
                for (var i = 1; i <= k; i++) {

                    var rowcount = table.rows.length; //count the number of rows
                    var row = table.insertRow(rowcount); //add a new row to the table

                    var cell1 = row.insertCell(0); //add a new cell to the row
                    var newimage = new Image(500, 500); //make a new image
                    newimage.src = "upload/download?arg1=" + i; //set image source to download link with params based on iterator
                    newimage.name = "newimage";
                    cell1.appendChild(newimage); //add image to table


                }
            })



}

function login() {
    initials = window.prompt("Please enter your full name");
    request({url: "login", body: initials, method: "POST"})
            .then(data => {
                //still deciding whether or not to display anything here

                output("Thank you for logging in, " + data + ".");
            })
            .catch(error => {
                output("Error: " + error);
            });
}



function output(message) {
    document.getElementById("output").innerHTML += message;
}

function outvac(message) {
    document.getElementById("vac").innerHTML = message;
}

function outon(message) {
    document.getElementById("a").innerHTML = message;
}
function submitForm(form) { //from OneNote to upload a form
    var body = new FormData(form);
    let xhr = new XMLHttpRequest();
    xhr.open("POST", "upload");
    // must not do this!!!! xhr.setRequestHeader("Content-type", "multipart/form-data");

    xhr.onload = () => {
        if (xhr.status >= 200 && xhr.status < 300) {
            output("returned " + xhr.response);
        } else {
            output("error " + xhr.statusText);
        }
    };
    xhr.onerror = () => {
        output("error " + xhr.statusText);
    };
    xhr.send(body);
    return false;
}


//display Status values from database
function displayStatus() {
    var vac;
    var volt;
    var d;
    var rad;
    request({url: "status", method: "GET"})
            .then(data => {
                if (data != "null") { //nullchecker for incoming data F
                    window.setInterval(function () { //refresh status, refreshes every 1000ms
                        displayStatus();
                    }, 1000);



                    let status = JSON.parse(data); //get data from java

                    //setting variables of status items
                    vac = status[0];
                    d = status[1];
                    volt = status[2];
                    rad = status[3];
                    if (volt == 0) {
                        document.getElementById("a").innerHTML = "Fusor Status: OFF";
                        document.getElementById("d").innerHTML = "";
                        document.getElementById("volt").innerHTML = "";
                        document.getElementById("rad").innerHTML = "";

                        return; //check to determine if the fusor is on 
                        //(hopefully voltage will not be 0 if fusor is on)
                    }
                    //loading status items into HTML
                    document.getElementById("a").innerHTML = "Fusor Status: ON"; //just like below, this is not working for whatever reason
                    //outon("Fusor Status: ON");
                    document.getElementById("d").innerHTML = "Deuterium Levels: " + d;
                    document.getElementById("volt").innerHTML = "Voltage: " + volt;
                    document.getElementById("rad").innerHTML = "Radiation Levels: " + rad;
                    //document.getElementById("vac").innerhtml = "Vacuum Pressure: " + vac; //this doesn't work and i have literally no idea why
                    outvac("Vacuum Pressure: " + vac); //forced to use this because the above line does not work



                }




            })
            .catch(error => {
                console.log("Error: " + error);
            });
}

