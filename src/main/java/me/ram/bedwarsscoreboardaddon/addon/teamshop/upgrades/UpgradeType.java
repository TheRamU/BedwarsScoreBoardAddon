package me.ram.bedwarsscoreboardaddon.addon.teamshop.upgrades;

public enum UpgradeType {

	FAST_DIG("FastDig", FastDig.class, false),
	SHARPNESS("Sharpness", FastDig.class, false),
	PROTECTION("Sharpness", Protection.class, false),
	HEAL("Heal", Heal.class, false),
	IRON_FORGE("IronForge", IronForge.class, false),
	TRAP("Trap", Trap.class, true),
	DEFENSE("Defense", Defense.class, true),
	COUNTER_OFFENSIVE_TRAP("CounterOffensiveTrap", CounterOffensiveTrap.class, true),
	ALARM_TRAP("AlarmTrap", AlarmTrap.class, true);

	private String name;
	private Class<? extends Upgrade> clazz;
	private boolean is_trap;

	private UpgradeType(String name, Class<? extends Upgrade> clazz, boolean is_trap) {
		this.name = name;
		this.clazz = clazz;
		this.is_trap = is_trap;
	}

	public String getName() {
		return name;
	}

	public Class<? extends Upgrade> getUpgradeClass() {
		return clazz;
	}

	public boolean isTrap() {
		return is_trap;
	}
}
