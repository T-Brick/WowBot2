package com.estrelsteel.wowbot.executer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

import com.estrelsteel.wowbot.BotSettings;
import com.estrelsteel.wowbot.WowFile;
import com.estrelsteel.wowbot.audio.WowAudioCore;
import com.estrelsteel.wowbot.command.Command;
import com.estrelsteel.wowbot.command.CommandWrapper;
import com.estrelsteel.wowbot.command.CommandWrapper.CommandStatus;
import com.estrelsteel.wowbot.command.audio.Leave;
import com.estrelsteel.wowbot.command.audio.Link;
import com.estrelsteel.wowbot.command.audio.Pause;
import com.estrelsteel.wowbot.command.audio.Queue;
import com.estrelsteel.wowbot.command.audio.Skip;
import com.estrelsteel.wowbot.command.audio.Summon;
import com.estrelsteel.wowbot.command.audio.play.Play;
import com.estrelsteel.wowbot.command.audio.play.PlayAgain;
import com.estrelsteel.wowbot.command.audio.play.PlayHelper;
import com.estrelsteel.wowbot.command.audio.play.PlayNext;
import com.estrelsteel.wowbot.command.misc.Help;
import com.estrelsteel.wowbot.command.misc.Quit;
import com.estrelsteel.wowbot.command.misc.ReactCommandCall;
import com.estrelsteel.wowbot.command.misc.Wow;
import com.estrelsteel.wowbot.user.Console;

import net.dv8tion.jda.api.entities.MessageReaction;

public class Executer {

	private Parser parser;
	private BotSettings settings;
	private ArrayList<Command> commandList;
	private HashMap<String, Integer> labels;

	private HashMap<Integer, Command> commands;
	private HashMap<Class<Command>, Integer> ids;

	private HashMap<Integer, List<String>> reactsForCommand;
	private HashMap<String, ReactCommandCall> reactCommands;

	public Executer(BotSettings settings) throws IOException {
		this.settings = settings;
		parser = new Parser(settings.getCommandLabel());
		labels = new HashMap<String, Integer>();

		commands = new HashMap<Integer, Command>();
		ids = new HashMap<Class<Command>, Integer>();

		reactsForCommand = new HashMap<Integer, List<String>>();
		reactCommands = new HashMap<String, ReactCommandCall>();
		
		loadReacts(settings.getReactCommandsPath());
		loadCommandList();
		loadCommands();
		loadLabels(settings.getCommandListPath());
	}

	public Parser getParser() {
		return parser;
	}

	private List<String> getReactsForCommand(int commandId) {
		return reactsForCommand.get(commandId);
	}

	public int getCommandId(Class<Command> command) {
		if (ids == null)
			return -1;
		Integer id = ids.get(command);
		return id == null ? -1 : id;
	}

	@SuppressWarnings("resource")
	public void launchConsole() throws InterruptedException {
		Scanner scan = new Scanner(System.in);
		String consoleCommand;
		while (true) {
			System.out.print("> ");
			consoleCommand = settings.getCommandLabel() + scan.nextLine();
			executePotentialCommand(parser.parse(consoleCommand));
		}
	}

	private Command findCommand(CommandWrapper wrapper) {
		Integer cmdId = labels.get(new String(wrapper.getArguments()[0]).toLowerCase());
		return commands.get(cmdId);
	}

	public static boolean executeCommand(Command cmd, CommandWrapper wrapper) {
		if (cmd == null)
			return false;

		if (cmd.isDeprecated())
			Console.log("Command '" + cmd + "' is deprecated.");
		if (!cmd.hasPermission(wrapper.getUser())) {
			Console.logErr("Invalid permissions on command '" + cmd + "'!");
			return false;
		}
		if (!cmd.isValid(wrapper)) {
			Console.logErr("Command '" + cmd + "' is not valid!");
			return false;
		}
		if (!cmd.execute(wrapper)) {
			Console.logErr("An error occured while executing command '" + cmd + "'!");
			return false;
		}
		return true;
	}

	public boolean executePotentialCommand(CommandWrapper wrapper) {
		if (wrapper == null)
			return false;

		if (wrapper.getStatus() == CommandStatus.MISSING_TRIGGER) {
			wrapper.getArguments()[0] = settings.getCommandLabel() + wrapper.getArguments()[0];
			wrapper.setStatus(CommandStatus.VALID);
		}

		Command cmd = findCommand(wrapper);
		if (cmd == null)
			return false;

		if (!cmd.isExecuteable()) {
			Console.logErr("Command '" + cmd + "' is not executable!");
			return false;
		}
		return executeCommand(cmd, wrapper);
	}

	public boolean executePotentialReactCommand(MessageReaction r, CommandWrapper wrapper) {
		if (r == null)
			return false;
		if (wrapper == null)
			return false;

		ReactCommandCall cmd = reactCommands.get(r.getReactionEmote().getName());
		return executeCommand(cmd, wrapper);
	}

	private Executer loadCommandList() throws IOException {
		commandList = new ArrayList<Command>();
		WowFile safeAddressFile = new WowFile(settings.getSafeURLPath());
		safeAddressFile.loadFile();
		PlayHelper.validAddress = safeAddressFile.getLines();
		WowAudioCore wac = new WowAudioCore();

		commandList.add(new Quit());
		commandList.add(new Help(x -> commands.get(labels.get(x))));
		commandList.add(new Wow());
		
		commandList.add(new Play(wac));
		commandList.add(new PlayNext(wac));
		commandList.add(new PlayAgain(wac));
		commandList.add(new Summon(wac));
		commandList.add(new Skip(wac));
		commandList.add(new Queue(wac, x -> getReactsForCommand(x)));
		commandList.add(new Link(wac));
		commandList.add(new Pause(wac));
		commandList.add(new Leave(wac));

		int id;
		for (int i = 0; i < commandList.size(); i++) {
			id = commandList.get(i).getId();
			labels.put(settings.getCommandLabel() + id, id);
		}

		return this;
	}

	private Executer loadLabels(String path) throws IOException {
		WowFile file = new WowFile(path).loadFile();
		String[] args;
		for (String l : file.getLines()) {
			args = l.split(" ");
			if (args[0].startsWith("#"))
				continue;
			labels.put(args[1], Integer.parseInt(args[0]));
		}
		return this;
	}

	private Executer loadReacts(String path) throws IOException {
		WowFile file = new WowFile(path).loadFile();
		String[] args;
		int commandId;
		String[] nextCommand;
		List<String> reacts;
		for (String l : file.getLines()) {
			args = l.split(" ");
			if (args[0].startsWith("#"))
				continue;

			commandId = Integer.parseInt(args[0]);
			reacts = reactsForCommand.get(commandId);

			if (reacts != null) {
				reacts.add(args[1]);
				reactsForCommand.replace(commandId, reacts);
			} else {
				reacts = new ArrayList<String>();
				reacts.add(args[1]);
				reactsForCommand.put(commandId, reacts);
			}

			nextCommand = new String[args.length - 2];
			for (int i = 2; i < args.length; i++) {
				nextCommand[i - 2] = args[i];
			}

			reactCommands.put(args[1].split(":")[1], new ReactCommandCall(this, nextCommand));
		}
		return this;
	}

	@SuppressWarnings("unchecked")
	private Executer loadCommands() {
		for (int i = 0; i < commandList.size(); i++) {
			commands.put(commandList.get(i).getId(), commandList.get(i));
			ids.put((Class<Command>) commandList.get(i).getClass(), commandList.get(i).getId());
		}
		return this;
	}

}
