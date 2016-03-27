package net.scrumplex.sprummlbot;

import com.github.theholywaffle.teamspeak3.api.event.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;
import java.util.TreeMap;

class ServerLogger {

    private static BufferedWriter plain = null;
    private static final SimpleDateFormat sdf = new SimpleDateFormat("d.M.Y HH:mm:ss.SSS");

    static void start() {
        Vars.API.addTS3Listeners(new TS3Listener() {
            @Override
            public void onTextMessage(TextMessageEvent e) {
                if (e.getInvokerId() == Vars.QID)
                    return;
                Map<String, Object> properties = new TreeMap<>();
                properties.put("type", "text_" + e.getTargetMode().name());
                properties.put("invoker_client_id", e.getInvokerId());
                properties.put("invoker_client_uid", e.getInvokerUniqueId());
                properties.put("invoker_client_name", e.getInvokerName());
                properties.put("target_id", "");
                properties.put("target_uid", "");
                properties.put("target_name", "");
                properties.put("message", e.getMessage());
                log(properties);
            }

            @Override
            public void onClientJoin(ClientJoinEvent e) {
                Map<String, Object> properties = new TreeMap<>();
                properties.put("type", "server_join");
                properties.put("invoker_client_id", "");
                properties.put("invoker_client_uid", "");
                properties.put("invoker_client_name", "");
                properties.put("target_id", e.getClientId());
                properties.put("target_uid", e.getUniqueClientIdentifier());
                properties.put("target_name", e.getClientNickname());
                properties.put("message", "");
                log(properties);
            }

            @Override
            public void onClientLeave(ClientLeaveEvent e) {
                Map<String, Object> properties = new TreeMap<>();
                properties.put("type", "server_leave");
                properties.put("invoker_client_id", e.getInvokerId());
                properties.put("invoker_client_uid", e.getInvokerUniqueId());
                properties.put("invoker_client_name", e.getInvokerName());
                properties.put("target_id", e.getClientId());
                properties.put("target_uid", e.getClientId());
                properties.put("target_name", "");
                properties.put("message", e.getReasonMessage());
                log(properties);
            }

            @Override
            public void onServerEdit(ServerEditedEvent e) {
                Map<String, Object> properties = new TreeMap<>();
                properties.put("type", "server_edit_vserver");
                properties.put("invoker_client_id", e.getInvokerId());
                properties.put("invoker_client_uid", e.getInvokerUniqueId());
                properties.put("invoker_client_name", e.getInvokerName());
                properties.put("target_id", "");
                properties.put("target_uid", "");
                properties.put("target_name", "");
                properties.put("message", "");
                log(properties);
            }

            @Override
            public void onChannelEdit(ChannelEditedEvent e) {
                Map<String, Object> properties = new TreeMap<>();
                properties.put("type", "channel_edit");
                properties.put("invoker_client_id", e.getInvokerId());
                properties.put("invoker_client_uid", e.getInvokerUniqueId());
                properties.put("invoker_client_name", e.getInvokerName());
                properties.put("target_id", "channel_" + e.getChannelId());
                properties.put("target_uid", "");
                properties.put("target_name", "");
                properties.put("message", "");
                log(properties);
            }

            @Override
            public void onChannelDescriptionChanged(ChannelDescriptionEditedEvent e) {
                Map<String, Object> properties = new TreeMap<>();
                properties.put("type", "channel_desc_change");
                properties.put("invoker_client_id", e.getInvokerId());
                properties.put("invoker_client_uid", e.getInvokerUniqueId());
                properties.put("invoker_client_name", e.getInvokerName());
                properties.put("target_id", "channel_" + e.getChannelId());
                properties.put("target_uid", "");
                properties.put("target_name", "");
                properties.put("message", "");
                log(properties);
            }

            @Override
            public void onClientMoved(ClientMovedEvent e) {
                Map<String, Object> properties = new TreeMap<>();
                properties.put("type", "server_move");
                properties.put("invoker_client_id", e.getInvokerId());
                properties.put("invoker_client_uid", e.getInvokerUniqueId());
                properties.put("invoker_client_name", e.getInvokerName());
                properties.put("target_id", e.getClientId());
                properties.put("target_uid", "");
                properties.put("target_name", "");
                properties.put("message", "moved_to:channel_" + e.getTargetChannelId());
                log(properties);
            }

            @Override
            public void onChannelCreate(ChannelCreateEvent e) {
                Map<String, Object> properties = new TreeMap<>();
                properties.put("type", "channel_create");
                properties.put("invoker_client_id", e.getInvokerId());
                properties.put("invoker_client_uid", e.getInvokerUniqueId());
                properties.put("invoker_client_name", e.getInvokerName());
                properties.put("target_id", "channel_" + e.getChannelId());
                properties.put("target_uid", "");
                properties.put("target_name", "");
                properties.put("message", "");
                log(properties);
            }

            @Override
            public void onChannelDeleted(ChannelDeletedEvent e) {
                Map<String, Object> properties = new TreeMap<>();
                properties.put("type", "channel_delete");
                properties.put("invoker_client_id", e.getInvokerId());
                properties.put("invoker_client_uid", e.getInvokerUniqueId());
                properties.put("invoker_client_name", e.getInvokerName());
                properties.put("target_id", "channel_" + e.getChannelId());
                properties.put("target_uid", "");
                properties.put("target_name", "");
                properties.put("message", "");
                log(properties);
            }

            @Override
            public void onChannelMoved(ChannelMovedEvent e) {
                Map<String, Object> properties = new TreeMap<>();
                properties.put("type", "channel_move");
                properties.put("invoker_client_id", e.getInvokerId());
                properties.put("invoker_client_uid", e.getInvokerUniqueId());
                properties.put("invoker_client_name", e.getInvokerName());
                properties.put("target_id", "channel_" + e.getChannelId());
                properties.put("target_uid", "");
                properties.put("target_name", "");
                properties.put("message", "channel_order:" + e.getChannelOrder() + ",parent:channel_" + e.getChannelParentId());
                log(properties);
            }

            @Override
            public void onChannelPasswordChanged(ChannelPasswordChangedEvent e) {
                Map<String, Object> properties = new TreeMap<>();
                properties.put("type", "channel_password_change");
                properties.put("invoker_client_id", e.getInvokerId());
                properties.put("invoker_client_uid", e.getInvokerUniqueId());
                properties.put("invoker_client_name", e.getInvokerName());
                properties.put("target_id", "channel_" + e.getChannelId());
                properties.put("target_uid", "");
                properties.put("target_name", "");
                properties.put("message", "");
                log(properties);
            }

            @Override
            public void onPrivilegeKeyUsed(PrivilegeKeyUsedEvent e) {
                Map<String, Object> properties = new TreeMap<>();
                properties.put("type", "server_privilege_key_used");
                properties.put("invoker_client_id", e.getClientId());
                properties.put("invoker_client_uid", e.getClientUniqueIdentifier());
                properties.put("invoker_client_name", "");
                properties.put("target_id", "group_" + e.getPrivilegeKeyType().name() + "_" + e.getPrivilegeKeyGroupId());
                properties.put("target_uid", "");
                properties.put("target_name", "");
                properties.put("message", "");
                log(properties);
            }
        });
    }

    private static void log(Map<String, Object> properties) {
        Calendar cal = Calendar.getInstance();
        try {
            if (plain == null) {
                SimpleDateFormat date = new SimpleDateFormat("d_M_Y__HH_mm_ss");
                File directory = new File("logs");
                directory.mkdir();
                File file = new File(directory, "serverlog_" + date.format(cal.getTime()) + ".log");
                if (!file.exists())
                    file.createNewFile();
                plain = new BufferedWriter(new FileWriter(file));
                Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            plain.flush();
                            plain.close();
                        } catch (IOException e) {
                            System.err.println("Couldn't close log stream!");
                        }
                    }
                }));
            }
            String write = "";
            for (String key : properties.keySet()) {
                if (key.equalsIgnoreCase("type"))
                    continue;
                write += key + ":\"" + properties.get(key) + "\", ";
            }
            plain.write(sdf.format(cal.getTime()) + "] EVENT-" + properties.get("type") + ": " + write + "\n");
            plain.flush();
        } catch (Exception ignored) {
        }
    }
}
