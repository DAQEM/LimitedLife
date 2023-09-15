package com.daqem.limitedlife.mixin;

import com.daqem.limitedlife.LimitedLife;
import com.daqem.limitedlife.event.EventServerTick;
import com.daqem.limitedlife.handlers.TitleHandler;
import com.daqem.limitedlife.player.LimitedLifePlayer;
import com.mojang.authlib.GameProfile;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Mixin(ServerPlayer.class)
public abstract class MixinServerPlayer extends Player implements LimitedLifePlayer {

    @Shadow
    public abstract void attack(Entity entity);

    @Shadow
    @Final
    private static Logger LOGGER;
    private int secondsToLive = LimitedLife.CONFIG.secondsToLive;
    private boolean isBoogeyman = false;

    public MixinServerPlayer(Level level, BlockPos blockPos, float f, GameProfile gameProfile) {
        super(level, blockPos, f, gameProfile);
    }

    public void sendTimeLeftMessage() {
        if (secondsToLive > 0) {
            MutableComponent message = LimitedLife.literal(getTimeLeftAsString()).withStyle(getNameColor());
            getServerPlayer().sendSystemMessage(message, true);
        }
    }

    @Override
    public int getSecondsToLive() {
        return secondsToLive;
    }

    @Override
    public void setSecondsToLive(int seconds) {
        this.secondsToLive = seconds;
        if (this.secondsToLive == 0) {
            handleTimeUp();
        }
    }

    @Override
    public void removeSecondToLive() {
        removeSecondsToLive(1);
    }

    @Override
    public void addSecondsToLive(int seconds) {
        setSecondsToLive(getSecondsToLive() + seconds);
    }

    @Override
    public void removeSecondsToLive(int seconds) {
        setSecondsToLive(getSecondsToLive() - seconds);
    }

    @Override
    public ChatFormatting getNameColor() {
        int secondsToLive = getSecondsToLive();
        if (secondsToLive <= LimitedLife.CONFIG.secondsFromRedColor) {
            return ChatFormatting.RED;
        } else if (secondsToLive <= LimitedLife.CONFIG.secondsFromYellowColor) {
            return ChatFormatting.YELLOW;
        }
        return ChatFormatting.GREEN;
    }

    @Override
    public void setNameColor() {
        if (getServer() != null) {
            Scoreboard scoreboard = getServer().getScoreboard();
            List<String> teamNames = List.of(ChatFormatting.RED.name(), ChatFormatting.YELLOW.name(), ChatFormatting.GREEN.name());
            Collection<String> currentTeamNames = scoreboard.getTeamNames();
            teamNames.forEach(teamName -> {
                if (!currentTeamNames.contains(teamName)) {
                    scoreboard.addPlayerTeam(teamName);
                }
                PlayerTeam playerTeam = scoreboard.getPlayerTeam(teamName);
                if (playerTeam != null) {
                    playerTeam.setColor(ChatFormatting.valueOf(teamName));
                }
            });
            PlayerTeam playerTeam = scoreboard.getPlayerTeam(getNameColor().name());
            if (playerTeam != null) {
                scoreboard.addPlayerToTeam(getGameProfile().getName(), playerTeam);
            }
        }
    }

    @Override
    public boolean canAttack(LimitedLifePlayer target) {
        ChatFormatting targetColor = target.getNameColor();
        boolean b = isBoogeyman()
                || (
                getNameColor() == ChatFormatting.RED
                        && (
                        targetColor == ChatFormatting.YELLOW
                                || targetColor == ChatFormatting.GREEN))
                || (
                getNameColor() == ChatFormatting.YELLOW
                        && targetColor == ChatFormatting.GREEN);
        LimitedLife.LOGGER.info("canAttack: " + b);
        LimitedLife.LOGGER.info("is Boogeyman: " + isBoogeyman());
        return b;
    }

    @Override
    public void handleTimeUp() {
        setBoogeyman(false);
        getServerPlayer().setGameMode(GameType.SPECTATOR);
        Component titleText = LimitedLife.translatable("out_of_time", getServerPlayer().getDisplayName().getString());
        if (getServer() != null) {
            getServer().getPlayerList().getPlayers().forEach(player -> {
                if (player instanceof LimitedLifePlayer limitedLifePlayer) {
                    TitleHandler.sendTitle(limitedLifePlayer, titleText, ChatFormatting.RED);
                }
            });
        }

    }

    @Override
    public void handleBoogeymanFail() {
        setBoogeyman(false);
        if (getServer() != null) {
            getServer().getPlayerList().getPlayers().forEach(player -> {
                if (player instanceof LimitedLifePlayer limitedLifePlayer) {
                    limitedLifePlayer.getServerPlayer().sendSystemMessage(LimitedLife.translatable("boogeyman_fail").withStyle(ChatFormatting.RED), false);
                }
            });
        }
        if (LimitedLife.CONFIG.boogeymanFailLosesColor) {
            removeTimeToNextColor();
        } else {
            removeSecondsToLive(LimitedLife.CONFIG.removedSecondsPerBoogeymanFail);
        }
    }

    private void removeTimeToNextColor() {
        switch (getNameColor()) {
            case RED -> setSecondsToLive(0);
            case YELLOW -> setSecondsToLive(LimitedLife.CONFIG.secondsFromRedColor);
            case GREEN -> setSecondsToLive(LimitedLife.CONFIG.secondsFromYellowColor);
        }
    }

