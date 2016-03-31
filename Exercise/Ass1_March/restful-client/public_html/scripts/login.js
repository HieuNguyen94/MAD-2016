/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

(function ($) {
    $(document).ready(function () {
        var goButton = $("#go-btn"),
                nextButton = $("#next-btn");
        overlayDiv = $("#overlay");
        var STATUS_OK = "OK",
                STATUS_FAILED = "FAILED";
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

        // BEGIN Form effect
        $('.toggle').on('click', function () {
            $('.container').stop().addClass('active');
        });

        $('.close').on('click', function () {
            $('.container').stop().removeClass('active');
        });
//        END Form effect
        goButton.click(function (e) {
            e.preventDefault();
            var username = $("#Username").val(),
                    password = $("#Password").val();
            if (username !== "" && password !== "") {
                loginHandler(username, password);
            }
        });
        nextButton.click(function (e) {
            e.preventDefault();
            var rUsername = $("#RUsername").val(),
                    rPassword = $("#RPassword").val(),
                    rConfirmPassword = $("#RRepeatPassword").val();

            if (rPassword !== rConfirmPassword) {
                // TODO show alert or something
            } else {
                registerHandler(rUsername, rUsername);
            }
        });
        function loginHandler(username, password) {
            preloader.on();
            $.ajax({
                url: "http://localhost:8080/restful/user/login",
                type: "POST",
                contentType: "application/json; charset=utf-8",
                data: JSON.stringify({"username": username, "password": password})
            }).done(function (data) {
                if (data === STATUS_OK) {
                    console.log("Login successful");
                    sessionStorage.setItem("username", username);
                    window.location.href = "main.html";
                } else {
                    console.log("Login failed");
                }
            }).fail(function (error) {
                console.error(error);
            }).always(function () {
                preloader.off();
            });
        }
        function registerHandler(username, password) {
            preloader.on();
            $.ajax({
                url: "http://localhost:8080/restful/user/createsimple",
                type: "POST",
                contentType: "application/json; charset=utf-8",
                data: JSON.stringify({"username": username, "password": password})
            }).done(function (data) {
                console.info("Send OK " + "data");
                window.location.href = "main.html";
                sessionStorage.setItem("username", username);
            }).fail(function (error) {
                console.error(error);
            }).always(function () {
                preloader.off();
            });
        }
    });
})(jQuery);
