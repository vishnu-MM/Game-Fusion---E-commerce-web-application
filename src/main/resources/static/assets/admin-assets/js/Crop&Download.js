var bs_modal = $('#modal');
var image = document.getElementById('image');
var cropper, reader, file;
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

bs_modal.on('shown.bs.modal', function () {
    cropper = new Cropper(image, {
        aspectRatio: 1,
        viewMode: 3,
        preview: '.preview'
    });
}).on('hidden.bs.modal', function () {
    cropper.destroy();
    cropper = null;
});

$("#crop").click(function () {
    var canvas = cropper.getCroppedCanvas();

    // Convert the canvas to a data URL
    var croppedImageDataURL = canvas.toDataURL();

    // Trigger the download
    var downloadLink = document.createElement('a');
    downloadLink.href = croppedImageDataURL;
    downloadLink.download = 'cropped_image.png';
    downloadLink.click();
});