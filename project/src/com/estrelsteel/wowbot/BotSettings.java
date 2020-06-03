package com.estrelsteel.wowbot;

public class BotSettings {

	private String token;
	public static long id;
	private long owner;
	private String commandLabel;
	private String commandListPath;
	private String safeURLPath;
	private String reactCommandsPath;

	public void updatePaths(String path) {
		commandListPath = path + commandListPath;
		safeURLPath = path + safeURLPath;
		reactCommandsPath = path + reactCommandsPath;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	@SuppressWarnings("static-method")
	public long getId() {
		return id;
	}

	@SuppressWarnings("static-method")
	public void setId(long id) {
		BotSettings.id = id;
	}

	public long getOwner() {
		return owner;
	}

	public void setOwner(long owner) {
		this.owner = owner;
	}

	public String getCommandLabel() {
		return commandLabel;
	}

	public void setCommandLabel(String commandLabel) {
		this.commandLabel = commandLabel;
	}

	public String getCommandListPath() {
		return commandListPath;
	}

	public void setCommandListPath(String commandListPath) {
		this.commandListPath = commandListPath;
	}

	public String getSafeURLPath() {
		return safeURLPath;
	}

	public void setSafeURLPath(String safeURLPath) {
		this.safeURLPath = safeURLPath;
	}

	public String getReactCommandsPath() {
		return reactCommandsPath;
	}

	public void setReactCommandsPath(String reactCommandsPath) {
		this.reactCommandsPath = reactCommandsPath;
	}

}
