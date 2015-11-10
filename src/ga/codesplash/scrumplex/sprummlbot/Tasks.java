package ga.codesplash.scrumplex.sprummlbot;

import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.ArrayUtils;

import com.github.theholywaffle.teamspeak3.TS3Api;
import com.github.theholywaffle.teamspeak3.api.wrapper.Client;

import ga.codesplash.scrumplex.sprummlbot.Configurations.Messages;

public class Tasks {

	static ScheduledExecutorService service = null;

	public static void init() {
		service = Executors.newScheduledThreadPool(1);
	}

	public static void startService() {

		service.scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				TS3Api api = Vars.API;
				try {
					if (Vars.DEBUG == 2) {
						Logger.out("Checking for Supports/AFKs... | Disable this message with debug=0");
					}

					for (String uid : Vars.INAFK.keySet()) {
						if (api.getClientByUId(uid) == null) {
							Vars.INAFK.remove(uid);
							Logger.out("AFK Not there anymore: " + api.getDatabaseClientByUId(uid).getNickname());
						}
					}
					for (Client c : api.getClients()) {
						String uid = c.getUniqueIdentifier();
						int cid = c.getId();
						int dbid = c.getDatabaseId();

						for (int group : Vars.GROUPPROTECT_LIST.keySet()) {
							if (Vars.GROUPPROTECT_LIST.get(group).contains(uid)) {
								if(!ArrayUtils.contains(c.getServerGroups(), group)) {
									api.addClientToServerGroup(group, dbid);
								}
							} else {
								if(ArrayUtils.contains(c.getServerGroups(), group)) {
									api.removeClientFromServerGroup(group, dbid);
								}
							}
						}/*

						for (int group : Vars.GROUPPROTECT_LIST.keySet()) {
							if (Vars.GROUPPROTECT_LIST.get(group).contains(uid)) {
								if (Arrays.asList(c.getServerGroups()).contains(group)) {
									api.addClientToServerGroup(group, dbid);
								}
							} else {
								if (!Arrays.asList(c.getServerGroups()).contains(group)) {
									api.removeClientFromServerGroup(group, dbid);
								}
							}
						}*/

						// AntiRec
						if (Vars.ANTIREC_ENABLED) {
							if (c.isRecording()) {
								if (!Vars.ANTIREC_WHITELIST.contains(uid)) {
									Logger.out("RECORD: " + c.getNickname());
									api.pokeClient(cid, Messages.get("you-mustnt-record-here"));
									api.kickClientFromServer(Messages.get("you-mustnt-record-here"), cid);
								}
							}
						}

						// AFK
						if (Vars.AFK_ENABLED) {
							if (c.isInputMuted() || !c.isInputHardware()) {
								if (c.getIdleTime() >= Vars.AFKTIME) {
									if (!Vars.INAFK.containsKey(c)) {
										if (!Vars.AFKALLOWED.contains(c.getChannelId())) {
											if (!c.getPlatform().equalsIgnoreCase("ServerQuery")) {
												if (!Vars.AFK_ALLOWED.contains(uid)) {
													Vars.INAFK.put(uid, c.getChannelId());
													api.moveClient(cid, Vars.AFKCHANNELID);
													api.sendPrivateMessage(cid, Messages.get("you-were-moved-to-afk"));
													Logger.out("AFK: " + c.getNickname());
												}
											}
										}
									}
								}
							}
							if (!c.isInputMuted() && c.isInputHardware()) {
								if (c.getIdleTime() < Vars.AFKTIME) {
									if (Vars.INAFK.containsKey(uid)) {
										api.moveClient(cid, Vars.INAFK.get(uid));
										Vars.INAFK.remove(uid);
										api.sendPrivateMessage(cid, Messages.get("you-were-moved-back-from-afk"));
										Logger.out("Back again: " + c.getNickname());
									}
								}
							}
						}

						// Support
						if (Vars.SUPPORT_ENABLED) {
							if (c.getChannelId() == Vars.SUPPORTCHANNELID) {
								if (Vars.INSUPPORT.contains(uid) == false) {
									api.sendPrivateMessage(cid, Messages.get("you-joined-support-channel"));
									Vars.INSUPPORT.add(uid);
									Logger.out("Support: " + c.getNickname());
									for (Client user : api.getClients()) {
										if (Vars.SUPPORTERS.contains(user.getUniqueIdentifier())) {
											api.sendPrivateMessage(user.getId(), Messages.get("someone-is-in-support"));
										}
									}
								}
							}

							if (c.getChannelId() != Vars.SUPPORTCHANNELID) {
								if (Vars.INSUPPORT.contains(uid)) {
									api.sendPrivateMessage(cid, Messages.get("you-are-not-longer-in-support-queue"));
									Vars.INSUPPORT.remove(uid);
									Logger.out("Not Support: " + c.getNickname());
								}
							}
						}
					}
				} catch (Exception e) {

				}
			}
		}, 0, Vars.TIMERTICK, TimeUnit.MILLISECONDS);
	}

	public static void startBroadCast() {
		service.scheduleAtFixedRate(new Runnable() {

			@Override
			public void run() {
				try {
					Random r = new Random();
					int i = r.nextInt(Vars.BROADCASTS.size());
					if (Vars.DEBUG == 2)
						Logger.out("Sending Broadcast...");
					for (Client c : Vars.API.getClients()) {
						if (!Vars.BROADCAST_IGNORE.contains(c.getUniqueIdentifier())) {
							Vars.API.sendPrivateMessage(c.getId(), Vars.BROADCASTS.get(i));

						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		}, 0, Vars.BROADCAST_INTERVAL, TimeUnit.SECONDS);
	}

	public static void startKeepAlive() {
		service.scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				if (Vars.DEBUG == 2)
					Logger.out("Checking for connection...");
				if (Vars.API.whoAmI() == null) {
					Logger.warn("Sprummlbot lost connection to server!");
					System.exit(2);
				}
			}
		}, 0, 15, TimeUnit.SECONDS);
	}

}
