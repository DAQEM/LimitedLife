package com.daqem.limitedlife.event;

import com.daqem.limitedlife.command.LimitedLifeCommand;
import dev.architectury.event.events.common.CommandRegistrationEvent;

public class EventRegisterCommands {

    public static void registerEvent() {
        CommandRegistrationEvent.EVENT.register((dispatcher, registry, selection) -> LimitedLifeCommand.registerCommand(dispatcher));
    }
}
