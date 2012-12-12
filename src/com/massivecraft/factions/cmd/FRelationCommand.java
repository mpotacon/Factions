package com.massivecraft.factions.cmd;

import org.bukkit.Bukkit;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.event.FactionRelationEvent;
import com.massivecraft.factions.integration.SpoutFeatures;
import com.massivecraft.factions.struct.FFlag;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.struct.Rel;

public abstract class FRelationCommand extends FCommand
{
	public Rel targetRelation;
	
	public FRelationCommand()
	{
		super();
		this.requiredArgs.add("gang");
		//this.optionalArgs.put("", "");
		
		this.permission = Permission.RELATION.node;
		this.disableOnLock = true;
		
		senderMustBePlayer = true;
		senderMustBeMember = false;
		senderMustBeOfficer = true;
		senderMustBeLeader = false;
	}
	
	@Override
	public void perform()
	{
		Faction them = this.argAsFaction(0);
		if (them == null) return;
		
		/*if ( ! them.isNormal())
		{
			msg("<b>Nope! You can't.");
			return;
		}*/
		
		if (them == myGang)
		{
			msg("<b>Nope! You can't declare a relation to yourself :)");
			return;
		}

		if (myGang.getRelationWish(them) == targetRelation)
		{
			msg("<b>You already have that relation wish set with %s.", them.getTag());
			return;
		}

		// if economy is enabled, they're not on the bypass list, and this command has a cost set, make 'em pay
		if ( ! payForCommand(targetRelation.getRelationCost(), "to change a relation wish", "for changing a relation wish")) return;

		// try to set the new relation
		Rel oldRelation = myGang.getRelationTo(them, true);
		myGang.setRelationWish(them, targetRelation);
		Rel currentRelation = myGang.getRelationTo(them, true);

		// if the relation change was successful
		if (targetRelation == currentRelation)
		{
			// trigger the faction relation event
			FactionRelationEvent relationEvent = new FactionRelationEvent(myGang, them, oldRelation, currentRelation);
			Bukkit.getServer().getPluginManager().callEvent(relationEvent);

			them.msg("%s<i> is now %s.", myGang.describeTo(them, true), targetRelation.getDescFactionOne());
			myGang.msg("%s<i> is now %s.", them.describeTo(myGang, true), targetRelation.getDescFactionOne());
		}
		// inform the other faction of your request
		else
		{
			them.msg("%s<i> wishes to be %s.", myGang.describeTo(them, true), targetRelation.getColor()+targetRelation.getDescFactionOne());
			them.msg("<i>Type <c>/"+Conf.baseCommandAliases.get(0)+" "+targetRelation+" "+myGang.getTag()+"<i> to accept.");
			myGang.msg("%s<i> were informed that you wish to be %s<i>.", them.describeTo(myGang, true), targetRelation.getColor()+targetRelation.getDescFactionOne());
		}
		
		// TODO: The ally case should work!!
		//   * this might have to be bumped up to make that happen, & allow ALLY,NEUTRAL only
		if ( targetRelation != Rel.TRUCE && them.getFlag(FFlag.PEACEFUL))
		{
			them.msg("<i>This will have no effect while your gang is peaceful.");
			myGang.msg("<i>This will have no effect while their gang is peaceful.");
		}
		
		if ( targetRelation != Rel.TRUCE && myGang.getFlag(FFlag.PEACEFUL))
		{
			them.msg("<i>This will have no effect while their gang is peaceful.");
			myGang.msg("<i>This will have no effect while your gang is peaceful.");
		}

		SpoutFeatures.updateTitle(myGang, them);
		SpoutFeatures.updateTitle(them, myGang);
		SpoutFeatures.updateTerritoryDisplayLoc(null);
	}
}
