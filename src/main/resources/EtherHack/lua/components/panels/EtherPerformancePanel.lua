require "ISUI/ISPanel"

--*********************************************************
--* Глобальные установки UI
--*********************************************************
EtherPerformancePanel = ISPanel:derive("EtherPerformancePanel"); -- Наследование от ISPanel

--*********************************************************
--* Создание метки
--*********************************************************
function EtherPerformancePanel:addLabel(posX, posY, title)
    local label = ISLabel:new(posX, posY + 3, getTextManager():getFontHeight(UIFont.Small), title, 1, 1, 1, 1, UIFont.Small, true)
	self:addChild(label)
    return label
end

--*********************************************************
--* Создание кнопки
--*********************************************************
function EtherPerformancePanel:addButton(posX, posY, buttonTitle, onClick)
    local buttonWidth, buttonHeight = 130, 16;
    local button = UIButton:new(posX, posY, buttonWidth, buttonHeight, buttonTitle, onClick)
    button:initialise();
    button:instantiate();
    button:setAnchorLeft(true);
    button:setAnchorRight(false);
    button:setAnchorTop(false);
    button:setAnchorBottom(true);
    self:addChild(button);
    table.insert(self.buttonList, button);
    return button
end

--*********************************************************
--* Создание слайдера
--*********************************************************
function EtherPerformancePanel:addSlider(posX, posY, width, height, value, minValue, maxValue, method)
    local slider = UISlider:new(posX, posY, width, height, value, minValue, maxValue, method)
    slider:initialise();
    slider:instantiate();
    self:addChild(slider);
    return slider
end

--*********************************************************
--* Добавление чекбоксов
--*********************************************************
function EtherPerformancePanel:addCheckBox(title, method, isSelected, valueGetter)
    local checkboxX = 15;
    local checkboxY = 5 + self.rows * 30;

    local checkbox = UICheckbox:new(checkboxX, checkboxY, title, isSelected, method);
    checkbox:initialise();
    checkbox:instantiate();
    checkbox:setAnchorLeft(true);
    checkbox:setAnchorRight(false);
    checkbox:setAnchorTop(false);
    checkbox:setAnchorBottom(true);
    checkbox.valueGetter = valueGetter;
    self:addChild(checkbox);

    self:setScrollHeight(self:getScrollHeight() + 30);

    self.rows = self.rows + 1;

    table.insert(self.checkBoxList, checkbox);
    return checkbox
end

--*********************************************************
--* Создание заголовка со слайдером
--*********************************************************
function EtherPerformancePanel:addSliderWithLabel(title, value, minValue, maxValue, method, valueGetter)
    local buttonY = 5 + self.rows * 30;

    self:addLabel(15, buttonY - 3, title)
    local slider = self:addSlider(self:getWidth() - 200 - 50, buttonY + 3, 200, 10, value, minValue, maxValue, method)
    slider.valueGetter = valueGetter;

    self:setScrollHeight(self:getScrollHeight() + 30);

    self.rows = self.rows + 1;

    table.insert(self.sliderList, slider);
    return slider
end

--*********************************************************
--* Создание заголовка с кнопкой
--*********************************************************
function EtherPerformancePanel:addButtonWithLabel(title, buttonTitle, func)
    local buttonY = 5 + self.rows * 30;

    self:addLabel(15, buttonY - 3, title)
    self:addButton(self:getWidth() - 130 - 20, buttonY, buttonTitle, func)

    self:setScrollHeight(self:getScrollHeight() + 30);

    self.rows = self.rows + 1;
end

--*********************************************************
--* Обновление значений элементов по текущим настройкам игры
--*********************************************************
function EtherPerformancePanel:refreshSettings()
    for i=1, #self.checkBoxList do
        local item = self.checkBoxList[i];
        if item.valueGetter ~= nil then
            item:setCheked(item.valueGetter());
        end
    end

    for i=1, #self.sliderList do
        local item = self.sliderList[i];
        if item.valueGetter ~= nil then
            item.currentValue = item.valueGetter();
        end
    end
end

