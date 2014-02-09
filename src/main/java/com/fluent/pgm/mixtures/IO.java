package com.fluent.pgm.mixtures;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;
import com.fluent.collections.FList;
import com.fluent.collections.FMap;
import com.fluent.core.F1;
import com.fluent.core.oo;
import com.fluent.math.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.nio.file.Paths;

import static com.fluent.collections.Lists.newFList;
import static com.fluent.collections.Maps.newFMap;
import static com.fluent.collections.Maps.newOrderedFMap;
import static com.fluent.core.oo.*;
import static com.fluent.pgm.mixtures.Sequence.Ngram;
import static com.fluent.util.ReadLines.Read_Lines;
import static java.lang.Double.NEGATIVE_INFINITY;
import static java.lang.Double.isInfinite;
import static java.nio.file.Files.list;
import static java.util.stream.Collectors.toCollection;

public class IO
{
    public static final IO IO = new IO();
    //
    static final ObjectMapper mapper =  new ObjectMapper() ;

    public IO to_json(MoMC model, Path file) throws IOException
    {
        mapper.writeValue(file.toFile(), model);
        return this;
    }

    public MoMC model_from(Path file) throws IOException
    {
        return mapper.readValue(file.toFile(), MoMC.class);
    }

    public FList<Sequence> char_data_from(String data_file) throws IOException
    {
        return data_from(data_file, Sequence::from_chars);
    }

    public FList<Sequence> data_from(String data_file, F1<String, Sequence> pipeline) throws IOException
    {
        return Read_Lines.from(Paths.get(data_file), pipeline);
    }

    public FList<oo<Sequence, String>> tagged_char_data_from(String data_directory) throws Exception
    {
        FList<Path> files = list(Paths.get(data_directory)).collect(toCollection(() -> newFList()));

        final F1<Path, F1<String, oo<Sequence, String>>> path_to_line_to_tagged_sequence =
                path -> line -> oo(Sequence.from_chars(line), path.toFile().getName().split("\\.txt")[0]);

        return files.throwing_flatmap(file -> Read_Lines.from(file, path_to_line_to_tagged_sequence.of(file)));
    }

    public FList<oo<Sequence, String>> tagged_word_data_from(String data_directory) throws Exception
    {
        FList<Path> files = list(Paths.get(data_directory)).collect(toCollection(() -> newFList()));

        final F1<Path, F1<String, oo<Sequence, String>>> path_to_line_to_tagged_sequence =
                path -> line -> oo(Sequence.from_words(line), path.toFile().getName().split("\\.txt")[0]);

        return files.throwing_flatmap(file -> Read_Lines.from(file, path_to_line_to_tagged_sequence.of(file)));
    }

    static class MoMC_Serialiser extends JsonSerializer<MoMC>
    {
        public void serialize(MoMC model, JsonGenerator generator, SerializerProvider provider) throws IOException
        {
            generator.writeStartObject();

            write_prior(model.prior(), generator);

            write_conditionals(model.transitions_per_tag(), generator);

            generator.writeEndObject();
        }

        static void write_conditionals(FMap<String, CPD> transitions, JsonGenerator generator) throws IOException
        {
            generator.writeArrayFieldStart("conditionals");

            for (oo<String, CPD> each : transitions)
            {
                generator.writeStartObject();
                generator.writeArrayFieldStart(each.$1);

                write_cpd_entry(generator, each);

                generator.writeEndArray();
                generator.writeEndObject();
            }

            generator.writeEndArray();
        }

        static void write_cpd_entry(JsonGenerator generator, oo<String, CPD> each) throws IOException
        {
            for (oo<Ngram, P> entry : each.$2.as_map())
            {
                generator.writeStartObject();
                generator.writeFieldName(entry.$1.context().toString());
                write_number_object(entry.$1.token().toString(), entry.$2.asLog(), generator);
                generator.writeEndObject();
            }
        }

        static void write_number_object(String name, double value, JsonGenerator generator) throws IOException
        {
            generator.writeStartObject();

            if (isInfinite(value))
                generator.writeNumberField(name, NEGATIVE_INFINITY);
            else
                generator.writeNumberField(name, new BigDecimal(value));

            generator.writeEndObject();
        }

        static void write_prior(MPD prior, JsonGenerator generator) throws IOException
        {
            generator.writeArrayFieldStart("prior");

            for (oo<String, P> each : prior.as_map())
            {
                write_number_object(each.$1, each.$2.asLog(), generator);
            }

            generator.writeEndArray();
        }
    }

    static class MoMC_Deserialiser extends JsonDeserializer<MoMC>
    {
        public MoMC deserialize(JsonParser parser, DeserializationContext context) throws IOException
        {
            JsonNode root = parser.getCodec().readTree(parser);

            return new MoMC(prior_from(root), conditionals_from(root));
        }

        static FMap<String, CPD> conditionals_from(JsonNode root)
        {
            JsonNode conditionals_node = root.get("conditionals");

            FMap<String, CPD> conditionals = newOrderedFMap();

            for (JsonNode conditional_pdf : conditionals_node)
            {
                String name = conditional_pdf.fieldNames().next();

                conditionals.plus(name, conditional_from(conditional_pdf.findValue(name)));
            }

            return conditionals;
        }

        static CPD conditional_from(JsonNode entries)
        {
            FMap<Ngram, P> map = newOrderedFMap();

            for (JsonNode entry : entries)
            {
                String context = entry.fieldNames().next();
                JsonNode conditional = entry.findValue(context);
                String item = conditional.fieldNames().next();

                map.plus(Ngram.from(Token.from(context), Token.from(item)), P.from_log(conditional.findValue(item).asDouble()));
            }
            return CPD.from(map);
        }

        static MPD prior_from(JsonNode root)
        {
            JsonNode prior = root.get("prior");

            FMap<String,P> tag_to_p = newFMap();

            for (JsonNode entry : prior)
            {
                String name = entry.fieldNames().next();

                tag_to_p.plus(name, P.from_log(entry.findValue(name).asDouble()));
            }

            return MPD.from(tag_to_p);
        }
    }
}
