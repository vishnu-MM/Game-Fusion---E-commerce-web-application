let userId;
$(document).ready(function() {
    $('#dropdownAccount').on('click', function (e) {
        e.stopPropagation();
        $.ajax({
            type: 'GET',
            url: '/user/my-profile',
            dataType: 'json',
            success: function(data) {
                userId = data.id;
                let name = data.firstName
                if (data.lastName != null) name+=' '+data.lastName;
                $('#name').text(name);
                $('#email').text(data.username);
                $('#phone').text(data.phone);
                toggleDropdown();
            },
            error: function() { console.log('Error fetching user data'); }
        });
    });

    $('#invite-link').on('click',function (){
        $.ajax({
            type: 'GET',
            url:'/user/referral-link',
            success: function (response){
                $('#referral-link').attr('value',response)
            },
            error:function (error){ alert("Something went wrong") }
        })
    })

    $('.dropdown-item').on('click', function() {
        toggleDropdown();
    });

    function toggleDropdown() {
        const dropdown = $('.dropdown-menu');
        if (dropdown.hasClass('show')) {
            dropdown.removeClass('show');
        } else {
            dropdown.addClass('show');
        }
    }
});
function showModal(id,qty) {
    $('#CartID').attr('value',id)
    $('#availableQty').attr('value',qty)
    $('#exampleModalLong').modal('show')
}
function hideModal() {
    $('#exampleModalLong').modal('hide')
}

function isValid() {
    const qty = parseInt(document.getElementById("qty").value);
    const availableQty = parseInt(document.getElementById("availableQty").value);
    if (qty <= 0) {
        alert("Please enter a quantity greater than 0.");
        return false;
    }
        if (availableQty < qty) {
        alert("Oops!! We dont have that much item in stock."+availableQty+" < "+qty);
        return false;
    }
    return true;
}
function copyToClipBoard() {
    alertify.set('notifier', 'position', 'middle-center');
    $.ajax({
        type: 'GET',
        url:'/user/referral-link',
        success: function (response){
            navigator.clipboard.writeText(response)
                .then(function() {

                    alertify.success('Link copied to clipboard!');
                })
                .catch(function(error) {
                    console.error('Error copying to clipboard: ', error);
                    alertify.error('Something went wrong');
                });
        },
        error:function (error){
            alert("Something went wrong")
        }
    })

}