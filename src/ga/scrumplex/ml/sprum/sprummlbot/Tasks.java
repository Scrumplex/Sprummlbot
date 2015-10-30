package ga.scrumplex.ml.sprum.sprummlbot;

import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.github.theholywaffle.teamspeak3.api.wrapper.Client;

import ga.scrumplex.ml.sprum.sprummlbot.Configurations.Messages;

public class Tasks {

	static ScheduledExecutorService service = null;

	public static void startService() {

		service = Executors.newScheduledThreadPool(1);
		service.scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				try {
					if (Config.DEBUG == 2) {
						Logger.out("Checking for Supports/AFKs... | Disable this message with debug=0");
					}

					for (String uid : Config.INAFK.keySet()) {
						if (Config.API.getClientByUId(uid) == null) {
							Config.INAFK.remove(uid);
							Logger.out(
									"AFK Not there anymore: " + Config.API.getDatabaseClientByUId(uid).getNickname());
						}
					}
					for (Client c : Config.API.getClients()) {

						// AntiRec
						if (Config.ANTIREC_ENABLED) {
							if (c.isRecording()) {
								if (!Config.ANTIREC_WHITELIST.contains(c.getUniqueIdentifier())) {
									Logger.out("RECORD: " + c.getNickname());
									Config.API.pokeClient(c.getId(), Messages.get("you-mustnt-record-here"));
									Config.API.kickClientFromServer(Messages.get("you-mustnt-record-here"), c.getId());
								}
							}
						}

						// AFK
						if (Config.AFK_ENABLED) {
							if (c.isInputMuted() || !c.isInputHardware()) {
								if (c.getIdleTime() >= Config.AFKTIME) {
									if (!Config.INAFK.containsKey(c)) {
										if (!Config.AFKALLOWED.contains(c.getChannelId())) {
											if (!c.getPlatform().equalsIgnoreCase("ServerQuery")) {
												if (!Config.AFK_ALLOWED.contains(c.getUniqueIdentifier())) {
													Config.INAFK.put(c.getUniqueIdentifier(), c.getChannelId());
													Config.API.moveClient(c.getId(), Config.AFKCHANNELID);
													Config.API.sendPrivateMessage(c.getId(),
															Messages.get("you-were-moved-to-afk"));
													Logger.out("AFK: " + c.getNickname());
												}
											}
										}
									}
								}
							}
							if (!c.isInputMuted() && c.isInputHardware()) {
								if (c.getIdleTime() < Config.AFKTIME) {
									if (Config.INAFK.containsKey(c.getUniqueIdentifier())) {
										Config.API.moveClient(c.getId(), Config.INAFK.get(c.getUniqueIdentifier()));
										Config.INAFK.remove(c.getUniqueIdentifier());
										Config.API.sendPrivateMessage(c.getId(),
												Messages.get("you-were-moved-back-from-afk"));
										Logger.out("Back again: " + c.getNickname());
									}
								}
							}
						}

						// Support
						if (Config.SUPPORT_ENABLED) {
							if (c.getChannelId() == Config.SUPPORTCHANNELID) {
								if (Config.INSUPPORT.contains(c.getUniqueIdentifier()) == false) {
									Config.API.sendPrivateMessage(c.getId(),
											Messages.get("you-joined-support-channel"));
									Config.INSUPPORT.add(c.getUniqueIdentifier());
									Logger.out("Support: " + c.getNickname());
									for (Client user : Config.API.getClients()) {
										if (Config.SUPPORTERS.contains(user.getUniqueIdentifier())) {
											Config.API.sendPrivateMessage(user.getId(),
													Messages.get("someone-is-in-support"));
										}
									}
								}
							}

							if (c.getChannelId() != Config.SUPPORTCHANNELID) {
								if (Config.INSUPPORT.contains(c.getUniqueIdentifier())) {
									Config.API.sendPrivateMessage(c.getId(),
											Messages.get("you-are-not-longer-in-support-queue"));
									Config.INSUPPORT.remove(c.getUniqueIdentifier());
									Logger.out("Not Support: " + c.getNickname());
								}
							}
						}
					}
				} catch (Exception e) {

				}
			}
		}, 0, Config.TIMERTICK, TimeUnit.MILLISECONDS);
	}

	public static void startBroadCast() {
		service.scheduleAtFixedRate(new Runnable() {

			@Override
			public void run() {
				try {
					Random r = new Random();
					int i = r.nextInt(Config.BROADCASTS.size());
					Logger.out("Sending Broadcast...");
					for (Client c : Config.API.getClients()) {
						if(!Config.BROADCAST_IGNORE.contains(c.getUniqueIdentifier())) {
							Config.API.sendPrivateMessage(c.getId(), Config.BROADCASTS.get(i));
							
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		}, 0, Config.BROADCAST_INTERVAL, TimeUnit.SECONDS);
	}

	public static void startKeepAlive() {
		service.scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				if (Config.DEBUG == 2) {
					Logger.out("Checking for connection...");
				}
				if (Config.API.whoAmI() == null) {
					Logger.warn("Sprummlbot lost connection to server!");
					System.exit(2);
				}
			}
		}, 0, 15, TimeUnit.SECONDS);
	}

}
