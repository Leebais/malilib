package fi.dy.masa.malilib.gui;

import fi.dy.masa.malilib.config.value.OptionListConfigValue;
import fi.dy.masa.malilib.gui.position.ScreenLocation;
import fi.dy.masa.malilib.gui.util.GuiUtils;
import fi.dy.masa.malilib.gui.widget.BaseTextFieldWidget;
import fi.dy.masa.malilib.gui.widget.ColorIndicatorWidget;
import fi.dy.masa.malilib.gui.widget.DoubleEditWidget;
import fi.dy.masa.malilib.gui.widget.DropDownListWidget;
import fi.dy.masa.malilib.gui.widget.IntegerEditWidget;
import fi.dy.masa.malilib.gui.widget.LabelWidget;
import fi.dy.masa.malilib.gui.widget.button.GenericButton;
import fi.dy.masa.malilib.gui.widget.button.OnOffButton;
import fi.dy.masa.malilib.overlay.InfoOverlay;
import fi.dy.masa.malilib.overlay.InfoWidgetManager;
import fi.dy.masa.malilib.overlay.widget.StringListRendererWidget;
import fi.dy.masa.malilib.render.text.TextRenderSettings;
import fi.dy.masa.malilib.util.StringUtils;

public class StringListRendererWidgetEditScreen extends BaseScreen
{
    protected final StringListRendererWidget widget;
    protected final DropDownListWidget<ScreenLocation> locationDropdownWidget;
    protected final LabelWidget nameLabelWidget;
    protected final LabelWidget textColorLabelWidget;
    protected final LabelWidget backgroundLabelWidget;
    protected final LabelWidget oddBackgroundLabelWidget;
    protected final LabelWidget evenWidthBackgroundLabelWidget;
    protected final LabelWidget priorityLabelWidget;
    protected final LabelWidget lineHeightLabelWidget;
    protected final LabelWidget textScaleLabelWidget;
    protected final LabelWidget textShadowLabelWidget;
    protected final GenericButton enabledToggleButton;
    protected final GenericButton backgroundEnabledToggleButton;
    protected final GenericButton oddEvenBackgroundToggleButton;
    protected final GenericButton evenWidthBackgroundToggleButton;
    protected final GenericButton renderNameToggleButton;
    protected final GenericButton marginEditButton;
    protected final GenericButton paddingEditButton;
    protected final GenericButton textShadowToggleButton;
    protected final ColorIndicatorWidget textColorWidget;
    protected final ColorIndicatorWidget backgroundColorWidget;
    protected final ColorIndicatorWidget oddBackgroundColorWidget;
    protected final IntegerEditWidget priorityEditWidget;
    protected final IntegerEditWidget lineHeightEditWidget;
    protected final DoubleEditWidget textScaleEditWidget;
    protected final BaseTextFieldWidget nameTextField;

