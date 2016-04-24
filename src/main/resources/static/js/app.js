
require([ "sockjs-0.3.4", "stomp", "jquery", "jquery-ui/autocomplete" ], function( sock, stomp, $ ) {
  $("<p>").html("jquery loaded").appendTo("body");
  $( "<input>" )
    .autocomplete({ source: [ "One", "Two", "Three" ]})
    .appendTo( "body" );

    $("#connect").click(connect);
    $("#disconnect").click(disconnect);
    
  var stompClient = null;
        
  function setConnected(connected) {
    $('#connect').disabled = connected;
    $('#disconnect').disabled = !connected;
  }

  function connect() {
    var socket = new SockJS('/stomp');
    stompClient = Stomp.over(socket);            
    stompClient.connect({}, function(frame) {
      setConnected(true);
      console.log('Connected: ' + frame);
	stompClient.subscribe('/topic/transactions', function(greeting){
	    console.log(greeting.body);
//        showGreeting(JSON.parse(greeting.body).content);
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
  
  function sendName() {
    var name = $('#name').value;
    stompClient.send("/app/hello", {}, JSON.stringify({ 'name': name }));
  }
        
  function showGreeting(message) {
    $('<p>').html(message).appendTo($('#response'));
  }
});
