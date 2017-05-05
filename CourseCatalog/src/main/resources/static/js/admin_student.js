function getStudents() {
    request({url: "protected/getStudents", method: "GET", body: ""})
            .then(data => {
                insertStudents("outputDiv", data);
            })
            .catch(error => {
                output("Error: " + error + "<br>", output);
            });
}

function insertStudents(parentID, students) {
    //This method takes in a parent div and then a JSON string of students
    var myTable = "<table><tr><th style='width: 100px; color: red;'>ID</th>";
    myTable += "<th style='width: 100px; color: red; text-align: right;'>First Name</th>";
    myTable += "<th style='width: 100px; color: red; text-align: right;'>Last Name</th>";
    myTable += "<th style='width: 100px; color: red; text-align: right;'>Login</th>";
    myTable += "<th style='width: 100px; color: red; text-align: right;'>Graduation Year</th><tr>";
    
    var studentsJSONString = JSON.parse(students);
    var array = new Array();
    for (var i = 0; i < studentsJSONString.length; i++) {
        var obj = {
            id: studentsJSONString[i].id,
            firstName: studentsJSONString[i].firstName,
            lastName: studentsJSONString[i].lastName,
            login: studentsJSONString[i].login,
            gradYear: studentsJSONString[i].gradYear
        };
        array.push(obj);
    }
    
    for (var i = 0; i < array.length; i++) {
        var tableRow = "<tr>";
        var id = "<td>" + array[i].id; + "</td>";
        var firstName = "<td>" + array[i].firstName; + "</td>";
        var lastName = "<td>" + array[i].lastName; + "</td>";
        var login = "<td>" + array[i].login; + "</td>";
        var gradYear = "<td>" + array[i].gradYear; + "</td>";
        tableRow += id;
        tableRow += firstName;
        tableRow += lastName;
        tableRow += login;
        tableRow += gradYear;
        tableRow += "</tr>";
        myTable += tableRow;
    }
    
    myTable += "</table>";

    output(myTable, parentID); 
}