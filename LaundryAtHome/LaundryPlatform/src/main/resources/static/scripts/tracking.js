function makeComplaint() {
    console.log("makeComplaint");

    if (document.getElementById("complaint_name").value === "" || document.getElementById("complaint_description").value === "") {
        alert("Please fill in all fields!");
        return;
    }

    var complaint = {};
    complaint['orderId'] = document.getElementById("order_id").textContent;
    complaint['title'] = document.getElementById("complaint_name").value;
    complaint['description'] = document.getElementById("complaint_description").value;

    $.ajax({
        type: 'POST',
        url: "http://" + location.hostname + ":81/order/complaint",
        contentType: "application/json",
        data: JSON.stringify(complaint),
        async: false,
        success: function (data) {
            console.log('success', data);
            alert("Complaint submitted successfully!");
        },
        error: function (data) {
            console.log('error', data);
            alert("Error submitting complaint!");
        }
    });
}

function cancelOrder() {
    console.log("cancelOrder");

    var orderId = document.getElementById("order_id").textContent;

    $.ajax({
        type: 'POST',
        url: "http://" + location.hostname + ":81/order/cancelOrder/" + orderId,
        async: false,
        success: function (data) {
            console.log('success', data);
            alert("Order cancelled successfully!");
        },
        error: function (data) {
            console.log('error', data);
            alert("Error cancelling order!");
        }
    });
}