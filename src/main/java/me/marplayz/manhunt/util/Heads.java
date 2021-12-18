package me.marplayz.manhunt.util;

import me.marplayz.manhunt.GUI.SettingMenu;
import org.bukkit.inventory.ItemStack;

public enum Heads {

	PRESENT("MmViY2QyMTU5ODU2ZDc5NWM4OTE1ZThmNTlhODQzNGM4ZTkzNWE0NWE0M2ZhNzFmMDgwOTc4OWJlNzVlM2RlMiJ9fX0=","present"),
	EMD("NTkzNTg3MDNhYjc3MjdkZjMzMjQzMzY5NjllODFkNmY5MmI3YWE3OWVkYjk2NmMwYmU5MWFiMTYxYmFkMWYwMSJ9fX0=", "emd"),
	INSTAGRAM("YjBlYzgyODQxOTkwOWU3YzJmYWZiYjNmNzU4NzNkNzk2ZTkwYmZjYjEyODhhNWNiYmQwMTYxNDYwMjdmMTc4OCJ9fX0=", "instagram"),
	PLASMA("NTkzNTg3MDNhYjc3MjdkZjMzMjQzMzY5NjllODFkNmY5MmI3YWE3OWVkYjk2NmMwYmU5MWFiMTYxYmFkMWYwMSJ9fX0=", "emp"),

	CLASSIC("M2YyZDM4ZWZhZTFlMGE1ODFmMjU4YWEwOWNhNjEwNzQ5ZDNlZmRkZjU4NjUzMDRmMmZhMThiN2ExYTBjM2JjZCJ9fX0=", "classic"), //gold
	RAPID("MWQxNzNhNDY1MTJmYzFhYzIyNzIxNGY1ZTZiOGE5MjQ1ZGE1NDcyYzQ4OWNiOTM2Yjk0NzY0YWFjMTNmOWJmOSJ9fX0=", "rapid"), //emerald
	GODFATHER("NTZlMjAwOTc0OTljZDg2MzBhODM2OTc5OTIyMTYwZmUzZjEzZDNkYWVhOGU1YWRlNTk1MzQ3OWY3YjIzZTkwOCJ9fX0=", "godfather"), //diamond
	ENHANCED("OWUxYTQyMjdkNmFiNTA0M2Q2YmI4MGUxYmZkNDI2NTg4ODUzZjMzMjU2ZWU0NTYxOGEwMmY2ZWUxZWRlNTgzMCJ9fX0=", "enhanced"), //redstone
	CREATIVE("MjBlODM2YjQxYjVlOTExN2FkZWIxYTM2OTYxM2UzMWMzMDM5NzEzMjcxMjlmY2NiOGYzNGIzY2Y1ZGNkY2Q4YyJ9fX0=", "creative"); //Lapiz

	private ItemStack item;
	private String idTag;
	private String prefix = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUv";

	private Heads(String texture, String id) {
		item = SettingMenu.createSkull(prefix + texture, id);
		idTag = id;
	}

	public ItemStack getItemStack(){
		return  item;
	}

	public String getName(){
		return idTag;
	}

}

