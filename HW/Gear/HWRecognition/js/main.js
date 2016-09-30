var SAAgent=null;
var SASocket=null;
var ProviderName="HWRecognition";
var CHANNEL_ID=333;
var sendON=false;

function updateStatus(status){
	document.getElementById("status").innerHTML=status;
}

function onReceive(channelId,data) {
	if(data=="run"){
		updateStatus("Running...");
		sendON=true;
	}else if(data=="stop") {
		sendON=false;
		updateStatus("Stopped!");
	}
}

var agentCallback={
	onconnect:function(socket){
		SASocket=socket;
		updateStatus("Connected to smartphone!");
		SASocket.setDataReceiveListener(onReceive);
		SASocket.setSocketStatusListener(function(reason){
			disconnect();
		});
	}
};

var peerAgentFindCallback={
	onpeeragentfound : function(peerAgent) {
		try {
			if (peerAgent.appName == ProviderName) {
				SAAgent.setServiceConnectionListener(agentCallback);
				SAAgent.requestServiceConnection(peerAgent);
			} else {
				alert(peerAgent.appName + " is Not expected app! ");
			}
		} catch (err) {
			console.log("error " + err.name + ": " + err.message);
		}
	}, 
	onerror : onerror
};

function onsuccess(agents){
	try{
		if(agents.length>0){
			SAAgent=agents[0];
			SAAgent.setPeerAgentFindListener(peerAgentFindCallback);
			SAAgent.findPeerAgents();
		}else{
			console.log("Agent not found!");
		}
	}catch(err){
		console.log(err);
	}
}

function connect() {
	if(SASocket){
		updateStatus("Already Connected!");
		return false;
	}
	webapis.sa.requestSAAgent(onsuccess,function(e){
		console.log(e);
	});
}

function send(output){
	try{
		SASocket.sendData(CHANNEL_ID,output);
	}catch(err){
		console.log(err);
	}
}

window.onload = function () {
    // TODO:: Do your initialization job
    document.addEventListener('tizenhwkey', function(e) {
        if(e.keyName == "back")
	try {
	    tizen.application.getCurrentApplication().exit();
	} catch (ignore) {
	}
    });
    window.addEventListener('devicemotion', function(e){
		if(sendON){
			output=e.acceleration.x;
			output+="|";
			output+=e.acceleration.y;
			output+="|";
			output+=e.acceleration.z;
			send(output);
		}
	});
	connect();
};
