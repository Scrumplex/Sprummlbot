package net.scrumplex.sprummlbot.module;

import com.github.theholywaffle.teamspeak3.api.event.ClientJoinEvent;
import net.scrumplex.sprummlbot.Sprummlbot;
import net.scrumplex.sprummlbot.config.Messages;
import net.scrumplex.sprummlbot.plugins.events.ClientJoinEventHandler;
import net.scrumplex.sprummlbot.wrapper.PermissionGroup;
import org.ini4j.Profile;

import java.util.ArrayList;
import java.util.List;

public class JoinMessage extends Module {

    private final List<Integer> ids = new ArrayList<>();
    private String welcomemsg;
    private boolean sendCommandsList;
    private PermissionGroup permGroup;

    @Override
    protected void load(Profile.Section sec) throws Exception {
        welcomemsg = sec.get("message");
        sendCommandsList = sec.get("send_commands_list", boolean.class);
        permGroup = PermissionGroup.getPermissionGroupByName(sec.get("group"));
    }

    @Override
    protected void start() {
        ids.add(getEventManager().addEventListener(new ClientJoinEventHandler() {
            @Override
            public void handleEvent(ClientJoinEvent e) {
                if (!permGroup.isClientInGroup(e.getUniqueClientIdentifier()))
                    return;
                Sprummlbot.getSprummlbot().getSyncAPI().sendPrivateMessage(e.getClientId(), welcomemsg.replace("%client-username%", e.getClientNickname()));
                if (sendCommandsList)
                    Sprummlbot.getSprummlbot().getSyncAPI().sendPrivateMessage(e.getClientId(), Messages.get("commandslist").replace("%commands%", Sprummlbot.getSprummlbot().getCommandManager().buildHelpMessage(e.getUniqueClientIdentifier())));
            }
        }));
    }

    @Override
    protected void stop() {
        for (int id : ids) {
            getEventManager().removeEventListener(id);
        }
        ids.clear();
    }
}
