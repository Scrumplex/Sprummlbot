package net.scrumplex.sprummlbot.module;

import com.github.theholywaffle.teamspeak3.api.wrapper.Client;
import net.scrumplex.sprummlbot.Sprummlbot;
import net.scrumplex.sprummlbot.config.Messages;
import net.scrumplex.sprummlbot.service.ServiceHook;
import net.scrumplex.sprummlbot.wrapper.PermissionGroup;
import org.ini4j.Profile;

import java.util.List;

public class AntiRecordingMessage extends Module {

    private PermissionGroup whitelistGroup;

    @Override
    protected void load(Profile.Section sec) throws Exception {
        whitelistGroup = PermissionGroup.getPermissionGroupByName(sec.get("whitelist_group"));
    }

    @Override
    protected void start() {
        getMainService().hook(this, new Hook(whitelistGroup));
    }

    @Override
    protected void stop() {
        getMainService().unhook(this);
    }

    private class Hook implements ServiceHook {
        final PermissionGroup whitelistedGroup;

        Hook(PermissionGroup whitelistGroup) {
            this.whitelistedGroup = whitelistGroup;
        }

        @Override
        public void handle(List<Client> clients) {
            for (Client c : clients) {
                if (c.isRecording()) {
                    if (whitelistedGroup.isPermitted(c.getUniqueIdentifier()) == PermissionGroup.Permission.DENIED) {
                        System.out.println("[Anti Recording] " + c.getNickname() + " was kicked.");
                        Sprummlbot.getSprummlbot().getDefaultAPI().kickClientFromServer(Messages.get("you-mustnt-record-here"), c.getId());
                    }
                }
            }
        }
    }
}
