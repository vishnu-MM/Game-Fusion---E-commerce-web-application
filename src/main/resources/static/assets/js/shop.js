
(function ($) {

    'use strict';
    /*Product Details*/
    var productDetails = function () {
        $('.product-image-slider').slick({
            slidesToShow: 1,
            slidesToScroll: 1,
            arrows: false,
            fade: false,
            asNavFor: '.slider-nav-thumbnails',
        });

        $('.slider-nav-thumbnails').slick({
            slidesToShow: 5,
            slidesToScroll: 1,
            asNavFor: '.product-image-slider',
            dots: false,
            focusOnSelect: true,
            prevArrow: '<button type="button" class="slick-prev"><i class="fi-rs-angle-left"></i></button>',
            nextArrow: '<button type="button" class="slick-next"><i class="fi-rs-angle-right"></i></button>'
        });

        // Remove active class from all thumbnail slides
        $('.slider-nav-thumbnails .slick-slide').removeClass('slick-active');

        // Set active class to first thumbnail slides
        $('.slider-nav-thumbnails .slick-slide').eq(0).addClass('slick-active');

        // On before slide change match active thumbnail to current slide
        $('.product-image-slider').on('beforeChange', function (event, slick, currentSlide, nextSlide) {
            var mySlideNumber = nextSlide;
            $('.slider-nav-thumbnails .slick-slide').removeClass('slick-active');
            $('.slider-nav-thumbnails .slick-slide').eq(mySlideNumber).addClass('slick-active');
        });

        $('.product-image-slider').on('beforeChange', function (event, slick, currentSlide, nextSlide) {
            var img = $(slick.$slides[nextSlide]).find("img");
            $('.zoomWindowContainer,.zoomContainer').remove();
            $(img).elevateZoom({
                zoomType: "inner",
                cursor: "crosshair",
                zoomWindowFadeIn: 500,
                zoomWindowFadeOut: 750
            });
        });
        //Elevate Zoom
        if ( $(".product-image-slider").length ) {
            $('.product-image-slider .slick-active img').elevateZoom({
                zoomType: "inner",
                cursor: "crosshair",
                zoomWindowFadeIn: 500,
                zoomWindowFadeOut: 750
            });
        }
        //Filter color/Size
        $('.list-filter').each(function () {
            $(this).find('a').on('click', function (event) {
                event.preventDefault();
                $(this).parent().siblings().removeClass('active');
                $(this).parent().toggleClass('active');
                $(this).parents('.attr-detail').find('.current-size').text($(this).text());
                $(this).parents('.attr-detail').find('.current-color').text($(this).attr('data-color'));
            });
        });
        //Qty Up-Down

        $('.detail-qty').each(function () {
            qtyVal = parseInt($(this).find('.qty-val').text(), 10);
            $('.qty-up').on('click', function (event) {
                event.preventDefault();
                qtyVal = qtyVal + 1;
                if (qtyVal <= qtyMax) {
                    $(this).next().text(qtyVal);
                } else {
                    qtyVal = qtyMax;
                    $(this).prev().text(qtyVal);
                }

            });
            $('.qty-down').on('click', function (event) {
                event.preventDefault();
                qtyVal = qtyVal - 1;
                if (qtyVal > 1) {
                    $(this).prev().text(qtyVal);
                } else {
                    qtyVal = 1;
                    $(this).prev().text(qtyVal);
                }
            });
        });

        $('.dropdown-menu .cart_list').on('click', function (event) {
            event.stopPropagation();
        });
    };
    /* WOW active */
    new WOW().init();

    //Load functions
    $(document).ready(function () {
        productDetails();
    });

})(jQuery);
var qtyVal
var qtyMax = document.getElementById('product-qty').value
alertify.set('notifier', 'position', 'top-center');
function addToCart(productId) {
    $.ajax({
        type: 'GET',
        url: '/add-to-cart?ProductId=' + productId + '&Quantity=' + qtyVal,
        success: function(data) {
            if (data) alertify.success('Product successfully added to cart');
            else alertify.error("Something went wrong!!")
        },
        error: function() {
            alertify.error("Something went wrong!!!")
            console.log('Error fetching user data');
        }
    });
}
function buyNow(productId) {
    window.location.href='/buy-now?ProductId='+ productId +'&Qty='+ qtyVal
}
