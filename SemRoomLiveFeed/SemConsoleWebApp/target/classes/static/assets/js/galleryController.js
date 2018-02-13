/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
//test function to attempt image retrieval from the database
function request(obj) {
    return new Promise((resolve, reject) => {
        let xhr = new XMLHttpRequest();
        //if no alternative method is supplied default to a get route
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
function getImage() {
    request({url: "/getImage"})
            .then(data => {
                console.log("starting decoding");
                var rawData = JSON.parse(data);
                console.log("finished parsing");

                var image = document.createElement('img');
                image.src = "data:image/png;base64," + btoa(rawData);
                document.body.appendChild(image);
            })
            .catch(error => {
                console.log(error);
            });

}