--*********************************************************
--* Создание дочерних элементов
--*********************************************************
function EtherPerformancePanel:createChildren()
    ISPanel.createChildren(self);

    self:setScrollChildren(true);
    self:setScrollHeight(0);
    self:addScrollBars();

    self:addCheckBox(getTranslate("UI_Performance_UncappedFPS"), function(isChecked) togglePerfUncappedFPS(isChecked) end, isPerfUncappedFPS(), function() return isPerfUncappedFPS(); end);

    self:addSliderWithLabel(getTranslate("UI_Performance_LockFPS"), getPerfLockFPS(), 30, 240, function(value)
        setPerfLockFPS(value);
    end, function() return getPerfLockFPS(); end);

    self:addSliderWithLabel(getTranslate("UI_Performance_LightingQuality"), getPerfLightingQuality(), 0, 3, function(value)
        setPerfLightingQuality(value);
    end, function() return getPerfLightingQuality(); end);

    self:addSliderWithLabel(getTranslate("UI_Performance_LightingFPS"), getPerfLightingFPS(), 5, 60, function(value)
        setPerfLightingFPS(value);
    end, function() return getPerfLightingFPS(); end);

    self:addSliderWithLabel(getTranslate("UI_Performance_WaterQuality"), getPerfWaterQuality(), 0, 2, function(value)
        setPerfWaterQuality(value);
    end, function() return getPerfWaterQuality(); end);

    self:addSliderWithLabel(getTranslate("UI_Performance_PuddlesQuality"), getPerfPuddlesQuality(), 0, 2, function(value)
        setPerfPuddlesQuality(value);
    end, function() return getPerfPuddlesQuality(); end);

    self:addSliderWithLabel(getTranslate("UI_Performance_FogQuality"), getPerfFogQuality(), 0, 2, function(value)
        setPerfFogQuality(value);
    end, function() return getPerfFogQuality(); end);

    self:addCheckBox(getTranslate("UI_Performance_Reflections"), function(isChecked) togglePerfReflectionsEnabled(isChecked) end, isPerfReflectionsEnabled(), function() return isPerfReflectionsEnabled(); end);
    self:addCheckBox(getTranslate("UI_Performance_StaticSkybox"), function(isChecked) togglePerfStaticSkybox(isChecked) end, isPerfStaticSkybox(), function() return isPerfStaticSkybox(); end);
    self:addCheckBox(getTranslate("UI_Performance_AnimationInterpolation"), function(isChecked) togglePerfAnimationInterpolation(isChecked) end, isPerfAnimationInterpolation(), function() return isPerfAnimationInterpolation(); end);

    self:addButtonWithLabel(getTranslate("UI_Performance_ResetTitle"), getTranslate("UI_Performance_ResetButton"), function()
        resetPerformanceSettings();
        self:refreshSettings();
    end);
end

--*********************************************************
--* Обработка событий колесика мыши
--*********************************************************
function EtherPerformancePanel:onMouseWheel(del)
	self:setYScroll(self:getYScroll() - (del * 40));
	return true;
end

--*********************************************************
--* Обработка prerender
--*********************************************************
function EtherPerformancePanel:prerender()
    self:setStencilRect(0,10,self:getWidth(),self:getHeight() - 20);
    ISPanel.prerender(self);
end

--*********************************************************
--* Обработка render
--*********************************************************
function EtherPerformancePanel:render()
    ISPanel.render(self);
    self:clearStencilRect();
end

--*********************************************************
--* Создание нового экземпляра меню
--*********************************************************
function EtherPerformancePanel:new(posX, posY, width, height)
    local menuTableData = {};

    menuTableData = ISPanel:new(posX, posY, width, height);
    setmetatable(menuTableData, self);
    menuTableData.background = true;
	menuTableData.backgroundColor = {r=0.0, g=0.0, b=0.0, a=0.0};
	menuTableData.borderColor = {r=0.0, g=0.0, b=0.0, a=0.0};
    menuTableData.moveWithMouse = true;
    self.__index = self;

    self.checkBoxList = {}; -- Список всех чекбоксов
    self.sliderList = {}; -- Список всех слайдеров
    self.buttonList = {}; -- Список всех кнопок
    self.rows = 0;

    return menuTableData;
end
