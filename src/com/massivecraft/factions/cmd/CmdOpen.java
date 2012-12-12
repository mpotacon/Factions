package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.struct.Permission;

public class CmdOpen extends FCommand
{
	public CmdOpen()
	{
		super();
		this.aliases.add("open");
		
		//this.requiredArgs.add("");
		this.optionalArgs.put("yes/no", "flip");
		
		this.permission = Permission.OPEN.node;
		this.disableOnLock = false;
		
		senderMustBePlayer = true;
		senderMustBeMember = false;
		senderMustBeOfficer = true;
		senderMustBeLeader = false;
	}
	
	@Override
	public void perform()
	{
		// if economy is enabled, they're not on the bypass list, and this command has a cost set, make 'em pay
		if ( ! payForCommand(Conf.econCostOpen, "to open or close the gang", "for opening or closing the gang")) return;

		myGang.setOpen(this.argAsBool(0, ! myGang.getOpen()));
		
		String open = myGang.getOpen() ? "open" : "closed";
		
		// Inform
		myGang.msg("%s<i> changed the gang to <h>%s<i>.", fme.describeTo(myGang, true), open);
		for (Faction faction : Factions.i.get())
		{
			if (faction == myGang)
			{
				continue;
			}
			faction.msg("<i>The gang %s<i> is now %s", myGang.getTag(faction), open);
		}
	}
	
}
