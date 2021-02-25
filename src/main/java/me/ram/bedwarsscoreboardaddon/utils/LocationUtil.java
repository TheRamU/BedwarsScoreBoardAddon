package me.ram.bedwarsscoreboardaddon.utils;

import org.bukkit.Location;
import org.bukkit.util.Vector;

public class LocationUtil {

	public static Location getPosition(Location location1, Location location2) {
		double X = location1.getX() - location2.getX();
		double Y = location1.getY() - location2.getY();
		double Z = location1.getZ() - location2.getZ();
		return new Location(location1.getWorld(), location1.getX() + X, location1.getY() + Y, location1.getZ() + Z, location1.getYaw(), location1.getPitch());
	}

	public static Vector getPositionVector(Location location1, Location location2) {
		double X = location1.getX() - location2.getX();
		double Y = location1.getY() - location2.getY();
		double Z = location1.getZ() - location2.getZ();
		return new Vector(X, Y, Z);
	}
}
