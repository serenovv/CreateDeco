package com.github.talrey.createdeco.fabric;

import com.github.talrey.createdeco.BlockRegistry;
import com.github.talrey.createdeco.CreateDecoMod;
import com.github.talrey.createdeco.CreativeTabs;
import com.simibubi.create.AllCreativeModeTabs;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;

import java.util.function.Supplier;

public class CreativeTabsImpl {
  private static final AllCreativeModeTabs.TabInfo PROPS = register(CreativeTabs.PROPS_KEY,
    ()-> FabricItemGroup.builder()
      .title(Component.translatableWithFallback("props_tab", "Create Deco Props"))
      .icon(()->BlockRegistry.GREEN_CAGE_LAMPS.get("Brass").asStack())
      .build());

  public static void register () {}

  public static CreativeModeTab props () {
    return PROPS.tab();
  }

  private static AllCreativeModeTabs.TabInfo register (String name, Supplier<CreativeModeTab> s) {
    ResourceLocation id = CreateDecoMod.id(name);
    ResourceKey<CreativeModeTab> key = ResourceKey.create(Registries.CREATIVE_MODE_TAB, id);
    CreativeModeTab tab = s.get();
    Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, key, tab);
    return new AllCreativeModeTabs.TabInfo(key, tab);
  }
}
