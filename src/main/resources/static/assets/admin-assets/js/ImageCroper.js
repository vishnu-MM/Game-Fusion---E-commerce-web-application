//
// $(function() {
//     // Multiple images preview in browser
//     let imagesPreview = function(input, placeToInsertImagePreview) {
//
//         if (input.files) {
//             let filesAmount = input.files.length;
//
//             for (i = 0; i < filesAmount; i++) {
//                 let reader = new FileReader();
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
let bs_modal = $('#modal');
let image = document.getElementById('image');
let cropper, reader, file, originalFileName;
let productId = document.getElementById('productId').value;
$("body").on("change", "#imageFiles", function(e) {
    let files = e.target.files;
    if (files && files.length > 0) {
        file = files[0];
        originalFileName = file.name;
        let done = function (url) {
            image.src = url;
            bs_modal.modal('show');
        };
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
bs_modal.on('shown.bs.modal', function () {
    cropper = new Cropper(image, {
        aspectRatio: 1,
        viewMode: 3,
        preview: '.preview'
    });
})
    .on('hidden.bs.modal', function () {
        cropper.destroy();
        cropper = null;
    });

$("#crop").click(function () {
    let canvas = cropper.getCroppedCanvas();
    let croppedImageDataURL = canvas.toDataURL();
    let blob = dataURItoBlob(croppedImageDataURL);
    let file = new File([blob], originalFileName, { type: 'image/png' });
    let formData = new FormData();
    formData.append('imageFiles', file);
    formData.append('productId', productId);
    $.ajax({
        url: '/dashboard/update-images/save/ajax',
        type: 'POST',
        data: formData,
        processData: false,
        contentType: false,
        beforeSend: function(xhr) {
            xhr.setRequestHeader($("meta[name='_csrf_header']").attr("content"), $("meta[name='_csrf']").attr("content"));
        },
        success: function(response) {
            location.reload()
        },
        error: function(error) {
            console.error(error);
        }
    });
});
function dataURItoBlob(dataURI) {
    let byteString = atob(dataURI.split(',')[1]);
    let ab = new ArrayBuffer(byteString.length);
    let ia = new Uint8Array(ab);
    for (let i = 0; i < byteString.length; i++) {
        ia[i] = byteString.charCodeAt(i);
    }
    return new Blob([ab], { type: 'image/png' });
}
$('.close-modal').on('click',function (e){
    bs_modal.hide()
    location.reload()
})