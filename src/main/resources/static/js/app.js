
require([ "sockjs-0.3.4", "stomp", "jquery", "jquery-ui/autocomplete" ], function( sock, stomp, $ ) {
    $("<p>").html("jquery loaded").appendTo("body");

    $("#connect").click(connect);
    $("#disconnect").click(disconnect);
    
    var stompClient = null;
        
    function setConnected(connected) {
	$('#connect').disabled = connected;
	$('#disconnect').disabled = !connected;
    }

    function appendTx(message) {
	var n = 10;
	var p = $('<p>').html(message.body).hide();
	$('#transactions-list').prepend(p);
	$('#transactions-list p').slice(n).slideUp();
	p.slideDown({
	    complete: function() {
		$('#transactions-list p').slice(n).remove();
	    }
	});
    }
    
    function connect() {
	var socket = new SockJS('/stomp');
	stompClient = Stomp.over(socket);            
	stompClient.connect({}, function(frame) {
	    setConnected(true);
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
	setConnected(false);
	console.log("Disconnected");
    }
  
});
