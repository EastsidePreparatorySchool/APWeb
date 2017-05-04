//function insertStudents(parentID, students) {
//    //This method takes in a parent div and then a JSON string of students
//    var myTable = "<table><tr><th style='width: 100px; color: red;'>ID</th>";
//    myTable += "<th style='width: 100px; color: red; text-align: right;'>First Name</th>";
//    myTable += "<th style='width: 100px; color: red; text-align: right;'>Last Name</th>";
//    myTable += "<th style='width: 100px; color: red; text-align: right;'>Login</th>";
//    myTable += "<th style='width: 100px; color: red; text-align: right;'>Graduation Year</th><tr>";
//    
//    var studentsJSONString = JSON.parse(students);
//    var array = new Array();
//    for (var i = 0; i < studentsJSONString.length; i++) {
//        var obj = {
//            id: studentsJSONString[i].id,
//            firstname: studentsJSONString[i].firstname,
//            lastname: studentsJSONString[i].lastname,
//            login: studentsJSONString[i].login,
//            gradyear: studentsJSONString[i].gradyear
//        };
//        array.push(obj);
//    }
//    
//    for (var i = 0; i < array.length; i++) {
//        var tableRow = "<tr>";
//        var id = "<td>" + array[i].id; + "</td>";
//        var firstname = "<td>" + array[i].firstname; + "</td>";
//        var lastname = "<td>" + array[i].lastname; + "</td>";
//        var login = "<td>" + array[i].login; + "</td>";
//        var gradyear = "<td>" + array[i].gradyear; + "</td>";
//        tableRow += id;
//        tableRow += firstname;
//        tableRow += lastname;
//        tableRow += login;
//        tableRow += gradyear;
//        tableRow += "</tr>";
//        myTable += tableRow;
//    }
//    
//    myTable += "</table>";
//
//    /// assemble table
//
//    document.getElementById(parentID).appendChild(myTable);
//}

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

    output(myTable);
    /// assemble table
    var div = document.getElementById(parentID);
    div.appendChild(myTable);    
}