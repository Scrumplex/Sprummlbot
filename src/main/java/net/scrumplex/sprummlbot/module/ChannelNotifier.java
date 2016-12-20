package net.scrumplex.sprummlbot.module;

import com.github.theholywaffle.teamspeak3.api.wrapper.Client;
import net.scrumplex.sprummlbot.Sprummlbot;
import net.scrumplex.sprummlbot.config.Messages;
import net.scrumplex.sprummlbot.core.Clients;
import net.scrumplex.sprummlbot.plugins.events.ClientMoveEventHandler;
import net.scrumplex.sprummlbot.tools.EasyMethods;
import net.scrumplex.sprummlbot.wrapper.PermissionGroup;
import org.ini4j.Profile;

import java.util.ArrayList;
import java.util.List;

public class ChannelNotifier extends Module {

    private final List<Integer> ids = new ArrayList<>();
    private List<Integer> channels;
    private boolean poke;
    private PermissionGroup notifyGroup;
    private String msg;

    @Override
    protected void load(Profile.Section sec) throws Exception {
        channels = EasyMethods.convertArrayToList(sec.getAll("channel", int[].class));
        poke = sec.get("poke", boolean.class);
        notifyGroup = PermissionGroup.getPermissionGroupByName(sec.get("notify_group"));
        msg = sec.get("message");
    }

    @Override
    protected void start() {
        ids.add(getEventManager().addEventListener((ClientMoveEventHandler) e ->
                Sprummlbot.getSprummlbot().getDefaultAPI().getClientInfo(e.getClientId()).onSuccess(c -> {
                    Clients.ClientFlags flags = Sprummlbot.getSprummlbot().getClientManager().getClientFlags(e.getClientId());
                    if (channels.contains(e.getTargetChannelId())) {
                        if (!flags.hasFlag(Clients.DefaultClientFlags.NOTIFY)) {
                            Sprummlbot.getSprummlbot().getSyncAPI().sendPrivateMessage(c.getId(), Messages.get("you-joined-notify-channel"));
                            flags.addClientFlag(Clients.DefaultClientFlags.NOTIFY);
                            System.out.println("[Channel Notifier] Added notify flag to " + c.getNickname() + "(" + e.getClientId() + ").");
                            Sprummlbot.getSprummlbot().getDefaultAPI().getClients().onSuccess(result -> {
                                for (Client user : result) {
                                    if (notifyGroup.isPermitted(user.getUniqueIdentifier()) == PermissionGroup.Permission.PERMITTED) {
                                        if (poke)
                                            Sprummlbot.getSprummlbot().getSyncAPI().pokeClient(user.getId(), msg);
                                        else
                                            Sprummlbot.getSprummlbot().getSyncAPI().sendPrivateMessage(user.getId(), msg);
                                    }
                                }
                            });
                        }
                    } else {
                        if (flags.hasFlag(Clients.DefaultClientFlags.NOTIFY)) {
                            flags.removeClientFlag(Clients.DefaultClientFlags.NOTIFY);
                            System.out.println("[Channel Notifier] Removed notify flag from " + c.getNickname() + "(" + e.getClientId() + ").");
                        }
                    }
                    Sprummlbot.getSprummlbot().getClientManager().updateClientFlags(e.getClientId(), flags);
                })));
    }

    @Override
    protected void stop() {
        for (int id : ids) {
            getEventManager().removeEventListener(id);
        }
        ids.clear();
    }
}
