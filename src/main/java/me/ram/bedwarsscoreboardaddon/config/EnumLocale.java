package me.ram.bedwarsscoreboardaddon.config;

public enum EnumLocale {
	EN_US("en_US"),
	ZH_CN("zh_CN"),
	ZH_TW("zh_TW");

	private String name;

	private EnumLocale(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public static EnumLocale getByName(String n) {
		for (EnumLocale type : values()) {
			if (type.getName().equals(n)) {
				return type;
			}
		}
		return null;
	}
}
