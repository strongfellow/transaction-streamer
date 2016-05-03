
require([ "sockjs-0.3.4", "stomp", "jquery", "jquery-ui/autocomplete" ], function( sock, stomp, $ ) {

    var stompClient = null;

    function appendTx(message) {
        var p = $('<p>').html(message.body).hide().addClass('tx');
	    $('#transactions-list').prepend(p);
	    $('#transactions-list p').slice(20).remove();
	    p.slideDown(400, function() {
	    });
    }

    function connect() {
	var socket = new SockJS('/stomp');
	stompClient = Stomp.over(socket);
	stompClient.connect({}, function(frame) {
	    console.log('Connected: ' + frame);
	    stompClient.subscribe('/topic/transactions', function(message) {
		appendTx(message);
	    });
	});
    }

    function disconnect() {
	if (stompClient != null) {
	    stompClient.disconnect();
	}
	console.log("Disconnected");
    }

    $(function() { connect(); });
});