    public StringListRendererWidgetEditScreen(StringListRendererWidget widget)
    {
        this.widget = widget;
        this.useTitleHierarchy = false;
        this.title = StringUtils.translate("malilib.gui.title.string_list_renderer_configuration");

        this.locationDropdownWidget = new DropDownListWidget<>(0, 0, -1, 16, 160, 10, ScreenLocation.VALUES,
                                                               OptionListConfigValue::getDisplayName, null);
        this.locationDropdownWidget.setSelectedEntry(widget.getScreenLocation());
        this.locationDropdownWidget.setSelectionListener(this::changeWidgetLocation);

        this.nameLabelWidget = new LabelWidget(0, 0, 0xFFFFFFFF, "malilib.label.name.colon");
        this.priorityLabelWidget = new LabelWidget(0, 0, 0xFFFFFFFF, "malilib.label.sort_index.colon");
        this.lineHeightLabelWidget = new LabelWidget(0, 0, 0xFFFFFFFF, "malilib.label.line_height.colon");
        this.textColorLabelWidget = new LabelWidget(0, 0, 0xFFFFFFFF, "malilib.label.text_color.colon");
        this.textScaleLabelWidget = new LabelWidget(0, 0, 0xFFFFFFFF, "malilib.label.text_scale.colon");
        this.textShadowLabelWidget = new LabelWidget(0, 0, 0xFFFFFFFF, "malilib.label.text_shadow.colon");
        this.backgroundLabelWidget = new LabelWidget(0, 0, 0xFFFFFFFF, "malilib.label.background.colon");
        this.evenWidthBackgroundLabelWidget = new LabelWidget(0, 0, 0xFFFFFFFF, "malilib.label.string_list_renderer.even_width_background.colon");
        this.oddBackgroundLabelWidget = new LabelWidget(0, 0, 0xFFFFFFFF, "malilib.label.config_status_indicator.background_odd.colon");
        this.oddBackgroundLabelWidget.addHoverStrings(StringUtils.translate("malilib.label.config_status_indicator.background_odd.hover"));

        this.nameTextField = new BaseTextFieldWidget(0, 0, 160, 16, widget.getName());
        this.nameTextField.setListener(widget::setName);

        this.priorityEditWidget = new IntegerEditWidget(0, 0, 72, 16, widget.getSortIndex(), -1000, 1000, widget::setSortIndex);
        this.lineHeightEditWidget = new IntegerEditWidget(0, 0, 72, 16, widget.getLineHeight(), 6, 40, widget::setLineHeight);
        this.textScaleEditWidget = new DoubleEditWidget(0, 0, 72, 16, widget.getTextScale(), 0.25, 20, widget::setTextScale);

        this.marginEditButton = GenericButton.simple(-1, 16, "malilib.label.margin", this::openMarginEditScreen);
        this.marginEditButton.setHoverStringProvider("tooltip", this.widget.getMargin()::getHoverTooltip);

        this.paddingEditButton = GenericButton.simple(-1, 16, "malilib.label.padding", this::openPaddingEditScreen);
        this.paddingEditButton.setHoverStringProvider("tooltip", this.widget.getPadding()::getHoverTooltip);

        this.enabledToggleButton = OnOffButton.simpleSlider(16, widget::isEnabled, widget::toggleEnabled);

        final TextRenderSettings textSettings = widget.getTextSettings();

        this.backgroundEnabledToggleButton = OnOffButton.simpleSlider(16, textSettings::getUseBackground, textSettings::toggleUseBackground);
        this.oddEvenBackgroundToggleButton = OnOffButton.simpleSlider(16, textSettings::getUseOddEvenBackground, textSettings::toggleUseOddEvenBackground);
        this.oddEvenBackgroundToggleButton.addHoverStrings(StringUtils.translate("malilib.label.config_status_indicator.background_odd.hover"));

        this.evenWidthBackgroundToggleButton = OnOffButton.simpleSlider(16, textSettings::getUseEvenWidthBackground, textSettings::toggleUseEvenWidthBackground);
        this.textShadowToggleButton = OnOffButton.simpleSlider(16, textSettings::getUseTextShadow, textSettings::toggleUseTextShadow);
        this.renderNameToggleButton = OnOffButton.simpleSlider(16, widget::getRenderName, widget::toggleRenderName);

        this.textColorWidget = new ColorIndicatorWidget(0, 0, 16, 16, textSettings::getTextColor, textSettings::setTextColor);
        this.backgroundColorWidget = new ColorIndicatorWidget(0, 0, 16, 16, textSettings::getBackgroundColor, textSettings::setBackgroundColor);
        this.oddBackgroundColorWidget = new ColorIndicatorWidget(0, 0, 16, 16, textSettings::getOddRowBackgroundColor, textSettings::setOddRowBackgroundColor);
    }

