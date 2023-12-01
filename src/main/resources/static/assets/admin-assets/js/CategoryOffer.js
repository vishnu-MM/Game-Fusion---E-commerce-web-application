let checkBox = $('#show-offer-input')
let inputField = $('#offer-input')

if (!checkBox.prop('checked')) inputField.hide()

checkBox.on('change',function (){
    if (checkBox.prop('checked')) inputField.show()
    else inputField.hide()
})

$('#add-category-btn').on('click',function (e){
    e.stopPropagation()
    e.preventDefault()

    let form = $('#add-category-form')
    let minimumAmount = $('#offer-input-minimum')
    let discount = $('#offer-input-percentage')
    let expiryDate = $('#offer-input-expiryDate')

    if ( checkBox.prop('checked') ) {

        if ( minimumAmount.val() === '') $('#offer-input-minimum-error').text("Minimum Price Cannot be empty")
        if ( discount.val() === '') $('#offer-input-percentage-error').text("Discount Percentage Cannot be empty")
        if ( expiryDate.val() === '') $('#offer-input-expiryDate-error').text("Expiry Date Percentage Cannot be empty")

        minimumAmount.on('input', function () { $('#offer-input-minimum-error').text(""); });
        discount.on('input', function () { $('#offer-input-percentage-error').text(""); });
        expiryDate.on('input', function () { $('#offer-input-expiryDate-error').text(""); });

        $('<input>').attr({
            type: 'hidden',
            name: 'minimumAmount',
            value: minimumAmount.val()
        }).appendTo(form);
        $('<input>').attr({
            type: 'hidden',
            name: 'discount',
            value: discount.val()
        }).appendTo(form);
        $('<input>').attr({
            type: 'hidden',
            name: 'expiryDate',
            value: expiryDate.val()
        }).appendTo(form);
    }
    // console.log(form.serializeArray())
    if ((!checkBox.prop('checked')) ||
       (( checkBox.prop('checked') &&
       (minimumAmount.val() !== '' &&  discount.val() !== '' && expiryDate.val() !== ''))))
        form.submit()
})