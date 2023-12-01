$(document).ready(function (){
    $.ajax({
        type: 'GET',
        url: '/dashboard/cancel-order-request/count',
        success:function (response){ $('#cancel-request-notification').text(response)},
        error:function (error) {console.log(error)}
    })
})