package net.scrumplex.sprummlbot.module;

import com.github.theholywaffle.teamspeak3.api.ChannelProperty;
import com.github.theholywaffle.teamspeak3.api.wrapper.ChannelInfo;
import com.github.theholywaffle.teamspeak3.api.wrapper.Client;
import com.github.theholywaffle.teamspeak3.api.wrapper.ClientInfo;
import net.scrumplex.sprummlbot.Sprummlbot;
import net.scrumplex.sprummlbot.wrapper.PermissionGroup;
import org.ini4j.Profile;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChannelStats extends Module {

    private int channelId;
    private String name;

    @Override
    protected void load(Profile.Section sec) throws Exception {
        channelId = sec.get("channel-id", int.class);
        name = sec.get("channel-name");
    }

    @Override
    protected void start() {
        getMainService().hook(this, clientInfo -> {
            try {
                final Calendar calendar = Calendar.getInstance();
                final Pattern groupPattern = Pattern.compile("<group>(.+?)</group>");
                final Pattern timePattern = Pattern.compile("<time>(.+?)</time>");
                final Pattern datePattern = Pattern.compile("<date>(.+?)</date>");
                List<Client> clients = Sprummlbot.getSprummlbot().getDefaultAPI().getClients().getUninterruptibly();
                ChannelInfo channel = Sprummlbot.getSprummlbot().getDefaultAPI().getChannelInfo(channelId).getUninterruptibly();
                String channelName = name;
                String description = "";

                Matcher matcher = groupPattern.matcher(channelName);
                if (matcher.find()) {
                    String group = matcher.group(1);
                    PermissionGroup permissionGroup = PermissionGroup.getPermissionGroupByName(group);
                    int count = 0;
                    StringBuilder sb = new StringBuilder("[center][size=15]Group Info[/size][/center][hr] \n\n");
                    for (Client c : clients) {
                        ClientInfo info = Sprummlbot.getSprummlbot().getDefaultAPI().getClientInfo(c.getId()).getUninterruptibly();
                        ChannelInfo cInfo = Sprummlbot.getSprummlbot().getDefaultAPI().getChannelInfo(c.getChannelId()).getUninterruptibly();
                        if (permissionGroup.isPermitted(c.getUniqueIdentifier()) == PermissionGroup.Permission.PERMITTED) {
                            sb.append("[b][URL=client://").append(c.getId()).append("/").append(c.getUniqueIdentifier()).append("~").append(c.getNickname()).append("]").append(c.getNickname()).append("[/URL][/b]\n").append("  Channel: [b][url=channelID://").append(c.getChannelId()).append("]").append(cInfo.getName()).append("[/url][/b]\n");
                            if (!info.getDescription().equalsIgnoreCase(""))
                                sb.append("  Description: [b]").append(info.getDescription()).append("[/b]\n");
                            count++;
                        }
                    }
                    sb.append("\n[hr]");
                    channelName = channelName.replace("<group>" + group + "</group>", String.valueOf(count));
                    description = sb.toString();
                }

                matcher = timePattern.matcher(channelName);
                if (matcher.find()) {
                    String timeFormat = matcher.group(1);
                    SimpleDateFormat sdf = new SimpleDateFormat(timeFormat);
                    channelName = channelName.replace("<time>" + timeFormat + "</time>", sdf.format(calendar.getTime()));
                }

                matcher = datePattern.matcher(channelName);
                if (matcher.find()) {
                    String dateFormat = matcher.group(1);
                    SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
                    channelName = channelName.replace("<date>" + dateFormat + "</date>", sdf.format(calendar.getTime()));
                }
                if (channelName.length() > 40) {
                    System.err.println("[Channel Stats] Generated channel name of channel_" + channelId + " is too long (" + channelName.length() + "/40). Generated Name: " + channelName);
                    return;
                }
                Map<ChannelProperty, String> options = new HashMap<>();
                if (!channel.getName().equals(channelName))
                    options.put(ChannelProperty.CHANNEL_NAME, channelName);
                if (!channel.getDescription().equals(description) || channel.getDescription().equals(""))
                    options.put(ChannelProperty.CHANNEL_DESCRIPTION, description);
                Sprummlbot.getSprummlbot().getDefaultAPI().editChannel(channelId, options);
            } catch (Exception ignored) {
            }
        });
    }
}
