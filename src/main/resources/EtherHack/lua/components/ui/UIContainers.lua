require "ISUI/ISPanel"

--*********************************************************
--* Глобальные установки UI
--*********************************************************
UIContainers = ISPanel:derive("UIContainers"); -- Наследование от ISPanel
UIContainers.instance = nil;

local fontHeightSmall = getTextManager():getFontHeight(UIFont.Small)

--*********************************************************
--* Создание дочерних элементов
--*********************************************************
function UIContainers:createChildren()
    ISPanel.createChildren(self);

    self.closeButton = ISButton:new(3, 0, 20, 20, "", self, function(self, button) self:close() end);
    self.closeButton:initialise();
    self.closeButton.borderColor.a = 0.0;
    self.closeButton.backgroundColor.a = 0;
    self.closeButton.backgroundColorMouseOver.a = 0;
    self.closeButton:setImage(self.closeTexture);
    self:addChild(self.closeButton);

    self.summary = ISLabel:new(0, 30, self.width, 20, "", 1, 1, 1, 1, UIFont.Small, true);
    self.summary:initialise();
    self.summary:instantiate();
    self:addChild(self.summary);

    self.datas = ISScrollingListBox:new(10, 55, self.width - 20, self.height - 120);
    self.datas:initialise();
    self.datas:instantiate();
    self.datas.itemheight = fontHeightSmall + 4 * 2
    self.datas.selected = 0;
    self.datas.joypadParent = self;
    self.datas.font = UIFont.NewSmall;
    self.datas.doDrawItem = self.drawDatas;
    self.datas.drawBorder = true;
    self.datas.backgroundColor = {r=0, g=0, b=0, a=1.0};
    self.datas:addColumn(getTranslate("UI_Containers_Container"), 0);
    self.datas:addColumn(getTranslate("UI_Containers_Distance"), 150)
    self.datas:addColumn(getTranslate("UI_Containers_Item"), 240)
    self:addChild(self.datas);

    self.radiusLabel = ISLabel:new(10, self.height - 55, 130, 20, getTranslate("UI_Containers_Radius"), 1, 1, 1, 1, UIFont.Small, true);
    self.radiusLabel:initialise();
    self.radiusLabel:instantiate();
    self:addChild(self.radiusLabel);

    self.radiusSlider = UISlider:new(140, self.height - 52, 180, 10, getContainersListRadius(), 5, 40, function(value)
        setContainersListRadius(value);
        self:loadContainers();
    end)
    self.radiusSlider:initialise();
    self.radiusSlider:instantiate();
    self:addChild(self.radiusSlider);

    self.refreshButton = UIButton:new(self.width - 130, self.height - 58, 110, 20, getTranslate("UI_Containers_RefreshButton"),
    function()
        self:loadContainers();
    end)
    self.refreshButton:initialise();
    self.refreshButton:instantiate();
    self:addChild(self.refreshButton);
end

--*********************************************************
--* Загрузка списка контейнеров
--*********************************************************
function UIContainers:loadContainers()
    self.datas:clear();

    local containers = getContainersList();
    local containersCount = 0;

    for _, container in ipairs(containers) do
        containersCount = containersCount + 1;
        local items = container.items;
        if items and #items > 0 then
            for _, item in ipairs(items) do
                self.datas:addItem(container.name, { container = container, item = item });
            end
        else
            self.datas:addItem(container.name, { container = container, item = nil });
        end
    end

    self.containersCount = containersCount;
    self.summary:setText(getTranslate("UI_Containers_Found") .. ": " .. containersCount);
end

