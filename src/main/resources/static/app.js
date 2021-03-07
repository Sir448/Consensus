var stompClient = null;
var options = [];

function setConnected(connected) {
    $("#connect").prop("disabled", connected);
    $("#disconnect").prop("disabled", !connected);
    if (connected) {
        $("#conversation").show();
    }
    else {
        $("#conversation").hide();
    }
    $("#greetings").html("");
}

function connect() {
    var socket = new SockJS('/gs-guide-websocket');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        setConnected(true);
        console.log('Connected: ' + frame);
        stompClient.subscribe('/topic/session/2', function (test) {
            showGreeting(JSON.parse(test.body));
        });
//        stompClient.subscribe('/test/2', function (test) {
//                    showGreeting(JSON.parse(test.body).content);
//        });
    });
}

function disconnect() {
    if (stompClient !== null) {
        stompClient.disconnect();
    }
    setConnected(false);
    console.log("Disconnected");
}

function sendName() {
    stompClient.send("/app/addOptions/session/2", {}, JSON.stringify({'options':options}));
//    stompClient.send("/app/changeSettings/session/2", {}, JSON.stringify({'Decision_Type': 1, 'Majority': 3}));
//    stompClient.send("/app/changeSettings/session/5", {}, JSON.stringify({'name': $("#name").val()}));
//    $("#greetings").append("<tr><td>Test:" + JSON.stringify({'Decision_Type': $("#decision_type").val(), 'Majority': $("#majority").val()}) + "</td></tr>");
//    $("#greetings").append("<tr><td>" + $("#name").val() + "</td></tr>");
}

function addName(){
    $("#greetings").append("<tr><td>" + $("#name").val() + "</td></tr>");
    options.push($("#name").val());
    $("#name").val('');
}

function showGreeting(message) {
    var output = "{"
    for (const key in message){
        output += `<br> ${key}: ${message[key]},`;
    }
    output+="<br>}"
    $("#greetings").append("<tr><td>" + output + "</td></tr>");
}

$(function () {
    $("form").on('submit', function (e) {
        e.preventDefault();
    });
    $( "#connect" ).click(function() { connect(); });
    $( "#disconnect" ).click(function() { disconnect(); });
    $("#add").click(function(){addName();});
    $( "#send" ).click(function() {
//     for(var i = 0; i < options.length; i++){
        $("#greetings").empty();
        sendName();
        options = [];
//     }
//        $("#greetings").val('');
     });
//    $("#send").click(function() {
//        $("#greetings").append("<tr><td>" + options[0] + "</td></tr>");
//    });
});