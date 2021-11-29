package info.tritusk.adventure.platform.forge.impl;

import info.tritusk.adventure.platform.forge.ForgeServerAudiences;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.KeybindComponent;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.flattener.ComponentFlattener;
import net.kyori.adventure.translation.GlobalTranslator;
import net.kyori.adventure.translation.TranslationRegistry;
import net.kyori.adventure.translation.Translator;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.resources.Language;
import net.minecraft.client.resources.LanguageManager;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.server.FMLServerAboutToStartEvent;
import net.minecraftforge.fml.event.server.FMLServerStoppingEvent;
import net.minecraftforge.fml.loading.FMLLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

@Mod("adventure-platform-forge")
@Mod.EventBusSubscriber(modid = "adventure-platform-forge")
public class ForgePlatform {

    public static final ComponentFlattener FLATTENER;
    private static final Pattern LOCALIZATION_PATTERN = Pattern.compile("%(?:(\\d+)\\$)?s");
    public static ForgeServerAudiences serverAudienceProvider;

    public static final Logger LOGGER = LogManager.getLogger();

    static {
        final ComponentFlattener.Builder flattenerBuilder = ComponentFlattener.basic().toBuilder();

        if (FMLLoader.getDist() == Dist.CLIENT) {
            flattenerBuilder.mapper(KeybindComponent.class, keybind -> KeyBinding.createNameSupplier(keybind.keybind()).get().getContents());
        }

        flattenerBuilder.complexMapper(TranslatableComponent.class, (translatable, consumer) -> {
            final String key = translatable.key();
            for (final Translator registry : GlobalTranslator.get().sources()) {
                if (registry instanceof TranslationRegistry && ((TranslationRegistry) registry).contains(key)) {
                    consumer.accept(GlobalTranslator.render(translatable, Locale.getDefault()));
                    return;
                }
            }
            final @NotNull String translated = I18n.get(key);
            final Matcher matcher = LOCALIZATION_PATTERN.matcher(translated);
            final List<Component> args = translatable.args();
            int argPosition = 0;
            int lastIdx = 0;
            while (matcher.find()) {
                // append prior
                if (lastIdx < matcher.start()) consumer.accept(Component.text(translated.substring(lastIdx, matcher.start())));
                lastIdx = matcher.end();

                final @Nullable String argIdx = matcher.group(1);
                // calculate argument position
                if (argIdx != null) {
                    try {
                        final int idx = Integer.parseInt(argIdx) - 1;
                        if (idx < args.size()) {
                            consumer.accept(args.get(idx));
                        }
                    } catch (final NumberFormatException ex) {
                        // ignore, drop the format placeholder
                    }
                } else {
                    final int idx = argPosition++;
                    if (idx < args.size()) {
                        consumer.accept(args.get(idx));
                    }
                }
            }

            // append tail
            if (lastIdx < translated.length()) {
                consumer.accept(Component.text(translated.substring(lastIdx)));
            }
        });
        FLATTENER = flattenerBuilder.build();
    }

    @SubscribeEvent
    public static void onServerStart(FMLServerAboutToStartEvent event) {
        serverAudienceProvider = new ForgeServerAudiences(event.getServer());
    }

    @SubscribeEvent
    public static void onServerSTop(FMLServerStoppingEvent event) {
        serverAudienceProvider.close();
        serverAudienceProvider = null;
    }
}