    @Override
    public void setBoogeyman(boolean boogeyman) {
        LOGGER.error("setBoogeyman: " + boogeyman);
        LOGGER.error("boogeymanUUID: " + EventServerTick.BOOGEYMAN);
        isBoogeyman = boogeyman;
        UUID boogeymanUUID = EventServerTick.BOOGEYMAN;
        if (boogeymanUUID == getUUID()) {
            if (!boogeyman) {
                EventServerTick.BOOGEYMAN = null;
            }
        } else if (boogeyman) {
            EventServerTick.BOOGEYMAN = getUUID();
        }
        LOGGER.error("new boogeymanUUID: " + EventServerTick.BOOGEYMAN);
    }

    @Override
    public boolean isBoogeyman() {
        return isBoogeyman;
    }

    @Override
    public String getTimeLeftAsString() {
        return secondsToTimeString(secondsToLive);
    }

    public String secondsToTimeString(int i) {
        int hours = i / 3600;
        int minutes = (i % 3600) / 60;
        int seconds = i % 60;
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    @Override
    public ServerPlayer getServerPlayer() {
        return (ServerPlayer) (Object) this;
    }

    @Override
    public boolean isSpectator() {
        return getServerPlayer().gameMode.getGameModeForPlayer() == GameType.SPECTATOR;
    }

    @Override
    public boolean isCreative() {
        return getServerPlayer().gameMode.getGameModeForPlayer() == GameType.CREATIVE;
    }

    @Inject(at = @At("TAIL"), method = "restoreFrom(Lnet/minecraft/server/level/ServerPlayer;Z)V")
    public void restoreFrom(ServerPlayer oldPlayer, boolean alive, CallbackInfo ci) {
        if (oldPlayer instanceof LimitedLifePlayer oldLimitedLifePlayer) {
            LimitedLife.LOGGER.error("Restoring player {}", getServerPlayer().getDisplayName().getString());
            setSecondsToLive(oldLimitedLifePlayer.getSecondsToLive());
            setBoogeyman(oldLimitedLifePlayer.isBoogeyman());
        }
    }

    @Inject(at = @At("TAIL"), method = "addAdditionalSaveData(Lnet/minecraft/nbt/CompoundTag;)V")
    public void addAdditionalSaveData(CompoundTag compoundTag, CallbackInfo ci) {
        CompoundTag limitedLifeTag = new CompoundTag();
        limitedLifeTag.putInt("secondsToLive", getSecondsToLive());
        limitedLifeTag.putBoolean("isBoogeyman", isBoogeyman());
        compoundTag.put("limitedLife", limitedLifeTag);
        LimitedLife.LOGGER.error("Saved player {}", getServerPlayer().getDisplayName().getString());
    }

    @Inject(at = @At("TAIL"), method = "readAdditionalSaveData(Lnet/minecraft/nbt/CompoundTag;)V")
    public void readAdditionalSaveData(CompoundTag compoundTag, CallbackInfo ci) {
        if (compoundTag.contains("limitedLife")) {
            CompoundTag limitedLifeTag = compoundTag.getCompound("limitedLife");
            secondsToLive = limitedLifeTag.getInt("secondsToLive");
            isBoogeyman = limitedLifeTag.getBoolean("isBoogeyman");
        }
        LimitedLife.LOGGER.error("Loaded player {}", getServerPlayer().getDisplayName().getString());
    }

    @Inject(at = @At("TAIL"), method = "die(Lnet/minecraft/world/damagesource/DamageSource;)V")
    public void die(DamageSource damageSource, CallbackInfo ci) {
        if (damageSource.getEntity() instanceof LimitedLifePlayer attacker) {
            if (attacker.isBoogeyman()) {
                attacker.setBoogeyman(false);
                attacker.addSecondsToLive(LimitedLife.CONFIG.gainedSecondsPerKillAsBoogeyman);
                TitleHandler.sendTitle(attacker, LimitedLife.literal("+" + secondsToTimeString(LimitedLife.CONFIG.gainedSecondsPerKillAsBoogeyman)), ChatFormatting.GREEN);
                this.removeSecondsToLive(LimitedLife.CONFIG.removedSecondsPerDeathByBoogeyman);
                TitleHandler.sendTitle(this, LimitedLife.literal("-" + secondsToTimeString(LimitedLife.CONFIG.removedSecondsPerDeathByBoogeyman)), ChatFormatting.RED);
            } else {
                if (attacker.canAttack(this)) {
                    attacker.addSecondsToLive(LimitedLife.CONFIG.gainedSecondsPerKill);
                    TitleHandler.sendTitle(attacker, LimitedLife.literal("+" + secondsToTimeString(LimitedLife.CONFIG.gainedSecondsPerKill)), ChatFormatting.GREEN);
                    this.removeSecondsToLive(LimitedLife.CONFIG.removedSecondsPerDeath);
                    TitleHandler.sendTitle(this, LimitedLife.literal("-" + secondsToTimeString(LimitedLife.CONFIG.removedSecondsPerDeath)), ChatFormatting.RED);
                }
            }
        } else {
            this.removeSecondsToLive(LimitedLife.CONFIG.removedSecondsPerDeath);
            TitleHandler.sendTitle(this, LimitedLife.literal("-" + secondsToTimeString(LimitedLife.CONFIG.removedSecondsPerDeath)), ChatFormatting.RED);
        }
    }
}
