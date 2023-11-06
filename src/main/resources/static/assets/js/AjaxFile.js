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
                let name = data.firstName+" "+data.lastName;
                $('#name').text(name);
                $('#email').text(data.username);
                $('#phone').text(data.phone);
                toggleDropdown();
            },
            error: function() {
                console.log('Error fetching user data');
            }
        });
    });

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

$(document).ready(function() {
    function sendGetRequest(url) {
        alert(url+" "+userId)
        $.ajax({
            type: 'GET',
            url: url+userId,
            dataType: 'json',
            success: function(data) {
                // Handle the response data
            },
            error: function() {
                console.log('Error sending GET request');
            }
        });
    }

    $('#editProfileLink').on('click', function(e) {
        e.preventDefault();
        const url = '/user/edit-profile/';
        sendGetRequest(url);
    });

    $('#myOrdersLink').on('click', function(e) {
        e.preventDefault();
        const url = '/user/my-orders/';
        sendGetRequest(url);
    });

    $('#myAddressLink').on('click', function(e) {
        e.preventDefault();
        const url = '/user/my-address/';
        sendGetRequest(url);
    });

    $('#forgotPasswordLink').on('click', function(e) {
        e.preventDefault();
        const url = '/user/forgot-password';
        sendGetRequest(url);
    });
});