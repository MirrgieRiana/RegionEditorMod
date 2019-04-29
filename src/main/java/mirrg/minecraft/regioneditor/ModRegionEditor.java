package mirrg.minecraft.regioneditor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import org.apache.logging.log4j.Logger;

import mirrg.boron.swing.UtilsSwing;
import mirrg.boron.util.i18n.LocalizerEngine;
import mirrg.boron.util.i18n.localizers.LocalizerResourceBundle;
import mirrg.boron.util.struct.ImmutableArray;
import mirrg.minecraft.regioneditor.gui.guis.GuiRegionEditor;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

@Mod(modid = ModRegionEditor.MODID, name = ModRegionEditor.NAME, version = ModRegionEditor.VERSION)
public class ModRegionEditor
{

	public static final String MODID = "mirrg.minecraft.regioneditor";
	public static final String NAME = "RegionEditor";
	public static final String VERSION = "0.0.1";

	private static Logger logger;

	private String lookAndFeel;

	private Locale locale;
	private LocalizerEngine localizerEngine = new LocalizerEngine();

	@EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		logger = event.getModLog();

		{
			Configuration configuration = new Configuration(event.getSuggestedConfigurationFile());
			lookAndFeel = configuration.getString("lookAndFeel", "general", "system", "If 'system', use system default. If 'default', do nothing.");
			configuration.save();
		}

		if (lookAndFeel.equals("default")) {

		} else if (lookAndFeel.equals("system")) {
			try {
				UtilsSwing.setSystemLookAndFeel();
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (lookAndFeel.equals("system")) {
			try {
				UtilsSwing.setSystemLookAndFeel();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		try {
			MainRegionEditor.i18n.registerLocalizer(LocalizerResourceBundle.create(MainRegionEditor.I18N_BASENAME, Locale.ENGLISH));
		} catch (IOException e) {
			logger.warn(e);
		}
		try {
			MainRegionEditor.i18n.registerLocalizer(LocalizerResourceBundle.create(MainRegionEditor.I18N_BASENAME, Locale.getDefault()));
		} catch (IOException e) {
			logger.warn("Could not load the language file: " + Locale.getDefault().toLanguageTag());
		}

		MainRegionEditor.i18n.registerLocalizerEngine(localizerEngine);

	}

	@EventHandler
	public void init(FMLInitializationEvent event)
	{
		if (event.getSide() == Side.CLIENT) {
			MinecraftForge.EVENT_BUS.register(new Object() {
				private final Object lock = new Object();
				private List<String> chatMessage = new ArrayList<>();
				private boolean doCapture = false;

				@SubscribeEvent
				public void handle(ClientChatReceivedEvent event)
				{
					synchronized (lock) {
						if (doCapture) chatMessage.add(event.getMessage().getUnformattedText());
					}
				}

				@SubscribeEvent
				public void handle(PlayerInteractEvent.RightClickItem event)
				{
					if (event.getWorld().isRemote) {
						ItemStack itemStack = event.getItemStack();
						if (itemStack.getItem() == Items.STICK) {
							if (itemStack.getDisplayName().equals("RegionEditor.show")) {

								{
									Locale localeNew = Minecraft.getMinecraft().getLanguageManager().getCurrentLanguage().getJavaLocale();
									if (locale == null || !locale.equals(localeNew)) {
										locale = localeNew;
										try {
											localizerEngine.setLocalizer(LocalizerResourceBundle.create(MainRegionEditor.I18N_BASENAME, localeNew));
										} catch (IOException e) {
											logger.warn("Could not load the language file: " + localeNew.toLanguageTag());
											localizerEngine.setLocalizer(null);
										}
									}
								}

								try {
									new GuiRegionEditor(null, MainRegionEditor.i18n, Optional.of(ss -> {

										for (String s : ss) {
											send(s);
										}

									}), Optional.of(new IChatMessageProvider() {
										@Override
										public void startCapture(String command)
										{
											synchronized (lock) {
												doCapture = true;
											}
											send(command);
										}

										@Override
										public ImmutableArray<String> stopCapture()
										{
											ImmutableArray<String> array;
											synchronized (lock) {
												doCapture = false;
												array = ImmutableArray.ofList(chatMessage);
												chatMessage.clear();
											}
											return array;
										}
									})).show();
								} catch (IOException e) {
									logger.error(e);
								}

							}
						}
					}
				}
			});
		}
	}

	private static void send(String s)
	{
		Minecraft.getMinecraft().ingameGUI.getChatGUI().addToSentMessages(s);
		if (ClientCommandHandler.instance.executeCommand(Minecraft.getMinecraft().player, s) != 0) {

		} else {
			Minecraft.getMinecraft().player.sendChatMessage(s);
		}
	}

}
