    let payment_method = 'RazorPay'
    let discount = 0
    let grandTotal = parseFloat($('#grandTotal').val())*100;
    let orderId = $('#orderId').val();
    let orderMainId = $('#orderMainId').val();
    console.log(orderId+" "+grandTotal)
    let options = {
    "key": "rzp_test_puElfrYmsC9EGx",
    "amount": grandTotal ,
    "currency": "INR",
    "name": "Game Fusion",
    "description": "Purchases",
    "image": "http://localhost:8080/static/Images/51mWHXY8hyL.jpg",
    "payment_id": orderId,
    
    "theme": {
        "color": "#088178",
    },
    "handler": function (response){
        console.log(response)
        window.location.href = '/verify-payment?orderId='+orderMainId
    }
};

let rzp1 = new Razorpay(options);
rzp1.on('payment.failed', function (response){
    console.log(response.error.code+" "+1);
    console.log(response.error.description+" "+2);
    console.log(response.error.source+" "+3);
    console.log(response.error.step+" "+4);
    console.log(response.error.reason+" "+5);
    console.log(response.error.metadata+" "+6);
    console.log(response.error.metadata.payment_id+" "+7);
    window.location.href = '/online-payment?orderId='+orderMainId
});

document.getElementById('rzp-button1').onclick = function(e){
    rzp1.open();    
    e.preventDefault();
}
