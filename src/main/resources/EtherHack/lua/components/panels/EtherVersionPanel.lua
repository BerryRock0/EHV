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

    self.entry = ISTextEntryBox:new(10, self.configs.y + self.configs.height + 10, self.width / 2 - 60, 24);
    self.entry.font = UIFont.Small;
    self.entry:initialise();
    self.entry:instantiate();
    self:addChild(self.entry);

    local changeButton = UIButton:new(self.entry.x + self.entry.width + 10, self.entry.y, 80, 24, getTranslate("UI_Settings_ConfigSave"), function ()
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

    self.uiElements = {}; -- Список всех элементов

    return menuTableData;
end
