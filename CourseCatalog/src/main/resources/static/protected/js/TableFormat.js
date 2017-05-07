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

    output(myTable, parentID); 
}

function insertCourseOfferings(parentID, courseOfferings) {
    //This method takes in a parent div and then a JSON string of course offerings
    var myTable = "<table><tr><th style='width: 100px; color: red;'>ID</th>";
    myTable += "<th style='width: 5px; color: red; text-align: right;'>Course ID</th>";
    myTable += "<th style='width: 25px; color: red; text-align: right;'>Year ID</th>";
    myTable += "<th style='width: 100px; color: red; text-align: right;'>Description</th>";
    myTable += "<th style='width: 25px; color: red; text-align: right;'>Extra Info</th><tr>";
    myTable += "<th style='width: 50px; color: red; text-align: right;'>Grade Levels</th><tr>";
    
    var courseOfferingsJSONString = JSON.parse(courseOfferings);
    var array = new Array();
    for (var i = 0; i < courseOfferingsJSONString.length; i++) {
        var obj = {
            id: courseOfferingsJSONString[i].id,
            courseId: courseOfferingsJSONString[i].courseId,
            yearId: courseOfferingsJSONString[i].yearId,
            description: courseOfferingsJSONString[i].description,
            info: courseOfferingsJSONString[i].info,
            gradeLevels: courseOfferingsJSONString[i].gradeLevels
        };
        array.push(obj);
    }
    
    for (var i = 0; i < array.length; i++) {
        var tableRow = "<tr>";
        var id = "<td>" + array[i].id; + "</td>";
        var courseId = "<td>" + array[i].courseId; + "</td>";
        var yearId = "<td>" + array[i].yearId; + "</td>";
        var description = "<td>" + array[i].description; + "</td>";
        var info = "<td>" + array[i].info; + "</td>";
        var gradeLevels = "<td>" + array[i].gradeLevels; + "</td>";
        tableRow += id;
        tableRow += courseId;
        tableRow += yearId;
        tableRow += description;
        tableRow += info;
        tableRow += gradeLevels;
        tableRow += "</tr>";
        myTable += tableRow;
    }
    
    myTable += "</table>";

    output(myTable, parentID);  
}

function insertAllRequests(parentID, allRequests) {
    //This method takes in a parent div and then a JSON string of course offerings
    var myTable = "<table><tr><th style='width: 100px; color: red;'>ID</th>";
    myTable += "<th style='width: 50px; color: red; text-align: right;'>Individual ID</th>";
    myTable += "<th style='width: 50px; color: red; text-align: right;'>Course ID</th>";
    myTable += "<th style='width: 50px; color: red; text-align: right;'>First Alt. Course</th>";
    myTable += "<th style='width: 50px; color: red; text-align: right;'>Second Alt. Course</th><tr>";
    myTable += "<th style='width: 50px; color: red; text-align: right;'>Notes</th><tr>";
    myTable += "<th style='width: 50px; color: red; text-align: right;'>Advisor Approved</th><tr>";
    myTable += "<th style='width: 50px; color: red; text-align: right;'>Parent Approved</th><tr>";
    
    var allRequestsJSONString = JSON.parse(allRequests);
    var array = new Array();
    for (var i = 0; i < allRequestsJSONString.length; i++) {
        var obj = {
            id: allRequestsJSONString[i].id,
            individual_id: allRequestsJSONString[i].individual_id,
            course_id: allRequestsJSONString[i].course_id,
            FirstAlternateCourse_id: allRequestsJSONString[i].FirstAlternateCourse_id,
            SecondAlternateCourse_id: allRequestsJSONString[i].SecondAlternateCourse_id,
            notes: allRequestsJSONString[i].notes,
            AdvisorReviewed: allRequestsJSONString[i].AdvisorReviewed,
            ParentReviewed: allRequestsJSONString[i].ParentReviewed
        };
        array.push(obj);
    }
    
    for (var i = 0; i < array.length; i++) {
        var tableRow = "<tr>";
        var id = "<td>" + array[i].id; + "</td>";
        var individual_id = "<td>" + array[i].individual_id; + "</td>";
        var course_id = "<td>" + array[i].course_id; + "</td>";
        var FirstAlternateCourse_id = "<td>" + array[i].FirstAlternateCourse_id; + "</td>";
        var SecondAlternateCourse_id = "<td>" + array[i].SecondAlternateCourse_id; + "</td>";
        var notes = "<td>" + array[i].notes; + "</td>";
        var AdvisorReviewed = "<td>" + array[i].AdvisorReviewed; + "</td>";
        var ParentReviewed = "<td>" + array[i].ParentReviewed; + "</td>";
        tableRow += id;
        tableRow += individual_id;
        tableRow += course_id;
        tableRow += FirstAlternateCourse_id;
        tableRow += SecondAlternateCourse_id;
        tableRow += notes;
        tableRow += AdvisorReviewed;
        tableRow += ParentReviewed;
        tableRow += "</tr>";
        myTable += tableRow;
    }
    
    myTable += "</table>";

    output(myTable, parentID);  
}