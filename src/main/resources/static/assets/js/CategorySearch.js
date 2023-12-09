    // Fetch and populate the category dropdown on page load
    $(document).ready(function () {
    $.ajax({
        url: '/user/category-list',
        type: 'GET',
        success: function (data) {
            var dropdown = $('#some-3');
            dropdown.empty();
            dropdown.append('<option>All Categories</option>');
            $.each(data, function (index, category) {
                dropdown.append('<option value="' + category.id + '">' + category.name + '</option>');
            });
        },
        error: function (error) {
            console.error('Error loading categories: ', error);
        }
    });
        $('#some-3').on('change', function () {
            var selectedCategoryId = $(this).val();
            if (selectedCategoryId !== "-1") {
                // Redirect to /category-filter/{id}
                window.location.href = '/user/category-filter/' + selectedCategoryId;
            }
        });
});

    // Handle search input and update results dynamically
    $('#some-2').on('input', function () {
    var searchValue = $(this).val();
    var categoryId = $('#some-3').val();

    $.ajax({
    url: 'user/category-list',
    type: 'GET',
    data: { search: searchValue},
    success: function (data) {
    // Update the search results dynamically
    // Replace the following line with your logic to update the UI
    console.log('Search results:', data);
},
    error: function (error) {
    console.error('Error searching categories: ', error);
}
});
});