--*********************************************************
--* Отрисовка данных
--*********************************************************
function UIContainers:drawDatas(y, item, alt)
    if y + self:getYScroll() + self.itemheight < 0 or y + self:getYScroll() >= self.height then
        return y + self.itemheight
    end

    if self.selected == item.index then
        self:drawRect(0, y, self:getWidth(), self.itemheight, 0.3, EtherMain.accentColor.r, EtherMain.accentColor.g, EtherMain.accentColor.b);
    end

    if alt then
        self:drawRect(0, y, self:getWidth(), self.itemheight, 0.3, 0.3, 0.3, 0.3);
    end

    self:drawRectBorder(0, y, self:getWidth(), self.itemheight, 0.5, 1, 1, 1);
    self:drawRectBorder(self.columns[1].size, y, self.columns[2].size, self.itemheight, 0.5, 1, 1, 1);

    local container = item.item.container;
    local clipX = self.columns[1].size
    local clipX2 = self.columns[2].size
    local clipY = math.max(0, y + self:getYScroll())
    local clipY2 = math.min(self.height, y + self:getYScroll() + self.itemheight)

    -- Название контейнера
    self:suspendStencil();
    self:setStencilRect(clipX, clipY, clipX2 - clipX, clipY2 - clipY)
    self:drawText(tostring(container.name), 5, y + 4, 1, 1, 1, 1, UIFont.Small);
    self:clearStencilRect();
    self:resumeStencil();

    -- Расстояние до контейнера
    self:drawText(tostring(math.floor(container.distance + 0.5)) .. "m", self.columns[2].size + 10, y + 4, 1, 1, 1, 1, UIFont.Small);

    -- Содержимое контейнера
    local lootItem = item.item.item;
    if lootItem then
        local itemName = tostring(lootItem.name);
        if lootItem.count and lootItem.count > 1 then
            itemName = itemName .. " x" .. tostring(lootItem.count);
        end
        self:drawText(itemName, self.columns[3].size + 10, y + 4, 1, 1, 1, 1, UIFont.Small);
    end

    return y + self.itemheight;
end

--*********************************************************
--* Обновление окна
--*********************************************************
function UIContainers:update()
    if not self.isLoaded then
        self:loadContainers();
        self.isLoaded = true;
    end
end

--*********************************************************
--* Обработка prerender
--*********************************************************
function UIContainers:prerender()
    ISPanel.prerender(self)

    self:drawRect( 0, 0, self.width, 20, 1.0, 0, 0, 0, 0.5)
    self:drawTextCentre(self.title, self:getWidth() / 2, 1, 1, 1, 1, 1, UIFont.Small);
end

--*********************************************************
--* Обработка render
--*********************************************************
function UIContainers:render()
    ISPanel.render(self)
end

--*********************************************************
--* Закрытие окна
--*********************************************************
function UIContainers:close()
    UIContainers.instance:setVisible(false);
    UIContainers.instance:removeFromUIManager();
    UIContainers.instance = nil;
end

--*********************************************************
--* Логика открытия окна
--*********************************************************
function UIContainers.openPanel()
    -- Если окно уже существует, закрываем его
    if UIContainers.instance ~= nil then
        UIContainers.instance:setVisible(false);
        UIContainers.instance:removeFromUIManager();
        UIContainers.instance = nil;
        return
    end

    -- Создаем новое окно
    UIContainers.instance = UIContainers:new();
    UIContainers.instance:initialise();
    UIContainers.instance:instantiate();
    UIContainers.instance:addToUIManager();
    UIContainers.instance:setVisible(true);
    UIContainers.instance:setAlwaysOnTop(true);
end

--*********************************************************
--* Создание нового экземпляра окна
--*********************************************************
function UIContainers:new()
    local menuTableData = {};

    local width = 560;
    local height = 420;

    local positionX = getCore():getScreenWidth() / 2 - width / 2;
    local positionY = getCore():getScreenHeight() / 2 - height / 2;

    menuTableData = ISPanel:new(positionX, positionY, width, height);
    setmetatable(menuTableData, self);
    menuTableData.borderColor = {r=0.0, g=0.0, b=0.0, a=0.0};
    menuTableData.backgroundColor = {r=0.0, g=0.0, b=0.0, a=0.8};
    menuTableData.title = getTranslate("UI_Containers_Title");
    menuTableData.moveWithMouse = true;
    menuTableData.isLoaded = false;
    menuTableData.containersCount = 0;
    self.__index = self;

    return menuTableData;
end
