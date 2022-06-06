var items = {'its': []};

function submitOrder() {
    console.log(items);

    $.ajax({
        type: 'POST',
        url: "http://" + location.hostname + ":81/order/make-order",
        contentType: "application/json",
        data: JSON.stringify(items),
        dataType: 'json',
        success: function(data) {
            console.log('success',data);
        },
    });
}

function addItem() {
    if (document.getElementById("type").value === "" || document.getElementById("color").value === "" || document.getElementById("number").value === "") {
        alert("Please select a type, color and a number!");
        return;
    }

    // Add to JSON Response
    var obj = {};
    obj['itemType'] = document.getElementById("type").value;
    obj['isDark'] = document.getElementById("color").value;
    obj['number'] = document.getElementById("number").value;
    items.its.push(obj);


    // Add to HTML Table
    var table = document.getElementById("lstItems").insertRow();

    var type = table.insertCell();
    type.innerHTML = document.getElementById("type").value;

    var color = table.insertCell();
    color.innerHTML = document.getElementById("color").value;

    var number = table.insertCell();
    number.innerHTML = document.getElementById("number").value;

    // Clear inputs
    document.getElementById("type").value = "";
    document.getElementById("number").value = "1";
    document.getElementById("color").value = "";
}