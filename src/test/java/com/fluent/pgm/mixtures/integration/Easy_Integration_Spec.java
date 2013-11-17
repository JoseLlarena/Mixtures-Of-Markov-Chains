package com.fluent.pgm.mixtures.integration;

import com.fluent.core.oo;
import com.fluent.math.*;
import com.fluent.pgm.mixtures.Base_Spec;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.nio.file.Paths;

import static com.fluent.pgm.mixtures.Easy.Easy;
import static com.fluent.pgm.mixtures.IO.IO;
import static org.hamcrest.Matchers.*;

public class Easy_Integration_Spec extends Base_Spec
{
    static final String ESTIMATED_SWITCHING = "C1";
    static final String ESTIMATED_REPEATING = "C2";
  //
    String model_file = "src/test/resources/momc-integration.json";
    String data_directory = "src/test/resources/tagged";
    String estimated_model_file = "src/test/resources/momc-integration-estimated.json";
    String data_file = "src/test/resources/data-generation.txt";

    @Before
    public void CONTEXT() throws Exception
    {
        IO.to_json(example_model(), Paths.get(model_file));
    }

    @Test
    public void generates_untagged_data_with_correct_statistics() throws Exception
    {
        Easy.untagged_data_from(model_file, data_file, 100_000);

        int frequency_of_a = IO.char_data_from(data_file).select(sequence -> sequence.at(1).equals(A)).size();

        So(frequency_of_a / 100_000.).shouldBe(closeTo(.3 * .3 + .6 * .7, .005));
    }

    @Test
    public void generates_tagged_data_with_correct_statistics() throws Exception
    {
        Easy.tagged_data_from(model_file, data_directory, 100_000);

        int frequency_of_switching = IO.tagged_char_data_from(data_directory).select(sequence_with_tag ->
                sequence_with_tag.$2.equals(SWITCHING)).size();

        So(frequency_of_switching / 100_000.).shouldBe(closeTo(.3, .005));
    }

    @Test
    public void completes_sequence_with_missing_tokens() throws Exception
    {
        So(Easy.complete_characters("ababa¬", model_file)).shouldBe("ababab");
        So(Easy.complete_characters("aaa¬", model_file)).shouldBe("aaaa");
        So(Easy.complete_characters("a¬a¬a", model_file)).shouldBe("aaaaa");
    }

    @Test
    public void tags_sequence() throws Exception
    {
        So(Easy.tag_characters("babababa", model_file)).shouldBe(SWITCHING);
        AND(Easy.tag_characters("abababab", model_file)).shouldBe(SWITCHING);
        AND(Easy.tag_characters("aaaaaaaa", model_file)).shouldBe(REPEATING);
        AND(Easy.tag_characters("bbbbbbbb", model_file)).shouldBe(REPEATING);
    }

    @Test
    public void fuzzy_tags_sequence() throws Exception
    {
        oo<String, P> max_fuzzy_tag = Easy.fuzzy_tag_characters("ab", model_file).max_as((tag, p) -> p);

        So(max_fuzzy_tag.$1()).shouldBe(SWITCHING);
        AND(max_fuzzy_tag.$2().toDouble()).shouldBe(greaterThan(.9));
    }

    @Test
    public void estimates_mixture_from_tagged_data() throws Exception
    {
        Easy.tagged_data_from(model_file, data_directory, 100_000);

        Easy.character_mixture_from_tagged(data_directory, estimated_model_file);

        So(Easy.tag_characters("babababa", estimated_model_file)).shouldBe(SWITCHING);
        So(Easy.tag_characters("aaaaaaaa", estimated_model_file)).shouldBe(REPEATING);
    }

    @Test
    public void estimates_mixture_from_untagged_data() throws Exception
    {
        Easy.untagged_data_from(model_file, data_file, 100_000);

        Easy.character_mixture_from_untagged(data_file, estimated_model_file);

        So(Easy.tag_characters("babababa", estimated_model_file)).shouldBe(ESTIMATED_SWITCHING);
        So(Easy.tag_characters("aaaaaaaa", estimated_model_file)).shouldBe(ESTIMATED_REPEATING);
    }

    @After
    public void CLEAN() throws Exception
    {
        Paths.get(model_file).toFile().delete();
        Paths.get(estimated_model_file).toFile().delete();
        Paths.get(data_file).toFile().delete();
        Paths.get(data_directory, SWITCHING + ".txt").toFile().delete();
        Paths.get(data_directory, REPEATING + ".txt").toFile().delete();
    }
}
