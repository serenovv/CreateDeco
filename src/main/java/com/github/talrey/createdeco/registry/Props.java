package com.github.talrey.createdeco.registry;

import com.github.talrey.createdeco.CreateDecoMod;
import com.github.talrey.createdeco.Registration;
import com.github.talrey.createdeco.blocks.CageLampBlock;
import com.github.talrey.createdeco.blocks.CoinStackBlock;
import com.github.talrey.createdeco.blocks.DecalBlock;
import com.github.talrey.createdeco.items.CoinStackItem;
import com.jozufozu.flywheel.util.NonNullSupplier;
import com.mojang.math.Vector3f;
import com.simibubi.create.AllItems;
import com.simibubi.create.repack.registrate.Registrate;
import com.simibubi.create.repack.registrate.builders.BlockBuilder;
import com.simibubi.create.repack.registrate.builders.ItemBuilder;
import com.simibubi.create.repack.registrate.util.entry.BlockEntry;
import com.simibubi.create.repack.registrate.util.entry.ItemEntry;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ModelBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

public class Props {
  public static HashMap<DyeColor, BlockEntry<DecalBlock>> DECAL_BLOCKS = new HashMap<>();

  public static HashMap<String, BlockEntry<CageLampBlock>> YELLOW_CAGE_LAMPS = new HashMap<>();
  public static HashMap<String, BlockEntry<CageLampBlock>>    RED_CAGE_LAMPS = new HashMap<>();
  public static HashMap<String, BlockEntry<CageLampBlock>>  GREEN_CAGE_LAMPS = new HashMap<>();
  public static HashMap<String, BlockEntry<CageLampBlock>>   BLUE_CAGE_LAMPS = new HashMap<>();

  public static HashMap<String, ItemEntry<Item>> COIN_ITEM               = new HashMap<>();
  public static HashMap<String, ItemEntry<CoinStackItem>> COINSTACK_ITEM = new HashMap<>();
  public static HashMap<String, BlockEntry<CoinStackBlock>> COIN_BLOCKS  = new HashMap<>();

  public static ArrayList<String> COIN_TYPES = new ArrayList<>();

  public static final ResourceLocation YELLOW_ON  = new ResourceLocation(CreateDecoMod.MODID, "block/palettes/cage_lamp/light_default");
  public static final ResourceLocation YELLOW_OFF = new ResourceLocation(CreateDecoMod.MODID, "block/palettes/cage_lamp/light_default_off");
  public static final ResourceLocation RED_ON     = new ResourceLocation(CreateDecoMod.MODID, "block/palettes/cage_lamp/light_redstone");
  public static final ResourceLocation RED_OFF    = new ResourceLocation(CreateDecoMod.MODID, "block/palettes/cage_lamp/light_redstone_off");
  public static final ResourceLocation GREEN_ON   = new ResourceLocation(CreateDecoMod.MODID, "block/palettes/cage_lamp/light_green");
  public static final ResourceLocation GREEN_OFF  = new ResourceLocation(CreateDecoMod.MODID, "block/palettes/cage_lamp/light_green_off");
  public static final ResourceLocation BLUE_ON    = new ResourceLocation(CreateDecoMod.MODID, "block/palettes/cage_lamp/light_soul");
  public static final ResourceLocation BLUE_OFF   = new ResourceLocation(CreateDecoMod.MODID, "block/palettes/cage_lamp/light_soul_off");

  public static ItemBuilder<CoinStackItem,?> buildCoinStackItem (Registrate reg, NonNullSupplier<Item> coin, String name) {
    return reg.item(name.toLowerCase(Locale.ROOT) + "_coinstack", (p)-> new CoinStackItem(p, name.toLowerCase(Locale.ROOT)))
      .properties(p -> (name.contains("Netherite")) ? p.fireResistant() : p)
      .recipe((ctx, prov)-> ShapelessRecipeBuilder.shapeless(ctx.get())
        .requires(coin.get(), 4)
        .unlockedBy("has_item", InventoryChangeTrigger.TriggerInstance.hasItems(coin.get()))
        .save(prov)
      )
      .lang(name + " Coinstack");
  }

