package com.fluent.pgm.new_api;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;
import com.fluent.collections.FList;
import com.fluent.collections.FMap;
import com.fluent.core.F1;
import com.fluent.core.oo;
import com.fluent.core.ooo;
import com.fluent.math.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.nio.file.Paths;

import static com.fluent.collections.Lists.newFList;
import static com.fluent.collections.Maps.newOrderedFMap;
import static com.fluent.core.oo.*;
import static com.fluent.pgm.new_api.MPX_Builder.MPX;
import static com.fluent.pgm.new_api.Seqence.Ngram;
import static com.fluent.util.ReadLines.Read_Lines;
import static java.nio.file.Files.list;
import static java.util.stream.Collectors.toCollection;

public class IO
{
    public static final IO IO = new IO();
    static final ObjectMapper mapper = (new ObjectMapper());

    public IO to_json(MoMC model, Path file) throws IOException
    {
        mapper.writeValue(file.toFile(), model);
        return this;
    }

    public MoMC model_from(Path file) throws IOException
    {
        return mapper.readValue(file.toFile(), MoMC.class);
    }

    public FList<Seqence> char_data_from(String data_file) throws IOException
    {
        return data_from(data_file, Seqence::from_chars);
    }

    public FList<Seqence> data_from(String data_file, F1<String, Seqence> pipeline) throws IOException
    {
        return Read_Lines.from(Paths.get(data_file), pipeline);
    }

    public FList<oo<Seqence, String>> tagged_char_data_from(String data_directory) throws Exception
    {
        FList<Path> files = list(Paths.get(data_directory)).collect(toCollection(() -> newFList( )));

        final F1<Path, F1<String, oo<Seqence, String>>> path_to_line_to_tagged_sequence =
                path -> line -> oo(Seqence.from_chars(line), path.toFile().getName().split("\\.txt")[0]);

        return files.throwing_flatten(file -> Read_Lines.from(file, path_to_line_to_tagged_sequence.of(file)));
    }

    public FList<oo<Seqence, String>> tagged_word_data_from(String data_directory) throws Exception
    {
        FList<Path> files = list(Paths.get(data_directory)).collect(toCollection(() -> newFList( )));

        final F1<Path, F1<String, oo<Seqence, String>>> path_to_line_to_tagged_sequence =
                path -> line -> oo(Seqence.from_words(line), path.toFile().getName().split("\\.txt")[0]);

        return files.throwing_flatten(file -> Read_Lines.from(file, path_to_line_to_tagged_sequence.of(file)));
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

            for (oo<String, P> each : prior.as_map())
            {
                write_number_object(each.$1, each.$2.asLog(), generator);
            }

            generator.writeEndArray();
        }

        public void serialize(MoMC model, JsonGenerator generator, SerializerProvider provider) throws IOException
        {
            generator.writeStartObject();

            write_prior(model.prior(), generator);

            write_conditionals(model.transitions_per_tag(), generator);

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
            FMap<Ngram, P> map = newOrderedFMap();

            for (JsonNode entry : entries)
            {
                String context = entry.fieldNames().next();
                JsonNode conditional = entry.findValue(context);
                String item = conditional.fieldNames().next();
                map.plus(Ngram.from(Token.from(context), Token.from(item)), P.from_log(conditional.findValue
                        (item).asDouble()));
            }
            return CPD_Builder.CPX_from(map);
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
