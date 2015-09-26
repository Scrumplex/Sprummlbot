package ga.scrumplex.ml.sprum.sprummlbot;

import java.util.Timer;
import java.util.TimerTask;

import com.github.theholywaffle.teamspeak3.TS3Api;
import com.github.theholywaffle.teamspeak3.api.wrapper.Client;

public class Register {

	public Register(int tick, final TS3Api api) {
		Timer timer = new Timer();
    	timer.scheduleAtFixedRate(new TimerTask() {
    		public void run() {
				if(Config.debug >= 1) {
					Logger.out("Checking for Supports/AFKs... | Disable this message with debug=0");
				}
    			for(Client c : api.getClients()) {
    				//AFK
    				if(Config.afk) {
        				if(c.isInputMuted() || c.isInputHardware() == false) {
        					if(c.getIdleTime() > Config.afkidle) {
        						if(Config.idle.containsKey(c) == false) {
        							if(Config.deniedchannels.contains(c.getChannelId()) == false) {
        								if(c.getPlatform().equalsIgnoreCase("ServerQuery") == false) {
        									if(Config.admins.contains(c.getUniqueIdentifier())) {
        										if(Config.moveadmins) {
                    								Config.idle.put(c.getId(), c.getChannelId());
                        				    		api.moveClient(c.getId(),Config.afkchannelid);
                        				    		api.sendPrivateMessage(c.getId(), Messages.get("you-were-moved-to-afk"));
                        				    		Logger.out("AFK: " + c.getNickname());
        										}
        									} else {
                								Config.idle.put(c.getId(), c.getChannelId());
                    				    		api.moveClient(c.getId(),Config.afkchannelid);
                    				    		api.sendPrivateMessage(c.getId(), Messages.get("you-were-moved-to-afk"));
                    				    		Logger.out("AFK: " + c.getNickname());
        									}
        								}
        							}
        						}
        					}
        				}
        				
        				if(c.getIdleTime() < Config.afkidle) {
        					if(Config.idle.containsKey(c.getId())) {
        			    		api.moveClient(c.getId(), Config.idle.get(c.getId()));
        			    		Config.idle.remove(c.getId());
    			    			api.sendPrivateMessage(c.getId(), Messages.get("you-were-moved-back-from-afk"));
    			    			Logger.out("Back again: " + c.getNickname());
        					}
        				}
    				}
    				
    				//Suport
    				if(Config.supports) {
        				if(c.getChannelId() == Config.supportchannelid) {
        					if(Config.support.contains(c.getId()) == false) {
    			    			api.sendPrivateMessage(c.getId(), Messages.get("you-joined-support-channel"));
            					Config.support.add(c.getId());
            					for(Client user : api.getClients()) {
            						if(Config.admins.contains(user.getUniqueIdentifier())) {
            			    			api.sendPrivateMessage(user.getId(), Messages.get("someone-is-in-support"));
            							Logger.out("Support: " + c.getNickname());
            						}
            					}
        					}
        				}
        				
        				if(c.getChannelId() != Config.supportchannelid) {
        					if(Config.support.contains(c.getId())) {
    			    			api.sendPrivateMessage(c.getId(), Messages.get("you-are-not-longer-in-support-queue"));
        						Config.support.remove((Object)c.getId());
        						Logger.out("Not Support: " + c.getNickname());
        					}
        				}
    				}
   		    	}
    		}
        }, 0, tick);
	}
	
}
