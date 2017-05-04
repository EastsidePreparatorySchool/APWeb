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
            firstname: studentsJSONString[i].firstname,
            lastname: studentsJSONString[i].lastname,
            login: studentsJSONString[i].login,
            gradyear: studentsJSONString[i].gradyear
        };
        array.push(obj);
    }
    
    for (var i = 0; i < array.length; i++) {
        var tableRow = "<tr>";
        var id = "<td>" + array[i].id; + "</td>";
        var firstname = "<td>" + array[i].firstname; + "</td>";
        var lastname = "<td>" + array[i].lastname; + "</td>";
        var login = "<td>" + array[i].login; + "</td>";
        var gradyear = "<td>" + array[i].gradyear; + "</td>";
        tableRow += id;
        tableRow += firstname;
        tableRow += lastname;
        tableRow += login;
        tableRow += gradyear;
        tableRow += "</tr>";
        myTable += tableRow;
    }
    
    myTable += "</table>";

    /// assemble table

    document.getElementById(parentID).appendChild(myTable);
}