package mekanism.generators.common;

import mekanism.generators.client.GeneratorsBlockStateProvider;
import mekanism.generators.client.GeneratorsItemModelProvider;
import mekanism.generators.client.GeneratorsLangProvider;
import mekanism.generators.client.GeneratorsSoundProvider;
import mekanism.generators.common.loot.GeneratorsLootProvider;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.client.model.generators.ExistingFileHelper;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;

@EventBusSubscriber(modid = MekanismGenerators.MODID, bus = Bus.MOD)
public class GeneratorsDataGenerator {

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator gen = event.getGenerator();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
        if (event.includeClient()) {
            //Client side data generators
            gen.addProvider(new GeneratorsLangProvider(gen));
            gen.addProvider(new GeneratorsSoundProvider(gen, existingFileHelper));
            gen.addProvider(new GeneratorsBlockStateProvider(gen, existingFileHelper));
            gen.addProvider(new GeneratorsItemModelProvider(gen, existingFileHelper));
        }
        if (event.includeServer()) {
            //Server side data generators
            gen.addProvider(new GeneratorsLootProvider(gen));
            gen.addProvider(new GeneratorsTagProvider(gen));
            gen.addProvider(new GeneratorsRecipeProvider(gen));
        }
    }
}