package malilib;

import java.text.SimpleDateFormat;

import malilib.gui.BaseScreen;
import malilib.gui.util.GuiUtils;
import malilib.input.callback.AdjustableValueHotkeyCallback;
import malilib.overlay.message.MessageDispatcher;
import malilib.overlay.message.MessageUtils;
import malilib.overlay.widget.MessageRendererWidget;

public class MaLiLibConfigInit
{
    protected static void init()
    {
        MaLiLibConfigs.Hotkeys.OPEN_ACTION_PROMPT_SCREEN.createCallbackForAction(MaLiLibActions.OPEN_ACTION_PROMPT_SCREEN);
        MaLiLibConfigs.Hotkeys.OPEN_CONFIG_SCREEN.createCallbackForAction(MaLiLibActions.OPEN_CONFIG_SCREEN);
        MaLiLibConfigs.Hotkeys.SCROLL_VALUE_ADJUST_DECREASE.createCallbackForAction(AdjustableValueHotkeyCallback::scrollAdjustDecrease);
        MaLiLibConfigs.Hotkeys.SCROLL_VALUE_ADJUST_INCREASE.createCallbackForAction(AdjustableValueHotkeyCallback::scrollAdjustIncrease);

        MaLiLibConfigs.Generic.CONFIG_WIDGET_BACKGROUND.addValueChangeListener(GuiUtils::reInitCurrentScreen);
        MaLiLibConfigs.Generic.CUSTOM_SCREEN_SCALE.addValueChangeListener(BaseScreen::applyCustomScreenScaleChange);
        MaLiLibConfigs.Generic.FILE_BROWSER_DATE_FORMAT.addValueChangeListener(MaLiLibConfigInit::checkFileBrowserDateFormat);
        MaLiLibConfigs.Generic.OPTION_LIST_CONFIG_USE_DROPDOWN.addValueChangeListener(GuiUtils::reInitCurrentScreen);
        /*
        MaLiLibConfigs.Generic.SERVER_MESSAGES.setValueChangeCallback((n, o) -> MessagePacketHandler.updateRegistration(n));
        MaLiLibConfigs.Generic.SERVER_MESSAGES.setValueLoadCallback(MessagePacketHandler::updateRegistration);
        */

        MaLiLibConfigs.Generic.CUSTOM_HOTBAR_MESSAGE_LIMIT.setValueChangeCallback((n, o) -> setCustomHotbarMessageLimit(n));
        MaLiLibConfigs.Generic.CUSTOM_HOTBAR_MESSAGE_LIMIT.setValueLoadCallback(MaLiLibConfigInit::setCustomHotbarMessageLimit);
    }

    private static void setCustomHotbarMessageLimit(int limit)
    {
        MessageRendererWidget widget = MessageUtils.findInfoWidget(MessageRendererWidget.class, null, MessageUtils.CUSTOM_ACTION_BAR_MARKER);

        if (widget != null)
        {
            widget.setMaxMessages(limit);
        }
    }

    private static void checkFileBrowserDateFormat()
    {
        try
        {
            SimpleDateFormat fmt = new SimpleDateFormat(MaLiLibConfigs.Generic.FILE_BROWSER_DATE_FORMAT.getValue());
            assert fmt != null;
        }
        catch (Exception ignore)
        {
            MessageDispatcher.error("malilib.message.error.config.file_browser_date_format_invalid");
        }
    }
}
