package ga.scrumplex.ml.sprum.sprummlbot;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.github.theholywaffle.teamspeak3.api.exception.TS3ConnectionFailedException;
import com.github.theholywaffle.teamspeak3.api.wrapper.Client;

import ga.scrumplex.ml.sprum.sprummlbot.stuff.Exceptions;

public class Register {

	public static void startService() {
		
		ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
		scheduler.scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				if(Config.QUERY.getSocket().isConnected()) {
					try {
						if(Config.DEBUG == 2) {
							Logger.out("Checking for Supports/AFKs... | Disable this message with debug=0");
						}
		    			for(Client c : Config.API.getClients()) {
		    				//AntiRec
		    				if(Config.ANTIREC_ENABLED) {
		    					if(c.isRecording()) {
		    						if(Config.TEAM.contains(c.getUniqueIdentifier())) {
		    							if(!Config.ANTIREC_IGNORE_TEAM) {
		    								Logger.out("RECORD: " + c.getNickname());
		    								Config.API.pokeClient(c.getId(), Messages.get("you-mustnt-record-here"));
		    								Config.API.kickClientFromServer(Messages.get("you-mustnt-record-here"), c.getId());
		    							}
		    						} else {
	    								Logger.out("RECORD: " + c.getNickname());
	    								Config.API.pokeClient(c.getId(), Messages.get("you-mustnt-record-here"));
	    								Config.API.kickClientFromServer(Messages.get("you-mustnt-record-here"), c.getId());
		    						}
		    					}
		    				}
		    				//AFK
		    				if(Config.AFK_ENABLED) {
		        				if(c.isInputMuted() || c.isInputHardware() == false) {
		        					if(c.getIdleTime() >= Config.AFKTIME) {
		        						if(Config.INAFK.containsKey(c) == false) {
		        							if(Config.AFKALLOWED.contains(c.getChannelId()) == false) {
		        								if(c.getPlatform().equalsIgnoreCase("ServerQuery") == false) {
		        									if(Config.TEAM.contains(c.getUniqueIdentifier())) {
		        										if(Config.AFK_MOVE_TEAM) {
		                    								Config.INAFK.put(c.getUniqueIdentifier(), c.getChannelId());
		                        				    		Config.API.moveClient(c.getId(),Config.AFKCHANNELID);
		                        				    		Config.API.sendPrivateMessage(c.getId(), Messages.get("you-were-moved-to-afk"));
		                        				    		Logger.out("AFK: " + c.getNickname());
		        										}
		        									} else {
		                								Config.INAFK.put(c.getUniqueIdentifier(), c.getChannelId());
		                    				    		Config.API.moveClient(c.getId(),Config.AFKCHANNELID);
		                    				    		Config.API.sendPrivateMessage(c.getId(), Messages.get("you-were-moved-to-afk"));
		                    				    		Logger.out("AFK: " + c.getNickname());
		        									}
		        								}
		        							}
		        						}
		        					}
		        				}
		        				if(!c.isInputMuted() && c.isInputHardware()) {
			        				if(c.getIdleTime() < Config.AFKTIME) {
			        					if(Config.INAFK.containsKey(c.getUniqueIdentifier())) {
			        			    		Config.API.moveClient(c.getId(), Config.INAFK.get(c.getId()));
			        			    		Config.INAFK.remove(c.getUniqueIdentifier());
			    			    			Config.API.sendPrivateMessage(c.getId(), Messages.get("you-were-moved-back-from-afk"));
			    			    			Logger.out("Back again: " + c.getNickname());
			        					}
			        				}
		        				}
		    				}
		    				
		    				//Suport
		    				if(Config.SUPPORT_ENABLED) {
		        				if(c.getChannelId() == Config.SUPPORTCHANNELID) {
		        					if(Config.INSUPPORT.contains(c.getId()) == false) {
		    			    			Config.API.sendPrivateMessage(c.getId(), Messages.get("you-joined-support-channel"));
		            					Config.INSUPPORT.add(c.getUniqueIdentifier());
		            					for(Client user : Config.API.getClients()) {
		            						if(Config.TEAM.contains(user.getUniqueIdentifier())) {
		            			    			Config.API.sendPrivateMessage(user.getId(), Messages.get("someone-is-in-support"));
		            							Logger.out("Support: " + c.getNickname());
		            						}
		            					}
		        					}
		        				}
		        				
		        				if(c.getChannelId() != Config.SUPPORTCHANNELID) {
		        					if(Config.INSUPPORT.contains(c.getId())) {
		    			    			Config.API.sendPrivateMessage(c.getId(), Messages.get("you-are-not-longer-in-support-queue"));
		        						Config.INSUPPORT.remove(c.getUniqueIdentifier());
		        						Logger.out("Not Support: " + c.getNickname());
		        					}
		        				}
		    				}
		   		    	}
					} catch (Exception e) {
						Exceptions.handle(e, "Unknown error!", false);
					}
				} else {
					Exceptions.handle(new TS3ConnectionFailedException(null), "Error while AFK/Support/AntiRecord Service");
				}
			}
		}, 0, Config.TIMERTICK, TimeUnit.MILLISECONDS);
	}
	
}
