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
--* Проверка нахождения контейнера в пределах радиуса досягаемости
--*********************************************************
function EtherReachRadius.isContainerInReach(container, playerObj)
	if not container or not playerObj then return false end

	local parent = container:getParent()
	if not parent then return false end

	local square = parent:getSquare()
	if not square then return false end

	local playerSquare = playerObj:getCurrentSquare()
	if not playerSquare then return false end

	return square:DistToProper(playerSquare) <= getReachRadius()
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
--* Открытие контейнера, находящегося на расстоянии
--*********************************************************
function EtherReachRadius.onObjectLeftMouseButtonDown(object, x, y)
	if not isEnableReachRadius() then return end
	if not object then return end

	local container = object:getContainer()
	if not container then return end

	local playerObj = getPlayer()
	if not playerObj then return end

	local square = object:getSquare()
	local playerSquare = playerObj:getCurrentSquare()
	if not square or not playerSquare then return end

	local distance = square:DistToProper(playerSquare)
	if distance <= 1 or distance > getReachRadius() then return end

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

Events.OnObjectLeftMouseButtonDown.Remove(EtherReachRadius.onObjectLeftMouseButtonDown);
Events.OnObjectLeftMouseButtonDown.Add(EtherReachRadius.onObjectLeftMouseButtonDown);