    @Override
    protected void initScreen()
    {
        super.initScreen();

        int x = this.x + 10;
        int y = this.y + 24;
        int tmpX;

        this.marginEditButton.updateHoverStrings();
        this.paddingEditButton.updateHoverStrings();

        this.locationDropdownWidget.setPosition(x, y);
        this.enabledToggleButton.setPosition(this.locationDropdownWidget.getRight() + 6, y);

        this.marginEditButton.setPosition(this.enabledToggleButton.getRight() + 6, y);
        this.paddingEditButton.setPosition(this.marginEditButton.getRight() + 6, y);

        y += 20;
        this.nameLabelWidget.setPosition(x, y + 4);
        this.nameTextField.setPosition(this.nameLabelWidget.getRight() + 6, y);
        this.renderNameToggleButton.setPosition(this.nameTextField.getRight() + 6, y);

        y += 20;
        this.textScaleLabelWidget.setPosition(x, y + 4);
        this.lineHeightLabelWidget.setPosition(x, y + 24);
        this.priorityLabelWidget.setPosition(x, y + 44);
        this.textShadowLabelWidget.setPosition(x, y + 64);

        tmpX = Math.max(this.textScaleLabelWidget.getRight(), this.lineHeightLabelWidget.getRight());
        tmpX = Math.max(tmpX, this.priorityLabelWidget.getRight()) + 6;
        this.textScaleEditWidget.setPosition(tmpX, y);
        this.lineHeightEditWidget.setPosition(tmpX, y + 20);
        this.priorityEditWidget.setPosition(tmpX, y + 40);

        tmpX = this.textShadowLabelWidget.getRight() + 6;
        this.textShadowToggleButton.setPosition(tmpX, y + 60);

        tmpX = this.priorityEditWidget.getRight() + 6;
        this.textColorLabelWidget.setPosition(tmpX, y + 4);
        this.backgroundLabelWidget.setPosition(tmpX, y + 24);
        this.oddBackgroundLabelWidget.setPosition(tmpX, y + 44);
        this.evenWidthBackgroundLabelWidget.setPosition(tmpX, y + 64);

        int tmpX1 = Math.max(this.textColorLabelWidget.getRight(), this.backgroundLabelWidget.getRight());
        int tmpX2 = Math.max(this.oddBackgroundLabelWidget.getRight(), this.textShadowLabelWidget.getRight());
        tmpX = Math.max(tmpX1, tmpX2);
        tmpX = Math.max(tmpX, this.evenWidthBackgroundLabelWidget.getRight()) + 6;
        this.textColorWidget.setPosition(tmpX, y);
        this.backgroundColorWidget.setPosition(tmpX, y + 20);
        this.oddBackgroundColorWidget.setPosition(tmpX, y + 40);

        tmpX += 22;
        this.backgroundEnabledToggleButton.setPosition(tmpX, y + 20);
        this.oddEvenBackgroundToggleButton.setPosition(tmpX, y + 40);

        tmpX = Math.max(tmpX, this.evenWidthBackgroundLabelWidget.getRight() + 6);
        this.evenWidthBackgroundToggleButton.setPosition(tmpX, y + 60);

        this.addWidget(this.locationDropdownWidget);
        this.addWidget(this.enabledToggleButton);
        this.addWidget(this.marginEditButton);
        this.addWidget(this.paddingEditButton);

        this.addWidget(this.nameLabelWidget);
        this.addWidget(this.nameTextField);
        this.addWidget(this.renderNameToggleButton);

        this.addWidget(this.textScaleLabelWidget);
        this.addWidget(this.textScaleEditWidget);

        this.addWidget(this.lineHeightLabelWidget);
        this.addWidget(this.lineHeightEditWidget);

        this.addWidget(this.priorityLabelWidget);
        this.addWidget(this.priorityEditWidget);

        this.addWidget(this.textShadowLabelWidget);
        this.addWidget(this.textShadowToggleButton);

        this.addWidget(this.textColorLabelWidget);
        this.addWidget(this.textColorWidget);

        this.addWidget(this.backgroundLabelWidget);
        this.addWidget(this.backgroundColorWidget);
        this.addWidget(this.backgroundEnabledToggleButton);

        this.addWidget(this.oddBackgroundLabelWidget);
        this.addWidget(this.oddBackgroundColorWidget);
        this.addWidget(this.oddEvenBackgroundToggleButton);

        this.addWidget(this.evenWidthBackgroundLabelWidget);
        this.addWidget(this.evenWidthBackgroundToggleButton);
    }

    @Override
    public void onGuiClosed()
    {
        super.onGuiClosed();

        InfoWidgetManager.INSTANCE.saveToFile();
    }

    protected void changeWidgetLocation(ScreenLocation location)
    {
        InfoOverlay.INSTANCE.getOrCreateInfoArea(this.widget.getScreenLocation()).removeWidget(this.widget);
        // This also sets the location in the widget
        InfoOverlay.INSTANCE.getOrCreateInfoArea(location).addWidget(this.widget);
    }

    protected void openMarginEditScreen()
    {
        EdgeIntEditScreen screen = new EdgeIntEditScreen(this.widget.getMargin(), false,
                                                         "malilib.gui.title.edit_margin", "malilib.label.margin");
        screen.setParent(GuiUtils.getCurrentScreen());
        BaseScreen.openScreen(screen);
    }

    protected void openPaddingEditScreen()
    {
        EdgeIntEditScreen screen = new EdgeIntEditScreen(this.widget.getPadding(), false,
                                                         "malilib.gui.title.edit_padding", "malilib.label.padding");
        screen.setParent(GuiUtils.getCurrentScreen());
        BaseScreen.openScreen(screen);
    }
}
