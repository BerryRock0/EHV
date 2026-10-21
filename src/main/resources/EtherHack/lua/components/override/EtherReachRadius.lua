--*********************************************************
--* Расширение радиуса досягаемости
--*********************************************************
-- Позволяет открывать и опустошать контейнеры (ящики, коробки, сейфы, шкафы),
-- а также забирать предметы с расстояния, на которое настроен радиус.
-- Обычная дистанция взаимодействия в игре - 2 тайла.

EtherReachRadius = EtherReachRadius or {};

-- Оригинальная функция перехода к контейнеру
if not EtherReachRadius.originalWalkToContainer and luautils and luautils.walkToContainer then
	EtherReachRadius.originalWalkToContainer = luautils.walkToContainer;
end

--*********************************************************
--* Расстояние до контейнера
--*********************************************************
function EtherReachRadius.getContainerDistance(container, playerObj)
	if not container or not playerObj then return nil end

	local parent = container:getParent()
	if not parent then return nil end

	local square = parent:getSquare()
	if not square then return nil end

	local playerSquare = playerObj:getCurrentSquare()
	if not playerSquare then return nil end

	return square:DistToProper(playerSquare)
end

--*********************************************************
--* Проверка нахождения контейнера в пределах радиуса досягаемости
--*********************************************************
function EtherReachRadius.isContainerInReach(container, playerObj)
	local distance = EtherReachRadius.getContainerDistance(container, playerObj)
	if not distance then return false end

	return distance > 1 and distance <= getReachRadius()
end

--*********************************************************
--* Переопределение перехода к контейнеру
--*********************************************************
-- Если контейнер находится в пределах радиуса досягаемости, игрок не идёт к нему,
-- а предметы переносятся сразу, без подхода.
if EtherReachRadius.originalWalkToContainer then
	function luautils.walkToContainer(container, playerNum)
		if isEnableReachRadius() then
			local playerObj = getSpecificPlayer(playerNum)
			if EtherReachRadius.isContainerInReach(container, playerObj) then
				return true
			end
		end

		return EtherReachRadius.originalWalkToContainer(container, playerNum)
	end
end

--*********************************************************
--* Открытие окна лута контейнера
--*********************************************************
function EtherReachRadius.openLootWindow(object, player)
	local playerObj = getSpecificPlayer(player)
	if not playerObj then return end

	local container = object:getContainer()
	if not container then return end

	if not container:isExplored() then
		container:requestServerItemsForContainer();
		container:setExplored(true);
	end

	local lootPanel = getPlayerLoot(playerObj:getPlayerNum())
	if not lootPanel then return end

	lootPanel:setNewContainer(container);
	lootPanel:setVisible(true);
	if lootPanel.lootAll then lootPanel.lootAll:setVisible(true); end
end

--*********************************************************
--* Перенос всех предметов контейнера в инвентарь игрока
--*********************************************************
function EtherReachRadius.takeAllItems(object, player)
	local playerObj = getSpecificPlayer(player)
	if not playerObj then return end

	local container = object:getContainer()
	if not container then return end

	if not container:isExplored() then
		container:requestServerItemsForContainer();
		container:setExplored(true);
	end

	local items = {};
	local itemList = container:getItems();
	if not itemList then return end

	for i = 0, itemList:size() - 1 do
		local item = itemList:get(i);
		if item then
			table.insert(items, item);
		end
	end

	for _, item in ipairs(items) do
		ISTimedActionQueue.add(ISInventoryTransferAction:new(playerObj, item, container, playerObj:getInventory()));
	end

	print("[EtherHack] Reach radius: " .. #items .. " item(s) taken from " .. tostring(object:getName()));
end

--*********************************************************
--* Пункты контекстного меню для контейнеров на расстоянии
--*********************************************************
-- В меню добавляется сразу два пункта: игра скрывает контекстное меню,
-- если в нём остался всего один пункт.
function EtherReachRadius.onFillWorldObjectContextMenu(player, context, worldobjects, test)
	if not isEnableReachRadius() then return end

	local playerObj = getSpecificPlayer(player)
	if not playerObj then return end

	print("[EtherHack] Reach radius: context menu for " .. tostring(worldobjects and #worldobjects or 0) .. " object(s)");
	for _, object in ipairs(worldobjects) do
		if object and object:getContainer() then
			local container = object:getContainer();
			if EtherReachRadius.isContainerInReach(container, playerObj) then
				context:addOption(getTranslate("UI_ReachRadius_Loot"), worldobjects, EtherReachRadius.onLootContainer, object, player);
				context:addOption(getTranslate("UI_ReachRadius_TakeAll"), worldobjects, EtherReachRadius.onTakeAllContainer, object, player);
				if (test) then return ISWorldObjectContextMenu.setTest() end
			end
		end
	end
end

function EtherReachRadius.onLootContainer(worldobjects, object, player)
	EtherReachRadius.openLootWindow(object, player);
end

function EtherReachRadius.onTakeAllContainer(worldobjects, object, player)
	EtherReachRadius.takeAllItems(object, player);
end

--*********************************************************
--* Открытие контейнера, находящегося на расстоянии
--*********************************************************
function EtherReachRadius.onObjectLeftMouseButtonDown(object, x, y)
	if not isEnableReachRadius() then return end
	if not object then return end

	local container = object:getContainer()
	if not container then return end

	local playerObj = getPlayer()
	if not playerObj then return end

	local distance = EtherReachRadius.getContainerDistance(container, playerObj)

	if not distance or distance <= 1 or distance > getReachRadius() then return end

	print("[EtherHack] Reach radius: opening a container at a distance of " .. tostring(distance));
	EtherReachRadius.openLootWindow(object, playerObj:getPlayerNum());
end

Events.OnObjectLeftMouseButtonDown.Remove(EtherReachRadius.onObjectLeftMouseButtonDown);
Events.OnObjectLeftMouseButtonDown.Add(EtherReachRadius.onObjectLeftMouseButtonDown);

Events.OnFillWorldObjectContextMenu.Remove(EtherReachRadius.onFillWorldObjectContextMenu);
Events.OnFillWorldObjectContextMenu.Add(EtherReachRadius.onFillWorldObjectContextMenu);
