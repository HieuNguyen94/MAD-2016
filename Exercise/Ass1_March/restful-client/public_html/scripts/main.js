(function ($) {

    $.fn.menumaker = function (options) {

        var cssmenu = $(this), settings = $.extend({
            title: "Menu",
            format: "dropdown",
            breakpoint: 768,
            sticky: false
        }, options);

        return this.each(function () {
            cssmenu.find('li ul').parent().addClass('has-sub');
            if (settings.format != 'select') {
                cssmenu.prepend('<div id="menu-button">' + settings.title + '</div>');
                $(this).find("#menu-button").on('click', function () {
                    $(this).toggleClass('menu-opened');
                    var mainmenu = $(this).next('ul');
                    if (mainmenu.hasClass('open')) {
                        mainmenu.hide().removeClass('open');
                    } else {
                        mainmenu.show().addClass('open');
                        if (settings.format === "dropdown") {
                            mainmenu.find('ul').show();
                        }
                    }
                });

                multiTg = function () {
                    cssmenu.find(".has-sub").prepend('<span class="submenu-button"></span>');
                    cssmenu.find('.submenu-button').on('click', function () {
                        $(this).toggleClass('submenu-opened');
                        if ($(this).siblings('ul').hasClass('open')) {
                            $(this).siblings('ul').removeClass('open').hide();
                        } else {
                            $(this).siblings('ul').addClass('open').show();
                        }
                    });
                };

                if (settings.format === 'multitoggle')
                    multiTg();
                else
                    cssmenu.addClass('dropdown');
            } else if (settings.format === 'select')
            {
                cssmenu.append('<select style="width: 100%"/>').addClass('select-list');
                var selectList = cssmenu.find('select');
                selectList.append('<option>' + settings.title + '</option>', {
                    "selected": "selected",
                    "value": ""});
                cssmenu.find('a').each(function () {
                    var element = $(this), indentation = "";
                    for (i = 1; i < element.parents('ul').length; i++)
                    {
                        indentation += '-';
                    }
                    selectList.append('<option value="' + $(this).attr('href') + '">' + indentation + element.text() + '</option');
                });
                selectList.on('change', function () {
                    window.location = $(this).find("option:selected").val();
                });
            }

            if (settings.sticky === true)
                cssmenu.css('position', 'fixed');

            resizeFix = function () {
                if ($(window).width() > settings.breakpoint) {
                    cssmenu.find('ul').show();
                    cssmenu.removeClass('small-screen');
                    if (settings.format === 'select') {
                        cssmenu.find('select').hide();
                    } else {
                        cssmenu.find("#menu-button").removeClass("menu-opened");
                    }
                }

                if ($(window).width() <= settings.breakpoint && !cssmenu.hasClass("small-screen")) {
                    cssmenu.find('ul').hide().removeClass('open');
                    cssmenu.addClass('small-screen');
                    if (settings.format === 'select') {
                        cssmenu.find('select').show();
                    }
                }
            };
            resizeFix();
            return $(window).on('resize', resizeFix);

        });
    };
})(jQuery);

