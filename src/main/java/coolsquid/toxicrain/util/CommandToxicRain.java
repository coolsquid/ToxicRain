package coolsquid.toxicrain.util;

import java.util.List;

import com.google.common.collect.Lists;

import coolsquid.toxicrain.config.ConfigManager;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;

public class CommandToxicRain extends CommandBase {

	@Override
	public String getName() {
		return "toxicrain";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return new TextComponentTranslation("toxicrain.command.usage").getUnformattedText();
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		if (args.length < 1) {
			sender.sendMessage(
					new TextComponentString("<ToxicRain> ").setStyle(new Style().setColor(TextFormatting.BLUE))
							.appendSibling(new TextComponentTranslation("toxicrain.command.usage")));
		} else {
			switch (args[0]) {
			case "delay":
				this.delay(args, sender);
				break;
			case "enable":
				this.enable(args, sender);
				break;
			case "disable":
				this.disable(args, sender);
				break;
			default:
				throw new WrongUsageException("toxicrain.command.error.unknownsubcommand", args[0]);
			}
		}
	}

	private void enable(String[] args, ICommandSender sender) throws CommandException {
		if (args.length == 2) {
			EntityPlayerMP player = getPlayer(sender.getServer(), sender, args[1]);
			player.getCapability(IPlayerData.CAPABILITY, EnumFacing.NORTH).setDelay(0);
			sender.sendMessage(new TextComponentString("<ToxicRain> ")
					.setStyle(new Style().setColor(TextFormatting.BLUE)).appendSibling(new TextComponentTranslation(
							"toxicrain.command.enable.success", player.getDisplayNameString())));
		} else if (args.length == 1) {
			if (sender.getCommandSenderEntity() instanceof EntityPlayerMP) {
				sender.getCommandSenderEntity().getCapability(IPlayerData.CAPABILITY, EnumFacing.NORTH).setDelay(0);
				sender.sendMessage(new TextComponentString("<ToxicRain> ")
						.setStyle(new Style().setColor(TextFormatting.BLUE)).appendSibling(new TextComponentTranslation(
								"toxicrain.command.enable.success", sender.getDisplayName().getUnformattedText())));
			} else {
				throw new WrongUsageException("toxicrain.command.error.sendernotplayer", sender.getName());
			}
		} else {
			throw new WrongUsageException("toxicrain.command.error.wrongnumberofarguments", args.length);
		}
	}

	private void disable(String[] args, ICommandSender sender) throws CommandException {
		if (args.length == 2) {
			EntityPlayerMP player = getPlayer(sender.getServer(), sender, args[1]);
			player.getCapability(IPlayerData.CAPABILITY, EnumFacing.NORTH).setDelay(-1);
			sender.sendMessage(new TextComponentString("<ToxicRain> ")
					.setStyle(new Style().setColor(TextFormatting.BLUE)).appendSibling(new TextComponentTranslation(
							"toxicrain.command.disable.success", player.getDisplayNameString())));
		} else if (args.length == 1) {
			if (sender.getCommandSenderEntity() instanceof EntityPlayerMP) {
				sender.getCommandSenderEntity().getCapability(IPlayerData.CAPABILITY, EnumFacing.NORTH).setDelay(-1);
				sender.sendMessage(new TextComponentString("<ToxicRain> ")
						.setStyle(new Style().setColor(TextFormatting.BLUE)).appendSibling(new TextComponentTranslation(
								"toxicrain.command.disable.success", sender.getDisplayName().getUnformattedText())));
			} else {
				throw new WrongUsageException("toxicrain.command.error.sendernotplayer", sender.getName());
			}
		} else {
			throw new WrongUsageException("toxicrain.command.error.wrongnumberofarguments", args.length);
		}
	}

	private void delay(String[] args, ICommandSender sender) throws CommandException {
		if (args.length == 3) {
			EntityPlayerMP player = getPlayer(sender.getServer(), sender, args[1]);
			int delay = parseInt(args[2]);
			if (delay < -1 || (delay < 0 && delay != -1)) {
				throw new WrongUsageException("toxicrain.command.error.baddelay", delay);
			}
			player.getCapability(IPlayerData.CAPABILITY, EnumFacing.NORTH).setDelay(delay);
			if (delay == 0) {
				sender.sendMessage(new TextComponentString("<ToxicRain> ")
						.setStyle(new Style().setColor(TextFormatting.BLUE)).appendSibling(new TextComponentTranslation(
								"toxicrain.command.enable.success", player.getDisplayName().getUnformattedText())));
			} else if (delay == -1) {
				sender.sendMessage(new TextComponentString("<ToxicRain> ")
						.setStyle(new Style().setColor(TextFormatting.BLUE)).appendSibling(new TextComponentTranslation(
								"toxicrain.command.disable.success", player.getDisplayName().getUnformattedText())));
			} else {
				sender.sendMessage(new TextComponentString("<ToxicRain> ")
						.setStyle(new Style().setColor(TextFormatting.BLUE)).appendSibling(new TextComponentTranslation(
								"toxicrain.command.delay.success", player.getDisplayName().getFormattedText(), args[1])));
			}
		} else if (args.length == 2) {
			if (sender.getCommandSenderEntity() instanceof EntityPlayerMP) {
				int delay = parseInt(args[1]);
				if (delay < -1 || (delay < 0 && delay != -1)) {
					throw new WrongUsageException("toxicrain.command.error.baddelay", delay);
				}
				sender.getCommandSenderEntity().getCapability(IPlayerData.CAPABILITY, EnumFacing.NORTH).setDelay(delay);
				if (delay == 0) {
					sender.sendMessage(new TextComponentString("<ToxicRain> ")
							.setStyle(new Style().setColor(TextFormatting.BLUE)).appendSibling(new TextComponentTranslation(
									"toxicrain.command.enable.success", sender.getDisplayName().getUnformattedText())));
				} else if (delay == -1) {
					sender.sendMessage(new TextComponentString("<ToxicRain> ")
							.setStyle(new Style().setColor(TextFormatting.BLUE)).appendSibling(new TextComponentTranslation(
									"toxicrain.command.disable.success", sender.getDisplayName().getUnformattedText())));
				} else {
					sender.sendMessage(new TextComponentString("<ToxicRain> ")
							.setStyle(new Style().setColor(TextFormatting.BLUE)).appendSibling(new TextComponentTranslation(
									"toxicrain.command.delay.success", sender.getDisplayName().getFormattedText(), args[1])));
				}
			} else {
				throw new WrongUsageException("toxicrain.command.error.sendernotplayer", sender.getName());
			}
		} else {
			throw new WrongUsageException("toxicrain.command.error.wrongnumberofarguments", args.length);
		}
	}

	@Override
	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args,
			BlockPos targetPos) {
		if (args.length == 1) {
			return getListOfStringsMatchingLastWord(args, "enable", "disable", "delay");
		} else if (args.length == 2) {
			return getListOfStringsMatchingLastWord(args,
					Lists.transform(server.getPlayerList().getPlayers(), (player) -> player.getDisplayNameString()));
		}
		return super.getTabCompletions(server, sender, args, targetPos);
	}

	@Override
	public boolean isUsernameIndex(String[] args, int index) {
		return index == 1;
	}

	@Override
	public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
		return ConfigManager.enableCommand && super.checkPermission(server, sender);
	}
}