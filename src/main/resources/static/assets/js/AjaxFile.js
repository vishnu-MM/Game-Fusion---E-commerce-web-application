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

function togglePhoneNumberInput() {
    let phoneNumberInput = document.getElementById("phone-number-input");
    let phoneNumberCheckbox = document.getElementById("use-default-phone");

    if (phoneNumberCheckbox.checked) {
        phoneNumberInput.value = "Your Default Phone Number";
        phoneNumberInput.disabled = true;
    } else {
        phoneNumberInput.value = "";
        phoneNumberInput.disabled = false;
    }
}