(function ($) {
    $(document).ready(function () {

        $(document).ready(function () {
            $("#cssmenu").menumaker({
                title: "Menu",
                format: "dropdown"
            });

            $("#cssmenu a").each(function () {
                var linkTitle = $(this).text();
                $(this).attr('data-title', linkTitle);
            });
        });
        var DATA_STRUCTURE = {
            "COVER_BACKGROUND_URL": "coverBackgroundUrl",
            "PROFILE_IMAGE_URL": "avatar",
            "NAME": "name",
            "PROFESSION": "profession",
            "ADDRESS": "address",
            "COMPANY": "company",
            "EMAIL": "email",
            "PASSWORD": "password",
            "USERNAME": "username"
        },
        CARD_TEMPLATE = "cardTemplate";
        var $cardList = $(".card-list"),
                userList = null;
        var cardMarkup = '<div class="col-md-4 col-sm-6 li">'
                + '<div class="card-container manual-flip">'
                + '<div class="card">'
                + '<div class="front">'
                + '<div class="cover">'
                + '<img src="${coverBackgroundUrl}"/>'
                + '</div>'
                + '<div class="user">'
                + '<img class="img-circle" src="${avatar}"/>'
                + '</div>'
                + '<div class="content">'
                + '<div class="main">'
                + '<h3 class="name">${name}</h3>'
                + '<p class="profession">${profession}</p>'
                + '<h5><i class="fa fa-map-marker fa-fw text-muted"></i> ${address}</h5>'
                + '<h5><i class="fa fa-building-o fa-fw text-muted"></i> ${company}</h5>'
                + '<h5><i class="fa fa-envelope-o fa-fw text-muted"></i> ${email}</h5>'
                + '</div>'
                + '<div class="footer">'
                + '<button class="btn btn-simple rotate-btn"  data-toggle="modal" data-target="#createnew">'
                + '<i class="fa fa-mail-forward"></i> Edit Information'
                + '</button>'
                + '</div>'
                + '</div>'
                + '</div> <!-- end front panel -->'
                + '</div> <!-- end card -->'
                + '</div> <!-- end card-container -->'
                + '</div> <!-- end col sm 3 -->';
        $.template(CARD_TEMPLATE, cardMarkup);
        var preloader = new $.materialPreloader({
            position: 'bottom',
            height: '7px',
            col_1: '#4285F4',
            col_2: '#EA4335',
            col_3: '#FBBC05',
            col_4: '#34A853',
            fadeIn: 50,
            fadeOut: 300
        });
        $(".card-list").on("click", "button", function () {
            var userIndex = $(this).parents(".li").index();
            if (userList !== null && userList.length > 0) {
                fillEditForm(userList[userIndex]);
            }
        });
        $("#save-btn").click(function() {
            
        });
        $(".rotate-btn").click(editHandler);
        function editHandler() {}// TODO
        function loadAllUser() {
            preloader.on();
            $.ajax({
                url: "http://localhost:8080/restful/user/all",
                method: "GET",
                xhrFields: {
                    withCredentials: true
                },
                beforeSend: function (xhr) {
                    xhr.setRequestHeader('Authorization', 'Basic ' + btoa("admin:admin"));
                }
            }).done(function (data) {
                userList = data;
                generateCard(data);
            }).fail(function (error) {
                console.error(error);
            }).always(function () {
                preloader.off();
            });
        }

        function generateCard(cardList) {
            var size = cardList.length;
            for (var i = 0; i < size; i++) {
                addCard(cardList[i]);
            }
        }
        function addCard(cardData) {
            // Filter data if necessary
            cardData[DATA_STRUCTURE.COVER_BACKGROUND_URL] = cardData[DATA_STRUCTURE.COVER_BACKGROUND_URL] || "images/rotating_card_thumb.png";
            cardData[DATA_STRUCTURE.PROFILE_IMAGE_URL] = cardData[DATA_STRUCTURE.PROFILE_IMAGE_URL] || "images/rotating_card_profile2.png";
            cardData[DATA_STRUCTURE.NAME] = cardData[DATA_STRUCTURE.NAME] || "Andrew Mike";
            cardData[DATA_STRUCTURE.PROFESSION] = cardData[DATA_STRUCTURE.PROFESSION] || "Web Developer";
            cardData[DATA_STRUCTURE.ADDRESS] = cardData[DATA_STRUCTURE.ADDRESS] || "Bucharest, Romania";
            cardData[DATA_STRUCTURE.COMPANY] = cardData[DATA_STRUCTURE.COMPANY] || "Bucharest, Romania";
            cardData[DATA_STRUCTURE.EMAIL] = cardData[DATA_STRUCTURE.EMAIL] || "mike@creative-tim.com";
            $.tmpl(CARD_TEMPLATE, cardData).appendTo($cardList);
        }
        function fillEditForm(data) {
            $("#inputName").val(data[DATA_STRUCTURE.NAME]);
            $("#inputProfession").val(data[DATA_STRUCTURE.PROFESSION]);
            $("#inputAddress").val(data[DATA_STRUCTURE.ADDRESS]);
            $("#inputCompany").val(data[DATA_STRUCTURE.COMPANY]);
            $("#inputEmail").val(data[DATA_STRUCTURE.EMAIL]);
            $("#inputUsername").val(data[DATA_STRUCTURE.USERNAME]);
            $("#inputPassword").val(data[DATA_STRUCTURE.PASSWORD]);
        }
        loadAllUser();
    });
})(jQuery);
