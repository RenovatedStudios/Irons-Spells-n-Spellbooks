package io.redspace.ironsspellbooks.particle;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.math.Vector3f;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.redspace.ironsspellbooks.registries.ParticleRegistry;
import net.minecraft.core.particles.DustParticleOptionsBase;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.FriendlyByteBuf;
import org.jetbrains.annotations.NotNull;

public class CastingParticleOptions extends DustParticleOptionsBase {
    public CastingParticleOptions(Vector3f color, float scale) {
        super(color, scale);
    }
    /*
    Copied From Dust Particle Options
     */
    public static final Codec<CastingParticleOptions> CODEC = RecordCodecBuilder.create((p_175793_) -> p_175793_.group(Vector3f.CODEC.fieldOf("color").forGetter((p_175797_) -> p_175797_.color), Codec.FLOAT.fieldOf("scale").forGetter((p_175795_) -> p_175795_.scale)).apply(p_175793_, CastingParticleOptions::new));
    @SuppressWarnings("deprecation")
    public static final Deserializer<CastingParticleOptions> DESERIALIZER = new Deserializer<CastingParticleOptions>() {
        public @NotNull CastingParticleOptions fromCommand(@NotNull ParticleType<CastingParticleOptions> p_123689_, @NotNull StringReader p_123690_) throws CommandSyntaxException {
            Vector3f vector3f = DustParticleOptionsBase.readVector3f(p_123690_);
            p_123690_.expect(' ');
            float f = p_123690_.readFloat();
            return new CastingParticleOptions(vector3f, f);
        }

        public @NotNull CastingParticleOptions fromNetwork(@NotNull ParticleType<CastingParticleOptions> p_123692_, @NotNull FriendlyByteBuf p_123693_) {
            return new CastingParticleOptions(DustParticleOptionsBase.readVector3f(p_123693_), p_123693_.readFloat());
        }
    };

    public @NotNull ParticleType<CastingParticleOptions> getType() {
        return ParticleRegistry.CASTING_PARTICLE.get();
    }
}
