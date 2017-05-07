

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

    outputTo(parentID,myTable);  
}
