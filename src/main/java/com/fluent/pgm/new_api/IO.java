package com.fluent.pgm.new_api;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;
import com.fluent.collections.FMap;
import com.fluent.core.oo;
import com.fluent.core.ooo;
import com.fluent.math.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Path;

import static com.fluent.collections.Maps.newOrderedFMap;
import static com.fluent.pgm.new_api.CPD_Builder.CPX_from;
import static com.fluent.pgm.new_api.MPX_Builder.MPX;

public class IO
{
    public static final IO IO = new IO();
    static final ObjectMapper mapper = (new ObjectMapper());

    public IO write_json(MoMC model, Path file) throws IOException
    {
        mapper.writeValue(file.toFile(), model);
        return this;
    }

    public MoMC read_from_json(Path file) throws IOException
    {
        return mapper.readValue(file.toFile(), MoMC.class);
    }

    static class MoMC_Serialiser extends JsonSerializer<MoMC>
    {
        static void write_conditionals(FMap<String, CPX> transitions, JsonGenerator generator) throws IOException
        {
            generator.writeArrayFieldStart("conditionals");

            for (oo<String, CPX> each : transitions)
            {
                generator.writeStartObject();
                generator.writeArrayFieldStart(each.$1);

                write_cpd_entry(generator, each);

                generator.writeEndArray();
                generator.writeEndObject();
            }

            generator.writeEndArray();
        }

        static void write_cpd_entry(JsonGenerator generator, oo<String, CPX> each) throws IOException
        {
            for (ooo<Context, Token, P> entry : each.$2)
            {
                generator.writeStartObject();
                generator.writeFieldName(entry.$1.toString());
                write_number_object(entry.$2.toString(), entry.$3.asLog(), generator);
                generator.writeEndObject();
            }
        }

        static void write_number_object(String name, double value, JsonGenerator generator) throws IOException
        {
            generator.writeStartObject();
            generator.writeNumberField(name, new BigDecimal(value));
            generator.writeEndObject();
        }

        static void write_prior(MPX prior, JsonGenerator generator) throws IOException
        {
            generator.writeArrayFieldStart("prior");

            for (oo<String, P> each : prior)
            {
                write_number_object(each.$1, each.$2.asLog(), generator);
            }

            generator.writeEndArray();
        }

        public void serialize(MoMC model, JsonGenerator generator, SerializerProvider provider) throws IOException
        {
            generator.writeStartObject();

            write_prior(model.prior(), generator);

            write_conditionals(model.transitions(), generator);

            generator.writeEndObject();
        }
    }

    static class MoMC_Deserialiser extends JsonDeserializer<MoMC>
    {
        static FMap<String, CPX> conditionals_from(JsonNode root)
        {
            JsonNode conditionals_node = root.get("conditionals");

            FMap<String, CPX> conditionals = newOrderedFMap();

            for (JsonNode conditional_pdf : conditionals_node)
            {
                String name = conditional_pdf.fieldNames().next();

                conditionals.plus(name, conditional_from(conditional_pdf.findValue(name)));
            }

            return conditionals;
        }

        static CPX conditional_from(JsonNode entries)
        {
            FMap<Seqence.Ngram, P> map = newOrderedFMap();

            for (JsonNode entry : entries)
            {
                String context = entry.fieldNames().next();
                JsonNode conditional = entry.findValue(context);
                String item = conditional.fieldNames().next();
                map.plus(new Seqence.Ngram(Token.from(context), Token.from(item)), P.from_log(conditional.findValue
                        (item).asDouble()));
            }
            return CPX_from(map);
        }

        static MPX prior_from(JsonNode root)
        {
            JsonNode prior = root.get("prior");

            MPX_Builder prior_to_be = MPX();

            for (JsonNode entry : prior)
            {
                String name = entry.fieldNames().next();

                prior_to_be.p(name, P.from_log(entry.findValue(name).asDouble()));
            }

            return prior_to_be.done();
        }

        public MoMC deserialize(JsonParser parser, DeserializationContext context) throws IOException
        {
            JsonNode root = parser.getCodec().readTree(parser);

            return new MoMC(prior_from(root), conditionals_from(root));
        }
    }
}
