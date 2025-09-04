package net.zead.secondchat.mixin;

import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.text.Text;
import net.minecraft.network.message.MessageSignatureData;
import net.minecraft.client.gui.hud.MessageIndicator;
import net.zead.secondchat.SecondChat;
import net.zead.secondchat.hud.SecondChatHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChatHud.class)
public class ChatHudMixin {
    @Inject(method = "addMessage(Lnet/minecraft/text/Text;Lnet/minecraft/network/message/MessageSignatureData;ILnet/minecraft/client/gui/hud/MessageIndicator;Z)V", at = @At("HEAD"), cancellable = true)
    private void onAddMessage(Text message, MessageSignatureData signature, int ticks, MessageIndicator indicator, boolean refresh, CallbackInfo ci) {
        SecondChat.LOGGER.debug("Mixin applied: attempting to process message");
        SecondChat instance = SecondChat.instance();
        if (instance == null) {
            SecondChat.LOGGER.error("SecondChat instance is null, skipping message processing");
            return;
        }
        String text = message.getString();
        SecondChat.LOGGER.debug("Received message: {} (raw: {}, style: {}, color: {})",
                text,
                message,
                message.getStyle(),
                message.getStyle().getColor() != null ? message.getStyle().getColor().getHexCode() : "null");
        if (instance.matches(text)) {
            SecondChat.LOGGER.debug("Message matches filter, adding to second chat: {}", text);
            SecondChatHud.addMessage(message);
            ci.cancel();
        } else {
            SecondChat.LOGGER.debug("Message does not match filter: {}", text);
        }
    }
}