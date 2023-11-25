//
// $(function() {
//     // Multiple images preview in browser
//     var imagesPreview = function(input, placeToInsertImagePreview) {
//
//         if (input.files) {
//             var filesAmount = input.files.length;
//
//             for (i = 0; i < filesAmount; i++) {
//                 var reader = new FileReader();
//
//                 reader.onload = function(event) {
//                     console.log(event.target.result)
//                     $($.parseHTML('<img class="m-2 border border-dark rounded" onclick="displayImage(this.src)">'))
//                         .attr('src', event.target.result).appendTo(placeToInsertImagePreview);
//                 }
//
//                 reader.readAsDataURL(input.files[i]);
//             }
//         }
//
//     };
//
//     $('#imageFiles').on('change', function() {
//         imagesPreview(this, 'div.gallery');
//     });
// });




var bs_modal = $('#modal');
var image = document.getElementById('image');

var cropper,reader,file;


$("body").on("change", "#imageFiles", function(e) {
    var files = e.target.files;
    var done = function (url) {
        image.src = url;
        bs_modal.modal('show');
    };


    if (files && files.length > 0) {
        file = files[0];

        if (URL) {
            done(URL.createObjectURL(file));
        } else if (FileReader) {
            reader = new FileReader();
            reader.onload = function (e) {
                done(reader.result);
            };
            reader.readAsDataURL(file);
        }
    }
});

bs_modal.on('shown.bs.modal', function() {
    console.log(image);
    cropper = new Cropper(image, {
        aspectRatio: 1,
        viewMode: 3,
        preview: '.preview'
    });
}).on('hidden.bs.modal', function() {
    cropper.destroy();
    cropper = null;
});

$("#crop").click(function() {
    canvas = cropper.getCroppedCanvas({
        width: 160,
        height: 160,
    });

    canvas.toBlob(function(blob) {
        url = URL.createObjectURL(blob);
        var reader = new FileReader();
        reader.readAsDataURL(blob);
        reader.onloadend = function() {
            var base64data = reader.result;
            //alert(base64data);
            $.ajax({
                type: "POST",
                dataType: "json",
                url: "crop_image_upload.php",
                data: {image: base64data},
                success: function(data) {
                    bs_modal.modal('hide');
                    alert("success upload image");
                }
            });
        };
    });
});

