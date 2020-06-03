package com.estrelsteel.wowbot;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import javax.security.auth.login.LoginException;

import org.yaml.snakeyaml.Yaml;

import com.estrelsteel.wowbot.command.Command;
import com.estrelsteel.wowbot.command.CommandWrapper;
import com.estrelsteel.wowbot.executer.EventListener;
import com.estrelsteel.wowbot.executer.Executer;
import com.estrelsteel.wowbot.user.Console;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Activity.ActivityType;

public class WowBot {

	private static final String title = "WowBot v2.0a";
	private static String path = new File(System.getProperty("java.class.path")).getParent();

	private JDA jda;
	private EventListener listener;
	private static Executer exe;
	private BotSettings settings;

	public static void main(String[] args) throws LoginException, InterruptedException {
		Console.log("Booting " + title);
		if (args.length > 0 && args[0] != null) {
			path = args[0];
		}
		Console.log("Configured path to " + path);
		try {
			new WowBot();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public WowBot() throws LoginException, IOException, InterruptedException {
		Console.log("Loading core settings...");
		Yaml yaml = new Yaml();

		try {
			InputStream input = new FileInputStream(path + "/wowbot.yaml");
			settings = yaml.load(input);
			input.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		settings.updatePaths(path);
		Console.log("Loading commands...");

		exe = new Executer(settings);

		Console.log("Launching...");

		jda = JDABuilder.createDefault(settings.getToken()).build();
		listener = new EventListener(exe);
		jda.addEventListener(listener);
		jda.setAutoReconnect(true);
		jda.getPresence().setActivity(Activity.of(ActivityType.DEFAULT, title));

		jda.awaitReady();

		Console.log(title + " launched and ready.");

		exe.launchConsole();
	}

	public static boolean runCommand(CommandWrapper wrapper) {
		if (exe == null)
			return false;
		return exe.executePotentialCommand(wrapper);
	}

	@SuppressWarnings("unchecked")
	public static int getCommandId(Class<?> command) {
		if (exe == null)
			return -1;
		return exe.getCommandId((Class<Command>) command);
	}
}
