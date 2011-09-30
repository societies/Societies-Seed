var Hello = {
    connection: null,
    start_time: null,

    log: function (msg) {
        $('#logXMPP').append("<p>" + msg + "</p>");
    },

    send_ping: function (to) {
        var ping = $iq({
            to: to,
            type: "get",
            id: "ping1"}).c("ping", {xmlns: "urn:xmpp:ping"});

        Hello.log("Sending ping to " + to + ".");

        Hello.start_time = (new Date()).getTime();
        Hello.connection.send(ping);
    },

    handle_pong: function (iq) {
        var elapsed = (new Date()).getTime() - Hello.start_time;
        Hello.log("Received pong from server in " + elapsed + "ms.");

        Hello.connection.disconnect();
        
        return false;
    }
};

$(document).bind('connect', function (ev, data) {
    var conn = new Strophe.Connection(Config.BOSH_SERVICE);

	    conn.connect(data.jid, data.password, function (status) {
        if (status === Strophe.Status.CONNECTED) {
            $(document).trigger('connected');
        } else if (status === Strophe.Status.DISCONNECTED) {
            $(document).trigger('disconnected');
        } else if (status === Strophe.Status.CONNECTING) {
            $(document).trigger('connecting');
        }
    });

    Hello.connection = conn;
});

$(document).bind('connected', function () {
    // inform the user
    Hello.log("Connection established.");

    Hello.connection.addHandler(Hello.handle_pong, null, "iq", null, "ping1");

    var domain = Strophe.getDomainFromJid(Hello.connection.jid);
    
    Hello.send_ping(domain);

});

$(document).bind('connecting', function () {
    // inform the user
    Hello.log("Connection is being established");


});

$(document).bind('disconnected', function () {
    Hello.log("Connection terminated.");

    // remove dead connection object
    Hello.connection = null;
});


function onDeviceReady() {
	console.log("PhoneGap Loaded, Device Ready");
	
	PhoneGap.addConstructor(function() {
	//Register the javascript plugin with PhoneGap
		console.log("Register Connection Listener plugin");
		PhoneGap.addPlugin('ConnectionListener', new ConnectionListener());
	 
	//Register the native class of plugin with PhoneGap : Not required anymore with 1.0.0
		//Use plugins.xml
		/*alert("Register Connection Listener plugin Java classes");

		navigator.add.addService("ConnectionPlugin","org.tssg.awalsh.ConnectionPlugin");*/
	});
}

function xmppConnect() {
	console.log("xmppConnect click");
    $('#loginDialog').hide();
    $('#logXMPP').show();
	$(document).trigger('connect', {
        jid: $('#username').val(),
        password: $('#userpass').val()
    });
    
    $('#userpass').val('');

}

function xmppConnectionRef() {
	console.log("xmppConnectionRef click");
    $('#logXMPP').hide();
    $('#loginDialog').show();
	
}

var watchID = null;

/**
 *  
 * @return Instance of ConnectionListener
 */
var ConnectionListener = function() { 
}
 
/**
 * @param directory The directory for which we want the listing
 * @param successCallback The callback which will be called when directory listing is successful
 * @param failureCallback The callback which will be called when directory listing encouters an error
 */
ConnectionListener.prototype.createListener = function(successCallback, failureCallback) {
 
	console.log("Create Connection Listener");

	return PhoneGap.exec(successCallback,    //Callback which will be called when plugin action is successful
	failureCallback,     //Callback which will be called when plugin action encounters an error
	'ConnectionPlugin',  //Telling PhoneGap that we want to run specified plugin
	'createListener',              //Telling the plugin, which action we want to perform
	[]);        //Passing a list of arguments to the plugin
};






var cameraCaptureByURI = 0;
var cameraCaptureByImage = 1;

var imageCapture = function(type) {
	function onURISuccess(imageURI) {
		alert("Captured Image URI: " + imageURI);
		console.log("Captured Image URI: " + imageURI);

	}
	
	function onImageSuccess(image) {
		console.log("Captured Image");
		jQuery("#smallImage").css("display","block");
		jQuery("#smallImage").attr("src","data:image/jpeg;base64," + image);

	}

	function onFail(message) {
		console.log('Failed because: ' + message);
	}
	console.log("imageCapture called");
	
	if (cameraCaptureByURI === type) {
		navigator.camera.getPicture(onURISuccess, onFail, {
			quality : 50,
			destinationType : Camera.DestinationType.FILE_URI
		});
		
	} else {
		navigator.camera.getPicture(onImageSuccess, onFail, {
			quality : 20,
			destinationType : Camera.DestinationType.DATA_URL
		});
	}

};


var connectionStatus = function() {

	var networkState = navigator.network.connection.type, states = {};

	states[Connection.UNKNOWN] = 'Unknown connection';
	states[Connection.ETHERNET] = 'Ethernet connection';
	states[Connection.WIFI] = 'WiFi connection';
	states[Connection.CELL_2G] = 'Cell 2G connection';
	states[Connection.CELL_3G] = 'Cell 3G connection';
	states[Connection.CELL_4G] = 'Cell 4G connection';
	states[Connection.NONE] = 'No network connection';

	alert('Connection type: ' + states[networkState]);
	console.log('Connection type: ' + states[networkState]);


};

var createConnectionListener = function() {
	console.log("Create Network Connection listener");

	function success(connectionStatus) {
		alert(connectionStatus.actionMessage + " " + connectionStatus.action);
		
	}
	
	function failure(connectionStatus) {
		alert(connectionStatus);
	}
    window.plugins.ConnectionListener.createListener(success, failure);
	
};




jQuery(function() {
	console.log("jQuery calls");

	document.addEventListener("deviceready", onDeviceReady, false);



	$('#cameraPictureURI').click(function() {
		imageCapture(cameraCaptureByURI);
	});

	$('#cameraPictureDisplay').click(function() {
		imageCapture(cameraCaptureByImage);
	});

	$('#connectStatus').click(function() {
		connectionStatus();
	});

	$('#connectStatusListener').click(function() {
		createConnectionListener();
	});
	
	$('#connectXMPP').click(function() {
		xmppConnect();
	});
});


