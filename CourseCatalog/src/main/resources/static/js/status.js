function updateName() {
    request({url: "protected/name", method: "get"})
            .then(data => {
                output("Name: "+data);
                document.getElementById("nameSpan").innerHTML = data;
            })
            .catch(error => {
                output("Error: " + error);
            });

}

updateName();