  public static ItemBuilder<Item,?> buildCoinItem (Registrate reg, NonNullSupplier<Item> coinstack, String name) {
    return reg.item(name.toLowerCase(Locale.ROOT) + "_coin", Item::new)
      .properties(p -> (name.contains("Netherite")) ? p.fireResistant() : p)
      .recipe((ctx, prov)-> ShapelessRecipeBuilder.shapeless(ctx.get(), 4)
        .requires(coinstack.get())
        .unlockedBy("has_item", InventoryChangeTrigger.TriggerInstance.hasItems(coinstack.get()))
        .save(prov)
      )
      .lang(name + " Coin");
  }

  public static BlockBuilder<CoinStackBlock,?> buildCoinStackBlock (
    Registrate reg, NonNullSupplier<Item> material, String name, ResourceLocation side, ResourceLocation bottom, ResourceLocation top
  ) {
    return reg.block(name.toLowerCase(Locale.ROOT)+"_coinstack_block", (p)->new CoinStackBlock(p, name.toLowerCase(Locale.ROOT)))
      .properties(props -> props.noOcclusion().strength(0.5f).sound(SoundType.CHAIN))
      .blockstate((ctx,prov)-> prov.getVariantBuilder(ctx.getEntry()).forAllStates(state -> {
        int layer = state.getValue(BlockStateProperties.LAYERS);
        return ConfiguredModel.builder().modelFile(prov.models().withExistingParent(
              ctx.getName() + "_" + layer, prov.modLoc("block/layers_bottom_top_" + layer)
            )
            .texture("side",   side)
            .texture("bottom", bottom)
            .texture("top",    top)
        ).build(); }))
      .addLayer(()-> RenderType::cutoutMipped)
      .lang(name + " Stack Block")
      .loot((table, block) -> {
        LootTable.Builder builder      = LootTable.lootTable();
        LootPool.Builder pool          = LootPool.lootPool().setRolls(ConstantValue.exactly(1));
        for (int layer = 1; layer <= 8; layer++) {
          LootItem.Builder<?> entry = LootItem.lootTableItem(material.get());
          entry.when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(block)
            .setProperties(StatePropertiesPredicate.Builder.properties()
              .hasProperty(BlockStateProperties.LAYERS, layer)
            )).apply(SetItemCountFunction.setCount(ConstantValue.exactly(layer)));
          pool.add(entry);
        }
        table.add(block, builder.withPool(pool));
      });
  }

  public static BlockBuilder<CageLampBlock, ?> buildCageLamp (
    Registrate reg, String name, DyeColor color, ResourceLocation cage, ResourceLocation lampOn, ResourceLocation lampOff
  ) {
    return reg.block(color.getName().toLowerCase(Locale.ROOT) + "_" + name.toLowerCase(Locale.ROOT) + "_lamp",
        (p)-> new CageLampBlock(p, new Vector3f(0.3f, 0.3f, 0f)))
      .initialProperties(Material.METAL)
      .properties(props-> props.noOcclusion().strength(0.5f).sound(SoundType.LANTERN).lightLevel((state)-> state.getValue(BlockStateProperties.LIT)?15:0))
      .blockstate((ctx,prov)-> prov.getVariantBuilder(ctx.get()).forAllStates(state-> {
        int y = 0;
        int x = 90;
        switch (state.getValue(BlockStateProperties.FACING)) {
          case NORTH -> y =   0;
          case SOUTH -> y = 180;
          case WEST  -> y = -90;
          case EAST  -> y =  90;
          case DOWN  -> x = 180;
          default    -> x =   0; // up
        }
        return ConfiguredModel.builder().modelFile(prov.models()
          .withExistingParent(ctx.getName() + (state.getValue(BlockStateProperties.LIT) ? "" : "_off"), prov.modLoc("block/cage_lamp"))
          .texture("cage", cage)
          .texture("lamp", state.getValue(BlockStateProperties.LIT) ? lampOn : lampOff)
          .texture("particle", cage)
        ).rotationX(x).rotationY(y).build(); }))
      .addLayer(()-> RenderType::cutoutMipped)
      .lang(color.name().charAt(0) + color.name().substring(1).toLowerCase() + " " + name + " Cage Lamp")
      .simpleItem();
  }

  public static void registerBlocks (Registrate reg) {
    reg.creativeModeTab(()->DecoCreativeModeTab.PROPS_GROUP);

    COIN_TYPES.forEach(metal -> {
      ResourceLocation side   = new ResourceLocation(CreateDecoMod.MODID, "block/" + metal.toLowerCase(Locale.ROOT) + "_coinstack_side");
      ResourceLocation top    = new ResourceLocation(CreateDecoMod.MODID, "block/" + metal.toLowerCase(Locale.ROOT) + "_coinstack_top");
      ResourceLocation bottom = new ResourceLocation(CreateDecoMod.MODID, "block/" + metal.toLowerCase(Locale.ROOT) + "_coinstack_bottom");
      COIN_BLOCKS.put(metal, buildCoinStackBlock(reg, ()->COINSTACK_ITEM.get(metal).get(), metal, side, top, bottom).register());
    });

    for (DyeColor color : DyeColor.values()) {
      DECAL_BLOCKS.put(color, reg.block(color.name().toLowerCase(Locale.ROOT) + "_decal", DecalBlock::new)
        .initialProperties(Material.METAL)
        .properties(props-> props.noOcclusion().strength(0.5f).sound(SoundType.LANTERN))
        .blockstate((ctx,prov)-> prov.getVariantBuilder(ctx.get()).forAllStates(state-> {
          int y = 0;
          switch (state.getValue(BlockStateProperties.HORIZONTAL_FACING)) {
            case NORTH: y =   0; break;
            case SOUTH: y = 180; break;
            case WEST:  y = -90; break;
            case EAST:  y =  90; break;
          }
          return ConfiguredModel.builder().modelFile(prov.models()
            .withExistingParent(ctx.getName(), prov.modLoc("block/decal"))
            .texture("face", prov.modLoc("block/palettes/decal/" + ctx.getName()))
            .texture("particle", prov.modLoc("block/palettes/decal/" + ctx.getName()))
          ).rotationY(y).build(); }))
        .addLayer(()-> RenderType::cutoutMipped)
        .lang(color.name().charAt(0) + color.name().substring(1).toLowerCase() + " Decal")
        .item()
        .model((ctx,prov)-> prov.singleTexture(ctx.getName(),
          prov.mcLoc("item/generated"),
          "layer0", prov.modLoc("block/palettes/decal/" + ctx.getName())
        ))
        .build()
        .recipe((ctx, prov)-> ShapelessRecipeBuilder.shapeless(ctx.get())
          .requires(AllItems.IRON_SHEET.get(), 1)
          .requires(DyeItem.byColor(color), 1)
          .unlockedBy("has_item", InventoryChangeTrigger.TriggerInstance.hasItems(AllItems.IRON_SHEET.get()))
          .unlockedBy("has_dye",  InventoryChangeTrigger.TriggerInstance.hasItems(DyeItem.byColor(color)))
          .save(prov)
        )
        .register());
    }

    Registration.METAL_TYPES.forEach((metal, getter) -> {
      ResourceLocation cage      = new ResourceLocation(CreateDecoMod.MODID, "block/palettes/cage_lamp/" + metal.toLowerCase(Locale.ROOT) + "_lamp");

      YELLOW_CAGE_LAMPS.put(metal, buildCageLamp(reg, metal, DyeColor.YELLOW, cage, YELLOW_ON, YELLOW_OFF)
        .register());
      RED_CAGE_LAMPS.put(metal, buildCageLamp(reg, metal, DyeColor.RED, cage, RED_ON, RED_OFF)
        .register());
      GREEN_CAGE_LAMPS.put(metal, buildCageLamp(reg, metal, DyeColor.GREEN, cage, GREEN_ON, GREEN_OFF)
        .register());
      BLUE_CAGE_LAMPS.put(metal, buildCageLamp(reg, metal, DyeColor.BLUE, cage, BLUE_ON, BLUE_OFF)
        .register());
    });
  }

  public static void registerItems (Registrate reg) {
    reg.creativeModeTab(()->DecoCreativeModeTab.PROPS_GROUP, DecoCreativeModeTab.PROPS_NAME);
    for (String metal : COIN_TYPES) {
      COIN_ITEM.put(metal, buildCoinItem(reg, ()->COINSTACK_ITEM.get(metal).get(), metal).register());
      COINSTACK_ITEM.put(metal, buildCoinStackItem(reg, ()->COIN_ITEM.get(metal).get(), metal).register());
    }
  }
}
