/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
$(document).ready(function () {
    var $userList = $('#user-list'),
        $formTemplate = $('.form-template'),
        $sepTemplate = $(".sep"),
        $detailModel = $("#myModal"),
        $createNewModel = $("#createnew"),
        userArray = [];
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
        $.each(data, function (index, value) {
            userArray.push({'id': value.id,
                            'username': value.username,
                            'password': value.password,
                            'email': value.email,
                            'status': value.status,
                            'name': value.name});
//            $("#users").append("User " + (index + 1).toString() + ": ");
//            $("#users").append(value.username + "</br>");
//            $("#users").append(value.email + "</br>");
        })
        generateUserList();
    });
    function addUser(index, userData){
        var $newUser = $formTemplate.clone();
        var $fieldNo = $newUser.find(".no"),
            $fieldUsername = $newUser.find(".name"),
            $fieldEmail = $newUser.find(".email"),
            $detailButton = $newUser.find(".detail-button");
        $newUser.css("display", "block");
        $fieldNo.html('#' + index);
        $fieldUsername.val(userData.username);
        $fieldEmail.val(userData.email);
        
        $newUser.appendTo($userList);
        ($sepTemplate.clone().css('display', 'block')).appendTo($userList);
        $detailButton.click(function(event) {
            $detailModel.find('.id').html(userData.id);
            $detailModel.find('.username').html(userData.username);
            $detailModel.find('.password').html(userData.password);
            $detailModel.find('.email').html(userData.email);
            $detailModel.find('.status').html(userData.status);
            $detailModel.find('.name').html(userData.name);            
        });
    }
    function generateUserList(){
        for (var i = 0, len = userArray.length; i < len; i++) {
            addUser(i, userArray[i]);
        }
    }
    
    $("#create-btn").click(function(event) {
        event.preventDefault();
        var nameField = $("#inputName").val(),
            emailField = $("#inputEmail").val(),
            usernameField = $("#inputUsername").val(),
            passwordField = $("#inputPassword").val();
            var content = {
                "username": usernameField,
                "email": emailField,
                "password": passwordField,
                "name": nameField};
            
        $.ajax({
            method: "POST",
            url: "http://localhost:8080/restful/user/create",
            contentType: "application/json; charset=utf-8",            
            xhrFields: {
                withCredentials: true
            }, 
            beforeSend: function (xhr) {
                xhr.setRequestHeader('Authorization', 'Basic ' + btoa("admin:admin"));
            },
            data: JSON.stringify(content)          
        }).done(function(data){
            console.log(data);
            if (data === "Y"){
                alert("Create succesfully");
            }            
            else if (data === "N"){
                alert("Username or email exists");
            }            
        }).fail(function(jqXHR, textStatus, errorThrown) {
            console.log(jqXHR, textStatus, errorThrown);
        });
        
    });
});


