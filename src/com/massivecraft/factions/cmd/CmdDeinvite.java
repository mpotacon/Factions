package com.massivecraft.factions.cmd;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.struct.Permission;

public class CmdDeinvite extends FCommand
{
	
	public CmdDeinvite()
	{
		super();
		this.aliases.add("deinvite");
		this.aliases.add("deinv");
		
		this.requiredArgs.add("player");
		//this.optionalArgs.put("", "");
		
		this.permission = Permission.DEINVITE.node;
		this.disableOnLock = true;
		
		senderMustBePlayer = true;
		senderMustBeMember = false;
		senderMustBeOfficer = true;
		senderMustBeLeader = false;
	}
	
	@Override
	public void perform()
	{
		FPlayer you = this.argAsBestFPlayerMatch(0);
		if (you == null) return;
		
		if (you.getFaction() == myGang)
		{
			msg("%s<i> is already a member of %s", you.getName(), myGang.getTag());
			msg("<i>You might want to: %s", p.cmdBase.cmdKick.getUseageTemplate(false));
			return;
		}
		
		myGang.deinvite(you);
		
		you.msg("%s<i> revoked your invitation to <h>%s<i>.", fme.describeTo(you), myGang.describeTo(you));
		
		myGang.msg("%s<i> revoked %s's<i> invitation.", fme.describeTo(myGang), you.describeTo(myGang));
	}
	
}
