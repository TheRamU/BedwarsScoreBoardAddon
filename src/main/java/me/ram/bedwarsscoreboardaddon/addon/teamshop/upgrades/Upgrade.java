package me.ram.bedwarsscoreboardaddon.addon.teamshop.upgrades;

import io.github.bedwarsrel.game.Game;
import io.github.bedwarsrel.game.Team;

public interface Upgrade {

	public UpgradeType getType();

	public String getName();

	public Game getGame();

	public Team getTeam();

	public int getLevel();

	public void setLevel(int level);

	public String getBuyer();

	public void setBuyer(String buyer);

	public void runUpgrade();
}
