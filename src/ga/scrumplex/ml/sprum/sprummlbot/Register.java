package ga.scrumplex.ml.sprum.sprummlbot;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.github.theholywaffle.teamspeak3.api.wrapper.Client;

import ga.scrumplex.ml.sprum.sprummlbot.stuff.Exceptions;

public class Register {

	public Register(int tick) {
		
		ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
		scheduler.scheduleAtFixedRate(new Runnable() {
			
			@Override
			public void run() {
				try {
					if(Config.debug >= 1) {
						Logger.out("Checking for Supports/AFKs... | Disable this message with debug=0");
					}
	    			for(Client c : Config.api.getClients()) {
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
	                        				    		Config.api.moveClient(c.getId(),Config.afkchannelid);
	                        				    		Config.api.sendPrivateMessage(c.getId(), Messages.get("you-were-moved-to-afk"));
	                        				    		Logger.out("AFK: " + c.getNickname());
	        										}
	        									} else {
	                								Config.idle.put(c.getId(), c.getChannelId());
	                    				    		Config.api.moveClient(c.getId(),Config.afkchannelid);
	                    				    		Config.api.sendPrivateMessage(c.getId(), Messages.get("you-were-moved-to-afk"));
	                    				    		Logger.out("AFK: " + c.getNickname());
	        									}
	        								}
	        							}
	        						}
	        					}
	        				}
	        				
	        				if(c.getIdleTime() < Config.afkidle) {
	        					if(Config.idle.containsKey(c.getId())) {
	        			    		Config.api.moveClient(c.getId(), Config.idle.get(c.getId()));
	        			    		Config.idle.remove(c.getId());
	    			    			Config.api.sendPrivateMessage(c.getId(), Messages.get("you-were-moved-back-from-afk"));
	    			    			Logger.out("Back again: " + c.getNickname());
	        					}
	        				}
	    				}
	    				
	    				//Suport
	    				if(Config.supports) {
	        				if(c.getChannelId() == Config.supportchannelid) {
	        					if(Config.support.contains(c.getId()) == false) {
	    			    			Config.api.sendPrivateMessage(c.getId(), Messages.get("you-joined-support-channel"));
	            					Config.support.add(c.getId());
	            					for(Client user : Config.api.getClients()) {
	            						if(Config.admins.contains(user.getUniqueIdentifier())) {
	            			    			Config.api.sendPrivateMessage(user.getId(), Messages.get("someone-is-in-support"));
	            							Logger.out("Support: " + c.getNickname());
	            						}
	            					}
	        					}
	        				}
	        				
	        				if(c.getChannelId() != Config.supportchannelid) {
	        					if(Config.support.contains(c.getId())) {
	    			    			Config.api.sendPrivateMessage(c.getId(), Messages.get("you-are-not-longer-in-support-queue"));
	        						Config.support.remove((Object)c.getId());
	        						Logger.out("Not Support: " + c.getNickname());
	        					}
	        				}
	    				}
	   		    	}
				} catch (Exception e) {
					Exceptions.handle(e, "Unknown error!", false);
				}
			}
		}, 0, Config.timertick, TimeUnit.MILLISECONDS);
	}
	
}
