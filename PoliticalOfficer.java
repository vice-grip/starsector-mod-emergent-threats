package data.hullmods.vice;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;

public class PoliticalOfficer extends BaseHullMod {
	
	//hullmod effects are handled by individual Andradanism skills
	private static String SL_MEMKEY = "$xo_supreme_leadership_is_active";
	private static String UD_MEMKEY = "$xo_unbreakable_defense_is_active";
	private static String UO_MEMKEY = "$xo_unstoppable_offense_is_active";
	//private static String UC_MEMKEY = "$xo_unwavering_conviction_is_active";
	private static String MM_MEMKEY = "$xo_mass_mobilization_is_active";
	private static float TIME_ACCELERATION_BONUS = 10f;
	
	@Override
	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
		
	}
	
	@Override
	public void applyEffectsAfterShipCreation(ShipAPI ship, String id) {
		if (!Global.getSector().getMemoryWithoutUpdate().is(SL_MEMKEY, true)) return;
		String manufacturer = ship.getVariant().getHullSpec().getManufacturer();
		if (isLionsGuard(manufacturer)) {
			ship.getMutableStats().getTimeMult().modifyMult(id, 1f + TIME_ACCELERATION_BONUS * 0.01f);
		}
	}
	
	private boolean isLionsGuard (String manufacturer) {
		if (manufacturer.equals("Lion's Guard") 
					|| manufacturer.equals("Sindrian Fuel Company")
					|| manufacturer.equals("Sindrian Diktat")) return true;
		else return false;
	}
	
	@Override
    public boolean isApplicableToShip(ShipAPI ship) {
		if (ship.getVariant().getHullSpec().getMinCrew() < 1f) return false;
		return true;
	}
	
	public String getUnapplicableReason(ShipAPI ship) {
		if (ship.getVariant().getHullSpec().getMinCrew() < 1f) return "Ship cannot be uncrewed";
		return null;
	}
	
	public String getDescriptionParam(int index, HullSize hullSize) {
		if (index == 0) return "Andradanism";
		if (index == 1) return "" + (int) TIME_ACCELERATION_BONUS + "%";
		return null;
	}
	
	@Override
	public void addPostDescriptionSection(TooltipMakerAPI tooltip, HullSize hullSize, ShipAPI ship, float width, boolean isForModSpec) {
		if (!Global.getSector().getMemoryWithoutUpdate().is(SL_MEMKEY, true)) {
			tooltip.addPara("%s", 10f, Misc.getNegativeHighlightColor(), "Andradanism executive officer missing, hullmod is inactive");
			return;
		}
		String s = "";
		String bonuses = "";
		String manufacturer = ship == null ? "" : ship.getVariant().getHullSpec().getManufacturer();
		if (isLionsGuard(manufacturer)) {
			s = "Enhanced bonus: %s";
			bonuses = "" + (int) TIME_ACCELERATION_BONUS + "% timeflow";
		}
		else {
			s = "Enhanced bonus: %s";
			boolean isBonuses = false;
			bonuses = "Supreme Leadership +10% CR";
			if (manufacturer.equals("Midline")) bonuses = "Supreme Leadership +5% CR";
			if (Global.getSector().getMemoryWithoutUpdate().is(UD_MEMKEY, true)) {
				bonuses += "\n                                Unbreakabe Defense +5% flux/armor";
				isBonuses = true;
			}
			if (Global.getSector().getMemoryWithoutUpdate().is(UO_MEMKEY, true)) {
				bonuses += "\n                                Unstoppable Offense +5% EW damage";
				isBonuses = true;
			}
			if (Global.getSector().getMemoryWithoutUpdate().is(MM_MEMKEY, true)) {
				bonuses += "\n                                Mass Mobilization +5% DP reduction";
				isBonuses = true;
			}
			if (isBonuses) s = "Enhanced bonuses: %s";
		}
		tooltip.addPara(s, 10f, Misc.getPositiveHighlightColor(), bonuses);
	}
}

