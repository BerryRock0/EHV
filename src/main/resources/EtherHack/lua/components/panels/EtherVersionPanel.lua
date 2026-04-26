require "ISUI/ISPanel"

--*********************************************************
--* Глобальные установки UI
--*********************************************************
EtherVersionPanel = ISPanel:derive("EtherVersionPanel"); -- Наследование от ISPanel

--*********************************************************
--* Создание метки
--*********************************************************
function EtherSettingsPanel:addLabel(posX, posY, title)
    local label = ISLabel:new(posX, posY + 3, getTextManager():getFontHeight(UIFont.Small), title, 1, 1, 1, 1, UIFont.Small, true)
	self:addChild(label)
    return label
end

--*********************************************************
--* Создание кнопки
--*********************************************************
function EtherSettingsPanel:addButton(posX, posY, buttonTitle, onClick, isOnlyNotInGame)
    local buttonWidth, buttonHeight = 130, 16;
    local button = UIButton:new(posX, posY, buttonWidth, buttonHeight, buttonTitle, onClick)
    button:initialise();
    button:instantiate();
    button:setAnchorLeft(true);
    button:setAnchorRight(false);
    button:setAnchorTop(false);
    button:setAnchorBottom(true);
    button.isOnlyNotInGame = isOnlyNotInGame;
    self:addChild(button);
    table.insert(self.buttonList, button);
    return button
end

--*********************************************************
--* Создание слайдера
--*********************************************************
function EtherSettingsPanel:addSlider(posX, posY, width, height, value, minValue, maxValue, method)
    local slider = UISlider:new(posX, posY, width, height, value, minValue, maxValue, method)
    slider:initialise();
    slider:instantiate();
    self:addChild(slider);
    return slider
end

--*********************************************************
--* Создание кнопки с заголовком
--*********************************************************
function EtherSettingsPanel:addButtonWithLabel(title, buttonTitle, func, isOnlyNotInGame)
    local rows = self.rows;
    local buttonY = 200 + rows * 25;

    self:addLabel(10, buttonY - 3, title)
    self:addButton(self:getWidth() - 130 - 10, buttonY, buttonTitle, func, isOnlyNotInGame)

    self:setScrollHeight(self:getScrollHeight() + 21);
    self.rows = self.rows + 1;
end

--*********************************************************
--* Создание выбора цвета с заголовком
--*********************************************************
function EtherSettingsPanel:addColorPickerWithLabel(title, func, startColor)
    local rows = self.rows;
    local buttonY = 200 + rows * 25;

    self:addLabel(10, buttonY - 3, title)

    local buttonWidth, buttonHeight = 16, 16;
    local button = ISButton:new(self:getWidth() - buttonWidth - 10, buttonY, buttonWidth, buttonHeight, "", self, func)
    button:initialise();
    button.backgroundColor = {r = startColor:getR(), g = startColor:getG(), b = startColor:getB(), a = 1};
	button.backgroundColorMouseOver = {r = startColor:getR(), g = startColor:getG(), b = startColor:getB(), a = 1};

    self:addChild(button);
    table.insert(self.buttonList, button);

    self:setScrollHeight(self:getScrollHeight() + 24);
    self.rows = self.rows + 1;
    return button
end

--*********************************************************
--* Создание заголовка с двумя кнопками
--*********************************************************
function EtherSettingsPanel:addSliderWithLabel(title, sliderMethod, value, minValue, maxValue)
    local rows = self.rows;
    local buttonY = 10 + rows * 25;

    self:addLabel(15, buttonY - 3, title)
    self:addSlider(self:getWidth() - 200 - 50, buttonY + 3, 200, 10, value, minValue, maxValue, sliderMethod)

    self:setScrollHeight(self:getScrollHeight() + 30);

    self.rows = self.rows + 1;
end


