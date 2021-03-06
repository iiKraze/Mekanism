package mekanism.common.config;

import java.util.ArrayList;
import java.util.List;
import mekanism.api.math.FloatingLong;
import mekanism.common.config.value.CachedBooleanValue;
import mekanism.common.config.value.CachedConfigValue;
import mekanism.common.config.value.CachedDoubleValue;
import mekanism.common.config.value.CachedEnumValue;
import mekanism.common.config.value.CachedFloatingLongValue;
import mekanism.common.config.value.CachedIntValue;
import mekanism.common.config.value.CachedLongValue;
import mekanism.common.tier.EnergyCubeTier;
import mekanism.common.tier.FluidTankTier;
import mekanism.common.tier.GasTankTier;
import mekanism.common.util.UnitDisplayUtils.EnergyType;
import mekanism.common.util.UnitDisplayUtils.TempType;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.ModConfig.Type;

public class GeneralConfig extends BaseMekanismConfig {

    private static final String CONVERSION_CATEGORY = "energy_conversion";
    private static final String MINER_CATEGORY = "digital_miner";
    private static final String LASER_SETTINGS = "laser";
    private static final String PUMP_CATEGORY = "pump";
    private static final String ENTANGLOPORTER_CATEGORY = "quantum_entangloporter";
    private static final String SECURITY_CATEGORY = "security";
    private static final String EVAPORATION_CATEGORY = "thermal_evaporation";

    private final ForgeConfigSpec configSpec;

    public final CachedBooleanValue logPackets;
    public final CachedBooleanValue allowChunkloading;
    public final CachedIntValue blockDeactivationDelay;
    public final CachedConfigValue<List<String>> cardboardModBlacklist;
    public final CachedBooleanValue prefilledGasTanks;
    public final CachedBooleanValue transmitterAlloyUpgrade;
    public final CachedIntValue maxUpgradeMultiplier;
    public final CachedDoubleValue boilerWaterConductivity;
    public final CachedDoubleValue heatPerFuelTick;
    public final CachedDoubleValue resistiveHeaterEfficiency;
    public final CachedDoubleValue superheatingHeatTransfer;
    public final CachedEnumValue<TempType> tempUnit;
    //Energy Conversion
    public final CachedBooleanValue blacklistIC2;
    public final CachedFloatingLongValue FROM_IC2;
    public final CachedFloatingLongValue TO_IC2;
    public final CachedBooleanValue blacklistForge;
    public final CachedFloatingLongValue FROM_FORGE;
    public final CachedFloatingLongValue TO_FORGE;
    public final CachedFloatingLongValue FROM_H2;
    public final CachedIntValue ETHENE_BURN_TIME;
    public final CachedFloatingLongValue maxEnergyPerSteam;
    public final CachedEnumValue<EnergyType> energyUnit;
    //Digital Miner
    public final CachedIntValue minerSilkMultiplier;
    public final CachedIntValue minerMaxRadius;
    public final CachedIntValue minerTicksPerMine;
    //Laser
    public final CachedBooleanValue aestheticWorldDamage;
    public final CachedIntValue laserRange;
    public final CachedFloatingLongValue laserEnergyNeededPerHardness;
    //Pump
    public final CachedIntValue maxPumpRange;
    public final CachedBooleanValue pumpWaterSources;
    public final CachedIntValue maxPlenisherNodes;
    //Quantum Entangloporter
    public final CachedFloatingLongValue entangloporterEnergyBuffer;
    public final CachedIntValue entangloporterFluidBuffer;
    public final CachedLongValue entangloporterGasBuffer;
    //Security
    public final CachedBooleanValue allowProtection;
    public final CachedBooleanValue opsBypassRestrictions;
    //Thermal Evaporation Tower
    public final CachedDoubleValue evaporationHeatDissipation;
    public final CachedDoubleValue evaporationTempMultiplier;
    public final CachedDoubleValue evaporationSolarMultiplier;
    public final CachedDoubleValue evaporationHeatCapacity;

