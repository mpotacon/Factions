package com.massivecraft.factions.cmd;

import org.bukkit.Bukkit;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.Conf;
import com.massivecraft.factions.P;
import com.massivecraft.factions.event.LandUnclaimAllEvent;
import com.massivecraft.factions.integration.Econ;
import com.massivecraft.factions.integration.SpoutFeatures;
import com.massivecraft.factions.struct.Permission;

public class CmdUnclaimall extends FCommand
{	
	public CmdUnclaimall()
	{
		this.aliases.add("unclaimall");
		this.aliases.add("declaimall");
		
		//this.requiredArgs.add("");
		//this.optionalArgs.put("", "");
		
		this.permission = Permission.UNCLAIM_ALL.node;
		this.disableOnLock = true;
		
		senderMustBePlayer = true;
		senderMustBeMember = false;
		senderMustBeOfficer = true;
		senderMustBeLeader = false;
	}
	
	@Override
	public void perform()
	{
		if (Econ.shouldBeUsed())
		{
			double refund = Econ.calculateTotalLandRefund(myGang.getLandRounded());
			if(Conf.bankEnabled && Conf.bankFactionPaysLandCosts)
			{
				if ( ! Econ.modifyMoney(myGang, refund, "to unclaim all gang land", "for unclaiming all gang land")) return;
			}
			else
			{
				if ( ! Econ.modifyMoney(fme      , refund, "to unclaim all gang land", "for unclaiming all gang land")) return;
			}
		}

		LandUnclaimAllEvent unclaimAllEvent = new LandUnclaimAllEvent(myGang, fme);
	Bukkit.getServer().getPluginManager().callEvent(unclaimAllEvent);
		// this event cannot be cancelled

		Board.unclaimAll(myGang.getId());
		myGang.msg("%s<i> unclaimed ALL of your gang's land.", fme.describeTo(myGang, true));
		SpoutFeatures.updateTerritoryDisplayLoc(null);

		if (Conf.logLandUnclaims)
			P.p.log(fme.getName()+" unclaimed everything for the gang: "+myGang.getTag());
	}
	
}
