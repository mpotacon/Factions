package com.massivecraft.factions.cmd;

import java.util.ArrayList;

import org.bukkit.Bukkit;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.event.FactionRenameEvent;
import com.massivecraft.factions.integration.SpoutFeatures;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.util.MiscUtil;

public class CmdTag extends FCommand
{
	
	public CmdTag()
	{
		this.aliases.add("tag");
		
		this.requiredArgs.add("new tag");
		//this.optionalArgs.put("", "");
		
		this.permission = Permission.TAG.node;
		this.disableOnLock = true;
		
		senderMustBePlayer = true;
		senderMustBeMember = false;
		senderMustBeOfficer = true;
		senderMustBeLeader = false;
	}
	
	@Override
	public void perform()
	{
		String tag = this.argAsString(0);
		
		// TODO does not first test cover selfcase?
		if (Factions.i.isTagTaken(tag) && ! MiscUtil.getComparisonString(tag).equals(myGang.getComparisonTag()))
		{
			msg("<b>That tag is already taken");
			return;
		}

		ArrayList<String> errors = new ArrayList<String>();
		errors.addAll(Factions.validateTag(tag));
		if (errors.size() > 0)
		{
			sendMessage(errors);
			return;
		}

		// if economy is enabled, they're not on the bypass list, and this command has a cost set, make sure they can pay
		if ( ! canAffordCommand(Conf.econCostTag, "to change the gang tag")) return;

		// trigger the faction rename event (cancellable)
		FactionRenameEvent renameEvent = new FactionRenameEvent(fme, tag);
		Bukkit.getServer().getPluginManager().callEvent(renameEvent);
		if(renameEvent.isCancelled()) return;

		// then make 'em pay (if applicable)
		if ( ! payForCommand(Conf.econCostTag, "to change the gang tag", "for changing the gang tag")) return;

		String oldtag = myGang.getTag();
		myGang.setTag(tag);

		// Inform
		myGang.msg("%s<i> changed your gang tag to %s", fme.describeTo(myGang, true), myGang.getTag(myGang));
		for (Faction faction : Factions.i.get())
		{
			if (faction == myGang)
			{
				continue;
			}
			faction.msg("<i>The gang %s<i> changed their name to %s.", fme.getColorTo(faction)+oldtag, myGang.getTag(faction));
		}

		if (Conf.spoutFactionTagsOverNames)
		{
			SpoutFeatures.updateTitle(myGang, null);
		}
	}
	
}
