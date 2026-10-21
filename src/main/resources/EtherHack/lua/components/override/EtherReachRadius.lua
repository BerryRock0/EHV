--*********************************************************
--* Расширение радиуса досягаемости
--*********************************************************
-- Позволяет открывать и опустошать контейнеры (ящики, коробки, сейфы, шкафы),
-- а также забирать предметы с любого расстояния, без подхода к контейнеру.
-- Обычная дистанция взаимодействия в игре - 2 тайла.
--
-- Важно: игра перезагружает свои lua-файлы при старте мира, поэтому
-- перехваты устанавливаются заново после загрузки игры (OnGameStart и по таймеру).

EtherReachRadius = EtherReachRadius or {};

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

	return distance > 1 and distance <= getReachRadius();
end

--*********************************************************
--* Переход к контейнеру без подхода
--*********************************************************
function EtherReachRadius.overrideWalkToContainer(container, playerNum)
	if isEnableReachRadius() then
		local playerObj = getSpecificPlayer(playerNum)
		if EtherReachRadius.isContainerInReach(container, playerObj) then
			return true
		end
	end

	return EtherReachRadius.originalWalkToContainer(container, playerNum);
end

--*********************************************************
--* Обработка клика по контейнеру на расстоянии
--*********************************************************
function EtherReachRadius.overrideDoClick(object, x, y)
	if isEnableReachRadius() and object and object:getContainer() then
		local playerObj = getSpecificPlayer(0);
		local container = object:getContainer();
		local distance = EtherReachRadius.getContainerDistance(container, playerObj);

		if distance and distance > 1 and distance <= getReachRadius() then
			print("[EtherHack] Reach radius: opening a container at a distance of " .. tostring(distance));
			EtherReachRadius.openLootWindow(object, 0);
			return;
		end
	end

	return EtherReachRadius.originalDoClick(object, x, y);
end

--*********************************************************
--* Установка перехватов
--*********************************************************
function EtherReachRadius.applyOverrides()
	-- Переход к контейнеру
	if luautils and luautils.walkToContainer and luautils.walkToContainer ~= EtherReachRadius.overrideWalkToContainer then
		EtherReachRadius.originalWalkToContainer = luautils.walkToContainer;
		luautils.walkToContainer = EtherReachRadius.overrideWalkToContainer;
		print("[EtherHack] Reach radius: the override of luautils.walkToContainer is applied");
	end

	-- Обработка клика по объекту мира
	if ISObjectClickHandler and ISObjectClickHandler.doClick and ISObjectClickHandler.doClick ~= EtherReachRadius.overrideDoClick then
		EtherReachRadius.originalDoClick = ISObjectClickHandler.doClick;
		ISObjectClickHandler.doClick = EtherReachRadius.overrideDoClick;
		print("[EtherHack] Reach radius: the override of ISObjectClickHandler.doClick is applied");
	end
end

--*********************************************************
--* Открытие окна лута контейнера
--*********************************************************
function EtherReachRadius.openLootWindow(object, playerNum)
	local playerObj = getSpecificPlayer(playerNum)
	if not playerObj then return end

	local container = object:getContainer()
	if not container then return end

	-- Содержимое контейнера запрашивает сервер: он сам наполняет контейнер лутом
	-- и только потом помечает его изученным. Помечать контейнер изученным здесь нельзя:
	-- в одиночной игре контейнер общий для клиента и сервера, и сервер не наполнит его.
	if not container:isExplored() then
		container:requestServerItemsForContainer();
	end

	local lootPanel = getPlayerLoot(playerNum)
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

	-- Запрос содержимого у сервера, флаг "изучен" ставит сам сервер
	if not container:isExplored() then
		container:requestServerItemsForContainer();
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
--* Открытие контейнера, находящегося на расстоянии (клик мышью)
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

--*********************************************************
--* Повторная установка перехватов после загрузки мира
--*********************************************************
function EtherReachRadius.onGameStart()
	EtherReachRadius.applyOverrides();
end

function EtherReachRadius.onTick()
	EtherReachRadius.tickCounter = (EtherReachRadius.tickCounter or 0) + 1;
	if EtherReachRadius.tickCounter >= 120 then
		EtherReachRadius.tickCounter = 0;
		EtherReachRadius.applyOverrides();
	end

	-- Диагностика: сообщаем, когда предметы контейнера дошли до окна лута
	if not EtherReachRadius.reportedItems then
		local lootPanel = getPlayerLoot(0);
		if lootPanel and lootPanel.inventory then
			local items = lootPanel.inventory:getItems();
			if items and items:size() > 0 then
				EtherReachRadius.reportedItems = true;
				print("[EtherHack] Reach radius: the loot panel shows " .. items:size() .. " item(s)");
			end
		end
	end
end

-- Установка перехватов при загрузке клиента
EtherReachRadius.applyOverrides();

Events.OnObjectLeftMouseButtonDown.Remove(EtherReachRadius.onObjectLeftMouseButtonDown);
Events.OnObjectLeftMouseButtonDown.Add(EtherReachRadius.onObjectLeftMouseButtonDown);

Events.OnFillWorldObjectContextMenu.Remove(EtherReachRadius.onFillWorldObjectContextMenu);
Events.OnFillWorldObjectContextMenu.Add(EtherReachRadius.onFillWorldObjectContextMenu);

Events.OnGameStart.Remove(EtherReachRadius.onGameStart);
Events.OnGameStart.Add(EtherReachRadius.onGameStart);

Events.OnTick.Remove(EtherReachRadius.onTick);
Events.OnTick.Add(EtherReachRadius.onTick);
