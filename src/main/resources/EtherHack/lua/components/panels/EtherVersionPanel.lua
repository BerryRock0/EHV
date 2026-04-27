require "ISUI/ISPanel"

--*********************************************************
--* Глобальные установки UI
--*********************************************************
EtherVersionPanel = ISPanel:derive("EtherVersionPanel"); -- Наследование от ISPanel

--*********************************************************
--* Создание метки
--*********************************************************
function EtherVersionPanel:addLabel(posX, posY, title)
    local label = ISLabel:new(posX, posY + 3, getTextManager():getFontHeight(UIFont.Small), title, 1, 1, 1, 1, UIFont.Small, true)
	label:initialise();
	self:addChild(label)
    return label
end

--*********************************************************
--* Создание кнопки
--*********************************************************
function EtherVersionPanel:addButton(posX, posY, buttonTitle, onClick, isOnlyNotInGame)
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
function EtherVersionPanel:addSlider(posX, posY, width, height, value, minValue, maxValue, method)
    local slider = UISlider:new(posX, posY, width, height, value, minValue, maxValue, method)
    slider:initialise();
    slider:instantiate();
    self:addChild(slider);
    return slider
end

--*********************************************************
--* Создание кнопки с заголовком
--*********************************************************
function EtherVersionPanel:addButtonWithLabel(title, buttonTitle, func, isOnlyNotInGame)
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
function EtherVersionPanel:addColorPickerWithLabel(title, func, startColor)
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
function EtherVersionPanel:addSliderWithLabel(title, sliderMethod, value, minValue, maxValue)
    local rows = self.rows;
    local buttonY = 10 + rows * 25;

    self:addLabel(15, buttonY - 3, title)
    self:addSlider((self:getWidth() or 300) - 200 - 50, buttonY + 3, 200, 10, value, minValue, maxValue, sliderMethod)

    self:setScrollHeight(self:getScrollHeight() + 30);

    self.rows = self.rows + 1;
end

--*********************************************************
--* Добавление чекбоксов
--*********************************************************
function EtherVersionPanel:addCheckBox(title, method, isSelected, isOnlyInGame)
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
    
	self.checkBoxList = self.checkBoxList or {}
    table.insert(self.checkBoxList, checkbox);
end

function EtherVersionPanel:createChildren()
    ISPanel.createChildren(self);

    self:setScrollChildren(true);
    self:setScrollHeight(0);
    self:addScrollBars();

	local textBox = ISTextEntryBox:new("", 10, 50, self.width / 2 - 60, 24);
	textBox.font = UIFont.Small;
	textBox:initialise();
    textBox:instantiate();
	textBox:setAnchorLeft(true);
    textBox:setAnchorRight(false);
    textBox:setAnchorTop(false);
    textBox:setAnchorBottom(true);
	self:addChild(textBox)
	
    local changeButton = UIButton:new(20, 100, 80, 24, getTranslate("UI_Settings_ConfigSave"),  function () changeGameVersion(textBox:getText()) end)
    changeButton:initialise();
    changeButton:instantiate();
    changeButton:setAnchorLeft(false);
    changeButton:setAnchorRight(true);
    changeButton:setAnchorTop(true);
    changeButton:setAnchorBottom(false);
    changeButton.update = function () changeButton.isEnable = true; end
    self:addChild(changeButton)
    
end

function changeVersion(version)

end

--*********************************************************
--* Обработка событий колесика мыши
--*********************************************************
function EtherVersionPanel:onMouseWheel(del)
	self:setYScroll(self:getYScroll() - (del * 40));
	return true;
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
--* Создание нового экземпляра меню
--*********************************************************
function EtherVersionPanel:new(posX, posY, width, height)
    local a = {};

    a = ISPanel:new(posX, posY, width, height);
    setmetatable(a, self);
    a.background = true;
	a.backgroundColor = {r=0.0, g=0.0, b=0.0, a=0.0};
	a.borderColor = {r=0.0, g=0.0, b=0.0, a=0.0};
    a.moveWithMouse = true;
    a.localPlayer = getPlayer();
    self.__index = self;

	a.checkBoxList = {}; -- Список всех чекбоксов
    a.buttonList = {}; -- Список всех кнопок
    self.rows = 0;

    return a;
end
