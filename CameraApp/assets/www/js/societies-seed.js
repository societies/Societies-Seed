var Client = {
		jid: '',
		password: '',
		connection: null,
		show_raw: true,
		show_log: true,

		// log to console if available
		log: function (msg) {
			if (Client.show_log && window.console) { console.log(msg); }
		},

		// show the raw XMPP information coming in
		raw_input: function (data) {
			if (Client.show_raw) {
				Client.log('RECV: ' + data);
			}
		},

		// show the raw XMPP information going out
		raw_output: function (data) {
			if (Client.show_raw) {
				Client.log('SENT: ' + data);
			}
		},

		// simplify connection status messages
		feedback: function(msg, col) {
			$('#feedback').html(msg).css('color', col);
		},

		// decide what to do with an incoming message
		handle_update: function (data) {
			var _d = $(data);
			var _message = _d.html();
			var _type = _d.attr('type');

			switch (_type) {
			case MessageType.MSG_TEXT:
				Client.show_text(_message);
				break;
			case MessageType.MSG_HTML:
				Client.show_html(_message);
				break;
			default:
				Client.log("Oh dear! I don't understand");
			}
		},

		// inject text
		show_text: function (m) {
			$('#message').text(m);
		},

		// inject html
		show_html: function (m) {
			var e = document.createElement('div');
			e.innerHTML = m;
			$('#message').html(e.childNodes[0].nodeValue);
		},

		on_connect: function (status) {
			if (status == Strophe.Status.CONNECTING) {
				Client.log('Connecting...');
				Client.feedback('Connecting... (1 of 2)', '#009900');
			} else if (status == Strophe.Status.CONNFAIL) {
				Client.log('Failed to connect!');
				Client.feedback('Connection failed', '#FF0000');
			} else if (status == Strophe.Status.DISCONNECTING) {
				Client.log('Disconnecting...');
				Client.feedback('Disconnecting...', '#CC6600');
			} else if (status == Strophe.Status.DISCONNECTED) {
				Client.log('Disconnected');
				Client.feedback('Disconnected', '#aa0000');
			} else if (status == Strophe.Status.CONNECTED) {
				Client.log("Almost done...");
				Client.feedback('Connecting... (2 of 2)', '#009900');
				// Init
				Client.connection.send($pres().c('priority').t('-1'));
				CommunityManager.init(Client.connection);
				
				// Modify UI
				Client.log("Connected");
				Client.feedback('Connected', '#009900');
				$('#profil h1').html(Client.jid);
				$('a#logout').show();
				$('#page_connection').slideUp();
				$('#page_discoverCommunities').slideDown();
				$('#page_myCommunities').slideDown();
			}
			return true;
		},
		
		on_disconnect: function() {
			$('#profil h1').html('Societies');
			$('a#logout').hide();
			$('#page_discoverCommunities').slideUp();
			$('#page_myCommunities').slideUp();
			$('#page_connection').slideDown();
		},
		
		on_joinCommunity: function(iq) {
			Client.log("Join IQ result", iq);
			Client.show_text('Community joined:'+$(iq).attr('from'));
		},
		
		on_error_joinCommunity: function(iq) {
			Client.log("Join IQ error", iq);
			Client.show_text('No community available:');
		},
		
		
		on_discoverCommunities: function(iq) {
			Client.log("Discover communities IQ result", iq);
			
			var communities = $(iq).find('item');
			var nbCommunities = communities.length;
			$('#page_discoverCommunities #content').html(nbCommunities+' communitie'+(nbCommunities > 1 ? 's' : '')+' available');
			$('<ul>').addClass('communityList').appendTo('#page_discoverCommunities #content');
			communities.each(function () {
	            var jid = $(this).attr('jid');
	            $('<li>').addClass('community').html('<a href="#">'+jid+'</a>').appendTo('#page_discoverCommunities #content ul');
	        });
		},
		on_error_discoverCommunities: function(iq) {
			Client.log("Discover communities IQ error", iq);
			$('#page_discoverCommunities #content').html('No communities available');
		},
};

jQuery(function() {
	console.log("init");
	
	// -- Init
	Client.feedback('Disconnected', '#aa0000');
	$('a#logout').hide();
	$('#page_discoverCommunities').hide();
	$('#page_myCommunities').hide();
	
	// -- If login action
	$('#loginConnection').click(function () {
		console.log("connect");
		// Connection
		Client.jid = $('#loginConnectionJid').get(0).value;
		Client.password = $('#loginConnectionPassword').get(0).value;
		var conn = new Strophe.Connection(Config.BOSH_SERVICE);
		console.log("go!");
		Client.connection = conn;
		Client.connection.rawInput = Client.raw_input;
		Client.connection.rawOutput = Client.raw_output;
		Client.connection.connect(
				Client.jid,
				Client.password,
				Client.on_connect,
				Client.on_connect_error
		);
		return false;
	});
	
	// -- If logout action
	$('#logout').live('click', function () {
		console.log('test');
		if (null != Client.connection) {
			Client.connection.disconnect();
		}
		Client.on_disconnect();
		return false;
	});
	
	
	// -- If discover communities
	$('#page_discoverCommunities h1').live('click', function () {
		$('#page_discoverCommunities #content').html('Wainting...');
		var managerJid = 'communitizer.socialblend.local';
		CommunityManager.discoverCommunities(managerJid, Client.on_discoverCommunities, Client.on_error_discoverCommunities);
		return false;
	});
	
	// -- If join a community
	$('.community a').live('click', function () {
		var communitiesEndpoint = 'communitizer.socialblend.local';
		var communityJid = $(this).text();
		CommunityManager.join(communitiesEndpoint, communityJid, Client.on_joinCommunity, Client.on_error_joinCommunity);
		return false;
	});
	
	
	
	
	
	// The following are not used for now
	
	// If logout action
	$('#userAction').bind('change', function () {
		$('#userAction option:selected').each(function () {
			if ('logout' == $(this).val()) {
				if (null != Client.connection) {
					Client.connection.disconnect();
				}
				Client.on_disconnect();
			}
		});
	});
	
	$('#search').bind('click', function () {
		Client.log("Discover communities");
		var managerJid = 'communitizer.socialblend.local';
		CommunityManager.init(Client.connection);
		CommunityManager.discoverCommunities(managerJid, Client.on_join, Client.on_error_join);
	});
});
