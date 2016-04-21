
require([ "sockjs-0.3.4", "stomp", "jquery", "jquery-ui/autocomplete" ], function( sock, stomp, sock, $ ) {
  $("<p>").html("jquery loaded").appendTo("body");
  $( "<input>" )
    .autocomplete({ source: [ "One", "Two", "Three" ]})
    .appendTo( "body" );

  var stompClient = null;
        
  function setConnected(connected) {
    document.getElementById('connect').disabled = connected;
    document.getElementById('disconnect').disabled = !connected;
    document.getElementById('conversationDiv').style.visibility = connected ? 'visible' : 'hidden';
    document.getElementById('response').innerHTML = '';
  }
        
  function connect() {
    var socket = new sock.SockJS('/stomp');
    stompClient = stomp.Stomp.over(socket);            
    stompClient.connect({}, function(frame) {
      setConnected(true);
      console.log('Connected: ' + frame);
      stompClient.subscribe('/topic/transactions', function(greeting){
        showGreeting(JSON.parse(greeting.body).content);
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
    var name = document.getElementById('name').value;
    stompClient.send("/app/hello", {}, JSON.stringify({ 'name': name }));
  }
        
  function showGreeting(message) {
    var response = document.getElementById('response');
    var p = document.createElement('p');
    p.style.wordWrap = 'break-word';
    p.appendChild(document.createTextNode(message));
    response.appendChild(p);
  }
});
