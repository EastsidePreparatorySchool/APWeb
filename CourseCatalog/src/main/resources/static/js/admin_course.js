function getCourseStudents() {
    document.getElementById("courseInput").value = "";
    request({url: "protected/getCourseStudents", method: "GET", body: document.getElementById("courseInput").value})
            .then(data => {
                insertStudents("courseOutput", data);
            })
            .catch(error => {
                output("Error: " + error + "<br>");
            });
}

