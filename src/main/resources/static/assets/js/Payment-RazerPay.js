    // let payment_method = 'RazorPay'
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
    "image": "https://neonmama.com/cdn/shop/products/pink_70c0d167-714a-4122-a090-f7e959706fbd_1600x.png?v=1635060263",
    "payment_id": orderId,
    "theme": { "color": "#088178",},
    "handler": function (response){ window.location.href = '/verify-payment?orderId='+orderMainId }
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
    Swal.fire({
        title: 'Payment failed!!!',
        text: 'Oops! Something went wrong...',
        icon: 'warning',
        showCancelButton: true,
        confirmButtonText: 'Try again',
        cancelButtonText: 'Try, after some times',
    }).then((result) => {
        if (result.isConfirmed) rzp1.open();
        else window.location.href='/my-orders'
    });
});

document.getElementById('rzp-button1').onclick = function(e){
    $.ajax({
        type: 'GET',
        url: '/payment-status-confirmation/'+$('#orderId').val(),
        success:function (response){
            if (response){
                rzp1.open();
                e.preventDefault();
            } else {
                alertify.set('notifier', 'position', 'top-center');
                alertify.success('Payment is already completed');
                window.location.href='/my-orders'
            }
        },
        error : function (error){console.log(error)}
    })

}
