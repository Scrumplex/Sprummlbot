package net.scrumplex.sprummlbot.module;

import com.github.theholywaffle.teamspeak3.api.event.ClientLeaveEvent;
import com.github.theholywaffle.teamspeak3.api.event.ClientMovedEvent;
import com.github.theholywaffle.teamspeak3.api.wrapper.Client;
import net.scrumplex.sprummlbot.Sprummlbot;
import net.scrumplex.sprummlbot.Vars;
import net.scrumplex.sprummlbot.config.Messages;
import net.scrumplex.sprummlbot.core.Clients;
import net.scrumplex.sprummlbot.plugins.events.ClientMoveEventHandler;
import net.scrumplex.sprummlbot.plugins.events.ClientQuitEventHandler;
import net.scrumplex.sprummlbot.service.ServiceHook;
import net.scrumplex.sprummlbot.tools.EasyMethods;
import net.scrumplex.sprummlbot.wrapper.PermissionGroup;
import org.ini4j.Profile;

import java.util.ArrayList;
import java.util.List;

public class AFKMover extends Module {

    private final List<String> afkConditions = new ArrayList<>();
    private final List<Integer> afks = new ArrayList<>();
    private final List<Integer> ids = new ArrayList<>();
    private int afkChannelId;
    private List<Integer> channels;
    private boolean excludeMode;
    private PermissionGroup whitelistGroup;
    private int afkTime;
    private int afkConditionsMinimumMet;

    @Override
    protected void load(Profile.Section sec) throws Exception {
        afkChannelId = sec.get("afk_channel_id", int.class);
        channels = EasyMethods.convertArrayToList(sec.getAll("channels", int[].class));
        excludeMode = sec.get("exclude_mode", boolean.class);
        whitelistGroup = PermissionGroup.getPermissionGroupByName(sec.get("whitelist_group"));
        afkTime = sec.get("max_afk_time", int.class) * 1000;
        if (sec.get("condition_away", boolean.class))
            afkConditions.add("away");
        if (sec.get("condition_mic_muted", boolean.class))
            afkConditions.add("mic_muted");
        if (sec.get("condition_mic_disabled", boolean.class))
            afkConditions.add("mic_disabled");
        if (sec.get("condition_speaker_muted", boolean.class))
            afkConditions.add("speaker_muted");
        if (sec.get("condition_speaker_disabled", boolean.class))
            afkConditions.add("speaker_disabled");
        afkConditionsMinimumMet = sec.get("condition_min", int.class);
    }


    @Override
    protected void start() {
        getMainService().hook(this, new Hook());
        ids.add(getEventManager().addEventListener(new ClientMoveEventHandler() {
            @Override
            public void handleEvent(final ClientMovedEvent e) {
                final Clients.ClientFlags flags = Vars.clients.getClientFlags(e.getClientId());
                if (flags.hasFlag(Clients.DefaultClientFlags.AFK) && afks.contains(e.getClientId())) {
                    if (e.getTargetChannelId() != afkChannelId) {
                        afks.remove((Integer) e.getClientId());
                        flags.removeClientFlag(Clients.DefaultClientFlags.AFK);
                        Vars.clients.updateClientFlags(e.getClientId(), flags);
                        System.out.println("[AFK Mover] Removed AFK flag from client(" + e.getClientId() + "). Cause: left channel");
                    }
                }
            }
        }));

        ids.add(getEventManager().addEventListener(new ClientQuitEventHandler() {
            @Override
            public void handleEvent(ClientLeaveEvent e) {
                Clients.ClientFlags flags = Vars.clients.getClientFlags(e.getClientId());
                if (flags.hasFlag(Clients.DefaultClientFlags.AFK) && afks.contains(e.getClientId())) {
                    afks.remove((Integer) e.getClientId());
                    flags.removeClientFlag(Clients.DefaultClientFlags.AFK);
                }
                Vars.clients.updateClientFlags(e.getClientId(), flags);
                System.out.println("[AFK Mover] Removed AFK flag from client(" + e.getClientId() + "). Cause: left server");
            }
        }));
    }

    @Override
    protected void stop() {
        getMainService().unhook(this);
        for (int id : ids) {
            getEventManager().removeEventListener(id);
        }
        ids.clear();
    }

    private class Hook implements ServiceHook {
        @Override
        public void handle(List<Client> clients) {
            for (Client c : clients) {
                int clid = c.getId();
                String uid = c.getUniqueIdentifier();
                boolean isAfk = isAfk(c);

                Clients.ClientFlags flags = Vars.clients.getClientFlags(clid);
                if (!flags.hasFlag(Clients.DefaultClientFlags.AFK) && !afks.contains(c.getId())) {
                    if (isAfk && c.getIdleTime() >= afkTime && !whitelistGroup.isClientInGroup(uid) && checkChannel(c.getChannelId()) && !c.getPlatform().equalsIgnoreCase("ServerQuery")) {
                        flags.addClientFlag(Clients.DefaultClientFlags.AFK, c.getChannelId());
                        Sprummlbot.getSprummlbot().getDefaultAPI().moveClient(clid, afkChannelId);
                        Sprummlbot.getSprummlbot().getDefaultAPI().sendPrivateMessage(clid, Messages.get("you-were-moved-to-afk"));
                        afks.add(c.getId());
                        System.out.println("[AFK Mover] Added AFK flag to " + c.getNickname() + "(" + clid + ").");
                    }
                } else if (afks.contains(c.getId())) {
                    if (!isAfk) {
                        int cid = (Integer) flags.getInformation(Clients.DefaultClientFlags.AFK);
                        Sprummlbot.getSprummlbot().getDefaultAPI().sendPrivateMessage(clid, Messages.get("you-were-moved-back-from-afk"));
                        Sprummlbot.getSprummlbot().getDefaultAPI().moveClient(clid, cid);
                        afks.remove((Integer) c.getId());
                        flags.removeClientFlag(Clients.DefaultClientFlags.AFK);
                        System.out.println("[AFK Mover] Removed AFK flag from " + c.getNickname() + "(" + clid + "). Cause: does not match conditions");
                    }
                }
                Vars.clients.updateClientFlags(clid, flags);
            }
        }

        private boolean checkChannel(int channelId) {
            if (!excludeMode) {
                return channels.contains(channelId);
            }
            return !channels.contains(channelId);
        }

        private boolean isAfk(Client c) {

            boolean isAway = c.isAway(), isMicMuted = c.isInputMuted(), isMicDisabled = !c.isInputHardware(), isSpeakerMuted = c.isOutputMuted(), isSpeakerDisabled = !c.isOutputHardware();
            int conditionMatch = 0;
            if (afkConditions.contains("away") && isAway)
                conditionMatch++;
            if (afkConditions.contains("mic_muted") && isMicMuted)
                conditionMatch++;
            if (afkConditions.contains("mic_disabled") && isMicDisabled)
                conditionMatch++;
            if (afkConditions.contains("speaker_muted") && isSpeakerMuted)
                conditionMatch++;
            if (afkConditions.contains("speaker_disabled") && isSpeakerDisabled)
                conditionMatch++;
            boolean isAfk = false;

            if ((afkConditionsMinimumMet == -1) && (conditionMatch == afkConditions.size()))
                isAfk = true;
            else if (afkConditionsMinimumMet <= conditionMatch)
                isAfk = true;

            return isAfk;
        }
    }
}