    GeneralConfig() {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        builder.comment("General Config. This config is synced from server to client.").push("general");

        logPackets = CachedBooleanValue.wrap(this, builder.comment("Log Mekanism packet names. Debug setting.")
              .define("logPackets", false));
        allowChunkloading = CachedBooleanValue.wrap(this, builder.comment("Disable to make the anchor upgrade not do anything.")
              .define("allowChunkloading", true));
        blockDeactivationDelay = CachedIntValue.wrap(this, builder.comment("How many ticks must pass until a block's active state is synced with the client, if it has been rapidly changing.")
              .define("blockDeactivationDelay", 100));
        cardboardModBlacklist = CachedConfigValue.wrap(this, builder.comment("Any mod ids added to this list will not be able to have any of their blocks, picked up by the cardboard box.")
              .define("cardboardModBlacklist", new ArrayList<>()));
        prefilledGasTanks = CachedBooleanValue.wrap(this, builder.comment("Add filled creative gas tanks to creative/JEI.")
              .define("prefilledGasTanks", true));
        transmitterAlloyUpgrade = CachedBooleanValue.wrap(this, builder.comment("Allow right clicking on Cables/Pipes/Tubes with alloys to upgrade the tier.")
              .define("transmitterAlloyUpgrade", true));
        //If this is less than 1, upgrades make machines worse. If less than 0, I don't even know.
        maxUpgradeMultiplier = CachedIntValue.wrap(this, builder.comment("Base factor for working out machine performance with upgrades - UpgradeModifier * (UpgradesInstalled/UpgradesPossible).")
              .defineInRange("maxUpgradeMultiplier", 10, 1, Integer.MAX_VALUE));
        boilerWaterConductivity = CachedDoubleValue.wrap(this, builder.comment("How much Boiler heat is immediately usable to convert water to steam.")
              .define("boilerWaterConductivity", 0.7));
        heatPerFuelTick = CachedDoubleValue.wrap(this, builder.comment("Amount of heat produced per fuel tick of a fuel's burn time in the Fuelwood Heater.")
              .define("heatPerFuelTick", 400D));
        resistiveHeaterEfficiency = CachedDoubleValue.wrap(this, builder.comment("How much heat energy is created from one Joule of regular energy in the Resistive Heater.")
              .defineInRange("resistiveHeaterEfficiency", 0.6, 0, 1));
        superheatingHeatTransfer = CachedDoubleValue.wrap(this, builder.comment("Amount of heat each Boiler heating element produces.")
              .define("superheatingHeatTransfer", 512_000D));
        tempUnit = CachedEnumValue.wrap(this, builder.comment("Displayed temperature unit in Mekanism GUIs.")
              .defineEnum("temperatureUnit", TempType.K));
        builder.comment("Energy Conversion Rate Settings").push(CONVERSION_CATEGORY);
        blacklistIC2 = CachedBooleanValue.wrap(this, builder.comment("Disables IC2 power integration. Requires world restart (server-side option in SMP).")
              .worldRestart()
              .define("blacklistIC2", false));
        FROM_IC2 = CachedFloatingLongValue.define(this, builder, "Conversion multiplier from EU to Joules (EU * JoulesToEU = Joules)",
              "JoulesToEU", FloatingLong.createConst(10), CachedFloatingLongValue.POSITIVE);
        TO_IC2 = CachedFloatingLongValue.define(this, builder, "Conversion multiplier from Joules to EU (Joules * EUToJoules = EU)",
              "EUToJoules", FloatingLong.createConst(0.1), CachedFloatingLongValue.POSITIVE);
        blacklistForge = CachedBooleanValue.wrap(this, builder.comment("Disables Forge Energy (FE,RF,IF,uF,CF) power integration. Requires world restart (server-side option in SMP).")
              .worldRestart()
              .define("blacklistForge", false));
        FROM_FORGE = CachedFloatingLongValue.define(this, builder, "Conversion multiplier from Forge Energy to Joules (FE * JoulesToForge = Joules)",
              "JoulesToForge", FloatingLong.createConst(2.5), CachedFloatingLongValue.POSITIVE);
        TO_FORGE = CachedFloatingLongValue.define(this, builder, "Conversion multiplier from Joules to Forge Energy (Joules * ForgeToJoules = FE)",
              "ForgeToJoules", FloatingLong.createConst(0.4), CachedFloatingLongValue.POSITIVE);
        FROM_H2 = CachedFloatingLongValue.define(this, builder, "How much energy is produced per mB of Hydrogen, also affects Electrolytic Separator usage, Ethylene burn rate and Gas generator energy capacity.",
              "HydrogenEnergyDensity", FloatingLong.createConst(200), CachedFloatingLongValue.POSITIVE);
        ETHENE_BURN_TIME = CachedIntValue.wrap(this, builder.comment("Burn time for Ethylene (1mB hydrogen + 2*bioFuel/tick*200ticks/100mB * 20x efficiency bonus).")
              .define("EthyleneBurnTime", 40));
        maxEnergyPerSteam = CachedFloatingLongValue.define(this, builder, "Maximum Joules per mB of Steam. Also affects Thermoelectric Boiler.",
              "maxEnergyPerSteam", FloatingLong.createConst(10));
        energyUnit = CachedEnumValue.wrap(this, builder.comment("Displayed energy type in Mekanism GUIs.")
              .defineEnum("energyType", EnergyType.FE));
        builder.pop();

        builder.comment("Digital Miner Settings").push(MINER_CATEGORY);
        minerSilkMultiplier = CachedIntValue.wrap(this, builder.comment("Energy multiplier for using silk touch mode with the Digital Miner.")
              .define("silkMultiplier", 6));
        minerMaxRadius = CachedIntValue.wrap(this, builder.comment("Maximum radius in blocks that the Digital Miner can reach. (Increasing this may have negative effects on stability and/or performance. We strongly recommend you leave it at the default value).")
              .defineInRange("maxRadius", 32, 1, Integer.MAX_VALUE));
        minerTicksPerMine = CachedIntValue.wrap(this, builder.comment("Number of ticks required to mine a single block with a Digital Miner (without any upgrades).")
              .defineInRange("ticksPerMine", 80, 1, Integer.MAX_VALUE));
        builder.pop();

        builder.comment("Laser Settings").push(LASER_SETTINGS);
        aestheticWorldDamage = CachedBooleanValue.wrap(this, builder.comment("If enabled, lasers can break blocks and the flamethrower starts fires.")
              .define("aestheticWorldDamage", true));
        laserRange = CachedIntValue.wrap(this, builder.comment("How far (in blocks) a laser can travel.")
              .define("range", 64));
        laserEnergyNeededPerHardness = CachedFloatingLongValue.define(this, builder, "Energy needed to destroy or attract blocks with a Laser (per block hardness level).",
              "energyNeededPerHardness", FloatingLong.createConst(100_000));
        builder.pop();

        builder.comment("Pump Settings").push(PUMP_CATEGORY);
        maxPumpRange = CachedIntValue.wrap(this, builder.comment("Maximum block distance to pull fluid from for the Electric Pump.")
              .define("maxPumpRange", 80));
        pumpWaterSources = CachedBooleanValue.wrap(this, builder.comment("If enabled makes Water and Heavy Water blocks be removed from the world on pump.")
              .define("pumpWaterSources", false));
        maxPlenisherNodes = CachedIntValue.wrap(this, builder.comment("Fluidic Plenisher stops after this many blocks.")
              .define("maxPlenisherNodes", 4_000));
        builder.pop();

        builder.comment("Quantum Entangloporter Settings").push(ENTANGLOPORTER_CATEGORY);
        entangloporterEnergyBuffer = CachedFloatingLongValue.define(this, builder, "Maximum energy buffer (Mekanism Joules) of an Entangoloporter frequency - i.e. the maximum transfer per tick per frequency. Default is ultimate tier energy cube capacity.",
              "energyBuffer", EnergyCubeTier.ULTIMATE.getBaseMaxEnergy(), true, CachedFloatingLongValue.POSITIVE);
        entangloporterFluidBuffer = CachedIntValue.wrap(this, builder.comment("Maximum fluid buffer (mb) of an Entangoloporter frequency - i.e. the maximum transfer per tick per frequency. Default is ultimate tier tank capacity.")
              .worldRestart()
              .defineInRange("fluidBuffer", FluidTankTier.ULTIMATE.getBaseStorage(), 1, Integer.MAX_VALUE));
        entangloporterGasBuffer = CachedLongValue.wrap(this, builder.comment("Maximum gas buffer (mb) of an Entangoloporter frequency - i.e. the maximum transfer per tick per frequency. Default is ultimate tier tank capacity.")
              .worldRestart()
              .defineInRange("gasBuffer", GasTankTier.ULTIMATE.getBaseStorage(), 1, Long.MAX_VALUE));
        builder.pop();

        builder.comment("Block security/protection Settings").push(SECURITY_CATEGORY);
        allowProtection = CachedBooleanValue.wrap(this, builder.comment("Enable the security system for players to prevent others from accessing their machines. Does NOT affect Frequencies.")
              .define("allowProtection", true));
        opsBypassRestrictions = CachedBooleanValue.wrap(this, builder.comment("Ops can bypass the block security restrictions if enabled.")
              .define("opsBypassRestrictions", false));
        builder.pop();

        builder.comment("Thermal Evaporation Plant Settings").push(EVAPORATION_CATEGORY);
        evaporationHeatDissipation = CachedDoubleValue.wrap(this, builder.comment("Thermal Evaporation Tower heat loss per tick.")
              .define("heatDissipation", 0.02));
        evaporationTempMultiplier = CachedDoubleValue.wrap(this, builder.comment("Temperature to amount produced ratio for Thermal Evaporation Tower.")
              .define("tempMultiplier", 0.1));
        evaporationSolarMultiplier = CachedDoubleValue.wrap(this, builder.comment("Heat to absorb per Solar Panel array of Thermal Evaporation Tower.")
              .define("solarMultiplier", 0.2));
        evaporationHeatCapacity = CachedDoubleValue.wrap(this, builder.comment("Heat capacity of Thermal Evaporation Tower layers (increases amount of energy needed to increase temperature).")
              .define("heatCapacity", 100D));
        builder.pop();
        builder.pop();
        configSpec = builder.build();
    }

    @Override
    public String getFileName() {
        return "general";
    }

    @Override
    public ForgeConfigSpec getConfigSpec() {
        return configSpec;
    }

    @Override
    public Type getConfigType() {
        return Type.SERVER;
    }
}