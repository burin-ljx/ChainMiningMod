package dev.burin.chainMining.network;

import dev.burin.chainMining.ChainMiningMod;
import dev.burin.chainMining.event.BreakBlockEventListener;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.handling.IPayloadHandler;

@MethodsReturnNonnullByDefault
public record ChainMiningPacket(boolean onPressed) implements CustomPacketPayload {
    public static final Type<ChainMiningPacket>TYPE = new Type<>(ChainMiningMod.of("chain_mining"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ChainMiningPacket> STREAM_CODEC =
        StreamCodec.ofMember(ChainMiningPacket::encode, ChainMiningPacket::decode);
    public static final IPayloadHandler<ChainMiningPacket> HANDLER = ChainMiningPacket::serverHandler;

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    private void encode(RegistryFriendlyByteBuf buf) {
        buf.writeBoolean(this.onPressed);
    }

    private static ChainMiningPacket decode(RegistryFriendlyByteBuf buf) {
        return new ChainMiningPacket(buf.readBoolean());
    }

    private void serverHandler(IPayloadContext context) {
        BreakBlockEventListener.setOnPressed(this.onPressed);
    }
}