--*********************************************************
--* Добавление чекбоксов
--*********************************************************
function EtherSettingsPanel:addCheckBox(title, method, isSelected, isOnlyInGame)
    local rows = self.rows;
    local checkboxX = 15;
    local checkboxY = 10 + rows * 20;

    local checkbox = UICheckbox:new(checkboxX, checkboxY, title, isSelected, method);
    checkbox:initialise();
    checkbox:instantiate();
    checkbox:setAnchorLeft(true);
    checkbox:setAnchorRight(false);
    checkbox:setAnchorTop(false);
    checkbox:setAnchorBottom(true);
    checkbox.isOnlyInGame = isOnlyInGame;
    self:addChild(checkbox);

    self:setScrollHeight(self:getScrollHeight() + checkbox.height + 5);

    self.rows = self.rows + 1;

    table.insert(self.checkBoxList, checkbox);
end

--*********************************************************
--* Обновление панели
--*********************************************************
function EtherSettingsPanel:updatePanel()
    for i=1, #self.checkBoxList do
        local item = self.checkBoxList[i];
        if item.isOnlyInGame and self.localPlayer == nil then
            item:setEnable(false);
        end
    end
    for i=1, #self.buttonList do
        local item = self.buttonList[i];
        if item.isOnlyNotInGame and self.localPlayer ~= nil then
            item:setEnable(false);
        end
    end
end

--*********************************************************
--* Обработка prerender
--*********************************************************
function EtherVersionPanel:prerender()
    self:setStencilRect(0,10,self:getWidth(),self:getHeight() - 20);
    ISPanel.prerender(self);
end

--*********************************************************
--* Обработка render
--*********************************************************
function EtherVersionPanel:render()
    ISPanel.render(self);
    self:clearStencilRect();
end

--*********************************************************
--* Обработка событий колесика мыши
--*********************************************************
function EtherVersionPanel:onMouseWheel(del)
	self:setYScroll(self:getYScroll() - (del * 40));
	return true;
end


function EtherVersionPanel:createChildren()
    ISPanel.createChildren(self);

    self:setScrollChildren(true);
    self:setScrollHeight(0);
    self:addScrollBars();

	self:addLabel(10, 10, getTranslate("UI_Settings_VersionTitle"))
	
    self.entry = ISTextEntryBox:new(10, self.height + 10, self.width / 2 - 60, 24);
    self.entry.font = UIFont.Small;
    self.entry:initialise();
    self.entry:instantiate();
    self:addChild(self.entry);

    local changeButton = UIButton:new(self.entry.x + self.entry.width + 10, self.entry.y, 80, 24, getTranslate("UI_Settings_VersionTitle"), function ()
        local versionString = self.entry:getText();
        if versionString ~= "" then
            changeVersion(versionString);
        end
    end)
    changeButton:initialise();
    changeButton:instantiate();
    changeButton:setAnchorLeft(true);
    changeButton:setAnchorRight(false);
    changeButton:setAnchorTop(false);
    changeButton:setAnchorBottom(true);
    changeButton.update = function ()
        local text = self.entry:getText();
        if text ~= "" then
            changeButton.isEnable = true;
        else
            changeButton.isEnable = false;
        end
    end
    self:addChild(changeButton)

	self:updatePanel();
end
--*********************************************************
--* Создание нового экземпляра меню
--*********************************************************
function EtherVersionPanel:new(posX, posY, width, height)
    local menuTableData = {};

    menuTableData = ISPanel:new(posX, posY, width, height);
    setmetatable(menuTableData, self);
    menuTableData.background = true;
	menuTableData.backgroundColor = {r=0.0, g=0.0, b=0.0, a=0.0};
	menuTableData.borderColor = {r=0.0, g=0.0, b=0.0, a=0.0};
    menuTableData.moveWithMouse = true;
    menuTableData.yRowPosition = 10;
    self.__index = self;

	self.checkBoxList = {}; -- Список всех чекбоксов
    self.buttonList = {}; -- Список всех кнопок
    self.uiElements = {}; -- Список всех элементов

    return menuTableData;
end
